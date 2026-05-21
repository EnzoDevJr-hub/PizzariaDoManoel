package Telas;

import javax.swing.*;
import java.awt.*;
import DAO.ModuloConexao;
import java.sql.*;

public class TelaLogin extends JFrame {
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnLimpar;

    public TelaLogin() {
        initComponents();
        // Tenta conectar ao banco
        conexao = ModuloConexao.conector();
        
        // IMPORTANTE: Verifica se a conexão foi estabelecida
        if (conexao == null) {
            JOptionPane.showMessageDialog(this, 
                "⚠ Não foi possível conectar ao banco de dados!\n\n" +
                "Verifique:\n" +
                "1. MySQL está rodando?\n" +
                "2. Banco 'pizzaria_db' existe?\n" +
                "3. Usuário e senha estão corretos?", 
                "Erro de Conexão", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void initComponents() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        // Fundo cinza
        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(new Color(217, 217, 217));

        // Card vermelho
        JPanel card = new JPanel();
        card.setBackground(new Color(123, 17, 19));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(27, 67, 50), 3),
            BorderFactory.createEmptyBorder(0, 30, 25, 30)
        ));
        card.setPreferredSize(new Dimension(340, 340));

        // Header verde
        JLabel header = new JLabel("Pizzaria do Sr. Manoel", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 18));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(new Color(27, 67, 50));
        header.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        card.add(header);

        // Título Login
        JLabel lblTitulo = new JLabel("Login", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.ITALIC, 26));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        card.add(lblTitulo);

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(Color.WHITE);
        sep.setMaximumSize(new Dimension(200, 2));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sep);
        card.add(Box.createVerticalStrut(15));

        // Label Usuário
        JLabel lblUser = new JLabel("Usuário ou Email");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblUser);
        card.add(Box.createVerticalStrut(5));

        // Campo Usuário
        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtUsuario.setBackground(new Color(245, 240, 232));
        txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        txtUsuario.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        card.add(txtUsuario);
        card.add(Box.createVerticalStrut(12));

        // Label Senha
        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setForeground(Color.WHITE);
        lblSenha.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSenha.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblSenha);
        card.add(Box.createVerticalStrut(5));

        // Campo Senha
        txtSenha = new JPasswordField();
        txtSenha.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtSenha.setBackground(new Color(245, 240, 232));
        txtSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        txtSenha.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        card.add(txtSenha);
        card.add(Box.createVerticalStrut(20));

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        painelBotoes.setBackground(new Color(123, 17, 19));
        painelBotoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnLimpar = new JButton("LIMPAR");
        btnLimpar.setBackground(new Color(245, 240, 232));
        btnLimpar.setForeground(Color.BLACK);
        btnLimpar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLimpar.setFocusPainted(false);
        btnLimpar.addActionListener(e -> {
            txtUsuario.setText("");
            txtSenha.setText("");
        });

        btnEntrar = new JButton("ENTRAR");
        btnEntrar.setBackground(new Color(245, 240, 232));
        btnEntrar.setForeground(Color.BLACK);
        btnEntrar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnEntrar.setFocusPainted(false);
        btnEntrar.addActionListener(e -> realizarLogin());

        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnEntrar);
        card.add(painelBotoes);

        fundo.add(card);
        setContentPane(fundo);
    }
    
    /**
     * Método para realizar o login
     */
    private void realizarLogin() {
        // 1. Valida campos vazios
        String usuarioStr = txtUsuario.getText().trim();
        String senhaStr = new String(txtSenha.getPassword()).trim();

        if (usuarioStr.isEmpty() || senhaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Preencha usuário e senha!", 
                "Atenção", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. VERIFICA SE A CONEXÃO ESTÁ NULA (ERRO PRINCIPAL)
        if (conexao == null) {
            JOptionPane.showMessageDialog(this, 
                "Sem conexão com o banco de dados!\n" +
                "Verifique se o MySQL está rodando e tente novamente.", 
                "Erro de Conexão", 
                JOptionPane.ERROR_MESSAGE);
            
            // Tenta reconectar
            conexao = ModuloConexao.conector();
            if (conexao == null) {
                return; // Se ainda falhar, cancela o login
            }
        }

        // 3. Lógica de autenticação no banco
        String sql = "SELECT * FROM usuarios WHERE (usuario=? OR email=?) AND senha=?";
        
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, usuarioStr);
            pst.setString(2, usuarioStr);
            pst.setString(3, senhaStr);

            rs = pst.executeQuery();

            if (rs.next()) {
                // Login bem-sucedido
                JOptionPane.showMessageDialog(this, 
                    "Bem-vindo, " + rs.getString("usuario") + "!", 
                    "Login Efetuado", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                TelaMenu menu = new TelaMenu();
                menu.setVisible(true);
                this.dispose();
            } else {
                // Credenciais inválidas
                JOptionPane.showMessageDialog(this, 
                    "Usuário e/ou senha inválidos!", 
                    "Erro de Login", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao consultar banco de dados:\n" + ex.getMessage(), 
                "Erro SQL", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            // Fecha recursos
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
            } catch (SQLException ex) {
                System.err.println("Erro ao fechar recursos: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}