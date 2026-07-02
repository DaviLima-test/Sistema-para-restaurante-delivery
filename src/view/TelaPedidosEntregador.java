package view;

import model.Produto;
import util.RemoveEmoji;
import model.Pedido;
import bd.BancoDados; // IMPORTANTE: Importando a classe do diretório correto

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * TelaPedidosEntregador — formato adaptável conectado ao banco de dados.
 */
public class TelaPedidosEntregador extends TelaMenu {

    private final Telabase sist;

    private JPanel painelDetalhe;
    private JPanel corpoPrincipal;
    private Pedido pedidoSelecionado;

    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);
    private static final Color COR_VERDE      = new Color(46, 174, 82);
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);
    private static final Color COR_BORDA      = new Color(230, 230, 230);
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);

    public TelaPedidosEntregador(Telabase sist) {
        super(sist);
        this.sist = sist;

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

        // ALTERADO: Busca no banco se este entregador já possui alguma corrida aceita
        Pedido pedidoAtivo = obterPedidoEmRota();

        if (pedidoAtivo != null) {
            pedidoSelecionado = pedidoAtivo;
            corpoPrincipal = new JPanel(new BorderLayout());
            corpoPrincipal.setBackground(Color.WHITE);
            corpoPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 150, 20, 150));

            painelDetalhe = criarPainelDetalhePreenchido(pedidoAtivo);
            RemoveEmoji.aplicar(painelDetalhe);
            corpoPrincipal.add(painelDetalhe, BorderLayout.CENTER);
        } else {
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

    private JPanel criarCabecalho() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(16, 30, 16, 30)
        ));

        String strTitulo = (obterPedidoEmRota() != null) ? "🚚  Sua Entrega em Andamento" : "📦  Pedidos Disponíveis para Entrega";
        Texto titulo = new Texto(strTitulo);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        p.add(titulo, BorderLayout.WEST);

        BotaoArredondado btnAtualizar = new BotaoArredondado("↻  Atualizar", 20, COR_PRIMARIA, 14);
        btnAtualizar.setPreferredSize(new Dimension(130, 38));
        btnAtualizar.addActionListener(e -> atualizarTela());
        p.add(btnAtualizar, BorderLayout.EAST);

        return p;
    }

    private JPanel criarPainelLista() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JLabel lblSub = new JLabel("Toque em um pedido para ver os detalhes");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);
        wrapper.add(lblSub, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);

        // ALTERADO: Busca os pedidos direto do banco de dados
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

        String icone = p.getEstado() == 2 ? "🍳" : (p.getEstado() == 3 ? "🕐" : "🚚");
        JLabel lblIcon = new JLabel(icone);
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 28));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 14);
        card.add(lblIcon, gbc);

        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 3, 0);

        // ALTERADO: Concatena o nome dos itens do ArrayList para exibir no Card
        StringBuilder nomesProdutos = new StringBuilder();
        for (Produto prod : p.getComidas()) {
            if (nomesProdutos.length() > 0) nomesProdutos.append(", ");
            nomesProdutos.append(prod.getNome());
        }

        JLabel lblNome = new JLabel(nomesProdutos.length() > 0 ? nomesProdutos.toString() : "Produtos");
        lblNome.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(lblNome, gbc);

        String[] textos = {"", "Recebido", "Em preparo", "Disponível", "Em rota", "Finalizado"};
        Color[] cores   = {Color.GRAY, Color.GRAY, new Color(29, 78, 216), new Color(200, 130, 0), COR_PRIMARIA, COR_VERDE};
        int st = Math.max(1, Math.min(p.getEstado(), 5));
        JLabel lblStatus = new JLabel("● " + textos[st]);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        lblStatus.setForeground(cores[st]);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        card.add(lblStatus, gbc);

        String hora = p.getHora_Entregue() != null ? p.getHora_Entregue() : "Imediato";
        JLabel lblDetalhe = new JLabel("Entrega: " + hora + "  •  Clique para ver detalhes");
        lblDetalhe.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDetalhe.setForeground(Color.GRAY);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
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
        JLabel lblDica = new JLabel("<html><div style='text-align:center; color:#bbb'>👆<br><br>Selecione um pedido<br>na lista ao lado</div></html>");
        lblDica.setFont(new Font("Arial", Font.PLAIN, 16));
        p.add(lblDica);
        return p;
    }

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
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(6));

        String[] textos = {"", "Recebido", "Em preparo", "Disponível", "Em rota", "Finalizado"};
        Color[] bgCores = {Color.GRAY, Color.LIGHT_GRAY, new Color(219, 234, 254), new Color(255, 243, 205), new Color(255, 228, 230), new Color(212, 237, 218)};
        Color[] fgCores = {Color.GRAY, Color.GRAY, new Color(29, 78, 216), new Color(133, 100, 4), COR_PRIMARIA, new Color(21, 87, 36)};
        int st = Math.max(1, Math.min(p.getEstado(), 5));

        JLabel badge = new JLabel("  " + textos[st] + "  ");
        badge.setFont(new Font("Arial", Font.BOLD, 12));
        badge.setOpaque(true);
        badge.setBackground(bgCores[st]);
        badge.setForeground(fgCores[st]);
        painel.add(badge);
        painel.add(Box.createVerticalStrut(20));

        // ALTERADO: Agrupa o nome das comidas do ArrayList para a seção visual
        StringBuilder listaItens = new StringBuilder();
        for (Produto prod : p.getComidas()) {
            if (listaItens.length() > 0) listaItens.append(", ");
            listaItens.append(prod.getNome());
        }

        // Seção: Produto
        painel.add(criarSecao("🍔  Produto", new String[][]{
                {"Itens",   listaItens.toString()},
                {"Horário", p.getHora_Entregue() != null ? p.getHora_Entregue() : "Imediato"}
        }));
        painel.add(Box.createVerticalStrut(14));

        boolean aceito = (p.getEstado() == 4);
        String nomeCliente = aceito ? (p.getCliente() != null ? p.getCliente().getNome() : "Cliente") : "Disponível após aceitar";
        String endCliente  = aceito ? "Rua das Oliveiras, 452 - Apt 12" : "Disponível após aceitar";
        String pagCliente  = aceito ? "Pago via App (Cartão)" : "Disponível após aceitar";

        painel.add(criarSecao("👤  Cliente", new String[][]{
                {"Identificador", nomeCliente},
                {"Endereço",     endCliente},
                {"Pagamento",    pagCliente}
        }));
        painel.add(Box.createVerticalStrut(14));

        // ALTERADO: Consome dados reais do objeto Restaurante mapeado
        String nomeRest = aceito ? (p.getRestaurante() != null ? p.getRestaurante().getNome() : "Restaurante") : "Disponível após aceitar";
        String endRest  = aceito ? (p.getRestaurante() != null ? p.getRestaurante().getLocalizacao() : "Localização") : "Disponível após aceitar";

        painel.add(criarSecao("🏪  Restaurante", new String[][]{
                {"Nome",       nomeRest},
                {"Endereço",    endRest}
        }));

        painel.add(Box.createVerticalGlue());
        painel.add(Box.createVerticalStrut(24));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        if (p.getEstado() == 2) { // Ainda em produção no restaurante
            JLabel info = new JLabel("O restaurante ainda está preparando este pedido.");
            info.setFont(new Font("Arial", Font.ITALIC, 13));
            info.setForeground(new Color(100, 100, 100));
            painel.add(info);
            return painel;
        } else if (p.getEstado() == 3) { // Pronto e disponível para aceite
            BotaoArredondado btnAceitar = new BotaoArredondado("✓  Aceitar Pedido", 20, COR_VERDE, 14);
            btnAceitar.addActionListener(e -> aceitarPedido(p));

            BotaoArredondado btnRecusar = new BotaoArredondado("✗  Recusar", 20, new Color(160, 160, 160), 14);
            btnRecusar.addActionListener(e -> recusarPedido(p));

            btnRow.add(btnAceitar);
            btnRow.add(btnRecusar);
        } else if (p.getEstado() == 4) {
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
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        int alturaCalculada = 45 + (pares.length * 24);
        s.setPreferredSize(new Dimension(320, alturaCalculada));
        s.setMinimumSize(new Dimension(250, alturaCalculada));

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
            chave.setPreferredSize(new Dimension(90, 20));

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

    private void selecionarPedido(Pedido p) {
        pedidoSelecionado = p;
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = criarPainelDetalhePreenchido(p);
        RemoveEmoji.aplicar(painelDetalhe);
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }

    // ─────────────────────────────────────────────────────────
    //  LÓGICA ALTERADA PARA ENVIAR REQUISIÇÕES AO BANCO (bd)
    // ─────────────────────────────────────────────────────────
    private void aceitarPedido(Pedido p) {
        // Atribui este entregador ao pedido e muda o estado para 4 (Em rota), de forma atômica
        int idEntregador = bd.BancoDados.obterIdUsuarioLogado();
        boolean sucesso = idEntregador != -1 && bd.BancoDados.aceitarEntrega(p.getId(), idEntregador);

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Pedido aceito!\nO painel de entrega foi iniciado.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            atualizarTela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao aceitar pedido no servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recusarPedido(Pedido p) {
        atualizarTela(); // Apenas limpa a seleção visual
    }

    private void concluirEntrega(Pedido p) {
        // Altera o estado para 5 (Entregue/Finalizado) no banco de dados
        boolean sucesso = bd.BancoDados.atualizarEstadoPedido(p.getId(), 5);

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Entrega concluída com sucesso! Bom trabalho.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            atualizarTela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao finalizar entrega no banco.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarCorrida(Pedido p) {
        int r = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja desistir?\nO pedido voltará para a lista.", "Aviso", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            // Libera o pedido: volta a status 3 (Pronto) sem entregador atribuído
            bd.BancoDados.liberarEntrega(p.getId());
            atualizarTela();
        }
    }

    private void atualizarTela() {
        pedidoSelecionado = null;
        if (sist != null) {
            sist.configuraTela(new TelaPedidosEntregador(sist));
        }
    }

    private Pedido obterPedidoEmRota() {
        // Busca se o entregador logado já possui um pedido em rota (status 4) atribuído a ele
        int idEntregador = bd.BancoDados.obterIdUsuarioLogado();
        if (idEntregador == -1) return null;
        return bd.BancoDados.obterPedidoAtivoEntregador(idEntregador);
    }

    private List<Pedido> obterPedidosDisponiveis() {
        // Pedidos prontos (status 3) e ainda sem entregador atribuído
        return bd.BancoDados.obterPedidosDisponiveisEntrega();
    }
}