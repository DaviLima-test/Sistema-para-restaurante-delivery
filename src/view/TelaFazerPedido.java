package view;

import util.RemoveEmoji;
import model.Produto;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import bd.BancoDados;
import repositorio.Dados;

/**
 * Interface gráfica (View) encarregada de exibir o cardápio de um restaurante específico
 * e gerenciar a montagem reativa do pedido do cliente.
 * <p>
 * Apresenta uma arquitetura de visualização dividida em duas colunas (Grid de 1x2):
 * A coluna esquerda lista os produtos retornados do banco de dados para o estabelecimento selecionado.
 * A coluna direita atua como um carrinho dinâmico local, que agrupa itens repetidos adicionados,
 * calcula subtotais em tempo real e fornece controles para incremento ou decremento de unidades.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public class TelaFazerPedido extends TelaMenu {

    /** Instância ativa de controle de navegação e troca de telas globais. */
    private final Telabase sist;

    /** Identificador numérico único do restaurante selecionado para filtragem do cardápio. */
    private final int idRestaurante;

    /** Contêiner central estruturado para abrigar as seções da tela. */
    private JPanel corpoPrincipal;

    /** Painel lateral direito reativo que gerencia visualmente os itens do carrinho e checkout. */
    private JPanel painelDetalhe;

    /** Listagem em cache local contendo os pratos disponíveis do restaurante atual. */
    private ArrayList<Produto> cardapio = new ArrayList<>();

    /** Tonalidade vermelha corporativa para realce do preço total e elementos de destaque. */
    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);

    /** Tonalidade verde para preços individuais, botões de pagamento e confirmação. */
    private static final Color COR_VERDE      = new Color(46, 174, 82);

    /** Cor cinza neutra de fundo destinada a painéis internos e placeholders vazios. */
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);

    /** Cor sutil padronizada para pintura de contornos e divisórias de componentes. */
    private static final Color COR_BORDA      = new Color(230, 230, 230);

    /** Cor sutil de fundo ativada na passagem do cursor do mouse sobre o card (Hover). */
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);

    /**
     * Construtor da tela de realização de pedidos.
     * <p>
     * Inicializa a lista global de persistência do carrinho caso necessário, consome a listagem do
     * cardápio via {@link BancoDados}, organiza a divisão espacial do layout e acopla as barras de rolagem.
     * </p>
     *
     * @param sist          O frame base de gerenciamento global de telas {@link Telabase}.
     * @param idRestaurante O identificador primário do restaurante selecionado.
     */
    public TelaFazerPedido(Telabase sist, int idRestaurante) {
        super(sist);
        this.sist = sist;
        this.idRestaurante = idRestaurante;

        if (Dados.listaCarrinho == null) {
            Dados.listaCarrinho = new ArrayList<>();
        }

        this.cardapio = BancoDados.getCardapioPorRestaurante(idRestaurante);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

        corpoPrincipal = new JPanel(new GridLayout(1, 2, 20, 0));
        corpoPrincipal.setBackground(Color.WHITE);
        corpoPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        corpoPrincipal.add(criarPainelLista());

        painelDetalhe = criarPainelCarrinho();
        corpoPrincipal.add(painelDetalhe);

        JScrollPane scroll = new JScrollPane(corpoPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scroll, BorderLayout.CENTER);

        setConteudoInterno(container);
    }

    /**
     * Constrói a seção de cabeçalho superior contendo títulos informativos e instruções da tela.
     * * @return Um {@link JPanel} contendo o layout de títulos formatado.
     */
    private JPanel criarCabecalho() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(16, 30, 16, 30)
        ));

        JPanel titulos = new JPanel();
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        titulos.setOpaque(false);

        Texto titulo = new Texto("🛍️ Realizar Pedido");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        titulos.add(titulo);

        JLabel sub = new JLabel("Selecione os pratos desejados e monte seu carrinho");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);
        titulos.add(sub);

        p.add(titulos, BorderLayout.WEST);
        return p;
    }

    /**
     * Constrói a coluna esquerda contendo a listagem de pratos disponíveis no cardápio.
     * Trata cenários visuais onde o estabelecimento não possui itens cadastrados.
     * * @return Um {@link JPanel} de rolagem com os cartões dos produtos empilhados.
     */
    private JPanel criarPainelLista() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JLabel lblSub = new JLabel("Cardápio Disponível");
        lblSub.setFont(new Font("Arial", Font.BOLD, 15));
        lblSub.setForeground(new Color(50, 50, 50));
        lblSub.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        wrapper.add(lblSub, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);
        lista.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        if (cardapio == null || cardapio.isEmpty()) {
            JLabel vazio = new JLabel("Este restaurante não possui pratos disponíveis.");
            vazio.setFont(new Font("Arial", Font.ITALIC, 13));
            vazio.setForeground(Color.GRAY);
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            lista.add(Box.createVerticalStrut(40));
            lista.add(vazio);
        } else {
            for (Produto produto : cardapio) {
                lista.add(criarCardProduto(produto));
                lista.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollLista = new JScrollPane(lista);
        scrollLista.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1, true));
        scrollLista.getVerticalScrollBar().setUnitIncrement(16);
        scrollLista.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollLista.setPreferredSize(new Dimension(0, 500));
        wrapper.add(scrollLista, BorderLayout.CENTER);

        return wrapper;
    }

    /**
     * Fabrica o componente visual individualizado (Card) para exibição de dados de um prato.
     * Configura ouvidores de mouse para aplicar reatividade de cor ao passar ou clicar no item.
     * * @param produto A entidade {@link Produto} que servirá de base para renderização das informações.
     * @return Um {@link JPanel} interativo configurado.
     */
    private JPanel criarCardProduto(Produto produto) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        JLabel icone = new JLabel("🍽️");
        icone.setFont(new Font("Arial", Font.PLAIN, 26));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 14);
        card.add(icone, gbc);

        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 3, 0);

        JLabel lblNome = new JLabel(produto.getNome());
        lblNome.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(lblNome, gbc);

        JLabel lblPreco = new JLabel(String.format("R$ %.2f", produto.getPreco()));
        lblPreco.setFont(new Font("Arial", Font.BOLD, 14));
        lblPreco.setForeground(COR_VERDE);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 12, 3, 0);
        card.add(lblPreco, gbc);

        JLabel lblDetalhe = new JLabel("Clique para adicionar ao carrinho");
        lblDetalhe.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDetalhe.setForeground(Color.GRAY);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(lblDetalhe, gbc);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(COR_CARD_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { card.setBackground(Color.WHITE); }
            @Override public void mouseClicked(MouseEvent e) { adicionarAoCarrinho(produto); }
        });

        return card;
    }

    /**
     * Constrói reativamente o painel do carrinho de compras realizando a leitura do repositório global.
     * <p>
     * Utiliza uma estrutura {@link LinkedHashMap} para fins de exibição na interface, agrupando objetos
     * repetidos da lista volátil, computando quantidades mapeadas e exibindo o valor de fechamento.
     * </p>
     * * @return Um {@link JPanel} estruturado contendo as linhas de faturamento do pedido.
     */
    private JPanel criarPainelCarrinho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitulo = new JLabel("🛒 Meu Pedido");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 30));
        painel.add(lblTitulo, BorderLayout.NORTH);

        if (Dados.listaCarrinho == null || Dados.listaCarrinho.isEmpty()) {
            JPanel placeholder = new JPanel(new GridBagLayout());
            placeholder.setBackground(COR_CINZA_BG);
            placeholder.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1, true));

            JLabel txtVazio = new JLabel("<html><div style='text-align:center;color:#bbb'>"
                    + "🛍️<br><br>Seu carrinho está vazio.<br>Clique nos pratos da esquerda<br>para adicioná-los.</div></html>");
            txtVazio.setFont(new Font("Arial", Font.PLAIN, 14));
            placeholder.add(txtVazio);

            painel.add(placeholder, BorderLayout.CENTER);
            return painel;
        }

        LinkedHashMap<Produto, Integer> itensAgrupados = new LinkedHashMap<>();
        for (Produto p : Dados.listaCarrinho) {
            itensAgrupados.put(p, itensAgrupados.getOrDefault(p, 0) + 1);
        }

        JPanel listaItens = new JPanel();
        listaItens.setLayout(new BoxLayout(listaItens, BoxLayout.Y_AXIS));
        listaItens.setBackground(Color.WHITE);

        double totalPedido = 0;

        for (Map.Entry<Produto, Integer> entry : itensAgrupados.entrySet()) {
            Produto p = entry.getKey();
            int qtd = entry.getValue();
            double subtotalItem = p.getPreco() * qtd;
            totalPedido += subtotalItem;

            JPanel linha = new JPanel(new BorderLayout(10, 0));
            linha.setBackground(Color.WHITE);
            linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            linha.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA));

            JLabel infoText = new JLabel(String.format("%s (x%d)", p.getNome(), qtd));
            infoText.setFont(new Font("Arial", Font.PLAIN, 14));
            linha.add(infoText, BorderLayout.WEST);

            JPanel acoesDireta = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            acoesDireta.setOpaque(false);

            JLabel lblSubtotalItem = new JLabel(String.format("R$ %.2f", subtotalItem));
            lblSubtotalItem.setFont(new Font("Arial", Font.BOLD, 13));
            lblSubtotalItem.setForeground(new Color(50, 50, 50));
            acoesDireta.add(lblSubtotalItem);

            JButton btnMenos = new JButton("－");
            btnMenos.setFocusable(false);
            btnMenos.setMargin(new Insets(2, 6, 2, 6));
            btnMenos.addActionListener(e -> removerOuDiminuir(p));
            acoesDireta.add(btnMenos);

            linha.add(acoesDireta, BorderLayout.EAST);
            listaItens.add(linha);
            listaItens.add(Box.createVerticalStrut(5));
        }

        JScrollPane scrollItens = new JScrollPane(listaItens);
        scrollItens.setBorder(null);
        scrollItens.getViewport().setBackground(Color.WHITE);
        painel.add(scrollItens, BorderLayout.CENTER);

        JPanel rodape = new JPanel();
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel linhaTotal = new JPanel(new BorderLayout());
        linhaTotal.setOpaque(false);
        JLabel lblTotalTxt = new JLabel("Total do Pedido:");
        lblTotalTxt.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel lblTotalValor = new JLabel(String.format("R$ %.2f", totalPedido));
        lblTotalValor.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalValor.setForeground(COR_PRIMARIA);

        linhaTotal.add(lblTotalTxt, BorderLayout.WEST);
        linhaTotal.add(lblTotalValor, BorderLayout.EAST);
        rodape.add(linhaTotal);
        rodape.add(Box.createVerticalStrut(15));

        BotaoArredondado btnFinalizar = new BotaoArredondado("💳 Realizar o pagamento", 20, COR_VERDE, 14);
        btnFinalizar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnFinalizar.addActionListener(e -> Pagarpedido());
        rodape.add(btnFinalizar);

        painel.add(rodape, BorderLayout.SOUTH);

        return painel;
    }

    /**
     * Insere uma referência de produto diretamente na lista estática global e força a repintura do painel.
     * * @param produto A instância de {@link Produto} que será injetada no carrinho.
     */
    private void adicionarAoCarrinho(Produto produto) {
        Dados.listaCarrinho.add(produto);
        atualizarPainelCarrinho();
    }

    /**
     * Exclui a primeira ocorrência encontrada do produto selecionado na lista estática global,
     * reduzindo a quantidade empilhada e disparando a reconstrução do painel.
     * * @param produto O item do tipo {@link Produto} que será removido/reduzido.
     */
    private void removerOuDiminuir(Produto produto) {
        Dados.listaCarrinho.remove(produto);
        atualizarPainelCarrinho();
    }

    /**
     * Gerencia a substituição estrutural do painel do carrinho na árvore do contêiner,
     * limpando o estado desatualizado, aplicando a limpeza de emojis e revalidando a interface.
     */
    private void atualizarPainelCarrinho() {
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = criarPainelCarrinho();
        RemoveEmoji.aplicar(painelDetalhe);
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }

    /**
     * Gatilho de controle de faturamento que redireciona o fluxo de navegação do usuário
     * para a view consolidada de fechamento {@link TelaCarrinho}.
     */
    private void Pagarpedido() {
        TelaCarrinho tc = new TelaCarrinho(sist);
        sist.configuraTela(tc);
    }
}