package view;

import model.Produto;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import bd.BancoDados;
import model.Login;

public class TelaGerenciarRestaurante extends TelaMenu {

    private final Telabase sist;

    private JPanel corpoPrincipal;
    private JPanel painelDetalhe;
    private Produto pratoSelecionado;

    private ArrayList<Produto> cardapio = new ArrayList<>();

    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);
    private static final Color COR_VERDE      = new Color(46, 174, 82);
    private static final Color COR_AMARELO    = new Color(255, 180, 0);
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);
    private static final Color COR_BORDA      = new Color(230, 230, 230);
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);
    private final String[] dadosRestaurante;
    public TelaGerenciarRestaurante(Telabase sist) {
        super(sist);
        this.sist = sist;
        dadosRestaurante = BancoDados.buscarRestaurantePorGerente(Login.GetEmail());

        setCardapio(); // troca por BancoDados.listarCardapio() quando o BD estiver integrado

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

        // Título + subtítulo em coluna
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

        // Botão principal de ação
        BotaoArredondado btnAdd = new BotaoArredondado("＋  Adicionar Prato", 20, COR_VERDE, 14);
        btnAdd.setPreferredSize(new Dimension(170, 42));
        btnAdd.addActionListener(e -> abrirFormulario(null)); // null = modo criação
        p.add(btnAdd, BorderLayout.EAST);

        return p;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DE LISTA DE PRATOS
    // ─────────────────────────────────────────────────────────
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

        if (cardapio.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum prato cadastrado. Clique em '+ Adicionar Prato'.");
            vazio.setFont(new Font("Arial", Font.ITALIC, 13));
            vazio.setForeground(Color.GRAY);
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            lista.add(Box.createVerticalStrut(40));
            lista.add(vazio);
        } else {
            for (Produto produto : cardapio) {
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

    /** Card clicável para cada prato do cardápio */
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

        // Ícone
        JLabel icone = new JLabel("🍽");
        icone.setFont(new Font("Arial", Font.PLAIN, 26));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 14);
        card.add(icone, gbc);

        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 3, 0);

        // Nome do prato
        JLabel lblNome = new JLabel(produto.getNome());
        lblNome.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(lblNome, gbc);

        // Preço badge
        JLabel lblPreco = new JLabel(String.format("R$ %.2f", produto.getPreco()));
        lblPreco.setFont(new Font("Arial", Font.BOLD, 14));
        lblPreco.setForeground(COR_VERDE);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 12, 3, 0);
        card.add(lblPreco, gbc);

        // Linha de detalhe
        JLabel lblDetalhe = new JLabel("Clique para editar ou remover");
        lblDetalhe.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDetalhe.setForeground(Color.GRAY);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(lblDetalhe, gbc);

        // Hover + clique
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(COR_CARD_HOVER); }
            @Override public void mouseExited(MouseEvent e)  {
                card.setBackground(produto == pratoSelecionado ? COR_CARD_HOVER : Color.WHITE);
            }
            @Override public void mouseClicked(MouseEvent e) { selecionarProduto(produto); }
        });

        return card;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DIREITO — Placeholder
    // ─────────────────────────────────────────────────────────
    private JPanel criarPlaceholder() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COR_CINZA_BG);
        p.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1, true));

        JLabel dica = new JLabel(
                "<html><div style='text-align:center;color:#bbb'>" +
                        "👆<br><br>Selecione um prato<br>para editar ou remover" +
                        "<br><br>ou clique em<br><b style='color:#ea1022'>+ Adicionar Prato</b><br>para criar um novo" +
                        "</div></html>"
        );
        dica.setFont(new Font("Arial", Font.PLAIN, 15));
        dica.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(dica);
        return p;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DIREITO — Detalhe do prato selecionado
    // ─────────────────────────────────────────────────────────
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

        // Card de resumo do prato
        painel.add(criarSecao("🍽  Informações do Prato", new String[][]{
                {"Nome",  produto.getNome()},
                {"Preço", String.format("R$ %.2f", produto.getPreco())}
        }));
        painel.add(Box.createVerticalStrut(24));

        // Botões de ação: Editar e Remover
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

    // ─────────────────────────────────────────────────────────
    //  PAINEL DIREITO — Formulário Adicionar / Editar
    // ─────────────────────────────────────────────────────────
    private JPanel criarFormularioProduto(Produto produtoEdit) {
        boolean modoEdicao = (produtoEdit != null);

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel lblTitulo = new JLabel(modoEdicao ? "✏  Editar Prato" : "＋  Novo Prato");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 30));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(20));

        // Campos
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

        // Feedback
        JLabel lblFeedback = new JLabel(" ");
        lblFeedback.setFont(new Font("Arial", Font.BOLD, 13));
        lblFeedback.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblFeedback);
        painel.add(Box.createVerticalStrut(12));

        // Botões Salvar + Cancelar
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        Color corSalvar = modoEdicao ? COR_AMARELO : COR_VERDE;
        String textoSalvar = modoEdicao ? "💾  Salvar Edição" : "＋  Adicionar Prato";
        BotaoArredondado btnSalvar  = new BotaoArredondado(textoSalvar, 20, corSalvar, 14);
        BotaoArredondado btnCancelar = new BotaoArredondado("Cancelar", 20, new Color(160, 160, 160), 14);

        btnSalvar.addActionListener(e -> {
            String nome  = campoNomeProduto.getText().trim();
            String precoTxt = campoPreco.getText().trim().replace(",", ".");

            if (nome.isEmpty() || precoTxt.isEmpty()) {
                lblFeedback.setText("⚠  Preencha todos os campos.");
                lblFeedback.setForeground(COR_PRIMARIA);
                return;
            }

            double preco;
            try {
                preco = Double.parseDouble(precoTxt);
                if (preco <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                lblFeedback.setText("⚠  Preço inválido. Use Ex: 29.90");
                lblFeedback.setForeground(COR_PRIMARIA);
                return;
            }

            if (modoEdicao) {
                // Edição: atualiza no modelo e no BD
                produtoEdit.setNome(nome);
                produtoEdit.setPreco(preco);
                BancoDados.atualizarCardapio(produtoEdit.getCodigo(), nome, String.valueOf(preco)); // integrar quando BD estiver ok
                lblFeedback.setText("✅  Produto atualizado!");
                lblFeedback.setForeground(COR_VERDE);
            } else {

                //int novoId = cardapio.isEmpty() ? 1 : cardapio.get(cardapio.size() - 1).getCodigo() + 1;
                Produto novoProduto = new Produto(nome, preco);
                cardapio.add(novoProduto);
                try {
                    String[] dados = BancoDados.buscarRestaurantePorGerente(Login.GetEmail());
                    if (dados == null) {
                        throw new NullPointerException("Restaurante não encontrado para o gerente atual.");
                    }
                    BancoDados.cadastrarCardapio(nome, String.valueOf(preco), dados[1], dados[2]); // integrar quando BD estiver ok
                }catch(NullPointerException exception){
                    System.out.println("Os daddos do cardapio estão null!");
                }
                    lblFeedback.setText("✅  Produto adicionado!");
                lblFeedback.setForeground(COR_VERDE);
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

    // ─────────────────────────────────────────────────────────
    //  HELPERS DE LAYOUT
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

    // ─────────────────────────────────────────────────────────
    //  LÓGICA
    // ─────────────────────────────────────────────────────────
    private void selecionarProduto(Produto produto) {
        pratoSelecionado = produto;
        trocarPainelDetalhe(criarPainelDetalhe(produto));
    }

    private void abrirFormulario(Produto produto) {
        trocarPainelDetalhe(criarFormularioProduto(produto));
    }

    private void confirmarRemocao(Produto produto) {
        int r = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Deseja remover o prato \"" + produto.getNome() + "\"?\nEsta ação não pode ser desfeita.",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        );
        if (r == JOptionPane.YES_OPTION) {
            cardapio.remove(produto);
            BancoDados.removerPrato(produto.getCodigo()); // integrar quando BD estiver ok
            sist.configuraTela(new TelaGerenciarRestaurante(sist));
        }
    }

    /** Troca o painel direito sem recriar toda a tela */
    private void trocarPainelDetalhe(JPanel novoPainel) {
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = novoPainel;
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }

    // ─────────────────────────────────────────────────────────
    //  Settando cardapio
    // ─────────────────────────────────────────────────────────
    private void setCardapio() {

            if (dadosRestaurante != null && dadosRestaurante.length > 0) {
                try {
                    // Converte o ID que veio em String para int
                    int idRestaurante = Integer.parseInt(dadosRestaurante[0]);

                    // Busca o cardápio filtrado por esse ID
                    cardapio = BancoDados.getCardapioPorRestaurante(idRestaurante);
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao converter o ID do restaurante para número: " + e.getMessage());
                    cardapio = new ArrayList<>();
                }
            } else {
                cardapio = new ArrayList<>();
            }
        }

}