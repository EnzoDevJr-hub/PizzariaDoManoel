package Telas;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import DAO.ModuloConexao;

public class TelaCadastroCliente extends JFrame {

    Connection conexao = null;
    
    private JTextField txtNome, txtContato, txtCPF, txtCEP;
    private JTextField txtEndereco, txtNumero, txtComplemento;
    private JTextField txtBairro, txtCidade, txtEstado;

    public TelaCadastroCliente() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    private void initComponents() {
        setTitle("Cadastro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 620);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(new Color(217, 217, 217));

        JPanel card = new JPanel();
        card.setBackground(new Color(123, 17, 19));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(27, 67, 50), 3),
            BorderFactory.createEmptyBorder(0, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(380, 570));

        // Header
        JLabel header = new JLabel("Cadastro Cliente", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 18));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(new Color(27, 67, 50));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        card.add(header);
        card.add(Box.createVerticalStrut(10));

        // Campos
        txtNome        = addCampo(card, "Nome completo");
        txtContato     = addCampo(card, "Contato (telefone/email)");
        txtCPF         = addCampo(card, "CPF (ex: 12345678-90)");
        txtCEP         = addCampo(card, "CEP (ex: 12345678)");
        txtEndereco    = addCampo(card, "Endereço");
        txtNumero      = addCampo(card, "Número");
        txtComplemento = addCampo(card, "Complemento (opcional)");
        txtBairro      = addCampo(card, "Bairro");
        txtCidade      = addCampo(card, "Cidade");
        txtEstado      = addCampo(card, "Estado");

        card.add(Box.createVerticalStrut(12));

        // Botões Limpar / Cadastrar
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        painelBotoes.setBackground(new Color(123, 17, 19));
        painelBotoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.setBackground(new Color(150, 150, 150));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLimpar.setFocusPainted(false);
        btnLimpar.addActionListener(e -> limparCampos());

        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBackground(new Color(245, 240, 232));
        btnCadastrar.setForeground(Color.BLACK);
        btnCadastrar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCadastrar.setFocusPainted(false);
        btnCadastrar.addActionListener(e -> cadastrar());

        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnCadastrar);
        card.add(painelBotoes);
        card.add(Box.createVerticalStrut(10));

        // Botão Voltar
        JButton btnVoltar = new JButton("Voltar ao Menu");
        btnVoltar.setBackground(new Color(27, 67, 50));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnVoltar.setFocusPainted(false);
        btnVoltar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVoltar.setMaximumSize(new Dimension(200, 35));
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> {
            TelaMenu menu = new TelaMenu();
            menu.setVisible(true);
            this.dispose();
        });
        card.add(btnVoltar);

        fundo.add(card);
        setContentPane(fundo);
    }

    private JTextField addCampo(JPanel pai, String placeholder) {
        JTextField campo = new JTextField(placeholder);
        campo.setForeground(Color.GRAY);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        campo.setBackground(new Color(245, 240, 232));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        campo.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        
        // Limpa placeholder ao clicar
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setText(placeholder);
                    campo.setForeground(Color.GRAY);
                }
            }
        });
        pai.add(campo);
        pai.add(Box.createVerticalStrut(5));
        return campo;
    }

    private void limparCampos() {
        for (JTextField f : new JTextField[]{txtNome, txtContato, txtCPF, txtCEP,
                txtEndereco, txtNumero, txtComplemento, txtBairro, txtCidade, txtEstado}) {
            f.setText("");
            f.dispatchEvent(new java.awt.event.FocusEvent(f, java.awt.event.FocusEvent.FOCUS_LOST));
        }
    }

    private void cadastrar() {
        // 1. Validação básica
        if (txtNome.getText().isEmpty() || txtNome.getForeground() == Color.GRAY) {
            JOptionPane.showMessageDialog(this, 
                "Preencha o nome do cliente!", 
                "Atenção", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 2. Verifica conexão
        if (conexao == null) {
            JOptionPane.showMessageDialog(this, 
                "Sem conexão com o banco de dados!", 
                "Erro de Conexão", 
                JOptionPane.ERROR_MESSAGE);
            conexao = ModuloConexao.conector();
            if (conexao == null) return;
        }

        // 3. SQL CORRIGIDO com nomes de colunas corretos
        String sql = "INSERT INTO clientes (nome_cliente, contato, cpf, cep, endereco, numero, complemento, bairro, cidade, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pst = null;
        
        try {
            pst = conexao.prepareStatement(sql);
            
            // Valores dos campos (permite placeholder se não preenchido)
            pst.setString(1, txtNome.getForeground() == Color.BLACK ? txtNome.getText() : "");
            pst.setString(2, txtContato.getForeground() == Color.BLACK ? txtContato.getText() : "");
            pst.setString(3, txtCPF.getForeground() == Color.BLACK ? txtCPF.getText() : "");
            pst.setString(4, txtCEP.getForeground() == Color.BLACK ? txtCEP.getText() : "");
            pst.setString(5, txtEndereco.getForeground() == Color.BLACK ? txtEndereco.getText() : "");
            pst.setString(6, txtNumero.getForeground() == Color.BLACK ? txtNumero.getText() : "");
            pst.setString(7, txtComplemento.getForeground() == Color.BLACK ? txtComplemento.getText() : "");
            pst.setString(8, txtBairro.getForeground() == Color.BLACK ? txtBairro.getText() : "");
            pst.setString(9, txtCidade.getForeground() == Color.BLACK ? txtCidade.getText() : "");
            pst.setString(10, txtEstado.getForeground() == Color.BLACK ? txtEstado.getText() : "");

            int linhasAfetadas = pst.executeUpdate();
            
            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Cliente " + txtNome.getText() + " cadastrado com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                limparCampos();
            }

        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this, 
                    "Erro: Este CPF já está cadastrado!", 
                    "Erro de Duplicidade", 
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao cadastrar cliente:\n" + ex.getMessage(), 
                    "Erro SQL", 
                    JOptionPane.ERROR_MESSAGE);
                System.err.println("Erro SQL: " + ex.getMessage());
            }
        } finally {
            ModuloConexao.fecharStatement(pst);
        }
    }
}