package view;

import bd.BancoDados;
import util.RemoveEmoji;

import model.Cartao;
import model.Pedido;
import model.Produto;
import repositorio.Dados;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Interface gráfica (View) do carrinho de compras do cliente na plataforma de delivery.
 * <p>
 * Apresenta uma disposição dividida em duas colunas através de uma malha (Grid de 1x2):
 * A coluna da esquerda exibe a listagem dos produtos adicionados com suporte a remoção individual,
 * enquanto a coluna da direita agrupa o resumo financeiro, endereço de destino, método de pagamento
 * e o controle de validação do fechamento do pedido.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public class TelaCarrinho extends TelaMenu {

    /** Instância de controle de navegação global de janelas do sistema. */
    private final Telabase sist;

    /** Painel lateral direito reativo responsável pelo resumo analítico e checkout. */
    private JPanel painelCheckout;

    /** Container central estruturado para abrigar as seções da tela. */
    private JPanel corpoPrincipal;

    /** Tonalidade vermelha institucional para realce visual e sinalizações de erro/alerta. */
    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);

    /** Tonalidade verde para botões de confirmação e indicadores financeiros positivos. */
    private static final Color COR_VERDE      = new Color(46, 174, 82);

    /** Cor cinza neutra clara para estilização de fundos de seções internas (cards). */
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);

    /** Cor sutil de delimitação para bordas de componentes. */
    private static final Color COR_BORDA      = new Color(230, 230, 230);

    /** Cor de realce para eventos de foco ou passagem do cursor (Hover). */
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);

    /**
     * Construtor da tela de carrinho de compras.
     * <p>
     * Garante a inicialização da lista de persistência volátil na memória, monta a árvore de
     * layouts dividida em colunas e encapsula os subpainéis em um contêiner de rolagem síncrona.
     * </p>
     *
     * @param sist O frame base de gerenciamento global de telas {@link Telabase}.
     */
    public TelaCarrinho(Telabase sist) {
        super(sist);
        this.sist = sist;

        if (Dados.listaCarrinho == null) {
            Dados.listaCarrinho = new ArrayList<>();
            criarItensCarrinho();
        }

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

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

    /**
     * Cria a barra de título superior contendo o rótulo da tela e o controle de esvaziamento total.
     * * @return Um {@link JPanel} de cabeçalho configurado.
     */
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

    /**
     * Constrói a subseção de listagem de produtos adicionados, tratando cenários de carrinho vazio.
     * * @return Um {@link JPanel} contendo os cartões de produtos empilhados verticalmente.
     */
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

    /**
     * Fabrica o componente visual individualizado de um produto contido no carrinho.
     * Utiliza o gerenciador {@link GridBagLayout} para garantir o alinhamento de textos e ícones.
     *
     * @param prod A entidade {@link Produto} que servirá de base para a renderização do card.
     * @return Um {@link JPanel} contendo as informações do item e botão de exclusão.
     */
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

        JLabel lblIcon = new JLabel("🍔");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 12);
        card.add(lblIcon, gbc);

        gbc.gridheight = 1;

        JLabel lblNome = new JLabel(prod.getNome() != null ? prod.getNome() : "Item Individual");
        lblNome.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(lblNome, gbc);

        JLabel lblPreco = new JLabel("R$ 28,90");
        lblPreco.setFont(new Font("Arial", Font.BOLD, 13));
        lblPreco.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1; gbc.gridy = 1;
        card.add(lblPreco, gbc);

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

    /**
     * Constrói a interface de fechamento analítico de valores, localização e condições de pagamento.
     * Valida de forma acoplada o estado do botão finalizador em caso de ausência de fundos ou itens.
     * * @return Um {@link JPanel} estruturado com as caixas informativas e gatilhos de submissão.
     */
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

        painel.add(criarSecaoCarrinho("📍 Endereço de Entrega", new String[][]{
                {"Local", "Casa"},
                {"Endereço", "Rua das Oliveiras, 452 - Apt 12"}
        }));
        painel.add(Box.createVerticalStrut(14));

        Cartao cartao = Cartao.GetPrincipal();
        boolean possuiCartao = (cartao != null);

        if (possuiCartao) {
            String cartaoTexto = "•••• " + cartao.getQuatroUltimosDigitos();
            painel.add(criarSecaoCarrinho("💳 Forma de Pagamento", new String[][]{
                    {"Cartão Ativo", cartaoTexto},
                    {"Tipo", cartao.getBandeira()}
            }));
        } else {
            painel.add(criarSecaoCarrinho("⚠️ Forma de Pagamento", new String[][]{
                    {"Status", "Nenhum cartão cadastrado!"},
                    {"Ação", "Cadastre um cartão no seu perfil"}
            }));
        }
        painel.add(Box.createVerticalStrut(14));

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

        BotaoArredondado btnFinalizar = new BotaoArredondado("🚀 Confirmar e Fazer Pedido", 22, COR_VERDE, 15);
        btnFinalizar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnFinalizar.setAlignmentX(Component.LEFT_ALIGNMENT);

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

    /**
     * Helper visual destinado a criar sub-caixas estilizadas cinzas para organização dos dados.
     *
     * @param titulo O título que será posicionado na parte superior do bloco.
     * @param pares  Uma matriz bidimensional de Strings representando pares de Chave e Valor.
     * @return Um {@link JPanel} contendo o layout formatado em linhas.
     */
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

            if(par[0].equals("Total Geral")) {
                chave.setFont(new Font("Arial", Font.BOLD, 13));
                valor.setFont(new Font("Arial", Font.BOLD, 14));
                valor.setForeground(COR_VERDE);
            }
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

    /**
     * Processa as regras de negócio de fechamento de compra, registrando o pedido no banco de dados.
     * Caso a operação falte ou suceda, limpa o carrinho e redireciona o cliente para seu histórico.
     *
     * @param valorTotal O valor financeiro consolidado acumulado do pedido.
     */
    private void finalizarPedidoDoCarrinho(double valorTotal) {
        if (Dados.listaCarrinho.isEmpty()) return;

        Produto principal = Dados.listaCarrinho.get(0);

        model.Restaurante rest = new model.Restaurante(1,"","",1);
        model.Cliente clienteLogado = new model.Cliente(Telabase.getLogin().GetEmail(), Telabase.getLogin().GetUser(), "");

        boolean sucessoBanco = BancoDados.criarPedido(Dados.listaCarrinho, Dados.listaCarrinho.get(0).getRestaurante().getId());

        if (sucessoBanco) {
            Dados.listaCarrinho.clear();
            JOptionPane.showMessageDialog(this,
                    util.RemoveEmoji.texto("🎉 Pedido enviado com sucesso!\nO restaurante já começou a preparar sua refeição."),
                    "Sucesso!", JOptionPane.INFORMATION_MESSAGE);

            if (sist != null) {
                sist.configuraTela(new TelaPedidosCliente(sist));
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Não foi possível processar seu pedido no banco de dados.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Força a reconstrução estrutural completa da tela, instanciando uma nova view para atualizar dados de remoção.
     */
    private void atualizarTela() {
        if (sist != null) {
            sist.configuraTela(new TelaCarrinho(sist));
        }
    }

    /**
     * Método interno utilitário para popular o carrinho com itens mockados caso ele seja aberto pela primeira vez vazio.
     */
    private void criarItensCarrinho() {
        Produto p1 = new Produto(); p1.setNome("X-Burguer Duplo Cheddar");
        Produto p2 = new Produto(); p2.setNome("Batata Frita Grande Média");
        Dados.listaCarrinho.add(p1);
        Dados.listaCarrinho.add(p2);
    }
}