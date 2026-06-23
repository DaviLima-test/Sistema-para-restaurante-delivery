package view;

import model.Cartao;
import model.Pedido;
import model.Produto;
import repositorio.Dados;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TelaCarrinho extends TelaMenu {

    private final Telabase sist;
    private JPanel painelCheckout;
    private JPanel corpoPrincipal;

    // Cores padronizadas do ecossistema
    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);
    private static final Color COR_VERDE      = new Color(46, 174, 82);
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);
    private static final Color COR_BORDA      = new Color(230, 230, 230);
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);

    public TelaCarrinho(Telabase sist) {
        super(sist);
        this.sist = sist;

        // Inicializa a lista do carrinho caso esteja nula no repositório
        if (Dados.listaCarrinho == null) {
            Dados.listaCarrinho = new ArrayList<>();
            // Itens de demonstração inicial caso o carrinho abra vazio pela primeira vez
            criarItensCarrinhoDemo();
        }

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

        // Layout Dividido: 40% Lista de Itens (Esquerda) e 60% Resumo e Pagamento (Direita)
        corpoPrincipal = new JPanel(new GridLayout(1, 2, 24, 0));
        corpoPrincipal.setBackground(Color.WHITE);
        corpoPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        corpoPrincipal.add(criarPainelItens());
        painelCheckout = criarPainelCheckout();
        corpoPrincipal.add(painelCheckout);

        JScrollPane scroll = new JScrollPane(corpoPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
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

        Texto titulo = new Texto("🛒 Meu Carrinho de Compras");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        p.add(titulo, BorderLayout.WEST);

        if (!Dados.listaCarrinho.isEmpty()) {
            BotaoArredondado btnLimpar = new BotaoArredondado("Limpar Carrinho", 20, new Color(180, 180, 180), 13);
            btnLimpar.setPreferredSize(new Dimension(140, 36));
            btnLimpar.addActionListener(e -> {
                Dados.listaCarrinho.clear();
                atualizarTela();
            });
            p.add(btnLimpar, BorderLayout.EAST);
        }

        return p;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DA ESQUERDA: LISTA DE PRODUTOS ADICIONADOS
    // ─────────────────────────────────────────────────────────
    private JPanel criarPainelItens() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JLabel lblSub = new JLabel("Revise os produtos escolhidos antes de fechar o pedido");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);
        lblSub.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        wrapper.add(lblSub, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);

        if (Dados.listaCarrinho.isEmpty()) {
            JLabel vazio = new JLabel("Seu carrinho está vazio.");
            vazio.setFont(new Font("Arial", Font.ITALIC, 14));
            vazio.setForeground(Color.GRAY);
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            lista.add(Box.createVerticalStrut(60));
            lista.add(vazio);
        } else {
            for (Produto prod : Dados.listaCarrinho) {
                lista.add(criarCardProdutoCarrinho(prod));
                lista.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollLista = new JScrollPane(lista);
        scrollLista.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1, true));
        scrollLista.getVerticalScrollBar().setUnitIncrement(14);
        scrollLista.setPreferredSize(new Dimension(0, 480));
        wrapper.add(scrollLista, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel criarCardProdutoCarrinho(Produto prod) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        // Ícone customizado por tipo de item
        JLabel lblIcon = new JLabel("🍔");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 12);
        card.add(lblIcon, gbc);

        gbc.gridheight = 1;

        // Nome do Produto
        JLabel lblNome = new JLabel(prod.nome != null ? prod.nome : "Item Individual");
        lblNome.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(lblNome, gbc);

        // Preço Mockado
        JLabel lblPreco = new JLabel("R$ 28,90");
        lblPreco.setFont(new Font("Arial", Font.BOLD, 13));
        lblPreco.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1; gbc.gridy = 1;
        card.add(lblPreco, gbc);

        // Botão de Excluir Item individual
        JButton btnRemover = new JButton("🗑️");
        btnRemover.setBorderPainted(false);
        btnRemover.setContentAreaFilled(false);
        btnRemover.setFocusPainted(false);
        btnRemover.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRemover.setToolTipText("Remover do carrinho");
        btnRemover.addActionListener(e -> {
            Dados.listaCarrinho.remove(prod);
            atualizarTela();
        });
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 2; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        card.add(btnRemover, gbc);

        return card;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DA DIREITA: REVISÃO DE ENDEREÇO, VALORES E CHECKOUT
    // ─────────────────────────────────────────────────────────
    private JPanel criarPainelCheckout() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel lblTitulo = new JPanel().add(new JLabel()) == null ? new JLabel() : new JLabel("Resumo da Compra");
        lblTitulo.setText("Resumo da Compra");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(20));

        // Seção 1: Destino da Entrega
        painel.add(criarSecaoCarrinho("📍 Endereço de Entrega", new String[][]{
                {"Local", "Casa"},
                {"Endereço", "Rua das Oliveiras, 452 - Apt 12"}
        }));
        painel.add(Box.createVerticalStrut(14));

        // ALTERADO: Método de pagamento integrado com tratamento de erro (Fallback para sem cartão)
        Cartao cartao = Cartao.GetPrincipal();
        boolean possuiCartao = (cartao != null);

        if (possuiCartao) {
            String cartaoTexto = "•••• " + cartao.getQuatroUltimosDigitos();
            painel.add(criarSecaoCarrinho("💳 Forma de Pagamento", new String[][]{
                    {"Cartão Ativo", cartaoTexto},
                    {"Tipo", cartao.getBandeira()}
            }));
        } else {
            // Exibe mensagem de erro avisando que não há cartões
            painel.add(criarSecaoCarrinho("⚠️ Forma de Pagamento", new String[][]{
                    {"Status", "Nenhum cartão cadastrado!"},
                    {"Ação", "Cadastre um cartão no seu perfil"}
            }));
        }
        painel.add(Box.createVerticalStrut(14));

        // Seção 3: Cálculos de Valores baseados nos itens do carrinho
        double subtotal = Dados.listaCarrinho.size() * 28.90;
        double taxaEntrega = Dados.listaCarrinho.isEmpty() ? 0.0 : 7.00;
        double totalGeral = subtotal + taxaEntrega;

        painel.add(criarSecaoCarrinho("💰 Valores", new String[][]{
                {"Subtotal", String.format("R$ %.2f", subtotal)},
                {"Taxa de Entrega", String.format("R$ %.2f", taxaEntrega)},
                {"Total Geral", String.format("R$ %.2f", totalGeral)}
        }));

        painel.add(Box.createVerticalGlue());
        painel.add(Box.createVerticalStrut(20));

        // Botão de Ação com validações acopladas
        BotaoArredondado btnFinalizar = new BotaoArredondado("🚀 Confirmar e Fazer Pedido", 22, COR_VERDE, 15);
        btnFinalizar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnFinalizar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ALTERADO: Validação do botão para impedir o envio caso não haja cartão ou se o carrinho estiver vazio
        if (Dados.listaCarrinho.isEmpty()) {
            btnFinalizar.setEnabled(false);
            btnFinalizar.setBackground(Color.LIGHT_GRAY);
            btnFinalizar.setText("🛒 Carrinho Vazio");
        } else if (!possuiCartao) {
            btnFinalizar.setEnabled(false);
            btnFinalizar.setBackground(Color.LIGHT_GRAY);
            btnFinalizar.setText("⚠️ Cadastre um Cartão para Comprar");
        } else {
            btnFinalizar.addActionListener(e -> finalizarPedidoDoCarrinho(subtotal));
        }

        painel.add(btnFinalizar);

        return painel;
    }

    private JPanel criarSecaoCarrinho(String titulo, String[][] pares) {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(COR_CINZA_BG);
        s.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        Texto lblTit = new Texto(titulo);
        lblTit.setFont(new Font("Arial", Font.BOLD, 13));
        lblTit.setForeground(COR_PRIMARIA);
        s.add(lblTit);
        s.add(Box.createVerticalStrut(6));

        for (String[] par : pares) {
            JPanel linha = new JPanel(new BorderLayout(6, 0));
            linha.setOpaque(false);

            JLabel chave = new JLabel(par[0] + ":");
            chave.setFont(new Font("Arial", Font.BOLD, 12));
            chave.setForeground(Color.GRAY);
            chave.setPreferredSize(new Dimension(120, 18));

            JLabel valor = new JLabel(par[1]);
            valor.setFont(new Font("Arial", Font.PLAIN, 12));
            valor.setForeground(Color.BLACK);

            // Destaca visualmente o valor do Total Geral
            if(par[0].equals("Total Geral")) {
                chave.setFont(new Font("Arial", Font.BOLD, 13));
                valor.setFont(new Font("Arial", Font.BOLD, 14));
                valor.setForeground(COR_VERDE);
            }
            // ALTERADO: Destaca visualmente em vermelho se não houver cartão cadastrado
            else if(par[1].equals("Nenhum cartão cadastrado!")) {
                valor.setFont(new Font("Arial", Font.BOLD, 12));
                valor.setForeground(COR_PRIMARIA);
            }

            linha.add(chave, BorderLayout.WEST);
            linha.add(valor, BorderLayout.CENTER);
            s.add(linha);
        }
        return s;
    }

    // ─────────────────────────────────────────────────────────
    //  REGRAS DE NEGÓCIO: ADIÇÃO E PRODUÇÃO DO PEDIDO
    // ─────────────────────────────────────────────────────────
    private void finalizarPedidoDoCarrinho(double valorTotal) {
        if (Dados.listaCarrinho.isEmpty()) return;

        Produto principal = Dados.listaCarrinho.get(0);

        model.Restaurante rest = new model.Restaurante();
        model.Cliente clienteLogado = new model.Cliente(Telabase.getLogin().GetEmail(), Telabase.getLogin().GetUser(), "");

        Pedido novoPedido = new Pedido(principal, "Imediato", null, null, rest, clienteLogado);
        novoPedido.setEstado(1);

        if (Dados.listaPedidos == null) {
            Dados.listaPedidos = new ArrayList<>();
        }
        Dados.listaPedidos.add(novoPedido);

        Dados.listaCarrinho.clear();

        JOptionPane.showMessageDialog(this,
                "🎉 Pedido enviado com sucesso!\nO restaurante já começou a preparar sua refeição.",
                "Sucesso!", JOptionPane.INFORMATION_MESSAGE);

        if (sist != null) {
            sist.configuraTela(new TelaPedidosCliente(sist));
        }
    }

    private void atualizarTela() {
        if (sist != null) {
            sist.configuraTela(new TelaCarrinho(sist));
        }
    }

    private void criarItensCarrinhoDemo() {
        Produto p1 = new Produto(); p1.nome = "X-Burguer Duplo Cheddar";
        Produto p2 = new Produto(); p2.nome = "Batata Frita Grande Média";
        Dados.listaCarrinho.add(p1);
        Dados.listaCarrinho.add(p2);
    }
}