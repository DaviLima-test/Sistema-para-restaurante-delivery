package view;

import model.Produto;
import util.RemoveEmoji;
import model.Pedido;
import bd.BancoDados;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Interface gráfica (View) destinada ao gerenciamento e monitoramento do fluxo
 * de pedidos recebidos em tempo real por um restaurante parceiro.
 * <p>
 * O painel divide-se em uma listagem cronológica de pedidos à esquerda (com suporte
 * a filtragem por abas) e uma seção detalhada à direita para acompanhamento do status.
 * Interage diretamente com a fachada de persistência {@link BancoDados}, permitindo
 * que o gerente transicione o estado de produção dos itens de forma reativa.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.3
 */
public class TelaPedidosRestaurante extends TelaMenu {

    /** Instância ativa do frame de coordenação global de telas. */
    private final Telabase sist;

    /** Contêiner estrutural central encarregado de organizar as colunas de dados. */
    private JPanel corpoPrincipal;

    /** Painel reativo lateral direito focado na renderização do faturamento e dados do cliente. */
    private JPanel painelDetalhe;

    /** Referência do objeto {@link Pedido} atualmente inspecionado e selecionado pelo usuário. */
    private Pedido pedidoSelecionado;

    /** O critério de filtro textual selecionado atualmente para segregar a exibição dos pedidos na lista. */
    private String filtroAtual = "Todos";

    /** Contêiner de fluxo horizontal que abriga e gerencia os botões mutáveis de abas. */
    private JPanel painelTabs;

    /** Painel vertical interno encarregado do empilhamento e renderização dinâmica dos cartões filtrados. */
    private JPanel painelListaCards;

    /** Rótulo informativo do subcabeçalho atualizado dinamicamente com os quantitativos globais. */
    private JLabel subHeaderStats;

    /** Código de estado lógico para um pedido recém-submetido pelo cliente. */
    static final int ESTADO_RECEBIDO   = 1;

    /** Código de estado lógico para um pedido aceito em fase de preparação na cozinha. */
    static final int ESTADO_PRODUCAO   = 2;

    /** Código de estado lógico indicando conclusão da cozinha e aguardo de retirada pelo entregador. */
    static final int ESTADO_PRONTO     = 3;

    /** Código de estado lógico atribuído quando o item é coletado e despachado em trânsito. */
    static final int ESTADO_EM_ROTA    = 4;

    /** Código de estado lógico finalizador do ciclo de faturamento. */
    static final int ESTADO_FINALIZADO = 5;

    /** Vetor indexado de Strings utilizado para a rotulagem legível dos estados na interface. */
    private static final String[] LABEL_ESTADO = {
            "", "Recebido", "Em Produção", "Pronto p/ Entrega", "Em Rota", "Entregue"
    };

    /** Paleta de cores de fundo específicas (badges) associadas ao estado lógico correspondente. */
    private static final Color[] BG_ESTADO = {
            Color.GRAY,
            new Color(255, 243, 205),
            new Color(219, 234, 254),
            new Color(209, 250, 229),
            new Color(255, 228, 230),
            new Color(243, 244, 246)
    };

    /** Matiz tipográfica de realce de contraste de texto vinculada a cada badge de status. */
    private static final Color[] FG_ESTADO = {
            Color.GRAY,
            new Color(133, 100,   4),
            new Color( 29,  78, 216),
            new Color( 21,  87,  36),
            new Color(234,  16,  34),
            new Color( 80,  80,  80)
    };

    /** Vetor de mascaramento para emoticons ou ícones customizados de status (Reservado). */
    private static final String[] ICON_ESTADO = {
            "", "", "", "", "", ""
    };

