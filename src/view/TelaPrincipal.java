
package view;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import static java.awt.SystemColor.scrollbar;

public class TelaPrincipal extends JPanel {
    JPanel conteudoInterno;

    public TelaPrincipal() {


        setLayout(new BorderLayout());
        setBackground(Color.WHITE);


        JPanel header = criarHeader();
        add(header, BorderLayout.NORTH);


        conteudoInterno = new JPanel();
        // Mudamos para GridBagLayout aqui para termos controle total das linhas

        conteudoInterno.setLayout(new GridBagLayout());
        conteudoInterno.setBackground(Color.WHITE);
        conteudoInterno.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Força os componentes a nascerem no Topo-Esquerda

        // Linha 0: Seção de Categorias
        gbc.gridy = 0;
        gbc.weighty = 0.0; // Zera o peso para não empurrar para baixo
        gbc.insets = new Insets(0, 0, 25, 0); // Margem de 25px abaixo das categorias
        conteudoInterno.add(criarSecaoCategorias(), gbc);

        // Linha 1: Lista de Restaurantes
        gbc.gridy = 1;
        gbc.weighty = 0.0; // Mantém em zero
        gbc.insets = new Insets(0, 0, 0, 0);
        conteudoInterno.add(criarListaRestaurantes(), gbc);

        // -------------------------------------------------------------
        // O SEGREDO: Linha 2 é a "Mola" que puxa tudo o que está acima para o topo
        // -------------------------------------------------------------
        gbc.gridy = 2;
        gbc.weighty = 1.0; // Sugará todo o espaço em branco vertical do fundo da tela
        JPanel molaInvisivel = new JPanel();
        molaInvisivel.setOpaque(false);
        conteudoInterno.add(molaInvisivel, gbc);

        // Criando o scroll principal do Feed
        JScrollPane scroll = new JScrollPane(conteudoInterno);


        scroll.setBorder(null);


        scroll.getVerticalScrollBar().setUnitIncrement(30);
        scroll.getHorizontalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        add(scroll, BorderLayout.CENTER);
        BotaoArredondado btn = new BotaoArredondado("Trocar de tela",30,Color.GRAY , 50);
        btn.addActionListener(e -> {
            TelaLogin ln = new TelaLogin();
            Telabase sist = (Telabase) SwingUtilities.getWindowAncestor(this);
            if(sist != null){
                sist.configuraTela(ln);
            }
        });
        conteudoInterno.add(btn,gbc);
    }

    // Método para criar botões com o mesmo padrão visual
    /*
    private void adicionarBotao(String texto, int y, java.awt.event.ActionListener acao) {
        BotaoArredondado() btn = new BotaoArredondado(texto);
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

        this.add(btn);
    }

     */

