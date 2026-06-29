package view;

import model.Pedido;
import repositorio.Dados;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * TelaPedidosEntregador — formato adaptável.
 * Se houver pedido aceito, exibe apenas ele em tela cheia.
 * Caso contrário, exibe a lista split (40/60).
 */
public class TelaPedidosEntregador extends TelaMenu {

    private final Telabase sist;

    private JPanel painelDetalhe;
    private JPanel corpoPrincipal;
    private Pedido pedidoSelecionado;

    // Cores padrão do projeto
    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);
    private static final Color COR_VERDE      = new Color(46, 174, 82);
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);
    private static final Color COR_BORDA      = new Color(230, 230, 230);
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);

    public TelaPedidosEntregador(Telabase sist) {
        super(sist);
        this.sist = sist;

        // Container raiz desta tela
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

        // VERIFICAÇÃO PRINCIPAL: Existe algum pedido em rota (aceito)?
        Pedido pedidoAtivo = obterPedidoEmRota();

        if (pedidoAtivo != null) {
            // ─────────────────────────────────────────────────────────
            // MODO PEDIDO ATIVO: Exibe apenas o pedido aceito (Tela Cheia)
            // ─────────────────────────────────────────────────────────
            pedidoSelecionado = pedidoAtivo;
            corpoPrincipal = new JPanel(new BorderLayout());
            corpoPrincipal.setBackground(Color.WHITE);
            // Adiciona margens generosas nas laterais para o painel não ficar esticado horizontalmente
            corpoPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 150, 20, 150));

            painelDetalhe = criarPainelDetalhePreenchido(pedidoAtivo);
            corpoPrincipal.add(painelDetalhe, BorderLayout.CENTER);
        } else {
            // ─────────────────────────────────────────────────────────
            // MODO LISTA: Lista (esquerda - 40%) + Detalhe/Placeholder (direita - 60%)
            // ─────────────────────────────────────────────────────────
            corpoPrincipal = new JPanel(new GridLayout(1, 2, 20, 0));
            corpoPrincipal.setBackground(Color.WHITE);
            corpoPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            corpoPrincipal.add(criarPainelLista());

            painelDetalhe = criarPainelDetalhePlaceholder();
            corpoPrincipal.add(painelDetalhe);
        }

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

        // Altera o título dinamicamente baseado no estado do entregador
        String strTitulo = (obterPedidoEmRota() != null) ? "🚚  Sua Entrega em Andamento" : "📦  Pedidos Disponíveis para Entrega";
        Texto titulo = new Texto(strTitulo);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        p.add(titulo, BorderLayout.WEST);

        BotaoArredondado btnAtualizar = new BotaoArredondado("↻  Atualizar", 20, COR_PRIMARIA, 14);
        btnAtualizar.setPreferredSize(new Dimension(130, 38));
        btnAtualizar.addActionListener(e -> atualizarTela());
        p.add(btnAtualizar, BorderLayout.EAST);

        return p;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DA LISTA DE PEDIDOS
    // ─────────────────────────────────────────────────────────
    private JPanel criarPainelLista() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JLabel lblSub = new JLabel("Toque em um pedido para ver os detalhes");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);
        lblSub.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        wrapper.add(lblSub, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);

        List<Pedido> pedidos = obterPedidosDisponiveis();

        if (pedidos.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum pedido disponível no momento.");
            vazio.setFont(new Font("Arial", Font.ITALIC, 14));
            vazio.setForeground(Color.GRAY);
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            lista.add(Box.createVerticalStrut(40));
            lista.add(vazio);
        } else {
            for (Pedido p : pedidos) {
                lista.add(criarCardPedido(p));
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

    private JPanel criarCardPedido(Pedido p) {
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

        JLabel lblIcon = new JLabel(p.getEstado() == 1 ? "🕐" : "🚚");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 28));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 14);
        card.add(lblIcon, gbc);

        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 3, 0);

        JLabel lblNome = new JLabel(p.getComida() != null ? p.getComida() : "Produto não informado");
        lblNome.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(lblNome, gbc);

        String[] textos = {"", "Pendente", "Em rota", "Finalizado"};
        Color[] cores   = {Color.GRAY, new Color(200, 130, 0), COR_PRIMARIA, COR_VERDE};
        int st = Math.max(1, Math.min(p.getEstado(), 3));
        JLabel lblStatus = new JLabel("● " + textos[st]);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        lblStatus.setForeground(cores[st]);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 10, 3, 0);
        card.add(lblStatus, gbc);

        String hora = p.getHora_Entregue() != null ? p.getHora_Entregue() : "--:--";
        JLabel lblDetalhe = new JLabel("Entrega às " + hora + "  •  Clique para ver detalhes");
        lblDetalhe.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDetalhe.setForeground(Color.GRAY);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(lblDetalhe, gbc);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(COR_CARD_HOVER); }
            @Override public void mouseExited(MouseEvent e)  {
                card.setBackground(p == pedidoSelecionado ? COR_CARD_HOVER : Color.WHITE);
            }
            @Override public void mouseClicked(MouseEvent e) { selecionarPedido(p); }
        });

        return card;
    }

    private JPanel criarPainelDetalhePlaceholder() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COR_CINZA_BG);
        p.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1, true));

        JLabel lblDica = new JLabel(
                "<html><div style='text-align:center; color:#bbb'>" +
                        "👆<br><br>Selecione um pedido<br>na lista ao lado" +
                        "</div></html>"
        );
        lblDica.setFont(new Font("Arial", Font.PLAIN, 16));
        lblDica.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(lblDica);
        return p;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DE DETALHE COMPLETO
    // ─────────────────────────────────────────────────────────
    private JPanel criarPainelDetalhePreenchido(Pedido p) {
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
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(6));

        String[] textos = {"", "Pendente", "Em rota", "Finalizado"};
        Color[] bgCores = {Color.GRAY, new Color(255, 243, 205), new Color(255, 228, 230), new Color(212, 237, 218)};
        Color[] fgCores = {Color.GRAY, new Color(133, 100, 4), COR_PRIMARIA, new Color(21, 87, 36)};
        int st = Math.max(1, Math.min(p.getEstado(), 3));

        JLabel badge = new JLabel("  " + textos[st] + "  ");
        badge.setFont(new Font("Arial", Font.BOLD, 12));
        badge.setOpaque(true);
        badge.setBackground(bgCores[st]);
        badge.setForeground(fgCores[st]);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(badge);
        painel.add(Box.createVerticalStrut(20));

        // Seção: Produto
        painel.add(criarSecao("🍔  Produto", new String[][]{
                {"Item",    p.getComida() != null ? p.getComida() : "—"},
                {"Horário", p.getHora_Entregue() != null ? p.getHora_Entregue() : "—"}
        }));
        painel.add(Box.createVerticalStrut(14));

        // Seção: Cliente (Dados revelados se o estado for 'Em rota')
        boolean aceito = (p.getEstado() == 2);
        String nomeCliente = aceito ? (p.getCliente() != null ? p.getCliente().getNome() : "João Silva") : "Disponível após aceitar";
        String endCliente  = aceito ? "Rua das Oliveiras, 452 - Apt 12" : "Disponível após aceitar";
        String pagCliente  = aceito ? "Pago via App (Cartão)" : "Disponível após aceitar";

        painel.add(criarSecao("👤  Cliente", new String[][]{
                {"Nome",      nomeCliente},
                {"Endereço",   endCliente},
                {"Pagamento",  pagCliente}
        }));
        painel.add(Box.createVerticalStrut(14));

        // Seção: Restaurante
        String nomeRest = aceito ? "📍 Central Burger & Cia" : "Disponível após aceitar";
        String endRest  = aceito ? "Av. Getúlio Vargas, 1060" : "Disponível após aceitar";

        painel.add(criarSecao("🏪  Restaurante", new String[][]{
                {"Nome",       nomeRest},
                {"Endereço",    endRest},
                {"Avaliação",   "★★★★★"}
        }));

        painel.add(Box.createVerticalGlue());
        painel.add(Box.createVerticalStrut(24));

        // Botões de ação dinâmicos baseados no estado do pedido
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (p.getEstado() == 1) {
            // Se o pedido está pendente, mostra opções de aceitar/recusar
            BotaoArredondado btnAceitar = new BotaoArredondado("✓  Aceitar Pedido", 20, COR_VERDE, 14);
            btnAceitar.addActionListener(e -> aceitarPedido(p));

            BotaoArredondado btnRecusar = new BotaoArredondado("✗  Recusar", 20, new Color(160, 160, 160), 14);
            btnRecusar.addActionListener(e -> recusarPedido(p));

            btnRow.add(btnAceitar);
            btnRow.add(btnRecusar);
        } else if (p.getEstado() == 2) {
            // Se o pedido já está aceito, mostra opções de concluir ou cancelar corrida
            BotaoArredondado btnConcluir = new BotaoArredondado("✓  Concluir Entrega", 20, COR_VERDE, 14);
            btnConcluir.addActionListener(e -> concluirEntrega(p));

            BotaoArredondado btnCancelar = new BotaoArredondado("✗  Cancelar Corrida", 20, COR_PRIMARIA, 14);
            btnCancelar.addActionListener(e -> cancelarCorrida(p));

            btnRow.add(btnConcluir);
            btnRow.add(btnCancelar);
        }

        painel.add(btnRow);
        return painel;
    }

    private JPanel criarSecao(String titulo, String[][] pares) {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(COR_CINZA_BG);
        s.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16) // Aumentado espaçamento interno vertical
        ));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Define dimensões fixas saudáveis para impedir o efeito sanfona do BoxLayout anterior
        int alturaCalculada = 45 + (pares.length * 24);
        s.setPreferredSize(new Dimension(320, alturaCalculada));
        s.setMinimumSize(new Dimension(250, alturaCalculada));

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
            chave.setPreferredSize(new Dimension(90, 20));

            JLabel valor = new JLabel(par[1]);
            valor.setFont(new Font("Arial", Font.PLAIN, 13));
            valor.setForeground(new Color(40, 40, 40));

            linha.add(chave, BorderLayout.WEST);
            linha.add(valor, BorderLayout.CENTER);

            s.add(linha);
            s.add(Box.createVerticalStrut(6)); // Espaçamento fixo seguro entre dados
        }
        return s;
    }

    // ─────────────────────────────────────────────────────────
    //  LÓGICA E REGRAS DE NEGÓCIO
    // ─────────────────────────────────────────────────────────
    private void selecionarPedido(Pedido p) {
        pedidoSelecionado = p;
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = criarPainelDetalhePreenchido(p);
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }

    private void aceitarPedido(Pedido p) {
        if (p.getEstado() == 1) {
            p.setEstado(2); // Muda para "Em rota"
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Pedido aceito!\nO painel de entrega foi iniciado.",
                    "Pedido em Rota", JOptionPane.INFORMATION_MESSAGE);
            atualizarTela(); // Recarrega o construtor que aplicará o modo tela cheia
        } else {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Este pedido já está em andamento.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void recusarPedido(Pedido p) {
        Object[] ops = {"Sim, recusar", "Cancelar"};
        int r = JOptionPane.showOptionDialog(
                SwingUtilities.getWindowAncestor(this),
                "Deseja recusar este pedido?", "Recusar pedido",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                null, ops, ops[1]);
        if (r == JOptionPane.YES_OPTION) {
            p.setEstado(1);
            atualizarTela();
        }
    }

    private void concluirEntrega(Pedido p) {
        p.setEstado(3); // Finalizado
        JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                "Entrega concluída com sucesso! Bom trabalho.",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        atualizarTela(); // Voltará ao modo lista original pois não há mais pedidos ativos
    }

    private void cancelarCorrida(Pedido p) {
        int r = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Tem certeza que deseja desistir desta entrega?\nO pedido voltará para a lista geral.",
                "Cancelar Corrida", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            p.setEstado(1); // Retorna para "Pendente"
            atualizarTela();
        }
    }

    private void atualizarTela() {
        pedidoSelecionado = null;
        if (sist != null) {
            sist.configuraTela(new TelaPedidosEntregador(sist));
        }
    }

    // ─────────────────────────────────────────────────────────
    //  MANIPULAÇÃO DE DADOS
    // ─────────────────────────────────────────────────────────
    private Pedido obterPedidoEmRota() {
        // Procura se o entregador possui alguma entrega ativa pendente de conclusão
        if (Dados.listaPedidos != null) {
            for (Pedido p : Dados.listaPedidos) {
                if (p.getEstado() == 2) {
                    return p;
                }
            }
        }
        return null;
    }

    private List<Pedido> obterPedidosDisponiveis() {
        // Bug de persistência corrigido para modo Demo: Salva no repositório estático se vazio
        if (Dados.listaPedidos == null || Dados.listaPedidos.isEmpty()) {
            Dados.listaPedidos = new java.util.ArrayList<>(criarPedidosDemo());
        }

        java.util.List<Pedido> filtrado = new java.util.ArrayList<>();
        for (Pedido p : Dados.listaPedidos) {
            if (p.getEstado() == 1) { // Apenas os pendentes entram na lista de ofertas
                filtrado.add(p);
            }
        }
        return filtrado;
    }

    private List<Pedido> criarPedidosDemo() {
        model.Produto pr1 = new model.Produto(); pr1.setNome("X-Burguer Duplo");
        model.Produto pr2 = new model.Produto(); pr2.setNome("Pizza Margherita");
        model.Produto pr3 = new model.Produto(); pr3.setNome("Frango Grelhado");

        model.Restaurante rest = new model.Restaurante(1,"","",1);
        model.Cliente cl1 = new model.Cliente("c1@mail.com",  "João Silva", "");
        model.Cliente cl2 = new model.Cliente("c2@mail.com",  "Maria Souza", "");
        model.Cliente cl3 = new model.Cliente("c3@mail.com",  "Pedro Lima", "");

        // Todos os demos iniciam com estado 1 (Pendente) para possibilitar o teste de clique
        Pedido p1 = new Pedido(pr1, "19:30", null, null, rest, cl1); p1.setEstado(1);
        Pedido p2 = new Pedido(pr2, "19:45", null, null, rest, cl2); p2.setEstado(1);
        Pedido p3 = new Pedido(pr3, "20:10", null, null, rest, cl3); p3.setEstado(1);

        return java.util.Arrays.asList(p1, p2, p3);
    }
}