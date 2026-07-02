package view;

import model.Produto;
import util.RemoveEmoji;
import model.Pedido;
import bd.BancoDados; // IMPORTANTE: Apontando para o pacote correto do banco

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * TelaPedidosRestaurante — Pedidos recebidos pelo restaurante conectados ao Banco de Dados.
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
    static final int ESTADO_EM_ROTA    = 4;
    static final int ESTADO_FINALIZADO = 5;

    private static final String[] LABEL_ESTADO = {
            "", "Recebido", "Em Produção", "Pronto p/ Entrega", "Em Rota", "Entregue"
    };
    private static final Color[] BG_ESTADO = {
            Color.GRAY,
            new Color(255, 243, 205),   // amarelo suave
            new Color(219, 234, 254),   // azul suave
            new Color(209, 250, 229),   // verde suave
            new Color(255, 228, 230),   // rosa suave (em rota)
            new Color(243, 244, 246)    // cinza
    };
    private static final Color[] FG_ESTADO = {
            Color.GRAY,
            new Color(133, 100,   4),   // amarelo escuro
            new Color( 29,  78, 216),   // azul escuro
            new Color( 21,  87,  36),   // verde escuro
            new Color(234,  16,  34),   // vermelho (em rota)
            new Color( 80,  80,  80)    // cinza
    };

    private static final String[] ICON_ESTADO = {
            "", "", "", "", "", ""
    };

    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);
    private static final Color COR_VERDE      = new Color(46, 174, 82);
    private static final Color COR_AZUL       = new Color(29, 78, 216);
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);
    private static final Color COR_BORDA      = new Color(230, 230, 230);
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);

    /** Descobre o ID do restaurante gerenciado pelo usuário logado (ou -1 se não encontrado). */
    private static int idRestauranteAtual() {
        String[] dados = bd.BancoDados.buscarRestaurantePorGerente(Telabase.getLogin().GetEmail());
        if (dados == null || dados.length == 0) return -1;
        try {
            return Integer.parseInt(dados[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static List<Pedido> pedidosDoRestaurante() {
        int idRest = idRestauranteAtual();
        if (idRest == -1) return new ArrayList<>();
        return bd.BancoDados.obterPedidosPorRestaurante(idRest);
    }

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

        Texto titulo = new Texto("Pedidos do Restaurante");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        titulos.add(titulo);

        // ALTERADO: Estatísticas vindas em tempo real do banco de dados
        List<Pedido> totalPedidos = pedidosDoRestaurante();
        long emProducao = totalPedidos.stream().filter(pd -> pd.getEstado() == ESTADO_PRODUCAO).count();
        long recebidos  = totalPedidos.stream().filter(pd -> pd.getEstado() == ESTADO_RECEBIDO).count();

        JLabel sub = new JLabel(recebidos + " novos  •  " + emProducao + " em produção");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);
        titulos.add(sub);

        p.add(titulos, BorderLayout.WEST);

        BotaoArredondado btnAtualizar = new BotaoArredondado("Atualizar", 20, COR_PRIMARIA, 14);
        btnAtualizar.setPreferredSize(new Dimension(130, 42));
        btnAtualizar.addActionListener(e -> atualizarTela());
        p.add(btnAtualizar, BorderLayout.EAST);

        return p;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DA LISTA
    // ─────────────────────────────────────────────────────────
    private JPanel criarPainelLista() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tabs.setOpaque(false);
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        String[] filtros = {"Todos", "Novos", "Em Produção", "Prontos"};
        for (String f : filtros) {
            BotaoArredondado btn = new BotaoArredondado(f, 20,
                    f.equals("Todos") ? COR_PRIMARIA : new Color(200, 200, 200), 12);
            btn.setPreferredSize(new Dimension(90, 30));
            tabs.add(btn);
        }
        wrapper.add(tabs, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);
        lista.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // ALTERADO: Carregando dados atualizados do banco
        List<Pedido> pedidos = pedidosDoRestaurante();
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
        scrollLista.setPreferredSize(new Dimension(0, 500));
        wrapper.add(scrollLista, BorderLayout.CENTER);

        return wrapper;
    }

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

        // ALTERADO: Agrupa os nomes das comidas da lista (ArrayList)
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

        // ALTERADO: Chamando getNome() do cliente conforme especificado
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

        // ALTERADO: Agrupa strings de pratos para a seção resumida de detalhes
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

        // ALTERADO: Chamando getNome() do cliente
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

    private void selecionarPedido(Pedido pd) {
        pedidoSelecionado = pd;
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = criarPainelDetalhe(pd);
        RemoveEmoji.aplicar(painelDetalhe);
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }

    // ─────────────────────────────────────────────────────────
    //  LÓGICA ALTERADA PARA INTERAGIR COM O BANCO DE DADOS (bd)
    // ─────────────────────────────────────────────────────────
    private void mudarEstado(Pedido pd, int novoEstado) {
        // ALTERADO: Atualiza o status do pedido de forma persistente no banco de dados, pelo ID do pedido
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

    private void recusarPedido(Pedido pd) {
        int r = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja recusar este pedido?\nO cliente será notificado.",
                "Recusar Pedido", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (r == JOptionPane.YES_OPTION) {
            // ALTERADO: Remove o pedido do banco pelo seu ID
            boolean sucesso = bd.BancoDados.cancelarPedidoNoBanco(pd.getId());
            if (sucesso) {
                atualizarTela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao processar recusa no banco.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int safeEstado(Pedido pd) {
        int e = pd.getEstado(); // Coleta o estado numérico padrão unificado
        return Math.max(1, Math.min(e, 5));
    }

    private void atualizarTela() {
        pedidoSelecionado = null;
        if (sist != null) {
            sist.configuraTela(new TelaPedidosRestaurante(sist));
        }
    }
}