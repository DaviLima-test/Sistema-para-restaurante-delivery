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
 * TelaPedidosEntregador — formato adaptável conectado ao banco de dados.
 * @version 1.2
 */
public class TelaPedidosEntregador extends TelaMenu {

    private final Telabase sist;

    private JPanel painelDetalhe;
    private JPanel corpoPrincipal;
    private Pedido pedidoSelecionado;

    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);
    private static final Color COR_VERDE      = new Color(46, 174, 82);
    private static final Color COR_AMARELO    = new Color(255, 180, 0);
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);
    private static final Color COR_BORDA      = new Color(230, 230, 230);
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);

    public TelaPedidosEntregador(Telabase sist) {
        super(sist);
        this.sist = sist;

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

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

        // Subpainel para alinhar múltiplos botões à direita organizadamente
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        painelBotoes.setOpaque(false);

        // Novo Botão: Acessa o histórico e calcula os rendimentos financeiros do entregador
        BotaoArredondado btnGanhos = new BotaoArredondado("📊 Meus Ganhos", 20, COR_AMARELO, 14);
        btnGanhos.setPreferredSize(new Dimension(150, 38));
        btnGanhos.addActionListener(e -> calcularEExibirGanhos());

        BotaoArredondado btnAtualizar = new BotaoArredondado("  Atualizar", 20, COR_PRIMARIA, 14);
        btnAtualizar.setPreferredSize(new Dimension(130, 38));
        btnAtualizar.addActionListener(e -> atualizarTela());

        painelBotoes.add(btnGanhos);
        painelBotoes.add(btnAtualizar);
        p.add(painelBotoes, BorderLayout.EAST);

        return p;
    }

    /**
     * Calcula os rendimentos totais com base nas entregas concluídas no Banco de Dados
     */
    private void calcularEExibirGanhos() {
        int idEntregador = bd.BancoDados.obterIdUsuarioLogado();
        if (idEntregador == -1) {
            JOptionPane.showMessageDialog(this, "Erro: Usuário não identificado ou deslogado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Busca a lista de todas as entregas com status 5 (Finalizado) deste entregador específico
        List<Pedido> historico = bd.BancoDados.obterPedidosConcluidosPorEntregador(idEntregador);
        int totalCorridas = historico.size();

        // Regra de negócio: R$ 8.50 fixos por cada entrega efetuada com sucesso
        double taxaPorCorrida = 8.50;
        double ganhosAcumulados = totalCorridas * taxaPorCorrida;

        String msgRelatorio = String.format(
                "📊 Extrato Consolidador de Repasses\n\n" +
                        "» Entregador ID: #%d\n" +
                        "» Entregas Concluídas no Histórico: %d\n" +
                        "» Valor Fixo por Corrida: R$ %.2f\n" +
                        "» Total Líquido a Receber: R$ %.2f\n\n" +
                        "Deseja exportar a folha de prestação de serviços impressa?",
                idEntregador, totalCorridas, taxaPorCorrida, ganhosAcumulados
        );

        int opcao = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                msgRelatorio,
                "Balanço de Carteira",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (opcao == JOptionPane.YES_OPTION) {
            exportarRelatorioFisico(idEntregador, totalCorridas, ganhosAcumulados);
        }
    }

    /**
     * Salva o relatório detalhado em formato .txt no computador
     */
    private void exportarRelatorioFisico(int idEntregador, int totalCorridas, double ganhos) {
        String nomeArquivo = "relatorio_ganhos_entregador_" + idEntregador + ".txt";

        try (java.io.FileWriter writer = new java.io.FileWriter(nomeArquivo)) {
            writer.write("==================================================\n");
            writer.write("        SISTEMA DELIVERY - RELATÓRIO DO MOTOBOY   \n");
            writer.write("==================================================\n");
            writer.write(String.format("Código do Prestador: #%d\n", idEntregador));
            writer.write("--------------------------------------------------\n");
            writer.write(String.format("Total de Corridas Efetuadas:  %d entregas\n", totalCorridas));
            writer.write(String.format("Valor Bruto por Serviço:      R$ 8,50\n"));
            writer.write(String.format("SALDO ACUMULADO DISPONÍVEL:   R$ %10.2f\n", ganhos));
            writer.write("==================================================\n");
            writer.write(" Gerado em: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()) + "\n");
            writer.write("==================================================\n");

            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Relatório de ganhos salvo com sucesso!\nArquivo: " + nomeArquivo,
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Erro ao tentar gravar arquivo de relatório: " + ex.getMessage(),
                    "Falha Crítica",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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

        StringBuilder listaItens = new StringBuilder();
        for (Produto prod : p.getComidas()) {
            if (listaItens.length() > 0) listaItens.append(", ");
            listaItens.append(prod.getNome());
        }

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

        if (p.getEstado() == 2) {
            JLabel info = new JLabel("O restaurante ainda está preparando este pedido.");
            info.setFont(new Font("Arial", Font.ITALIC, 13));
            info.setForeground(new Color(100, 100, 100));
            painel.add(info);
            return painel;
        } else if (p.getEstado() == 3) {
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

    private void aceitarPedido(Pedido p) {
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
        atualizarTela();
    }

    private void concluirEntrega(Pedido p) {
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
        int idEntregador = bd.BancoDados.obterIdUsuarioLogado();
        if (idEntregador == -1) return null;
        return bd.BancoDados.obterPedidoAtivoEntregador(idEntregador);
    }

    private List<Pedido> obterPedidosDisponiveis() {
        return bd.BancoDados.obterPedidosDisponiveisEntrega();
    }
}