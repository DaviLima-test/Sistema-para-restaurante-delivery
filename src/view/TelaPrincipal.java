package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import model.*;

public class TelaPrincipal extends TelaMenu {
    private JPanel conteudoApp;
    public TelaPrincipal(Telabase sist) {
        // O super() chama o construtor de TelaMenu, montando o Header, Overlay e Barra Lateral automaticamente
        super(sist);
        JPanel meuFeed = new JPanel(new GridBagLayout());
        meuFeed.setBackground(Color.WHITE);
        meuFeed.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;


        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        meuFeed.add(criarSecaoCategorias(), gbc);

        gbc.gridy = 1;
        meuFeed.add(criarListaRestaurantes(), gbc);


        gbc.gridy = 2;
        gbc.weighty = 1.0;
        JPanel molaInvisivel = new JPanel();
        molaInvisivel.setOpaque(false);
        meuFeed.add(molaInvisivel, gbc);

        // Configura o Scroll do Feed
        JScrollPane scroll = new JScrollPane(meuFeed);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(30);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        // Cria um painel wrapper para envelopar o scroll
        JPanel containerFinal = new JPanel(new BorderLayout());
        containerFinal.add(scroll, BorderLayout.CENTER);

        // A MÁGICA AQUI: Enviamos este painel para a classe abstrata cuidar dele!
        setConteudoInterno(containerFinal);
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
        listaHorizontal.setLayout(new BoxLayout(listaHorizontal, BoxLayout.X_AXIS));
        listaHorizontal.setBackground(Color.WHITE);
        listaHorizontal.setAlignmentX(Component.LEFT_ALIGNMENT);

        listaHorizontal.add(new BotaoArredondado("Mercado", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(Box.createRigidArea(new Dimension(20, 15)));
        listaHorizontal.add(new BotaoArredondado("Restaurantes", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(Box.createRigidArea(new Dimension(20, 15)));
        listaHorizontal.add(new BotaoArredondado("Bebidas", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(Box.createRigidArea(new Dimension(20, 15)));
        listaHorizontal.add(new BotaoArredondado("Farmácia", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(Box.createRigidArea(new Dimension(20, 15)));
        listaHorizontal.add(new BotaoArredondado("Pet Shop", 20, Color.decode("#e96769"), 20));
        listaHorizontal.add(Box.createRigidArea(new Dimension(20, 15)));
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
        scrollHorizontal.setPreferredSize(new Dimension(largura_desejada, altura));
        scrollHorizontal.setMinimumSize(new Dimension(largura_desejada, altura));
        scrollHorizontal.setMaximumSize(new Dimension(largura_desejada, altura));

        scrollHorizontal.addMouseWheelListener(e -> {
            JScrollBar bar = scrollHorizontal.getHorizontalScrollBar();
            int cliques = e.getWheelRotation();
            int novaPosicao = bar.getValue() + (cliques * bar.getUnitIncrement() * 2);
            bar.setValue(novaPosicao);
            e.consume();
        });

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
        gbc.insets = new Insets(0, 0, 15, 20);
        panel.add(titulo, gbc);

        JPanel listaHorizontal = new JPanel();
        listaHorizontal.setLayout(new BoxLayout(listaHorizontal, BoxLayout.X_AXIS));
        listaHorizontal.setBackground(Color.WHITE);

        listaHorizontal.add(new CardRestaurante("Burguer King", "4.7", "15-25 min", "R$ 4,99"));
        listaHorizontal.add(Box.createHorizontalStrut(15));
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

        JScrollPane scrollHorizontal = new JScrollPane(listaHorizontal, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollHorizontal.setBorder(null);
        scrollHorizontal.setOpaque(true);
        scrollHorizontal.setBackground(Color.BLACK);
        scrollHorizontal.getViewport().setOpaque(false);

        scrollHorizontal.getHorizontalScrollBar().setUnitIncrement(40);
        int largura_desejada = scrollHorizontal.getViewport().getWidth() / 4;
        int altura = 100;
        scrollHorizontal.setPreferredSize(new Dimension(largura_desejada, altura));
        scrollHorizontal.setMinimumSize(new Dimension(largura_desejada, altura));
        scrollHorizontal.setMaximumSize(new Dimension(largura_desejada, altura));

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