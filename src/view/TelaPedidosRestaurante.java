package view;

import model.Pedido;
import model.Produto;
import model.Cliente;
import model.Restaurante;
import repositorio.Dados;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * TelaPedidosRestaurante — Pedidos recebidos pelo restaurante.
 *
 * Layout (padrão TelaMenu — idêntico ao TelaPedidosEntregador):
 * ┌──────────────────────────────────────────────────────┐
 * │  Header (TelaMenu)                                   │
 * ├──────────────────────────────────────────────────────┤
 * │  Cabeçalho da tela  [↻ Atualizar]                   │
 * ├────────────────────────┬─────────────────────────────┤
 * │  Lista de pedidos (40%)│  Detalhe do pedido (60%)    │
 * │  card por status       │  Placeholder → detalhes     │
 * └────────────────────────┴─────────────────────────────┘
 *
 * Estados do pedido (do ponto de vista do restaurante):
 *  1 = Recebido (novo, aguardando ação do restaurante)
 *  2 = Em Produção
 *  3 = Pronto / Aguardando Entregador
 *  4 = Finalizado (entregue)
 */
public class TelaPedidosRestaurante extends TelaMenu {

    private final Telabase sist;

    private JPanel corpoPrincipal;
    private JPanel painelDetalhe;
    private Pedido pedidoSelecionado;

    // ── Estados restaurante ───────────────────────────────────
    static final int ESTADO_RECEBIDO   = 1;
    static final int ESTADO_PRODUCAO   = 2;
    static final int ESTADO_PRONTO     = 3;
    static final int ESTADO_FINALIZADO = 4;

