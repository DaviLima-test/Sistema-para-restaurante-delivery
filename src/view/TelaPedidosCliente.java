package view;

import bd.BancoDados;
import model.Produto;

import model.Pedido;
import repositorio.Dados;
import bd.BancoDados; // IMPORTANTE: Import do seu Banco de Dados
import util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TelaPedidosCliente extends TelaMenu {

    private final Telabase sist;
    private JPanel painelDetalhe;
    private JPanel corpoPrincipal;
    private Pedido pedidoSelecionado;

    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);
    private static final Color COR_VERDE      = new Color(46, 174, 82);
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);
    private static final Color COR_BORDA      = new Color(230, 230, 230);
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);

    public TelaPedidosCliente(Telabase sist) {
        super(sist);
        this.sist = sist;

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

        corpoPrincipal = new JPanel(new GridLayout(1, 2, 20, 0));
        corpoPrincipal.setBackground(Color.WHITE);
        corpoPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        corpoPrincipal.add(criarPainelLista());
        painelDetalhe = criarPainelDetalhePlaceholder();
        corpoPrincipal.add(painelDetalhe);

        JScrollPane scroll = new JScrollPane(corpoPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
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

        Texto titulo = new Texto("Meus Pedidos Realizados");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        p.add(titulo, BorderLayout.WEST);

        BotaoArredondado btnAtualizar = new BotaoArredondado("Atualizar Status", 20, COR_PRIMARIA, 14);
        btnAtualizar.setPreferredSize(new Dimension(160, 38));
        btnAtualizar.addActionListener(e -> atualizarTela());
        p.add(btnAtualizar, BorderLayout.EAST);

        return p;
    }

    private JPanel criarPainelLista() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JLabel lblSub = new JLabel("Acompanhe o andamento dos seus pedidos");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);
        lblSub.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        wrapper.add(lblSub, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);

        // ALTERADO: Agora busca direto do banco de dados
        List<Pedido> meusPedidos = obterMeusPedidos();

        if (meusPedidos.isEmpty()) {
            JLabel vazio = new JLabel("Você ainda não fez nenhum pedido.");
            vazio.setFont(new Font("Arial", Font.ITALIC, 14));
            vazio.setForeground(Color.GRAY);
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            lista.add(Box.createVerticalStrut(40));
            lista.add(vazio);
        } else {
            for (Pedido p : meusPedidos) {
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

        JLabel lblIcon = new JLabel("🍔");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 28));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 14);
        card.add(lblIcon, gbc);

        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 3, 0);

        // Concatena o nome dos itens para exibir no card
        StringBuilder nomesComidas = new StringBuilder();
        for (Produto prod : p.getComidas()) {
            if (nomesComidas.length() > 0) nomesComidas.append(", ");
            nomesComidas.append(prod.getNome());
        }

        JLabel lblNome = new JLabel(nomesComidas.length() > 0 ? nomesComidas.toString() : "Produto");
        lblNome.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(lblNome, gbc);

        String[] textos = {"", "Recebido", "Em preparo", "Pronto", "A caminho", "Entregue"};
        Color[] cores   = {Color.GRAY, new Color(200, 130, 0), new Color(200, 130, 0), new Color(29, 78, 216), COR_PRIMARIA, COR_VERDE};
        int st = Math.max(1, Math.min(p.getEstado(), 5));

        JLabel lblStatus = new JLabel("● " + textos[st]);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        lblStatus.setForeground(cores[st]);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        card.add(lblStatus, gbc);

        JLabel lblDetalhe = new JLabel("Restaurante Central • Ver detalhes");
        lblDetalhe.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDetalhe.setForeground(Color.GRAY);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(lblDetalhe, gbc);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(COR_CARD_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { card.setBackground(p == pedidoSelecionado ? COR_CARD_HOVER : Color.WHITE); }
            @Override public void mouseClicked(MouseEvent e) { selecionarPedido(p); }
        });

        return card;
    }

    private JPanel criarPainelDetalhePlaceholder() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COR_CINZA_BG);
        p.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1, true));
        JLabel lblDica = new JLabel("<html><div style='text-align:center; color:#bbb'>🛍️<br><br>Selecione um pedido para<br>ver o histórico de entrega</div></html>");
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

        JLabel lblTitulo = new JLabel("Acompanhamento do Pedido");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(20));

        // ALTERADO: Junta o nome dos produtos para a seção Resumo
        StringBuilder itensLista = new StringBuilder();
        for (Produto prod : p.getComidas()) {
            if (itensLista.length() > 0) itensLista.append(", ");
            itensLista.append(prod.getNome());
        }

        painel.add(criarSecao("Resumo", new String[][]{
                {"Itens Solicitados", itensLista.toString()},
                {"Previsão de Entrega", p.getHora_Entregue() != null ? p.getHora_Entregue() : "Em breve"}
        }));
        painel.add(Box.createVerticalStrut(14));

        String nomeEntregador = (p.getEntregador() != null && p.getEstado() >= 2) ? "Motoboy Parceiro" : "Buscando entregador parceiro...";
        painel.add(criarSecao("Status do Envio", new String[][]{
                {"Entregador Responsável", nomeEntregador},
                {"Endereço de Destino", "Rua das Oliveiras, 452 - Apt 12"}
        }));

        painel.add(Box.createVerticalGlue());

        if (p.getEstado() == 1) {
            BotaoArredondado btnCancelar = new BotaoArredondado("Cancelar Pedido", 20, COR_PRIMARIA, 14);
            btnCancelar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            btnCancelar.addActionListener(e -> cancelarPedido(p));
            painel.add(btnCancelar);
        }

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
            chave.setForeground(Color.GRAY);
            chave.setPreferredSize(new Dimension(130, 20));
            JLabel valor = new JLabel(par[1]);
            valor.setFont(new Font("Arial", Font.PLAIN, 13));
            linha.add(chave, BorderLayout.WEST);
            linha.add(valor, BorderLayout.CENTER);
            s.add(linha);
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

    // ALTERADO: Lógica de exclusão/cancelamento integrada com o Banco de Dados
    private void cancelarPedido(Pedido p) {
        int r = JOptionPane.showConfirmDialog(this, "Deseja realmente cancelar este pedido?", "Cancelar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {

            // Supondo que você tenha ou adicionará um método deletarPedido(id) na sua classe de banco
            // Se a sua classe Pedido ainda não tiver getIdPedido(), passe as referências do cliente.
            boolean canceladoComSucesso = BancoDados.cancelarPedidoNoBanco(p.getId());

            if (canceladoComSucesso) {
                JOptionPane.showMessageDialog(this, "Pedido cancelado com sucesso!");
                atualizarTela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar o pedido no servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void atualizarTela() {
        pedidoSelecionado = null;
        if (sist != null) sist.configuraTela(new TelaPedidosCliente(sist));
    }

    // ALTERADO: Busca apenas os pedidos do cliente atualmente logado
    private List<Pedido> obterMeusPedidos() {
        int idCliente = BancoDados.obterIdUsuarioLogado();
        if (idCliente == -1) return new java.util.ArrayList<>();
        return BancoDados.obterPedidosPorCliente(idCliente);
    }
}