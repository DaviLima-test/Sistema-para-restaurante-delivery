package view;

import util.RemoveEmoji;
import model.Produto;
import model.Restaurante;
import model.Login;
import bd.BancoDados;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Interface gráfica responsável pela interação do gerente com o cardápio do restaurante.
 * Permite listar, incluir, editar e remover pratos visualmente.
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public class TelaGerenciarRestaurante extends TelaMenu {

    private final Telabase sist;
    private JPanel corpoPrincipal;
    private JPanel painelDetalhe;
    private Produto pratoSelecionado;
    private Restaurante restaurante;

    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);
    private static final Color COR_VERDE      = new Color(46, 174, 82);
    private static final Color COR_AMARELO    = new Color(255, 180, 0);
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);
    private static final Color COR_BORDA      = new Color(230, 230, 230);
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);

    /**
     * Construtor da tela de gerenciamento. Inicializa o modelo do restaurante
     * focado no gerente logado e renderiza os componentes Swing.
     * * @param sist Instância da janela base de navegação do sistema.
     */
    public TelaGerenciarRestaurante(Telabase sist) {
        super(sist);
        this.sist = sist;

        String[] dadosRestaurante = BancoDados.buscarRestaurantePorGerente(Login.GetEmail());
        if (dadosRestaurante != null && dadosRestaurante.length > 0) {
            try {
                int id = Integer.parseInt(dadosRestaurante[0]);
                String nome = dadosRestaurante.length > 1 ? dadosRestaurante[1] : "";
                String loc = dadosRestaurante.length > 2 ? dadosRestaurante[2] : "";
                int est = dadosRestaurante.length > 3 ? Integer.parseInt(dadosRestaurante[3]) : 5;

                this.restaurante = new Restaurante(id, nome, loc, est);
                this.restaurante.carregarCardapio();
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter dados do restaurante: " + e.getMessage());
                this.restaurante = new Restaurante();
            }
        } else {
            this.restaurante = new Restaurante();
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

    /**
     * Cria a secção superior da tela com o título, o botão de faturamento/comanda e inclusão.
     * * @return JPanel contendo o cabeçalho formatado.
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

        Texto titulo = new Texto("🍽  Gerenciar Restaurante");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        titulos.add(titulo);

        JLabel sub = new JLabel("Cardápio — adicione, edite ou remova pratos");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);
        titulos.add(sub);

        p.add(titulos, BorderLayout.WEST);

        // Subpainel horizontal para alinhar os múltiplos botões na direita
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        painelBotoes.setOpaque(false);

        // Novo botão: Consultar faturamento baseado em todos os pedidos feitos no Banco de Dados
        BotaoArredondado btnFaturamento = new BotaoArredondado("📊 Ver Ganhos / Comanda", 20, COR_AMARELO, 14);
        btnFaturamento.setPreferredSize(new Dimension(210, 42));
        btnFaturamento.addActionListener(e -> {
            if (restaurante == null || restaurante.getId() <= 0) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                        "Erro: Estabelecimento não identificado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Busca todos os pedidos ativos/concluídos vinculados a este restaurante específico
            ArrayList<model.Pedido> pedidos = BancoDados.obterPedidosPorRestaurante(restaurante.getId());
            if(pedidos.isEmpty()){
                System.out.println("Null");
            }
            double faturamentoTotal = 0;
            int totalPedidos = pedidos.size();

            // Loop para acumular dinamicamente o valor de cada pedido direto do banco de dados
            // Procure por esta linha dentro do btnFaturamento na TelaGerenciarRestaurante:
            for (model.Pedido ped : pedidos) {
                System.out.println(ped);
                faturamentoTotal += BancoDados.obterValorPedidoNoBanco(ped.getId());
                System.out.println(faturamentoTotal);
            }

            double lucroLiquido = faturamentoTotal * 0.75; // Margem padrão estipulada de 75%

            String msgRelatorio = String.format(
                    "📈 Relatório de Desempenho Finaceiro\n\n" +
                            "» Restaurante: %s\n" +
                            "» Total de Pedidos Feitos: %d\n" +
                            "» Faturamento Bruto Total: R$ %.2f\n" +
                            "» Lucro Líquido Real (75%%): R$ %.2f\n\n" +
                            "Deseja exportar a comanda impressa consolidada de fechamento corporativo?",
                    restaurante.getNome(), totalPedidos, faturamentoTotal, lucroLiquido
            );

            int opcao = JOptionPane.showConfirmDialog(
                    SwingUtilities.getWindowAncestor(this),
                    msgRelatorio,
                    "Balanço de Caixa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (opcao == JOptionPane.YES_OPTION) {
                exportarComandaConsolidada(totalPedidos, faturamentoTotal, lucroLiquido);
            }
        });

        // Botão padrão de inserção de itens
        BotaoArredondado btnAdd = new BotaoArredondado("Adicionar Prato", 20, COR_VERDE, 14);
        btnAdd.setPreferredSize(new Dimension(170, 42));
        btnAdd.addActionListener(e -> abrirFormulario(null));

        painelBotoes.add(btnFaturamento);
        painelBotoes.add(btnAdd);

        p.add(painelBotoes, BorderLayout.EAST);

        return p;
    }

    /**
     * Gera e salva um relatório físico agindo como a comanda geral de faturamento do estabelecimento.
     * * @param totalPedidos Contagem de transações efetuadas.
     * @param faturamento Valor financeiro bruto apurado.
     * @param lucro Ganho líquido líquido calculado.
     */
    private void exportarComandaConsolidada(int totalPedidos, double faturamento, double lucro) {
        String nomeArquivo = "comanda_geral_restaurante_" + restaurante.getId() + ".txt";

        try (java.io.FileWriter writer = new java.io.FileWriter(nomeArquivo)) {
            writer.write("==================================================\n");
            writer.write("       SISTEMA DELIVERY - COMANDA DE FECHAMENTO   \n");
            writer.write("==================================================\n");
            writer.write(String.format("Restaurante: %-30s\n", restaurante.getNome()));
            writer.write(String.format("Localização: %-30s\n", restaurante.getLocalizacao()));
            writer.write("--------------------------------------------------\n");
            writer.write(String.format("Volume de Pedidos:             %d transações\n", totalPedidos));
            writer.write(String.format("Faturamento Bruto Acumulado:   R$ %10.2f\n", faturamento));
            writer.write(String.format("Lucro Líquido Estimado (75%%):  R$ %10.2f\n", lucro));
            writer.write("==================================================\n");
            writer.write(" Emitido em: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()) + "\n");
            writer.write("==================================================\n");

            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Comanda geral exportada com sucesso!\nSalvo em seu diretório como: " + nomeArquivo,
                    "Arquivo Salvo",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Falha crítica ao gravar arquivo da comanda: " + ex.getMessage(),
                    "Erro de Escrita",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Constrói a listagem lateral esquerda com todos os pratos cadastrados.
     * * @return JPanel estruturado com a lista de componentes em um scroll.
     */
    private JPanel criarPainelLista() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JLabel lblSub = new JLabel("Clique em um prato para ver opções");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);
        lblSub.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        wrapper.add(lblSub, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);
        lista.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        if (restaurante.getCardapio().isEmpty()) {
            JLabel vazio = new JLabel("Nenhum prato cadastrado. Clique em 'Adicionar Prato'.");
            vazio.setFont(new Font("Arial", Font.ITALIC, 13));
            vazio.setForeground(Color.GRAY);
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            lista.add(Box.createVerticalStrut(40));
            lista.add(vazio);
        } else {
            for (Produto produto : restaurante.getCardapio()) {
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
     * Cria um painel individual clicável para representar visualmente um Produto.
     * * @param produto O produto cujas informações serão exibidas.
     * @return JPanel customizado agindo como cartão interativo.
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

        JLabel icone = new JLabel("🍽");
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

        JLabel lblDetalhe = new JLabel("Clique para editar ou remover");
        lblDetalhe.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDetalhe.setForeground(Color.GRAY);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(lblDetalhe, gbc);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(COR_CARD_HOVER); }
            @Override public void mouseExited(MouseEvent e)  {
                card.setBackground(produto == pratoSelecionado ? COR_CARD_HOVER : Color.WHITE);
            }
            @Override public void mouseClicked(MouseEvent e) { selecionarProduto(produto); }
        });

        return card;
    }

    /**
     * Painel padrão exibido no lado direito enquanto nenhum item está selecionado.
     * * @return JPanel com mensagem informativa centralizada.
     */
    private JPanel criarPlaceholder() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COR_CINZA_BG);
        p.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1, true));

        JLabel dica = new JLabel(
                "<html><div style='text-align:center;color:#bbb'>" +
                        "👆<br><br>Selecione um prato<br>para editar ou remover" +
                        "<br><br>ou clique em<br><b style='color:#ea1022'>Adicionar Prato</b><br>para criar um novo" +
                        "</div></html>"
        );
        dica.setFont(new Font("Arial", Font.PLAIN, 15));
        dica.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(dica);
        return p;
    }

    /**
     * Cria a visão detalhada de exibição e ações para um prato previamente selecionado.
     * * @param produto O produto selecionado.
     * @return JPanel formatado contendo os dados e os botões de controle.
     */
    private JPanel criarPainelDetalhe(Produto produto) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel lblTitulo = new JLabel("Produto Selecionado");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 30));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(20));

        painel.add(criarSecao("🍽  Informações do Prato", new String[][]{
                {"Nome",  produto.getNome()},
                {"Preço", String.format("R$ %.2f", produto.getPreco())}
        }));
        painel.add(Box.createVerticalStrut(24));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotaoArredondado btnEditar = new BotaoArredondado("✏  Editar Prato", 20, COR_AMARELO, 14);
        btnEditar.addActionListener(e -> abrirFormulario(produto));

        BotaoArredondado btnRemover = new BotaoArredondado("🗑  Remover", 20, COR_PRIMARIA, 14);
        btnRemover.addActionListener(e -> confirmarRemocao(produto));

        btnRow.add(btnEditar);
        btnRow.add(btnRemover);
        painel.add(btnRow);

        painel.add(Box.createVerticalGlue());
        return painel;
    }

    /**
     * Monta o formulário de captura e validação de dados para cadastrar ou editar um produto.
     * * @param produtoEdit O produto alvo de modificação, ou null caso seja uma nova inserção.
     * @return JPanel contendo os inputs textuais formatados.
     */
    private JPanel criarFormularioProduto(Produto produtoEdit) {
        boolean modoEdicao = (produtoEdit != null);

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel lblTitulo = new JLabel(modoEdicao ? "✏  Editar Prato" : "Novo Prato");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 30));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(20));

        CampoTextoArredondado campoNomeProduto = new CampoTextoArredondado(20, 16, COR_BORDA, 15);
        CampoTextoArredondado campoPreco     = new CampoTextoArredondado(10, 16, COR_BORDA, 15);

        if (modoEdicao) {
            campoNomeProduto.setText(produtoEdit.getNome());
            campoPreco.setText(String.format("%.2f", produtoEdit.getPreco()).replace(",", "."));
        }

        painel.add(criarGrupoCampo("Nome do Produto *", campoNomeProduto, "Ex: X-Burguer Duplo"));
        painel.add(Box.createVerticalStrut(16));
        painel.add(criarGrupoCampo("Preço (R$) *", campoPreco, "Ex: 29.90"));
        painel.add(Box.createVerticalStrut(28));

        JLabel lblFeedback = new JLabel(" ");
        lblFeedback.setFont(new Font("Arial", Font.BOLD, 13));
        lblFeedback.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblFeedback);
        painel.add(Box.createVerticalStrut(12));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        Color corSalvar = modoEdicao ? COR_AMARELO : COR_VERDE;
        String textoSalvar = modoEdicao ? "  Salvar Edição" : "Adicionar Prato";
        BotaoArredondado btnSalvar  = new BotaoArredondado(textoSalvar, 20, corSalvar, 14);
        BotaoArredondado btnCancelar = new BotaoArredondado("Cancelar", 20, new Color(160, 160, 160), 14);

        btnSalvar.addActionListener(e -> {
            String nome  = campoNomeProduto.getText().trim();
            String precoTxt = campoPreco.getText().trim().replace(",", ".");

            if (nome.isEmpty() || precoTxt.isEmpty()) {
                lblFeedback.setText(RemoveEmoji.texto("⚠  Preencha todos os campos."));
                lblFeedback.setForeground(COR_PRIMARIA);
                return;
            }

            double preco;
            try {
                preco = Double.parseDouble(precoTxt);
                if (preco <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                lblFeedback.setText(RemoveEmoji.texto("⚠  Preço inválido. Use Ex: 29.90"));
                lblFeedback.setForeground(COR_PRIMARIA);
                return;
            }

            if (modoEdicao) {
                restaurante.atualizarPrato(produtoEdit, nome, preco);
                lblFeedback.setText(RemoveEmoji.texto("✅  Produto updated!"));
                lblFeedback.setForeground(COR_VERDE);
            } else {
                boolean sucesso = restaurante.adicionarPrato(nome, preco);
                if (sucesso) {
                    lblFeedback.setText(RemoveEmoji.texto("✅  Produto adicionado!"));
                    lblFeedback.setForeground(COR_VERDE);
                } else {
                    lblFeedback.setText(RemoveEmoji.texto("⚠  Erro ao salvar o produto no sistema."));
                    lblFeedback.setForeground(COR_PRIMARIA);
                }
            }

            Timer t = new Timer(600, ev -> sist.configuraTela(new TelaGerenciarRestaurante(sist)));
            t.setRepeats(false);
            t.start();
        });

        btnCancelar.addActionListener(e -> trocarPainelDetalhe(criarPlaceholder()));

        btnRow.add(btnSalvar);
        btnRow.add(btnCancelar);
        painel.add(btnRow);

        painel.add(Box.createVerticalGlue());
        return painel;
    }

    /**
     * Auxiliar visual para montar uma secção estruturada de exibição de dados com linhas em grade.
     * * @param titulo O título da secção.
     * @param pares  Matriz contendo mapeamentos de Chave e Valor em texto.
     * @return JPanel estilizado de resumo.
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
        int alturaCalculada = 45 + (pares.length * 26);
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
            chave.setPreferredSize(new Dimension(80, 22));
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
     * Estrutura um grupo vertical padronizado contendo Label, Input e Dica auxiliar.
     * * @param label Texto informativo do campo.
     * @param campo Componente de input visual.
     * @param hint  Dica complementar exibida abaixo do campo.
     * @return JPanel contendo o agrupamento alinhado.
     */
    private JPanel criarGrupoCampo(String label, JComponent campo, String hint) {
        JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
        g.setOpaque(false);
        g.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(new Color(50, 50, 50));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        g.add(lbl);
        g.add(Box.createVerticalStrut(5));

        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        g.add(campo);
        g.add(Box.createVerticalStrut(4));

        JLabel hintLabel = new JLabel(hint);
        hintLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        hintLabel.setForeground(new Color(160, 160, 160));
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        g.add(hintLabel);

        return g;
    }

    /**
     * Define o produto em foco e reconstrói o painel detalhado dinamicamente.
     * * @param produto Produto selecionado pelo utilizador.
     */
    private void selecionarProduto(Produto produto) {
        pratoSelecionado = produto;
        trocarPainelDetalhe(criarPainelDetalhe(produto));
    }

    /**
     * Alterna o painel de detalhes lateral pelo formulário ativo de edição ou criação.
     * * @param produto O produto a editar, ou null para um novo formulário de criação.
     */
    private void abrirFormulario(Produto produto) {
        trocarPainelDetalhe(criarFormularioProduto(produto));
    }

    /**
     * Exibe um diálogo de confirmação gráfica e delega a remoção do item ao modelo.
     * * @param produto O produto a ser deletado definitivamente.
     */
    private void confirmarRemocao(Produto produto) {
        int r = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Deseja remover o prato \"" + produto.getNome() + "\"?\nEsta ação não pode ser desfeita.",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        );
        if (r == JOptionPane.YES_OPTION) {
            restaurante.removerPrato(produto);
            sist.configuraTela(new TelaGerenciarRestaurante(sist));
        }
    }

    /**
     * Realiza a substituição dinâmica do componente do painel lateral direito
     * aplicando limpeza reformatada.
     * * @param novoPainel Novo painel Swing a ser renderizado na área lateral.
     */
    private void trocarPainelDetalhe(JPanel novoPainel) {
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = novoPainel;
        RemoveEmoji.aplicar(painelDetalhe);
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }
}