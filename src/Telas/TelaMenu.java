package Telas;

import javax.swing.*;
import java.awt.*;

public class TelaMenu extends JFrame {

    public TelaMenu() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(new Color(217, 217, 217));

        JPanel card = new JPanel();
        card.setBackground(new Color(123, 17, 19));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(27, 67, 50), 3),
            BorderFactory.createEmptyBorder(0, 30, 30, 30)
        ));
        card.setPreferredSize(new Dimension(340, 340));

        // Header
        JLabel header = new JLabel("Pizzaria do Sr. Manoel", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 18));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(new Color(27, 67, 50));
        header.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        card.add(header);

        // Título Menu
        JLabel lblMenu = new JLabel("Menu", SwingConstants.CENTER);
        lblMenu.setFont(new Font("Serif", Font.ITALIC, 30));
        lblMenu.setForeground(Color.WHITE);
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMenu.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0));
        card.add(lblMenu);

        // Botão Pedidos
        JButton btnPedidos = criarBotao("Pedidos");
        btnPedidos.addActionListener(e -> {
            TelaPedidos tela = new TelaPedidos();
            tela.setVisible(true);
            this.dispose();
        });
        card.add(btnPedidos);
        card.add(Box.createVerticalStrut(12));

        // Botão Cadastro Cliente
        JButton btnCadastro = criarBotao("Cadastro Cliente");
        btnCadastro.addActionListener(e -> {
            TelaCadastroCliente tela = new TelaCadastroCliente();
            tela.setVisible(true);
            this.dispose();
        });
        card.add(btnCadastro);
        card.add(Box.createVerticalStrut(12));

        // Botão Caixa
        JButton btnCaixa = criarBotao("Caixa");
        btnCaixa.addActionListener(e -> {
            TelaCaixa tela = new TelaCaixa();
            tela.setVisible(true);
            this.dispose();
        });
        card.add(btnCaixa);

        fundo.add(card);
        setContentPane(fundo);
    }

    private JButton criarBotao(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(new Color(245, 240, 232));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}