    private static final String[] LABEL_ESTADO = {
        "", "Recebido", "Em Produção", "Pronto p/ Entrega", "Finalizado"
    };
    private static final Color[] BG_ESTADO = {
        Color.GRAY,
        new Color(255, 243, 205),   // amarelo suave
        new Color(219, 234, 254),   // azul suave
        new Color(209, 250, 229),   // verde suave
        new Color(243, 244, 246)    // cinza
    };
    private static final Color[] FG_ESTADO = {
        Color.GRAY,
        new Color(133, 100,   4),   // amarelo escuro
        new Color( 29,  78, 216),   // azul escuro
        new Color( 21,  87,  36),   // verde escuro
        new Color( 80,  80,  80)    // cinza
    };
    private static final String[] ICON_ESTADO = {
        "", "🕐", "\uD83C\uDF73", "✅", "📦"
    };

    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);
    private static final Color COR_VERDE      = new Color(46, 174, 82);
    private static final Color COR_AZUL       = new Color(29, 78, 216);
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);
    private static final Color COR_BORDA      = new Color(230, 230, 230);
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);

    public TelaPedidosRestaurante(Telabase sist) {
        super(sist);
        this.sist = sist;

        // Garante dados demo se lista vazia
        if (Dados.listaPedidos == null || Dados.listaPedidos.isEmpty()) {
            Dados.listaPedidos = new ArrayList<>(criarPedidosDemo());
        }

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

    // ─────────────────────────────────────────────────────────
    //  CABEÇALHO
    // ─────────────────────────────────────────────────────────
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

        Texto titulo = new Texto("🧾  Pedidos do Restaurante");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        titulos.add(titulo);

        long emProducao = Dados.listaPedidos.stream()
                .filter(pd -> pd.getEstadoRestaurante() == ESTADO_PRODUCAO).count();
        long recebidos  = Dados.listaPedidos.stream()
                .filter(pd -> pd.getEstadoRestaurante() == ESTADO_RECEBIDO).count();

        JLabel sub = new JLabel(recebidos + " novos  •  " + emProducao + " em produção");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);
        titulos.add(sub);

        p.add(titulos, BorderLayout.WEST);

        BotaoArredondado btnAtualizar = new BotaoArredondado("↻  Atualizar", 20, COR_PRIMARIA, 14);
        btnAtualizar.setPreferredSize(new Dimension(130, 42));
        btnAtualizar.addActionListener(e -> sist.configuraTela(new TelaPedidosRestaurante(sist)));
        p.add(btnAtualizar, BorderLayout.EAST);

        return p;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DA LISTA
    // ─────────────────────────────────────────────────────────
    private JPanel criarPainelLista() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        // Tabs de filtro por status
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tabs.setOpaque(false);
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        String[] filtros = {"Todos", "Novos", "Em Produção", "Prontos"};
        for (String f : filtros) {
            BotaoArredondado btn = new BotaoArredondado(f, 20,
                    f.equals("Todos") ? COR_PRIMARIA : new Color(200, 200, 200), 12);
            btn.setPreferredSize(new Dimension(90, 30));
            // Futura integração: adicionar ActionListener para filtrar a lista
            tabs.add(btn);
        }
        wrapper.add(tabs, BorderLayout.NORTH);

        // Lista de cards
        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);
        lista.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        List<Pedido> pedidos = Dados.listaPedidos;
        // Ordena: Recebidos primeiro, depois Em Produção, depois Prontos, Finalizados por último
        pedidos.sort((a, b) -> {
            int ea = safeEstado(a), eb = safeEstado(b);
            if (ea == ESTADO_FINALIZADO && eb != ESTADO_FINALIZADO) return 1;
            if (eb == ESTADO_FINALIZADO && ea != ESTADO_FINALIZADO) return -1;
            return Integer.compare(ea, eb);
        });

        if (pedidos.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum pedido recebido ainda.");
            vazio.setFont(new Font("Arial", Font.ITALIC, 13));
            vazio.setForeground(Color.GRAY);
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            lista.add(Box.createVerticalStrut(40));
            lista.add(vazio);
        } else {
            for (Pedido pd : pedidos) {
                lista.add(criarCardPedido(pd));
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

    /** Card de pedido — mesmo padrão do TelaPedidosEntregador */
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

        JLabel lblNome = new JLabel(pd.getComida() != null ? pd.getComida() : "Produto");
        lblNome.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(lblNome, gbc);

        // Badge de status com cor dinâmica
        JLabel badge = new JLabel("  " + LABEL_ESTADO[st] + "  ");
        badge.setFont(new Font("Arial", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBackground(BG_ESTADO[st]);
        badge.setForeground(FG_ESTADO[st]);
        badge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 12, 3, 0);
        card.add(badge, gbc);

        String clienteNome = pd.getCliente() != null ? pd.getCliente().getNome() : "Cliente";
        String hora        = pd.getHora_Entregue() != null ? pd.getHora_Entregue() : "--:--";
        JLabel detalhe = new JLabel("👤 " + clienteNome + "  •  Pedido para " + hora);
        detalhe.setFont(new Font("Arial", Font.PLAIN, 12));
        detalhe.setForeground(Color.GRAY);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(detalhe, gbc);

        // Hover + clique
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(COR_CARD_HOVER); }
            @Override public void mouseExited(MouseEvent e)  {
                card.setBackground(pd == pedidoSelecionado ? COR_CARD_HOVER : Color.WHITE);
            }
            @Override public void mouseClicked(MouseEvent e) { selecionarPedido(pd); }
        });

        return card;
    }

    // ─────────────────────────────────────────────────────────
    //  PLACEHOLDER
    // ─────────────────────────────────────────────────────────
    private JPanel criarPlaceholder() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COR_CINZA_BG);
        p.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1, true));

        JLabel dica = new JLabel(
            "<html><div style='text-align:center;color:#bbb'>" +
            "👆<br><br>Selecione um pedido<br>na lista ao lado<br>" +
            "para ver os detalhes<br>e gerenciar a produção" +
            "</div></html>"
        );
        dica.setFont(new Font("Arial", Font.PLAIN, 15));
        dica.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(dica);
        return p;
    }

    // ─────────────────────────────────────────────────────────
    //  DETALHE DO PEDIDO
    // ─────────────────────────────────────────────────────────
    private JPanel criarPainelDetalhe(Pedido pd) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        // ─ Título
        JLabel lblTitulo = new JLabel("Detalhes do Pedido");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 30));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(8));

        // Badge de status
        int st = safeEstado(pd);
        JLabel badge = new JLabel("  " + ICON_ESTADO[st] + "  " + LABEL_ESTADO[st] + "  ");
        badge.setFont(new Font("Arial", Font.BOLD, 13));
        badge.setOpaque(true);
        badge.setBackground(BG_ESTADO[st]);
        badge.setForeground(FG_ESTADO[st]);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(badge);
        painel.add(Box.createVerticalStrut(20));

        // ─ Seções de dados
        painel.add(criarSecao("🍔  Pedido", new String[][]{
            {"Item",     pd.getComida() != null ? pd.getComida() : "—"},
            {"Horário",  pd.getHora_Entregue() != null ? pd.getHora_Entregue() : "—"},
            {"Status",   LABEL_ESTADO[st]}
        }));
        painel.add(Box.createVerticalStrut(14));

        String nomeCliente = pd.getCliente() != null ? pd.getCliente().getNome() : "Não informado";
        painel.add(criarSecao("👤  Cliente", new String[][]{
            {"Nome",       nomeCliente},
            {"Endereço",   "Rua das Flores, 55"},
            {"Pagamento",  "Pago via App"}
        }));
        painel.add(Box.createVerticalStrut(24));

        // ─ Botões de ação contextual
        JPanel btnArea = criarBotoesAcao(pd);
        btnArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(btnArea);

        painel.add(Box.createVerticalGlue());
        return painel;
    }

    /** Botões diferentes para cada estado do pedido */
    private JPanel criarBotoesAcao(Pedido pd) {
        JPanel area = new JPanel();
        area.setLayout(new BoxLayout(area, BoxLayout.Y_AXIS));
        area.setOpaque(false);

        int st = safeEstado(pd);

        if (st == ESTADO_RECEBIDO) {
            // Restaurante pode aceitar ou rejeitar o pedido
            JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

            BotaoArredondado btnAceitar = new BotaoArredondado("\uD83C\uDF73 Iniciar Produção", 20, COR_AZUL, 13);
            btnAceitar.addActionListener(e -> mudarEstado(pd, ESTADO_PRODUCAO));

            BotaoArredondado btnRecusar = new BotaoArredondado("✗  Recusar Pedido", 20, COR_PRIMARIA, 13);
            btnRecusar.addActionListener(e -> recusarPedido(pd));

            row.add(btnAceitar);
            row.add(btnRecusar);
            area.add(row);

        } else if (st == ESTADO_PRODUCAO) {
            // Sinaliza que o prato ficou pronto
            BotaoArredondado btnPronto = new BotaoArredondado("✅  Marcar como Pronto", 20, COR_VERDE, 14);
            btnPronto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            btnPronto.addActionListener(e -> mudarEstado(pd, ESTADO_PRONTO));
            area.add(btnPronto);

        } else if (st == ESTADO_PRONTO) {
            // Aguardando entregador — apenas informativo
            JLabel info = new JLabel("⏳  Aguardando entregador retirar o pedido...");
            info.setFont(new Font("Arial", Font.ITALIC, 13));
            info.setForeground(new Color(100, 100, 100));
            info.setAlignmentX(Component.LEFT_ALIGNMENT);
            area.add(info);
            area.add(Box.createVerticalStrut(12));

            BotaoArredondado btnFinalizar = new BotaoArredondado("📦  Marcar como Entregue", 20, new Color(100, 100, 100), 13);
            btnFinalizar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            btnFinalizar.addActionListener(e -> mudarEstado(pd, ESTADO_FINALIZADO));
            area.add(btnFinalizar);

        } else {
            // Finalizado
            JLabel info = new JLabel("✅  Pedido concluído e entregue ao cliente.");
            info.setFont(new Font("Arial", Font.ITALIC, 13));
            info.setForeground(COR_VERDE);
            info.setAlignmentX(Component.LEFT_ALIGNMENT);
            area.add(info);
        }

        return area;
    }

    // ─────────────────────────────────────────────────────────
    //  HELPER DE SECÃO (idêntico ao padrão do projeto)
    // ─────────────────────────────────────────────────────────
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
        lblTit.setAlignmentX(Component.LEFT_ALIGNMENT);
        s.add(lblTit);
        s.add(Box.createVerticalStrut(8));

        for (String[] par : pares) {
            JPanel linha = new JPanel(new BorderLayout(8, 0));
            linha.setOpaque(false);
            linha.setAlignmentX(Component.LEFT_ALIGNMENT);
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

    // ─────────────────────────────────────────────────────────
    //  LÓGICA
    // ─────────────────────────────────────────────────────────
    private void selecionarPedido(Pedido pd) {
        pedidoSelecionado = pd;
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = criarPainelDetalhe(pd);
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }

    private void mudarEstado(Pedido pd, int novoEstado) {
        pd.setEstadoRestaurante(novoEstado);
        String[] msgs = {
            "", "Pedido recebido.", "Produção iniciada!",
            "Prato pronto! Aguardando entregador.", "Pedido finalizado."
        };
        JOptionPane.showMessageDialog(
            SwingUtilities.getWindowAncestor(this),
            msgs[novoEstado], "Status Atualizado", JOptionPane.INFORMATION_MESSAGE
        );
        sist.configuraTela(new TelaPedidosRestaurante(sist));
    }

    private void recusarPedido(Pedido pd) {
        int r = JOptionPane.showConfirmDialog(
            SwingUtilities.getWindowAncestor(this),
            "Tem certeza que deseja recusar este pedido?\nO cliente será notificado.",
            "Recusar Pedido", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        );
        if (r == JOptionPane.YES_OPTION) {
            Dados.listaPedidos.remove(pd);
            sist.configuraTela(new TelaPedidosRestaurante(sist));
        }
    }

    /** Lê o estadoRestaurante com fallback seguro */
    private int safeEstado(Pedido pd) {
        int e = pd.getEstadoRestaurante();
        return Math.max(1, Math.min(e, 4));
    }

    // ─────────────────────────────────────────────────────────
    //  DADOS DEMO
    // ─────────────────────────────────────────────────────────
    private List<Pedido> criarPedidosDemo() {
        Restaurante rest = new Restaurante("aa","",1);
        Produto pr1 = new Produto(); pr1.setNome("X-Burguer Duplo");
        Produto pr2 = new Produto(); pr2.setNome("Pizza Margherita");
        Produto pr3 = new Produto(); pr3.setNome("Frango Grelhado");
        Produto pr4 = new Produto(); pr4.setNome("Combo Família");

        Cliente cl1 = new Cliente("c1@mail.com", "João Silva",   "");
        Cliente cl2 = new Cliente("c2@mail.com", "Maria Souza",  "");
        Cliente cl3 = new Cliente("c3@mail.com", "Pedro Lima",   "");
        Cliente cl4 = new Cliente("c4@mail.com", "Ana Ferreira", "");

        Pedido p1 = new Pedido(pr1, "19:30", null, null, rest, cl1); p1.setEstadoRestaurante(ESTADO_RECEBIDO);
        Pedido p2 = new Pedido(pr2, "19:45", null, null, rest, cl2); p2.setEstadoRestaurante(ESTADO_PRODUCAO);
        Pedido p3 = new Pedido(pr3, "20:10", null, null, rest, cl3); p3.setEstadoRestaurante(ESTADO_PRONTO);
        Pedido p4 = new Pedido(pr4, "18:50", null, null, rest, cl4); p4.setEstadoRestaurante(ESTADO_FINALIZADO);

        return java.util.Arrays.asList(p1, p2, p3, p4);
    }
}
