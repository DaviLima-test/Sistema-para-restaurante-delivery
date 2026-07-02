package view;

import util.RemoveEmoji;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import bd.BancoDados;
import model.*;
import repositorio.Dados;

public class TelaPrincipal extends TelaMenu {
    private JPanel conteudoApp;
    private CardLayout cardLayout;
    private JPanel painelDinamico;
    private int estado = 1;

    public TelaPrincipal(Telabase sist) {
        super(sist);
        JPanel meuFeed = new JPanel(new GridBagLayout());
        meuFeed.setBackground(Color.WHITE);
        meuFeed.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margem interna melhorada

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        cardLayout = new CardLayout();
        painelDinamico = new JPanel(cardLayout);
        painelDinamico.setOpaque(false);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        meuFeed.add(criarSecaoCategorias(), gbc);

        // Adiciona as listas ao painel dinâmico controlado por CardLayout
        painelDinamico.add(criarListaRestaurantes(), "RESTAURANTES");
        painelDinamico.add(criarListaCardapio(), "CARDAPIO");

        gbc.gridy = 1;
        meuFeed.add(painelDinamico, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1.0;
        JPanel molaInvisivel = new JPanel();
        molaInvisivel.setOpaque(false);
        meuFeed.add(molaInvisivel, gbc);

        JScrollPane scroll = new JScrollPane(meuFeed);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(30);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        JPanel containerFinal = new JPanel(new BorderLayout());
        containerFinal.add(scroll, BorderLayout.CENTER);

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

        BotaoArredondado bnt_rest = new BotaoArredondado("Restaurante", 20, Color.decode("#e96769"), 20);
        BotaoArredondado bnt_card = new BotaoArredondado("Cardapio", 20, Color.decode("#e96769"), 20);

        bnt_rest.addActionListener(e -> {
            estado = 1;
            cardLayout.show(painelDinamico, "RESTAURANTES");
        });

        bnt_card.addActionListener(e -> {
            estado = 2;
            cardLayout.show(painelDinamico, "CARDAPIO");
        });

        listaHorizontal.add(bnt_rest);
        listaHorizontal.add(Box.createRigidArea(new Dimension(20, 0)));
        listaHorizontal.add(bnt_card);

        panel.add(listaHorizontal);
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
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(titulo, gbc);

        JPanel listaVertical = new JPanel(new GridBagLayout());
        listaVertical.setBackground(Color.WHITE);

        GridBagConstraints gbcCards = new GridBagConstraints();
        gbcCards.insets = new Insets(10, 10, 10, 10);
        gbcCards.anchor = GridBagConstraints.NORTHWEST;
        gbcCards.fill = GridBagConstraints.NONE;
        gbcCards.weightx = 0;
        gbcCards.weighty = 0;

        int coluna = 0;
        int linha = 0;

        ArrayList<Restaurante> restaurantes = BancoDados.getRestaurantes();

        for (Restaurante re : restaurantes) {

            CardRestaurante cdr = new CardRestaurante(
                    re.getNome(),
                    re.getLocalizacao(),
                    String.valueOf(re.getEstrelas()),
                    "🏪"
            );

            cdr.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    TelaFazerPedido tfp = new TelaFazerPedido(sist, re.getId());
                    sist.configuraTela(tfp);
                }
            });

            gbcCards.gridx = coluna;
            gbcCards.gridy = linha;

            listaVertical.add(cdr, gbcCards);

            coluna++;

            if (coluna == 4) {
                coluna = 0;
                linha++;
            }
        }

        // Empurra tudo para o canto superior esquerdo
        JPanel filler = new JPanel();
        filler.setOpaque(false);

        gbcCards.gridx = 0;
        gbcCards.gridy = linha + 1;
        gbcCards.gridwidth = 4;
        gbcCards.weightx = 1;
        gbcCards.weighty = 1;
        gbcCards.fill = GridBagConstraints.BOTH;

        listaVertical.add(filler, gbcCards);

        JScrollPane scrollVertical = new JScrollPane(
                listaVertical,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollVertical.setBorder(null);
        scrollVertical.setOpaque(false);
        scrollVertical.getViewport().setOpaque(false);
        scrollVertical.getVerticalScrollBar().setUnitIncrement(20);

        Dimension tamanhoScroll = new Dimension(900, 500);
        scrollVertical.setPreferredSize(tamanhoScroll);
        scrollVertical.setMinimumSize(tamanhoScroll);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(scrollVertical, gbc);

        return panel;
    }

    private JPanel criarListaCardapio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titulo = new JLabel("Pratos Disponíveis");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(titulo, gbc);

        JPanel listaVertical = new JPanel(new GridBagLayout());
        listaVertical.setBackground(Color.WHITE);

        GridBagConstraints gbcCards = new GridBagConstraints();
        gbcCards.insets = new Insets(10, 10, 10, 10);
        gbcCards.anchor = GridBagConstraints.NORTHWEST;
        gbcCards.fill = GridBagConstraints.NONE;
        gbcCards.weightx = 0;
        gbcCards.weighty = 0;

        int coluna = 0;
        int linha = 0;

        ArrayList<Produto> cardapios = BancoDados.getPratos();

        for (Produto prat : cardapios) {

            CardRestaurante cdr = new CardRestaurante(
                    prat.getNome(),
                    String.format("R$ %.2f", prat.getPreco()),
                    prat.getRestaurante().getNome(),
                    "🍽️"
            );

            cdr.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Dados.adicionarProduto(prat);

                    JOptionPane.showMessageDialog(
                            null,
                            RemoveEmoji.texto("🛒 " + prat.getNome() + " adicionado ao carrinho!"),
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            });

            gbcCards.gridx = coluna;
            gbcCards.gridy = linha;

            listaVertical.add(cdr, gbcCards);

            coluna++;

            if (coluna == 4) {
                coluna = 0;
                linha++;
            }
        }

        // Empurra tudo para o canto superior esquerdo
        JPanel filler = new JPanel();
        filler.setOpaque(false);

        gbcCards.gridx = 0;
        gbcCards.gridy = linha + 1;
        gbcCards.gridwidth = 4;
        gbcCards.weightx = 1;
        gbcCards.weighty = 1;
        gbcCards.fill = GridBagConstraints.BOTH;

        listaVertical.add(filler, gbcCards);

        JScrollPane scrollVertical = new JScrollPane(
                listaVertical,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollVertical.setBorder(null);
        scrollVertical.setOpaque(false);
        scrollVertical.getViewport().setOpaque(false);
        scrollVertical.getVerticalScrollBar().setUnitIncrement(20);

        Dimension tamanhoScroll = new Dimension(900, 500);
        scrollVertical.setPreferredSize(tamanhoScroll);
        scrollVertical.setMinimumSize(tamanhoScroll);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(scrollVertical, gbc);

        return panel;
    }
}