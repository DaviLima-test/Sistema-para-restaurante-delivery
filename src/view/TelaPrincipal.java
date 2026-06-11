
package view;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import model.*;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import static java.awt.SystemColor.scrollbar;

public class TelaPrincipal extends JPanel {
    private boolean menu_aberto = false;
    private JLayeredPane camadas;
    private JPanel conteudoApp;
    private JPanel conteudoInterno;
    private JPanel barra_lateral;
    private JPanel header;
    private Overlay overlay;
    private Telabase sist;
    public TelaPrincipal() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. Inicializa o Painel de Camadas principal
        camadas = new JLayeredPane();
        add(camadas, BorderLayout.CENTER);

        // 2. Inicializa o Container Principal do App (Camada de Fundo)
        conteudoApp = new JPanel();
        conteudoApp.setBackground(Color.WHITE);
        conteudoApp.setLayout(new BorderLayout());

        // 3. Inicializa a Barra Lateral (Camada de Frente)
        barra_lateral = new JPanel();
        barra_lateral.setLayout(new BoxLayout(barra_lateral, BoxLayout.Y_AXIS));
        barra_lateral.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        barra_lateral.setBackground(Color.WHITE);
        barra_lateral.setVisible(false); // Começa escondida até clicar no hambúrguer

        // --- ADICIONANDO ITENS AO MENU ---
        adicionarItemMenu("Perfil", e -> {
            TelaPerfil tp = new TelaPerfil();
            sist = (Telabase) SwingUtilities.getWindowAncestor(this);
            if (sist != null) {
                sist.configuraTela(tp);
        }
        });
        adicionarItemMenu("Carrinho", e -> {

        });
        adicionarItemMenu("Pedidos", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ação dos Pedidos
            }
        });
        adicionarItemMenu("Carteira", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ação da Carteira
            }
        });

        // Verificações seguras contra NullPointerException
        if ("restaurante".equals(Login.GetTipo())) {
            adicionarItemMenu("Gerenciar restaurante", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Ação do Restaurante
                }
            });
        }
        if ("entregador".equals(Login.GetTipo())) {
            adicionarItemMenu("Pedidos a serem entregues", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Ação do Entregador
                }
            });
        }

        adicionarItemMenu("Logout(APAGA O COOKIE DE SESSAO)", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] opcoes = {"Sim", "Não"};
                int resposta = JOptionPane.showOptionDialog(
                        SwingUtilities.getWindowAncestor(barra_lateral), // Pai real da janela
                        "Você realmente deseja apagar os cookies?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, opcoes, opcoes[0]
                );

                if (resposta == JOptionPane.YES_OPTION) {
                    Login.apagarCookie();
                    System.exit(0);
                }
            }
        });

        adicionarItemMenu("Sair", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] opcoes = {"Sim", "Não"};
                int resposta = JOptionPane.showOptionDialog(
                        SwingUtilities.getWindowAncestor(barra_lateral), // Pai real da janela
                        "Você realmente deseja sair?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, opcoes, opcoes[0]
                );

                if (resposta == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // 4. Inicializa o Overlay (Camada do Meio)
        overlay = new Overlay();
        overlay.setVisible(false);
        overlay.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Fecha o menu se clicar no espaço cinza
                menu_aberto = false;
                overlay.setVisible(false);
                barra_lateral.setVisible(false);
                camadas.repaint();
            }
        });

        // 5. Montagem do Cabeçalho e Conteúdo Interno do Feed
        header = criarHeader();
        conteudoApp.add(header, BorderLayout.NORTH);

        conteudoInterno = new JPanel();
        conteudoInterno.setLayout(new GridBagLayout());
        conteudoInterno.setBackground(Color.WHITE);
        conteudoInterno.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Linha 0: Categorias
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 25, 0);
        conteudoInterno.add(criarSecaoCategorias(), gbc);

        // Linha 1: Lista de Restaurantes
        gbc.gridy = 1;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 25, 0);
        conteudoInterno.add(criarListaRestaurantes(), gbc);

        // Linha 2: Mola Invisível para empurrar itens para cima
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        JPanel molaInvisivel = new JPanel();
        molaInvisivel.setOpaque(false);
        conteudoInterno.add(molaInvisivel, gbc);

        // Configurando o Scroll do Feed
        JScrollPane scroll = new JScrollPane(conteudoInterno);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(30);
        scroll.getHorizontalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        conteudoApp.add(scroll, BorderLayout.CENTER);

        // 6. Painel inferior com o botão de "Trocar de Tela"
        JPanel panel = new JPanel(new GridLayout(1, 1));
        BotaoArredondado btn = new BotaoArredondado("Trocar de tela", 30, Color.GRAY, 50);
        btn.addActionListener(e -> {
            TelaLogin ln = new TelaLogin();
            sist = (Telabase) SwingUtilities.getWindowAncestor(this);
            if (sist != null) {
                sist.configuraTela(ln);
            }
        });
        panel.add(btn);
        conteudoApp.add(panel, BorderLayout.SOUTH);

        // 7. ADICIONANDO AS TRÊS CAMADAS NO JLAYEREDPANE
        camadas.add(conteudoApp, JLayeredPane.DEFAULT_LAYER);  // Camada 0 (Fundo)
        camadas.add(overlay, JLayeredPane.PALETTE_LAYER);      // Camada 100 (Meio)
        camadas.add(barra_lateral, JLayeredPane.MODAL_LAYER);  // Camada 200 (Frente)

        // 8. Ouvinte para ajustar dinamicamente o tamanho das camadas do LayeredPane
        camadas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int largura = camadas.getWidth();
                int altura = camadas.getHeight();

                // CORREÇÃO AQUI: conteúdo completo do app e o overlay preenchem a tela inteira
                conteudoApp.setBounds(0, 0, largura, altura);
                overlay.setBounds(0, 0, largura, altura);

                // A barra lateral ocupa a altura inteira com tamanho fixo
                barra_lateral.setBounds(0, 0, 250, altura);
            }
        });
        int larguraInicial = Telabase.Width ;
        int alturaInicial = Telabase.Height; // Altura padrão aproximada caso não encontre
        conteudoApp.setBounds(0, 0, larguraInicial, alturaInicial);
        overlay.setBounds(0, 0, larguraInicial, alturaInicial);
        barra_lateral.setBounds(0, 0, 250, alturaInicial);
        // Ouvinte para ajustar a responsividade dos itens internos do feed
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                ajustarLarguraDoFeed();
            }
        });
    }
    private void ajustarLarguraDoFeed() {
        // 1. Pega a largura total atual da janela
        int larguraDisponivel = this.getWidth();

        // 2. Se o menu hambúrguer estiver aberto, precisamos descontar os 250px dele
        if (menu_aberto) {
            larguraDisponivel -= 250;
        }

        // 3. Deixa uma pequena folga para a barra de rolagem vertical (ex: 20px) para não dar bug
        larguraDisponivel -= 20;

        // 4. Força o painel interno a ter exatamente essa largura, mantendo a altura que ele já tinha
        int alturaAtual = conteudoInterno.getPreferredSize().height;
        conteudoInterno.setPreferredSize(new Dimension(larguraDisponivel, alturaAtual));

        // 5. Atualiza o layout
        conteudoInterno.revalidate();
    }
    private JPanel criarHeader() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(800, 80));

        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 20);
        ImageIcon inc_hambuger = new ImageIcon("img/hambuger_icon.png");
        Image novaImg = inc_hambuger.getImage().getScaledInstance(
                50,
                50,
                Image.SCALE_SMOOTH
        );

        JButton bnt_hambuger = new JButton(new ImageIcon(novaImg));
        bnt_hambuger.setPreferredSize(new Dimension(50,50));
        bnt_hambuger.setMaximumSize(new Dimension(50,50));
        gbc.gridx = 0;
        gbc.weightx = 0.0;


        p.add(bnt_hambuger,gbc);
        Texto logo = new Texto("AIFood");
        logo.setFont(new Font("Arial", Font.BOLD, 30));
        logo.setForeground(new Color(234, 16, 34)); // Vermelho iFood
        logo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {


            }
        });
        gbc.gridx = 1;
        p.add(logo, gbc);


        CampoTextoArredondado busca = new CampoTextoArredondado(18, 15, new Color(240, 240, 240),30);
        busca.setText(" Buscar Restaurantes e pratos ...");
        busca.setForeground(Color.GRAY);
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        busca.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(busca.getText().equals(" Buscar Restaurantes e pratos ...")){
                    busca.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(busca.getText().isEmpty()){
                    busca.setText(" Buscar Restaurantes e pratos ...");
                }
            }
        });
        bnt_hambuger.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            menu_aberto = !menu_aberto;

            if(menu_aberto){
                overlay.setVisible(true);
                barra_lateral.setPreferredSize(new Dimension(250,0));
                barra_lateral.setVisible(true);
                //conteudoInterno.setBackground(new Color(40, 40, 40));
            }else{
                //System.out.println("Deu aqui");
                overlay.setVisible(false);
                barra_lateral.setPreferredSize(new Dimension(0,0));
                barra_lateral.setVisible(false);

                //conteudoInterno.setBackground(Color.WHITE);
            }
            overlay.setVisible(menu_aberto);
            ajustarLarguraDoFeed();
            Container paneltop = p.getTopLevelAncestor();
            if(paneltop != null) {
                paneltop.revalidate();
                paneltop.repaint();
            }
            camadas.repaint();

            }


        });
        p.add(busca, gbc);

        return p;
    }
    private void adicionarItemMenu(String texto , ActionListener e) {
        BotaoArredondado btn = new BotaoArredondado(texto,25,Color.decode("#e96769"),20);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Ocupa toda a largura
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK)); // Linha divisória
        btn.addActionListener(e);
        barra_lateral.add(btn);
        barra_lateral.add(Box.createVerticalStrut(15));
    }





    private JPanel criarSecaoCategorias() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Categorias");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);

        panel.add(Box.createVerticalStrut(15));


        JPanel listaHorizontal = new JPanel();
        listaHorizontal.setLayout(new BoxLayout(listaHorizontal,BoxLayout.X_AXIS));

        listaHorizontal.setBackground(Color.WHITE);
        listaHorizontal.setAlignmentX(Component.LEFT_ALIGNMENT);

        // AQUI AO INVÊS DDE TER ESSES DE DEBUG VAI SER OS TIPOS DOS RESTUARANTES PEGADOS NO BD
        listaHorizontal.add(new BotaoArredondado("Mercado", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(Box.createRigidArea(new Dimension(20,15)));
        listaHorizontal.add(new BotaoArredondado("Restaurantes", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(Box.createRigidArea(new Dimension(20,15)));
        listaHorizontal.add(new BotaoArredondado("Bebidas", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(Box.createRigidArea(new Dimension(20,15)));
        listaHorizontal.add(new BotaoArredondado("Farmácia", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(Box.createRigidArea(new Dimension(20,15)));
        listaHorizontal.add(new BotaoArredondado("Pet Shop", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(Box.createRigidArea(new Dimension(20,15)));
        listaHorizontal.add(new BotaoArredondado("Mercado", 20, Color.decode("#e96769"), 20));


        JScrollPane scrollHorizontal = new JScrollPane(listaHorizontal);
        scrollHorizontal.setBorder(null);
        scrollHorizontal.setOpaque(true);
        scrollHorizontal.getViewport().setOpaque(false);

        scrollHorizontal.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollHorizontal.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        scrollHorizontal.getHorizontalScrollBar().setUnitIncrement(20);
        scrollHorizontal.setAlignmentX(Component.LEFT_ALIGNMENT);

        int largura_desejada = Telabase.Width;
        int altura = 100;
        scrollHorizontal.setPreferredSize(new Dimension(largura_desejada,altura));
        scrollHorizontal.setMinimumSize(new Dimension(largura_desejada,altura));
        scrollHorizontal.setMaximumSize(new Dimension(largura_desejada,altura));

        scrollHorizontal.addMouseWheelListener(e -> {
            JScrollBar bar = scrollHorizontal.getHorizontalScrollBar();
            int cliques = e.getWheelRotation();

            int novaPosicao = bar.getValue() + (cliques * bar.getUnitIncrement() * 2);
            bar.setValue(novaPosicao);



            e.consume();
        });

        //panel.add(listaHorizontal);
        panel.add(scrollHorizontal);
        return panel;
    }

    private JPanel criarListaRestaurantes() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;


        JLabel titulo = new JLabel("Lojas Disponíveis");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 20); // Espaço de 15px abaixo do título
        panel.add(titulo, gbc);


        JPanel listaHorizontal = new JPanel();
        listaHorizontal.setLayout(new BoxLayout(listaHorizontal, BoxLayout.X_AXIS));
        listaHorizontal.setBackground(Color.WHITE);


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


        listaHorizontal.setOpaque(true);
        listaHorizontal.setBackground(Color.WHITE);

        JScrollPane scrollHorizontal = new JScrollPane(listaHorizontal,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollHorizontal.setBorder(null);
        scrollHorizontal.setOpaque(true);
        scrollHorizontal.setBackground(Color.BLACK);
        scrollHorizontal.getViewport().setOpaque(false);


        scrollHorizontal.getHorizontalScrollBar().setUnitIncrement(40);
        int largura_desejada = scrollHorizontal.getViewport().getWidth()/4;
        int altura = 100;
        scrollHorizontal.setPreferredSize(new Dimension(largura_desejada,altura));
        scrollHorizontal.setMinimumSize(new Dimension(largura_desejada,altura));
        scrollHorizontal.setMaximumSize(new Dimension(largura_desejada,altura));


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

        return panel;
    }

}

