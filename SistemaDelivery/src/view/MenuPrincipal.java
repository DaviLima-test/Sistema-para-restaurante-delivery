package view;

import java.awt.*;
import java.io.File;
import javax.swing.*;

public class MenuPrincipal extends JFrame {
    private JPanel contentPane;

    public MenuPrincipal() {
        setTitle("Sistema de Gestão de Delivery");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 600); 
        setLocationRelativeTo(null); 
        
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // Painel Lateral para Banner
        JPanel painelEsquerda = new JPanel();
        painelEsquerda.setBounds(0, 0, 380, 600); 
        painelEsquerda.setLayout(null);
        painelEsquerda.setBackground(new Color(234, 29, 44)); 
        contentPane.add(painelEsquerda);

        // Tenta carregar a imagem do banner
        try {
            File arqBanner = new File("img/banner_delivery.jpg"); 
            if (arqBanner.exists()) {
                ImageIcon iconOriginal = new ImageIcon(arqBanner.getAbsolutePath());
                Image imgRedim = iconOriginal.getImage().getScaledInstance(380, 600, Image.SCALE_SMOOTH);
                JLabel lblImagemLateral = new JLabel(new ImageIcon(imgRedim));
                lblImagemLateral.setBounds(0, 0, 380, 600);
                painelEsquerda.add(lblImagemLateral);
            }
        } catch (Exception e) {
            System.out.println("Banner não carregado.");
        }

        // Títulos da Área de Operação
        JLabel lblTitulo = new JLabel("GERENCIADOR DE PEDIDOS");
        lblTitulo.setForeground(new Color(30, 30, 30)); 
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(420, 50, 380, 40);
        contentPane.add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("Controle Administrativo");
        lblSubtitulo.setForeground(new Color(100, 100, 100));
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitulo.setBounds(420, 90, 380, 20);
        contentPane.add(lblSubtitulo);

        // Botões do Sistema
        adicionarBotao("Cadastrar Produto", 160, e -> new TelaCadastroProduto().setVisible(true));
        
        adicionarBotao("Visualizar Cardápio", 230, e -> {
            TabelaDeProdutos tela = new TabelaDeProdutos();
            tela.atualizarTabela(""); 
            tela.setVisible(true);
        });

        adicionarBotao("Lançar Novo Pedido", 300, e -> new TelaPedido().setVisible(true));
        
        adicionarBotao("Relatórios Gerenciais", 370, e -> {
            TabelaDePedidos tela = new TabelaDePedidos();
            tela.atualizarTabela();
            tela.setVisible(true);
        });

        adicionarBotao("Logística de Entrega", 440, e -> new TelaEntregador().setVisible(true));

        // Rodapé Informativo
        JLabel lblInfo = new JLabel("Sistema Desenvolvido para Gestão Interna");
        lblInfo.setForeground(new Color(150, 150, 150));
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblInfo.setBounds(420, 530, 380, 20);
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblInfo);
    }

    // Método para criar botões com o mesmo padrão visual
    private void adicionarBotao(String texto, int y, java.awt.event.ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(234, 29, 44)); 
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder()); 
        btn.setBounds(460, y, 300, 50); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(acao);
        
        // Efeito visual ao passar o mouse
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { 
                btn.setBackground(new Color(200, 20, 30)); 
            }
            public void mouseExited(java.awt.event.MouseEvent evt) { 
                btn.setBackground(new Color(234, 29, 44)); 
            }
        });
        
        contentPane.add(btn);
    }    

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MenuPrincipal frame = new MenuPrincipal();
                frame.setVisible(true);
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
        });
    }
}