    /** Cor vermelha corporativa aplicada em botões de destaque, cancelamento e títulos secundários. */
    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);

    /** Tonalidade verde para confirmações e estados de conclusão física de tarefas. */
    private static final Color COR_VERDE      = new Color(46, 174, 82);

    /** Tonalidade azul utilizada para gatilhos de avanço de fluxo ou aceite operacional. */
    private static final Color COR_AZUL       = new Color(29, 78, 216);

    /** Cor cinza neutra para panos de fundo de contêineres e placeholders informativos. */
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);

    /** Cor sutil e padronizada para pintura de contornos e divisórias de componentes. */
    private static final Color COR_BORDA      = new Color(230, 230, 230);

    /** Cor sutil de fundo ativada na passagem do cursor do mouse sobre os cartões (Hover). */
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);

    /**
     * Recupera o identificador numérico primário do restaurante atrelado ao e-mail do gerente logado.
     * * * @return O ID do restaurante mapeado no banco, ou {@code -1} se houver falhas de parse ou ausência.
     */
    private static int idRestauranteAtual() {
        String[] dados = bd.BancoDados.buscarRestaurantePorGerente(Telabase.getLogin().GetEmail());
        if (dados == null || dados.length == 0) return -1;
        try {
            return Integer.parseInt(dados[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Consome a API de dados para obter a listagem de pedidos vinculados ao restaurante ativo.
     * * * @return Uma coleção {@link List} contendo os pedidos interceptados, ou uma lista vazia caso não encontre o ID.
     */
    private static List<Pedido> pedidosDoRestaurante() {
        int idRest = idRestauranteAtual();
        if (idRest == -1) return new ArrayList<>();
        return bd.BancoDados.obterPedidosPorRestaurante(idRest);
    }

    /**
     * Construtor da tela de gerenciamento de pedidos do restaurante.
     * <p>
     * Monta o esqueleto do cabeçalho de estatísticas, divide espacialmente a tela em duas colunas centrais,
     * injeta o painel de listagem e define o placeholder inicial da direita.
     * </p>
     *
     * @param sist O frame base de gerenciamento global de telas {@link Telabase}.
     */
    public TelaPedidosRestaurante(Telabase sist) {
        super(sist);
        this.sist = sist;

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

        corpoPrincipal = new JPanel(new GridLayout(1, 2, 20, 0));
        corpoPrincipal.setBackground(Color.WHITE);
        corpoPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        corpoPrincipal.add(criarPainelLista());

        painelDetalhe = criarPlaceholder();
        corpoPrincipal.add(painelDetalhe);

        JScrollPane scroll = new JScrollPane(corpoPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scroll, BorderLayout.CENTER);

        setConteudoInterno(container);
    }

    /**
     * Cria e preenche a barra superior informativa da interface.
     * Computa via streams o balanço de pedidos em andamento/novos para atualização das métricas do painel.
     * * * @return Um {@link JPanel} estruturado contendo textos e botão de refresh.
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

        Texto titulo = new Texto("Pedidos do Restaurante");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        titulos.add(titulo);

        subHeaderStats = new JLabel();
        subHeaderStats.setFont(new Font("Arial", Font.PLAIN, 13));
        subHeaderStats.setForeground(Color.GRAY);
        titulos.add(subHeaderStats);

        atualizarStatsCabecalho();

        p.add(titulos, BorderLayout.WEST);

        BotaoArredondado btnAtualizar = new BotaoArredondado("Atualizar", 20, COR_PRIMARIA, 14);
        btnAtualizar.setPreferredSize(new Dimension(130, 42));
        btnAtualizar.addActionListener(e -> atualizarTela());
        p.add(btnAtualizar, BorderLayout.EAST);

        return p;
    }

    /**
     * Atualiza as métricas numéricas exibidas no subcabeçalho com base no estado atual da base de dados.
     */
    private void atualizarStatsCabecalho() {
        List<Pedido> totalPedidos = pedidosDoRestaurante();
        long emProducao = totalPedidos.stream().filter(pd -> pd.getEstado() == ESTADO_PRODUCAO).count();
        long recebidos  = totalPedidos.stream().filter(pd -> pd.getEstado() == ESTADO_RECEBIDO).count();
        subHeaderStats.setText(recebidos + " novos  •  " + emProducao + " em produção");
    }

    /**
     * Cria a seção de listagem empilhada de pedidos da coluna esquerda.
     * Instancia as estruturas dos contêineres vazios e aciona a primeira renderização dos cards.
     * * * @return Um {@link JPanel} encapsulado em uma barra de rolagem contendo as estruturas das abas.
     */
    private JPanel criarPainelLista() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        painelTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        painelTabs.setOpaque(false);
        painelTabs.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        wrapper.add(painelTabs, BorderLayout.NORTH);

        painelListaCards = new JPanel();
        painelListaCards.setLayout(new BoxLayout(painelListaCards, BoxLayout.Y_AXIS));
        painelListaCards.setBackground(Color.WHITE);
        painelListaCards.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scrollLista = new JScrollPane(painelListaCards);
        scrollLista.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1, true));
        scrollLista.getVerticalScrollBar().setUnitIncrement(16);
        scrollLista.setPreferredSize(new Dimension(0, 500));
        wrapper.add(scrollLista, BorderLayout.CENTER);

        renderizarAbasELista();

        return wrapper;
    }

    /**
     * Reconstrói dinamicamente a barra de abas de filtragem e processa a segregação lógica
     * dos pedidos com base no estado selecionado, redesenhando a listagem em tempo de execução.
     */
    private void renderizarAbasELista() {
        painelTabs.removeAll();
        String[] filtros = {"Todos", "Novos", "Em Produção", "Prontos"};

        for (String f : filtros) {
            Color bg = f.equals(filtroAtual) ? COR_PRIMARIA : new Color(200, 200, 200);
            BotaoArredondado btn = new BotaoArredondado(f, 20, bg, 12);
            btn.setPreferredSize(new Dimension(90, 30));
            btn.addActionListener(e -> {
                filtroAtual = f;
                renderizarAbasELista();
            });
            painelTabs.add(btn);
        }
        painelTabs.revalidate();
        painelTabs.repaint();

        painelListaCards.removeAll();

        List<Pedido> pedidos = pedidosDoRestaurante();
        List<Pedido> pedidosFiltrados = new ArrayList<>();

        for (Pedido pd : pedidos) {
            int st = safeEstado(pd);
            if (filtroAtual.equals("Todos")) {
                pedidosFiltrados.add(pd);
            } else if (filtroAtual.equals("Novos") && st == ESTADO_RECEBIDO) {
                pedidosFiltrados.add(pd);
            } else if (filtroAtual.equals("Em Produção") && st == ESTADO_PRODUCAO) {
                pedidosFiltrados.add(pd);
            } else if (filtroAtual.equals("Prontos") && st == ESTADO_PRONTO) {
                pedidosFiltrados.add(pd);
            }
        }

        pedidosFiltrados.sort((a, b) -> {
            int ea = safeEstado(a), eb = safeEstado(b);
            if (ea == ESTADO_FINALIZADO && eb != ESTADO_FINALIZADO) return 1;
            if (eb == ESTADO_FINALIZADO && ea != ESTADO_FINALIZADO) return -1;
            return Integer.compare(ea, eb);
        });

        if (pedidosFiltrados.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum pedido nesta categoria.");
            vazio.setFont(new Font("Arial", Font.ITALIC, 13));
            vazio.setForeground(Color.GRAY);
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelListaCards.add(Box.createVerticalStrut(40));
            painelListaCards.add(vazio);
        } else {
            for (Pedido pd : pedidosFiltrados) {
                painelListaCards.add(criarCardPedido(pd));
                painelListaCards.add(Box.createVerticalStrut(10));
            }
        }
        painelListaCards.revalidate();
        painelListaCards.repaint();
    }

    /**
     * Fabrica e customiza o componente visual individualizado (Card) que resume os dados básicos de um pedido.
     * Concatena os nomes dos produtos e amarra listeners de mouse para clique e seleção.
     * * * @param pd A instância de {@link Pedido} que alimentará o componente gráfico.
     * @return O painel configurado em {@link GridBagLayout}.
     */
    private JPanel criarCardPedido(Pedido pd) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        int st = safeEstado(pd);
        JLabel icone = new JLabel(ICON_ESTADO[st]);
        icone.setFont(new Font("Arial", Font.PLAIN, 26));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 14);
        card.add(icone, gbc);

        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 3, 0);

        StringBuilder nomesComidas = new StringBuilder();
        for (Produto prod : pd.getComidas()) {
            if (nomesComidas.length() > 0) nomesComidas.append(", ");
            nomesComidas.append(prod.getNome());
        }

        JLabel lblNome = new JLabel(nomesComidas.length() > 0 ? nomesComidas.toString() : "Produtos");
        lblNome.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(lblNome, gbc);

        JLabel badge = new JLabel("  " + LABEL_ESTADO[st] + "  ");
        badge.setFont(new Font("Arial", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBackground(BG_ESTADO[st]);
        badge.setForeground(FG_ESTADO[st]);
        badge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        card.add(badge, gbc);

        String clienteNome = pd.getCliente() != null ? pd.getCliente().getNome() : "Cliente";
        String hora        = pd.getHora_Entregue() != null ? pd.getHora_Entregue() : "Imediato";

        JLabel detalhe = new JLabel(clienteNome + "  •  Pedido para " + hora);
        detalhe.setFont(new Font("Arial", Font.PLAIN, 12));
        detalhe.setForeground(Color.GRAY);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(detalhe, gbc);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(COR_CARD_HOVER); }
            @Override public void mouseExited(MouseEvent e)  {
                card.setBackground(pd == pedidoSelecionado ? COR_CARD_HOVER : Color.WHITE);
            }
            @Override public void mouseClicked(MouseEvent e) { selecionarPedido(pd); }
        });

        return card;
    }

    /**
     * Instancia o componente neutro (Placeholder) da coluna da direita antes de qualquer seleção.
     * * * @return O painel contendo a mensagem de instrução textual.
     */
    private JPanel criarPlaceholder() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COR_CINZA_BG);
        p.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1, true));

        JLabel dica = new JLabel(
                "<html><div style='text-align:center;color:#bbb'>" +
                        "Selecione um pedido<br>na lista ao lado<br>" +
                        "para ver os detalhes<br>e gerenciar a produção" +
                        "</div></html>"
        );
        dica.setFont(new Font("Arial", Font.PLAIN, 15));
        dica.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(dica);
        return p;
    }

    /**
     * Gera dinamicamente o formulário estendido e detalhado de um pedido previamente selecionado.
     * Mapeia informações do cliente, método de cobrança e alinha as ações de produção.
     * * * @param pd O {@link Pedido} em foco.
     * @return O painel estruturado preenchido.
     */
    private JPanel criarPainelDetalhe(Pedido pd) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel lblTitulo = new JLabel("Detalhes do Pedido");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 30));
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(8));

        int st = safeEstado(pd);
        JLabel badge = new JLabel("  " + LABEL_ESTADO[st] + "  ");
        badge.setFont(new Font("Arial", Font.BOLD, 13));
        badge.setOpaque(true);
        badge.setBackground(BG_ESTADO[st]);
        badge.setForeground(FG_ESTADO[st]);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        painel.add(badge);
        painel.add(Box.createVerticalStrut(20));

        StringBuilder listaItens = new StringBuilder();
        for (Produto prod : pd.getComidas()) {
            if (listaItens.length() > 0) listaItens.append(", ");
            listaItens.append(prod.getNome());
        }

        painel.add(criarSecao("Pedido", new String[][]{
                {"Itens",    listaItens.toString()},
                {"Horário",  pd.getHora_Entregue() != null ? pd.getHora_Entregue() : "Imediato"},
                {"Status",   LABEL_ESTADO[st]}
        }));
        painel.add(Box.createVerticalStrut(14));

        String nomeCliente = pd.getCliente() != null ? pd.getCliente().getNome() : "Não informado";
        painel.add(criarSecao("Cliente", new String[][]{
                {"Nome",       nomeCliente},
                {"Endereço",   "Rua das Flores, 55"},
                {"Pagamento",  "Pago via App"}
        }));
        painel.add(Box.createVerticalStrut(24));

        JPanel btnArea = criarBotoesAcao(pd);
        btnArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(btnArea);

        painel.add(Box.createVerticalGlue());
        return painel;
    }

    /**
     * Avalia o status atual do pedido para gerar os botões corretos de ação e transição de workflow.
     * * * @param pd O {@link Pedido} inspecionado.
     * @return Um painel contendo botões, ou mensagens textuais informativas de fluxo.
     */
    private JPanel criarBotoesAcao(Pedido pd) {
        JPanel area = new JPanel();
        area.setLayout(new BoxLayout(area, BoxLayout.Y_AXIS));
        area.setOpaque(false);

        int st = safeEstado(pd);

        if (st == ESTADO_RECEBIDO) {
            JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

            BotaoArredondado btnAceitar = new BotaoArredondado("Iniciar Produção", 20, COR_AZUL, 13);
            btnAceitar.addActionListener(e -> mudarEstado(pd, ESTADO_PRODUCAO));

            BotaoArredondado btnRecusar = new BotaoArredondado("Recusar Pedido", 20, COR_PRIMARIA, 13);
            btnRecusar.addActionListener(e -> recusarPedido(pd));

            row.add(btnAceitar);
            row.add(btnRecusar);
            area.add(row);

        } else if (st == ESTADO_PRODUCAO) {
            BotaoArredondado btnPronto = new BotaoArredondado("Marcar como Pronto", 20, COR_VERDE, 14);
            btnPronto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            btnPronto.addActionListener(e -> mudarEstado(pd, ESTADO_PRONTO));
            area.add(btnPronto);

        } else if (st == ESTADO_PRONTO) {
            JLabel info = new JLabel("Aguardando entregador aceitar o pedido...");
            info.setFont(new Font("Arial", Font.ITALIC, 13));
            info.setForeground(new Color(100, 100, 100));
            area.add(info);

        } else if (st == ESTADO_EM_ROTA) {
            JLabel info = new JLabel("Pedido a caminho com o entregador.");
            info.setFont(new Font("Arial", Font.ITALIC, 13));
            info.setForeground(new Color(100, 100, 100));
            area.add(info);

        } else {
            JLabel info = new JLabel("Pedido concluído e entregue ao cliente.");
            info.setFont(new Font("Arial", Font.ITALIC, 13));
            info.setForeground(COR_VERDE);
            area.add(info);
        }

        return area;
    }

    /**
     * Fabrica uma subseção visual formatada em pares de chave/valor para exibição limpa de metadados.
     * * * @param titulo O título da seção (ex: "Cliente").
     * @param pares  Matriz bi-dimensional de Strings contendo as linhas formatadas.
     * @return O painel contendo o layout da seção.
     */
    private JPanel criarSecao(String titulo, String[][] pares) {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(COR_CINZA_BG);
        s.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        int h = 45 + (pares.length * 26);
        s.setPreferredSize(new Dimension(320, h));
        s.setMinimumSize(new Dimension(250, h));

        Texto lblTit = new Texto(titulo);
        lblTit.setFont(new Font("Arial", Font.BOLD, 13));
        lblTit.setForeground(COR_PRIMARIA);
        s.add(lblTit);
        s.add(Box.createVerticalStrut(8));

        for (String[] par : pares) {
            JPanel linha = new JPanel(new BorderLayout(8, 0));
            linha.setOpaque(false);
            JLabel chave = new JLabel(par[0] + ":");
            chave.setFont(new Font("Arial", Font.BOLD, 13));
            chave.setForeground(new Color(80, 80, 80));
            chave.setPreferredSize(new Dimension(90, 22));
            JLabel valor = new JLabel(par[1]);
            valor.setFont(new Font("Arial", Font.PLAIN, 13));
            valor.setForeground(new Color(40, 40, 40));
            linha.add(chave, BorderLayout.WEST);
            linha.add(valor, BorderLayout.CENTER);
            s.add(linha);
            s.add(Box.createVerticalStrut(6));
        }
        return s;
    }

    /**
     * Substitui o contêiner detalhado atual e reconstrói a seção à direita com base no pedido focado.
     * * * @param pd O objeto {@link Pedido} selecionado para inspeção.
     */
    private void selecionarPedido(Pedido pd) {
        pedidoSelecionado = pd;
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = criarPainelDetalhe(pd);
        RemoveEmoji.aplicar(painelDetalhe);
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }

    /**
     * Dispara a alteração persistente de status de um pedido diretamente na base de dados.
     * Exibe avisos dialogados de confirmação ao usuário e força a atualização da árvore de visualização.
     * * * @param pd          A instância de {@link Pedido} afetada.
     * @param novoEstado O inteiro do novo estado lógico de destino.
     */
    private void mudarEstado(Pedido pd, int novoEstado) {
        boolean sucesso = bd.BancoDados.atualizarEstadoPedido(pd.getId(), novoEstado);

        if (sucesso) {
            String[] msgs = {
                    "", "Pedido recebido.", "Produção iniciada!",
                    "Prato pronto! Aguardando entregador.", "Pedido a caminho.", "Pedido finalizado."
            };
            JOptionPane.showMessageDialog(this, msgs[novoEstado], "Status Atualizado", JOptionPane.INFORMATION_MESSAGE);
            atualizarTela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar status no servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Remove ou cancela permanentemente o registro de um pedido na base de dados após confirmação por modal.
     * * * @param pd O {@link Pedido} a ser deletado/recusado.
     */
    private void recusarPedido(Pedido pd) {
        int r = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja recusar este pedido?\nO cliente será notificado.",
                "Recusar Pedido", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (r == JOptionPane.YES_OPTION) {
            boolean sucesso = bd.BancoDados.cancelarPedidoNoBanco(pd.getId());
            if (sucesso) {
                atualizarTela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao processar recusa no banco.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Normaliza e valida o estado inteiro embutido no pedido para evitar estouros de índice em vetores.
     * * * @param pd O {@link Pedido} a ser validado.
     * @return Um número inteiro seguro limitado estritamente entre 1 e 5.
     */
    private int safeEstado(Pedido pd) {
        int e = pd.getEstado();
        return Math.max(1, Math.min(e, 5));
    }

    /**
     * Realiza a reconstrução estrutural completa da janela, forçando uma nova instância
     * limpa a consumir os dados atualizados do banco de dados.
     */
    private void atualizarTela() {
        pedidoSelecionado = null;
        if (sist != null) {
            sist.configuraTela(new TelaPedidosRestaurante(sist));
        }
    }
}