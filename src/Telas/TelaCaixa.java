package Telas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import DAO.ModuloConexao;

public class TelaCaixa extends JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    private JComboBox<String> comboTipo;
    private JTextField txtValor;
    private JTextField txtData;
    private JLabel lblSaldo;
    private DefaultTableModel tableModel;
    private double saldoAtual = 0.0;

    public TelaCaixa() {
        initComponents();
        conexao = ModuloConexao.conector();
        
        if (conexao != null) {
            listarMovimentacoes();
            calcularSaldo();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Sem conexão com o banco!", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
       
        setTitle("Caixa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel fundo = new JPanel(new BorderLayout(10, 10));
        fundo.setBackground(new Color(217, 217, 217));
        fundo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // === SALDO NO TOPO ===
        JPanel painelSaldo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelSaldo.setBackground(new Color(217, 217, 217));

        JPanel cardSaldo = new JPanel(new BorderLayout());
        cardSaldo.setBackground(new Color(27, 67, 50));
        cardSaldo.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        cardSaldo.setPreferredSize(new Dimension(250, 70));

        JLabel lblTituloSaldo = new JLabel("Saldo atual", SwingConstants.CENTER);
        lblTituloSaldo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTituloSaldo.setForeground(Color.WHITE);

        lblSaldo = new JLabel("R$ 0,00", SwingConstants.CENTER);
        lblSaldo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblSaldo.setForeground(Color.WHITE);

        cardSaldo.add(lblTituloSaldo, BorderLayout.NORTH);
        cardSaldo.add(lblSaldo, BorderLayout.CENTER);
        painelSaldo.add(cardSaldo);
        fundo.add(painelSaldo, BorderLayout.NORTH);

        // === PAINEL CENTRAL (formulário + tabela) ===
        JPanel painelCentro = new JPanel(new GridLayout(1, 2, 15, 0));
        painelCentro.setBackground(new Color(217, 217, 217));

        // --- Formulário ---
        JPanel cardForm = new JPanel();
        cardForm.setBackground(new Color(123, 17, 19));
        cardForm.setLayout(new BoxLayout(cardForm, BoxLayout.Y_AXIS));
        cardForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(27, 67, 50), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Tipo (combo com dropdown)
        addLabel(cardForm, "Tipo:");
        comboTipo = new JComboBox<>(new String[]{"Entrada", "Saída"});
        comboTipo.setBackground(new Color(245, 240, 232));
        comboTipo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        comboTipo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cardForm.add(comboTipo);
        cardForm.add(Box.createVerticalStrut(10));

        // Valor
        addLabel(cardForm, "Valor (R$):");
        txtValor = new JTextField();
        txtValor.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtValor.setBackground(new Color(245, 240, 232));
        txtValor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtValor.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        cardForm.add(txtValor);
        cardForm.add(Box.createVerticalStrut(10));

        // Data
        addLabel(cardForm, "Data:");
        txtData = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        txtData.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtData.setBackground(new Color(245, 240, 232));
        txtData.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtData.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        cardForm.add(txtData);
        cardForm.add(Box.createVerticalStrut(20));

        // Botões Limpar / Registrar
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelBotoes.setBackground(new Color(123, 17, 19));
        painelBotoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.setBackground(new Color(150, 150, 150));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLimpar.setFocusPainted(false);
        btnLimpar.addActionListener(e -> {
            txtValor.setText("");
            txtData.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            comboTipo.setSelectedIndex(0);
        });

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(new Color(245, 240, 232));
        btnRegistrar.setForeground(Color.BLACK);
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.addActionListener(e -> registrar());

        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnRegistrar);
        cardForm.add(painelBotoes);
        cardForm.add(Box.createVerticalStrut(10));

        // Botão Voltar ao Menu
        JButton btnVoltar = new JButton("Voltar ao Menu");
        btnVoltar.setBackground(new Color(27, 67, 50));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnVoltar.setFocusPainted(false);
        btnVoltar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVoltar.setMaximumSize(new Dimension(160, 32));
        btnVoltar.addActionListener(e -> {
            TelaMenu menu = new TelaMenu();
            menu.setVisible(true);
            this.dispose();
        });
        cardForm.add(btnVoltar);

        // --- Tabela de lançamentos ---
        JPanel cardTabela = new JPanel(new BorderLayout());
        cardTabela.setBackground(new Color(123, 17, 19));
        cardTabela.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(27, 67, 50), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTabTitulo = new JLabel("Últimas movimentações", SwingConstants.CENTER);
        lblTabTitulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTabTitulo.setForeground(Color.WHITE);
        lblTabTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        cardTabela.add(lblTabTitulo, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Data", "Tipo", "Valor"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabela = new JTable(tableModel);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tabela.setBackground(new Color(245, 240, 232));
        tabela.setGridColor(new Color(27, 67, 50));
        tabela.getTableHeader().setBackground(new Color(27, 67, 50));
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabela.setRowHeight(22);

        cardTabela.add(new JScrollPane(tabela), BorderLayout.CENTER);

        painelCentro.add(cardForm);
        painelCentro.add(cardTabela);
        fundo.add(painelCentro, BorderLayout.CENTER);

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

    /**
     * MÉTODO CORRIGIDO: Lista todas as movimentações do banco
     */
    private void listarMovimentacoes() {
        if (conexao == null) return;
        
        tableModel.setNumRows(0); // Limpa a tabela

        // SQL que COMBINA pedidos + movimentações de caixa
        String sql = 
            // Pedidos (vendas)
            "SELECT data_pedido AS data, 'Entrada (Pedido)' AS tipo, valor_total AS valor " +
            "FROM pedidos " +
            "UNION ALL " +
            // Movimentações do caixa
            "SELECT data_movimentacao AS data, " +
            "CASE WHEN tipo_movimentacao = 'Entrada' THEN 'Entrada' ELSE 'Saída' END AS tipo, " +
            "valor " +
            "FROM movimentacoes_caixa " +
            "ORDER BY data DESC";

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                String data = rs.getString("data");
                String tipo = rs.getString("tipo");
                double valor = rs.getDouble("valor");

                tableModel.addRow(new Object[]{
                    data,
                    tipo,
                    String.format("R$ %.2f", valor)
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erro ao carregar movimentações:\n" + e.getMessage(), 
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

    /**
     * MÉTODO NOVO: Calcula o saldo atual baseado no banco
     */
    private void calcularSaldo() {
        if (conexao == null) return;
        
        String sql = 
            "SELECT " +
            "  (SELECT COALESCE(SUM(valor_total), 0) FROM pedidos) + " +
            "  (SELECT COALESCE(SUM(valor), 0) FROM movimentacoes_caixa WHERE tipo_movimentacao = 'Entrada') - " +
            "  (SELECT COALESCE(SUM(valor), 0) FROM movimentacoes_caixa WHERE tipo_movimentacao = 'Saída') " +
            "AS saldo_total";
        
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            
            if (rs.next()) {
                saldoAtual = rs.getDouble("saldo_total");
                lblSaldo.setText(String.format("R$ %.2f", saldoAtual));
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao calcular saldo: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
            } catch (SQLException ex) {
                System.err.println("Erro ao fechar recursos: " + ex.getMessage());
            }
        }
    }

    /**
     * MÉTODO CORRIGIDO: Registra movimentação NO BANCO DE DADOS
     */
    private void registrar() {
        // 1. Validações
        String valorTxt = txtValor.getText().trim().replace(",", ".");
        if (valorTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Informe o valor!", 
                "Atenção", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (conexao == null) {
            JOptionPane.showMessageDialog(this, 
                "Sem conexão com o banco!", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double valor = Double.parseDouble(valorTxt);
            String tipo = (String) comboTipo.getSelectedItem();
            String dataStr = txtData.getText().trim();
            
            // Converte data para formato SQL (yyyy-MM-dd)
            SimpleDateFormat formatoBR = new SimpleDateFormat("dd/MM/yyyy");
            Date dataObj = formatoBR.parse(dataStr);
            java.sql.Date dataSql = new java.sql.Date(dataObj.getTime());
            
            // 2. SALVA NO BANCO
            String sql = "INSERT INTO movimentacoes_caixa (tipo_movimentacao, valor, data_movimentacao) VALUES (?, ?, ?)";
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tipo);
            pst.setDouble(2, valor);
            pst.setDate(3, dataSql);
            
            int linhasAfetadas = pst.executeUpdate();
            
            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Movimentação registrada com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // 3. ATUALIZA A INTERFACE
                listarMovimentacoes();
                calcularSaldo();
                
                // Limpa os campos
                txtValor.setText("");
                txtData.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                comboTipo.setSelectedIndex(0);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Valor inválido! Use apenas números.", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        } catch (java.text.ParseException ex) {
            JOptionPane.showMessageDialog(this, 
                "Data inválida! Use o formato DD/MM/AAAA", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao registrar movimentação:\n" + ex.getMessage(), 
                "Erro SQL", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            ModuloConexao.fecharStatement(pst);
        }
    }
}