    private JPanel criarHeader() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(800, 80));

        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 20);

        // Logo (Texto vermelho marcante)
        JLabel logo = new JLabel("AIFood");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(new Color(234, 16, 34)); // Vermelho iFood
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        p.add(logo, gbc);


        CampoTextoArredondado busca = new CampoTextoArredondado(20, 15, new Color(240, 240, 240),30);
        busca.setText(" Busque por pratos ou restaurantes...");
        busca.setForeground(Color.GRAY);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(busca, gbc);

        return p;
    }

    private JPanel criarSecaoCategorias() {
        JPanel painelSecao = new JPanel();
        painelSecao.setLayout(new BoxLayout(painelSecao, BoxLayout.Y_AXIS));
        painelSecao.setBackground(Color.WHITE);

        // O TÍTULO
        JLabel titulo = new JLabel("Categorias");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelSecao.add(titulo);

        painelSecao.add(Box.createVerticalStrut(15));

        // O CONTEÚDO HORIZONTAL
        JPanel listaHorizontal = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        listaHorizontal.setBackground(Color.WHITE);
        listaHorizontal.setAlignmentX(Component.LEFT_ALIGNMENT);

        listaHorizontal.add(new BotaoArredondado("Mercado", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Restaurantes", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Bebidas", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Farmácia", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Pet Shop", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Mercado", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Restaurantes", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Bebidas", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Farmácia", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Pet Shop", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Mercado", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Restaurantes", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Bebidas", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Farmácia", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(new BotaoArredondado("Pet Shop", 20, Color.decode("#e96769"), 20));
        // O SCROLL HORIZONTAL
        JScrollPane scrollHorizontal = new JScrollPane(listaHorizontal);
        scrollHorizontal.setBorder(null);
        scrollHorizontal.setOpaque(true);
        scrollHorizontal.getViewport().setOpaque(false);

        scrollHorizontal.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollHorizontal.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        scrollHorizontal.getHorizontalScrollBar().setUnitIncrement(16);
        scrollHorizontal.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --------------------------------------------------------------------------
        // ADICIONADO: Proteção contra o conflito de Scroll também nas Categorias!
        // --------------------------------------------------------------------------
        scrollHorizontal.addMouseWheelListener(e -> {
            JScrollBar bar = scrollHorizontal.getHorizontalScrollBar();
            int cliques = e.getWheelRotation();
            // Controla o movimento horizontal baseado na rodinha do mouse
            int novaPosicao = bar.getValue() + (cliques * bar.getUnitIncrement() * 2);
            bar.setValue(novaPosicao);

            // Alerta o Java que o scroll vertical pai não deve interferir aqui
            e.consume();
        });
        // --------------------------------------------------------------------------

        painelSecao.add(scrollHorizontal);
        return painelSecao;
    }

    private JPanel criarListaRestaurantes() {
// 1. O painel principal da seção (GridBagLayout para empilhar o Título e o Carrossel)
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;

        // LINHA 0: O Título da Seção (Fica em cima)
        JLabel titulo = new JLabel("Lojas Disponíveis");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0); // Espaço de 15px abaixo do título
        panel.add(titulo, gbc);

        // LINHA 1: O contêiner que vai alinhar os cards lado a lado (X_AXIS)
        JPanel listaHorizontal = new JPanel();
        listaHorizontal.setLayout(new BoxLayout(listaHorizontal, BoxLayout.X_AXIS));
        listaHorizontal.setBackground(Color.WHITE);

        // Adicionando os Cards na horizontal
        listaHorizontal.add(new CardRestaurante("Burguer King", "4.7", "15-25 min", "R$ 4,99"));
        listaHorizontal.add(Box.createHorizontalStrut(15)); // Espaço horizontal de 15px entre os cards

        listaHorizontal.add(new CardRestaurante("Pizza Hut", "4.5", "30-40 min", "Grátis"));
        listaHorizontal.add(Box.createHorizontalStrut(15));

        listaHorizontal.add(new CardRestaurante("Subway - Centro", "4.3", "20-30 min", "R$ 2,00"));
        listaHorizontal.add(Box.createHorizontalStrut(15));

        listaHorizontal.add(new CardRestaurante("Jonas", "4.3", "20-30 min", "R$ 2,00"));
        listaHorizontal.add(Box.createHorizontalStrut(15));

        listaHorizontal.add(new CardRestaurante("Pizza Hut", "4.5", "30-40 min", "Grátis"));
        listaHorizontal.add(Box.createHorizontalStrut(15));

        listaHorizontal.add(new CardRestaurante("ASASASAS - Centro", "4.3", "20-30 min", "R$ 2,00"));
        listaHorizontal.add(Box.createHorizontalStrut(15));
        listaHorizontal.add(new CardRestaurante("PASASASt", "4.5", "30-40 min", "Grátis"));
        listaHorizontal.add(Box.createHorizontalStrut(15));

        listaHorizontal.add(new CardRestaurante("12o", "4.3", "20-30 min", "R$ 2,00"));
        listaHorizontal.add(Box.createHorizontalStrut(15));
        listaHorizontal.add(new CardRestaurante("AAAAAAAAAAA", "4.5", "30-40 min", "Grátis"));
        listaHorizontal.add(Box.createHorizontalStrut(15));

        listaHorizontal.add(new CardRestaurante("21", "4.3", "20-30 min", "R$ 2,00"));
        listaHorizontal.add(Box.createHorizontalStrut(15));
        listaHorizontal.setPreferredSize(new Dimension(2500, 100));

        listaHorizontal.setOpaque(true);
        listaHorizontal.setBackground(Color.WHITE);

        JScrollPane scrollHorizontal = new JScrollPane(listaHorizontal,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollHorizontal.setBorder(null);
        scrollHorizontal.setOpaque(true);
        scrollHorizontal.setBackground(Color.BLACK);
        scrollHorizontal.getViewport().setOpaque(false);

        // TRAVA 3: Define uma altura limite para o Scroll na tela do app
        scrollHorizontal.setPreferredSize(new Dimension(800, 120));
        scrollHorizontal.setMinimumSize(new Dimension(100, 120));
        scrollHorizontal.getHorizontalScrollBar().setUnitIncrement(40);

        /*
        BotaoArredondado ir_dir = new BotaoArredondado(">",100,Color.black,50);
        BotaoArredondado ir_esq = new BotaoArredondado("<",100,Color.GRAY,50);

        ir_dir.addActionListener(e->{
            JScrollBar bar = scrollHorizontal.getHorizontalScrollBar();
            int novaPosicao = bar.getValue() + (bar.getUnitIncrement() * 10);
            bar.setValue(novaPosicao);
        });

        ir_esq.addActionListener(e->{
            JScrollBar bar = scrollHorizontal.getHorizontalScrollBar();
            int novaPosicao = bar.getValue() - (bar.getUnitIncrement() * 10);
            bar.setValue(novaPosicao);
        });


         */
        scrollHorizontal.addMouseWheelListener(e -> {
            JScrollBar bar = scrollHorizontal.getHorizontalScrollBar();
            int cliques = e.getWheelRotation();
            int novaPosicao = bar.getValue() + (cliques * bar.getUnitIncrement() * 2);
            bar.setValue(novaPosicao);
            e.consume();
        });

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(scrollHorizontal, gbc);
        /*
        gbc.insets = new Insets(0, 0, 40, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 2;
        panel.add(ir_esq,gbc);
        //gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(ir_dir,gbc);

         */
        return panel;
    }

}

