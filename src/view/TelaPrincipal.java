
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
    private JPanel conteudoInterno;
    private JPanel barra_lateral;
    public TelaPrincipal() {


        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        barra_lateral = new JPanel();
        barra_lateral.setLayout(new BoxLayout(barra_lateral,BoxLayout.Y_AXIS));
        barra_lateral.setBackground(Color.WHITE);
        adicionarItemMenu("Perfil", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        adicionarItemMenu("Carrinho", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        adicionarItemMenu("Pedidos", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        adicionarItemMenu("Carteira", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });


        if(Login.GetTipo().equals(("restaurante"))){
            adicionarItemMenu("Gerenciar restaurante", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });
          ;
        }
        if(Login.GetTipo().equals("entregador")){
            adicionarItemMenu("Pedidos a serem entregues", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });

        }
        adicionarItemMenu("Logout(APAGA O COOKIE DE SESSAO)", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] opcoes = {"Sim", "Não"};

                int resposta = JOptionPane.showOptionDialog(
                        null,
                        "Você realmente deseja apagar os cookies?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,             // Usa o ícone padrão
                        opcoes,           // Array com os textos dos botões
                        opcoes[0]         // Botão focado por padrão (Sim)
                );

                if (resposta == JOptionPane.YES_OPTION) {
                    Login.apagarCookie();
                    System.exit(0); // Fecha o programa completamente, se preferir
                }
            }
        });
        adicionarItemMenu("Sair", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    Object[] opcoes = {"Sim", "Não"};

                    int resposta = JOptionPane.showOptionDialog(
                            null,
                            "Você realmente deseja sair?",
                            "Confirmação",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,             // Usa o ícone padrão
                            opcoes,           // Array com os textos dos botões
                            opcoes[0]         // Botão focado por padrão (Sim)
                    );

                    if (resposta == JOptionPane.YES_OPTION) {
                        // Código para sair...
                        System.exit(0); // Fecha o programa completamente, se preferir
                    }

            }
        });
        barra_lateral.setPreferredSize(new Dimension(0,0));
        add(barra_lateral,BorderLayout.WEST);

        JPanel header = criarHeader();
        add(header, BorderLayout.NORTH);


        conteudoInterno = new JPanel();
        // Mudamos para GridBagLayout aqui para termos controle total das linhas

        conteudoInterno.setLayout(new GridBagLayout());
        conteudoInterno.setBackground(Color.WHITE);
        conteudoInterno.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

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
        gbc.insets = new Insets(0, 0, 25, 0);
        conteudoInterno.add(criarListaRestaurantes(), gbc);


        gbc.gridy = 2;
        gbc.weighty = 1.0;
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

        JPanel panel = new JPanel(new GridLayout(1,1));
        add(scroll, BorderLayout.CENTER);
        BotaoArredondado btn = new BotaoArredondado("Trocar de tela",30,Color.GRAY , 50);
        btn.addActionListener(e -> {
            TelaLogin ln = new TelaLogin();
            Telabase sist = (Telabase) SwingUtilities.getWindowAncestor(this);
            if(sist != null){
                sist.configuraTela(ln);
            }
        });
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                ajustarLarguraDoFeed();
            }
        });
        panel.add(btn);
        add(panel,BorderLayout.AFTER_LAST_LINE);
        //conteudoInterno.add(panel,gbc);
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
        JLabel logo = new JLabel("AIFood");
        logo.setFont(new Font("Arial", Font.BOLD, 30));
        logo.setForeground(new Color(234, 16, 34)); // Vermelho iFood
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

                System.out.println(menu_aberto);
            if(menu_aberto){
                barra_lateral.setPreferredSize(new Dimension(250,0));
                barra_lateral.setVisible(true);

            }else{
                System.out.println("Deu aqui");
                barra_lateral.setPreferredSize(new Dimension(0,0));
                barra_lateral.setVisible(false);
            }
            ajustarLarguraDoFeed();
            Container paneltop = p.getTopLevelAncestor();
            if(paneltop != null) {
                paneltop.revalidate();
                paneltop.repaint();
            }
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

