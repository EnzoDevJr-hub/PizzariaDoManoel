package DAO;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 * Classe responsável pela conexão com o banco de dados MySQL
 */
public class ModuloConexao {
    
    // Configurações do banco de dados
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/pizzaria_db?useTimezone=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Altere se sua senha for diferente
    
    /**
     * Método responsável por estabelecer a conexão com o banco
     * @return Connection ou null se houver erro
     */
    public static Connection conector() {
        Connection conexao = null;
        
        try {
            // Carrega o driver JDBC
            Class.forName(DRIVER);
            
            // Estabelece a conexão
            conexao = DriverManager.getConnection(URL, USER, PASSWORD);
            
            // Mensagem de sucesso (apenas para debug - remova em produção)
            System.out.println("✓ Conexão estabelecida com sucesso!");
            
            return conexao;
            
        } catch (ClassNotFoundException e) {
            // Erro: Driver não encontrado
            System.err.println("✗ Erro: Driver JDBC não encontrado!");
            System.err.println("Baixe o MySQL Connector/J em: https://dev.mysql.com/downloads/connector/j/");
            JOptionPane.showMessageDialog(null, 
                "Driver JDBC não encontrado!\nBaixe o MySQL Connector/J", 
                "Erro de Driver", 
                JOptionPane.ERROR_MESSAGE);
            return null;
            
        } catch (SQLException e) {
            // Erro: Problemas na conexão
            System.err.println("✗ Erro de conexão com o banco de dados:");
            System.err.println("Mensagem: " + e.getMessage());
            System.err.println("Código: " + e.getErrorCode());
            
            // Mensagens específicas para erros comuns
            if (e.getMessage().contains("Access denied")) {
                JOptionPane.showMessageDialog(null, 
                    "Usuário ou senha incorretos!\nVerifique as credenciais do MySQL.", 
                    "Erro de Autenticação", 
                    JOptionPane.ERROR_MESSAGE);
            } else if (e.getMessage().contains("Unknown database")) {
                JOptionPane.showMessageDialog(null, 
                    "Banco de dados 'pizzaria_db' não encontrado!\nExecute o script SQL primeiro.", 
                    "Banco Não Encontrado", 
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Erro ao conectar ao banco de dados!\nVerifique se o MySQL está rodando.\n\n" + e.getMessage(), 
                    "Erro de Conexão", 
                    JOptionPane.ERROR_MESSAGE);
            }
            return null;
        }
    }
    
    /**
     * Testa se a conexão está funcionando
     * @return true se conectou com sucesso
     */
    public static boolean testarConexao() {
        Connection conn = conector();
        if (conn != null) {
            try {
                conn.close();
                return true;
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão de teste: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    
    /**
     * Fecha uma conexão aberta
     * @param conn conexão a ser fechada
     */
    public static void fecharConexao(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("✓ Conexão fechada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
    
    /**
     * Fecha um PreparedStatement
     * @param pst statement a ser fechado
     */
    public static void fecharStatement(PreparedStatement pst) {
        try {
            if (pst != null && !pst.isClosed()) {
                pst.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar statement: " + e.getMessage());
        }
    }
    
    /**
     * Fecha um ResultSet
     * @param rs result set a ser fechado
     */
    public static void fecharResultSet(ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar result set: " + e.getMessage());
        }
    }
}