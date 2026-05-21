package Telas;

import Model.Produto;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import DAO.ModuloConexao;
import java.util.ArrayList;

public class TelaPedidos extends JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    ArrayList<Integer> idClientes = new ArrayList<>();
    ArrayList<Integer> idProdutos = new ArrayList<>();
    
    private JComboBox<String> comboCliente;
    private JComboBox<Produto> comboProduto;
    private JComboBox<String> comboPagamento;
    private JTextField txtDesconto;
    private JTextField txtTotal;

    public TelaPedidos() {
        initComponents();
        conexao = ModuloConexao.conector();
        
        // Verifica se conectou
        if (conexao != null) {
            popularComboBoxes();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Sem conexão com o banco!\nReconecte e tente novamente.", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void popularComboBoxes() {
        try {
            // Limpa as listas
            idClientes.clear();
            idProdutos.clear();
            
            // === CARREGA CLIENTES ===
            String sqlCli = "SELECT id_cliente, nome_cliente FROM clientes ORDER BY nome_cliente";
            pst = conexao.prepareStatement(sqlCli);
            rs = pst.executeQuery();
            
            comboCliente.removeAllItems();
            comboCliente.addItem("Selecione o cliente");
            idClientes.add(0); 
            
            while (rs.next()) {
                comboCliente.addItem(rs.getString("nome_cliente"));
                idClientes.add(rs.getInt("id_cliente"));
            }
            rs.close();
            pst.close();

            // === CARREGA PRODUTOS ===
            String sqlProd = "SELECT id_produto, nome_produto, valor_produto FROM produtos ORDER BY nome_produto";
            pst = conexao.prepareStatement(sqlProd);
            rs = pst.executeQuery();
            
            comboProduto.removeAllItems();
            comboProduto.addItem(new Produto(0, "Selecione o produto", 0, ""));
            idProdutos.add(0);

            while (rs.next()) {
                Produto p = new Produto(
                    rs.getInt("id_produto"), 
                    rs.getString("nome_produto"), 
                    rs.getDouble("valor_produto"), 
                    ""
                );
                comboProduto.addItem(p);
                idProdutos.add(p.getId());
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erro ao carregar dados do banco:\n" + e.getMessage(), 
                "Erro SQL", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
            } catch (SQLException ex) {
                System.err.println("Erro ao fechar recursos: " + ex.getMessage());
            }
        }
    }

    private void initComponents() {
        setTitle("Pedidos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 540);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(new Color(217, 217, 217));

        JPanel card = new JPanel();
        card.setBackground(new Color(123, 17, 19));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(27, 67, 50), 3),
            BorderFactory.createEmptyBorder(0, 25, 25, 25)
        ));
        card.setPreferredSize(new Dimension(380, 490));

        // Header
        JLabel header = new JLabel("Tela Pedidos", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 18));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(new Color(27, 67, 50));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        card.add(header);
        card.add(Box.createVerticalStrut(12));

        // Seleção de Cliente
        addLabel(card, "Seleção de cliente:");
        comboCliente = new JComboBox<>(new String[]{"Selecione o cliente"});
        estilizarCombo(comboCliente);
        card.add(comboCliente);
        card.add(Box.createVerticalStrut(10));

        // Seleção de Produto
        addLabel(card, "Seleção de Produtos:");
        comboProduto = new JComboBox<>();
        comboProduto.addItem(new Produto(0, "Selecione o produto", 0, ""));
        estilizarCombo(comboProduto);
        comboProduto.addActionListener(e -> calcularTotal());
        card.add(comboProduto);
        card.add(Box.createVerticalStrut(10));

        // Separador Pagamento
        JLabel lblSep = new JLabel("Pagamento", SwingConstants.CENTER);
        lblSep.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSep.setForeground(new Color(123, 17, 19));
        lblSep.setOpaque(true);
        lblSep.setBackground(new Color(245, 240, 232));
        lblSep.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        lblSep.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        card.add(lblSep);
        card.add(Box.createVerticalStrut(10));

        // Forma de Pagamento
        addLabel(card, "Forma de pagamento:");
        comboPagamento = new JComboBox<>(new String[]{
            "Selecione", "Dinheiro", "Cartão de Crédito", "Cartão de Débito", "Pix"
        });
        estilizarCombo(comboPagamento);
        card.add(comboPagamento);
        card.add(Box.createVerticalStrut(10));

        // Desconto
        addLabel(card, "Desconto % (máx 15%):");
        txtDesconto = new JTextField("0");
        txtDesconto.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtDesconto.setBackground(new Color(245, 240, 232));
        txtDesconto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtDesconto.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        txtDesconto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { calcularTotal(); }
        });
        card.add(txtDesconto);
        card.add(Box.createVerticalStrut(10));

        // Total
        addLabel(card, "Total do pedido:");
        txtTotal = new JTextField("R$ 0,00");
        txtTotal.setFont(new Font("SansSerif", Font.BOLD, 14));
        txtTotal.setBackground(new Color(245, 240, 232));
        txtTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtTotal.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        txtTotal.setEditable(false);
        card.add(txtTotal);
        card.add(Box.createVerticalStrut(18));

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        painelBotoes.setBackground(new Color(123, 17, 19));
        painelBotoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnVoltar = new JButton("Voltar ao MENU");
        btnVoltar.setBackground(new Color(27, 67, 50));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnVoltar.setFocusPainted(false);
        btnVoltar.addActionListener(e -> {
            TelaMenu menu = new TelaMenu();
            menu.setVisible(true);
            this.dispose();
        });

        JButton btnFinalizar = new JButton("Finalizar");
        btnFinalizar.setBackground(new Color(245, 240, 232));
        btnFinalizar.setForeground(Color.BLACK);
        btnFinalizar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnFinalizar.setFocusPainted(false);
        btnFinalizar.addActionListener(e -> finalizar());
        
        painelBotoes.add(btnVoltar);
        painelBotoes.add(btnFinalizar);
        card.add(painelBotoes);

        fundo.add(card);
        setContentPane(fundo);
    }

    private void addLabel(JPanel pai, String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pai.add(lbl);
        pai.add(Box.createVerticalStrut(3));
    }

    private void estilizarCombo(JComboBox combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBackground(new Color(245, 240, 232));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    }

    private void calcularTotal() {
        Object item = comboProduto.getSelectedItem();
        
        if (item instanceof Produto) {
            Produto p = (Produto) item;
            double preco = p.getValor();
            
            try {
                double desc = Double.parseDouble(txtDesconto.getText().replace(",", "."));
                if (desc > 15) { 
                    desc = 15; 
                    txtDesconto.setText("15"); 
                }
                if (desc < 0) {
                    desc = 0;
                    txtDesconto.setText("0");
                }
                
                double total = preco - (preco * desc / 100);
                txtTotal.setText(String.format("R$ %.2f", total));
            } catch (NumberFormatException ex) {
                txtTotal.setText(String.format("R$ %.2f", preco));
            }
        }
    }

    private void finalizar() {
        // 1. Validações
        if (conexao == null) {
            JOptionPane.showMessageDialog(this, 
                "Sem conexão com o banco!", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (comboCliente.getSelectedIndex() <= 0 || comboProduto.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, 
                "Selecione Cliente e Produto!", 
                "Atenção", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (comboPagamento.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, 
                "Selecione a forma de pagamento!", 
                "Atenção", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Prepara o SQL
        String sql = "INSERT INTO pedidos (id_cliente, forma_pagamento, desconto, valor_total, data_pedido, tipo_movimentacao) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            pst = conexao.prepareStatement(sql);
            
            // Pega o ID do cliente
            int idCli = idClientes.get(comboCliente.getSelectedIndex());
            
            pst.setInt(1, idCli);
            pst.setString(2, comboPagamento.getSelectedItem().toString());
            pst.setDouble(3, Double.parseDouble(txtDesconto.getText().replace(",", ".")));
            
            // Limpa o "R$" do total
            String valorLimpo = txtTotal.getText().replace("R$ ", "").replace(",", ".");
            pst.setDouble(4, Double.parseDouble(valorLimpo));
            
            pst.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            pst.setString(6, "Entrada");

            // 3. Executa
            int adicionado = pst.executeUpdate();
            
            if (adicionado > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Pedido salvo com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Limpa para o próximo
                comboCliente.setSelectedIndex(0);
                comboProduto.setSelectedIndex(0);
                comboPagamento.setSelectedIndex(0);
                txtDesconto.setText("0");
                txtTotal.setText("R$ 0,00");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao salvar pedido:\n" + e.getMessage(), 
                "Erro SQL", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            ModuloConexao.fecharStatement(pst);
        }
    }
}