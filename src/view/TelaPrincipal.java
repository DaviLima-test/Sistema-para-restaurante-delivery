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

    private final ArrayList<Restaurante> todosRestaurantes = new ArrayList<>();
    private final ArrayList<Produto> todosCardapios = new ArrayList<>();
    private JPanel listaVerticalRestaurantes;
    private JPanel listaVerticalCardapio;
    private String filtroAtual = "";

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

        listaVerticalRestaurantes = new JPanel(new GridBagLayout());
        listaVerticalRestaurantes.setBackground(Color.WHITE);

        todosRestaurantes.clear();
        todosRestaurantes.addAll(BancoDados.getRestaurantes());

        popularRestaurantes(filtroAtual);

        JScrollPane scrollVertical = new JScrollPane(
                listaVerticalRestaurantes,
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

    /** Repopula o grid de restaurantes filtrando pelo nome ou localização. */
    private void popularRestaurantes(String filtro) {
        if (listaVerticalRestaurantes == null) return;

        listaVerticalRestaurantes.removeAll();

        GridBagConstraints gbcCards = new GridBagConstraints();
        gbcCards.insets = new Insets(10, 10, 10, 10);
        gbcCards.anchor = GridBagConstraints.NORTHWEST;
        gbcCards.fill = GridBagConstraints.NONE;
        gbcCards.weightx = 0;
        gbcCards.weighty = 0;

        String busca = filtro == null ? "" : filtro.toLowerCase();
        int coluna = 0;
        int linha = 0;

        for (Restaurante re : todosRestaurantes) {
            boolean corresponde = busca.isEmpty()
                    || re.getNome().toLowerCase().contains(busca)
                    || (re.getLocalizacao() != null && re.getLocalizacao().toLowerCase().contains(busca));

            if (!corresponde) continue;

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

            listaVerticalRestaurantes.add(cdr, gbcCards);

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

        listaVerticalRestaurantes.add(filler, gbcCards);

        listaVerticalRestaurantes.revalidate();
        listaVerticalRestaurantes.repaint();
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

        listaVerticalCardapio = new JPanel(new GridBagLayout());
        listaVerticalCardapio.setBackground(Color.WHITE);

        todosCardapios.clear();
        todosCardapios.addAll(BancoDados.getPratos());

        popularCardapio(filtroAtual);

        JScrollPane scrollVertical = new JScrollPane(
                listaVerticalCardapio,
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

    /** Repopula o grid de pratos filtrando pelo nome do prato ou do restaurante. */
    private void popularCardapio(String filtro) {
        if (listaVerticalCardapio == null) return;

        listaVerticalCardapio.removeAll();

        GridBagConstraints gbcCards = new GridBagConstraints();
        gbcCards.insets = new Insets(10, 10, 10, 10);
        gbcCards.anchor = GridBagConstraints.NORTHWEST;
        gbcCards.fill = GridBagConstraints.NONE;
        gbcCards.weightx = 0;
        gbcCards.weighty = 0;

        String busca = filtro == null ? "" : filtro.toLowerCase();
        int coluna = 0;
        int linha = 0;

        for (Produto prat : todosCardapios) {
            boolean corresponde = busca.isEmpty()
                    || prat.getNome().toLowerCase().contains(busca)
                    || (prat.getRestaurante() != null && prat.getRestaurante().getNome().toLowerCase().contains(busca));

            if (!corresponde) continue;

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

            listaVerticalCardapio.add(cdr, gbcCards);

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

        listaVerticalCardapio.add(filler, gbcCards);

        listaVerticalCardapio.revalidate();
        listaVerticalCardapio.repaint();
    }

    /**
     * Chamado pela barra de pesquisa do cabeçalho (TelaMenu) sempre que o
     * usuário digita. Filtra tanto a lista de restaurantes quanto a de
     * pratos, para que a busca funcione independentemente da aba ativa.
     */
    @Override
    protected void aoBuscar(String texto) {
        filtroAtual = texto == null ? "" : texto;
        popularRestaurantes(filtroAtual);
        popularCardapio(filtroAtual);
    }
}