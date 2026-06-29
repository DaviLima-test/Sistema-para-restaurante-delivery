package view;

import model.Cartao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * TelaCarteira — Exibe os cartões cadastrados à esquerda (40%)
 * e os detalhes ou formulário de cadastro à direita (60%).
 */
public class TelaCarteira extends TelaMenu {

    private final Telabase sist;

    private JPanel painelDetalhe;
    private JPanel corpoPrincipal;
    private Cartao cartaoSelecionado;

    // Lista simulada de cartões (substitua por Dados.listaCartoes se preferir)
    private static List<Cartao> listaCartoesMemoria;

    // Cores padrão do seu projeto
    private static final Color COR_PRIMARIA = new Color(234, 16, 34);
    private static final Color COR_VERDE = new Color(46, 174, 82);
    private static final Color COR_CINZA_BG = new Color(245, 245, 245);
    private static final Color COR_BORDA = new Color(230, 230, 230);
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);

    public TelaCarteira(Telabase sist) {
        super(sist);
        this.sist = sist;

        // Inicializa dados fictícios se a lista estiver vazia
        if (listaCartoesMemoria == null) {
            inicializarCartoes();
        }

        // Container raiz desta tela
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

        // MODO PADRÃO: Lista (esquerda - 40%) + Detalhe/Formulário (direita - 60%)
        corpoPrincipal = new JPanel(new GridLayout(1, 2, 20, 0));
        corpoPrincipal.setBackground(Color.WHITE);
        corpoPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Adiciona a lista de cartões cadastrados
        corpoPrincipal.add(criarPainelLista());

        // Define o lado direito inicial como placeholder informativo
        painelDetalhe = criarPainelDetalhePlaceholder();
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

        Texto titulo = new Texto("💳  Minha Carteira e Pagamentos");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        p.add(titulo, BorderLayout.WEST);

        // Botão para abrir o formulário de adição de cartão
        BotaoArredondado btnAdicionar = new BotaoArredondado("+ Adicionar Cartão", 20, COR_VERDE, 14);
        btnAdicionar.setPreferredSize(new Dimension(170, 38));
        btnAdicionar.addActionListener(e -> exibirFormularioCadastro());
        p.add(btnAdicionar, BorderLayout.EAST);

        return p;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DA LISTA DE CARTÕES
    // ─────────────────────────────────────────────────────────
    private JPanel criarPainelLista() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JLabel lblSub = new JLabel("Seus métodos de pagamento salvos");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);
        lblSub.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        wrapper.add(lblSub, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);

        if (listaCartoesMemoria.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum cartão cadastrado.");
            vazio.setFont(new Font("Arial", Font.ITALIC, 14));
            vazio.setForeground(Color.GRAY);
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            lista.add(Box.createVerticalStrut(40));
            lista.add(vazio);
        } else {
            for (Cartao c : listaCartoesMemoria) {
                lista.add(criarCardCartao(c));
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

    private JPanel criarCardCartao(Cartao c) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(c == cartaoSelecionado ? COR_PRIMARIA : COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        // Ícone decorativo baseado na bandeira
        JLabel lblIcon = new JLabel("💳");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 14);
        card.add(lblIcon, gbc);

        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 3, 0);

        // Nome mascarado do cartão (Ex: **** **** **** 4521)
        JLabel lblNome = new JLabel("•••• •••• •••• " + c.getQuatroUltimosDigitos());
        lblNome.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(lblNome, gbc);

        // Badge de principal ou tipo
        if (c.isPrincipal()) {
            JLabel lblStatus = new JLabel("● Principal");
            lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
            lblStatus.setForeground(COR_VERDE);
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(0, 10, 3, 0);
            card.add(lblStatus, gbc);
        }

        JLabel lblDetalhe = new JLabel(c.getBandeira().toUpperCase() + "  •  Vencimento: " + c.getValidade());
        lblDetalhe.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDetalhe.setForeground(Color.GRAY);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(lblDetalhe, gbc);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(COR_CARD_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(c == cartaoSelecionado ? COR_CARD_HOVER : Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                selecionarCartao(c);
            }
        });

        return card;
    }

    private JPanel criarPainelDetalhePlaceholder() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COR_CINZA_BG);
        p.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1, true));

        JLabel lblDica = new JLabel(
                "<html><div style='text-align:center; color:#bbb'>" +
                        "👆<br><br>Selecione um cartão para ver detalhes<br>" +
                        "ou clique em <b>+ Adicionar Cartão</b> para cadastrar." +
                        "</div></html>"
        );
        lblDica.setFont(new Font("Arial", Font.PLAIN, 16));
        lblDica.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(lblDica);
        return p;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DE DETALHE DO CARTÃO SELECIONADO
    // ─────────────────────────────────────────────────────────
    private JPanel criarPainelDetalhePreenchido(Cartao c) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel lblTitulo = new JLabel("Informações do Cartão");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 30));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(20));

        // Seção Visual Simulando um Cartão Físico
        painel.add(criarSecao("💳  Dados do Método de Pagamento", new String[][]{
                {"Bandeira", c.getBandeira().toUpperCase()},
                {"Número", "•••• •••• •••• " + c.getQuatroUltimosDigitos()},
                {"Titular", c.getTitular().toUpperCase()},
                {"Validade", c.getValidade()},
                {"Status", c.isPrincipal() ? "Método Principal de Cobrança" : "Opcional"}
        }));

        painel.add(Box.createVerticalGlue());
        painel.add(Box.createVerticalStrut(24));

        // Botões de Ação na base do painel de detalhes
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (!c.isPrincipal()) {
            BotaoArredondado btnPrincipal = new BotaoArredondado("⭐ Tornar Principal", 20, COR_VERDE, 14);
            btnPrincipal.addActionListener(e -> definirComoPrincipal(c));
            btnRow.add(btnPrincipal);
        }

        BotaoArredondado btnRemover = new BotaoArredondado("🗑️ Excluir Cartão", 20, COR_PRIMARIA, 14);
        btnRemover.addActionListener(e -> excluirCartao(c));
        btnRow.add(btnRemover);

        painel.add(btnRow);
        return painel;
    }

    // ─────────────────────────────────────────────────────────
    //  FORMULÁRIO PARA CADASTRAR NOVO CARTÃO
    // ─────────────────────────────────────────────────────────
    private JPanel criarPainelFormularioCadastro() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel lblTitulo = new JLabel("Cadastrar Novo Cartão");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 30));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(15));

        // Campos do Formulário
        JTextField txtNumero = criarCampoTextoForm("Número do Cartão (16 dígitos)");
        JTextField txtTitular = criarCampoTextoForm("Nome Completo do Titular");
        JTextField txtValidade = criarCampoTextoForm("Validade (MM/AA)");
        JTextField txtCVV = criarCampoTextoForm("Código CVV (3 dígitos)");
        JTextField txtBandeira = criarCampoTextoForm("Bandeira (Visa, Mastercard...)");

        painel.add(new JLabel("Número do Cartão:"));
        painel.add(txtNumero);
        painel.add(Box.createVerticalStrut(8));
        painel.add(new JLabel("Nome do Titular (como impresso):"));
        painel.add(txtTitular);
        painel.add(Box.createVerticalStrut(8));

        // Linha dividida para Validade e CVV
        JPanel linhaDividida = new JPanel(new GridLayout(1, 2, 10, 0));
        linhaDividida.setOpaque(false);
        linhaDividida.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel pVal = new JPanel(new BorderLayout());
        pVal.setOpaque(false);
        pVal.add(new JLabel("Validade:"), BorderLayout.NORTH);
        pVal.add(txtValidade, BorderLayout.CENTER);

        JPanel pCvv = new JPanel(new BorderLayout());
        pCvv.setOpaque(false);
        pCvv.add(new JLabel("CVV:"), BorderLayout.NORTH);
        pCvv.add(txtCVV, BorderLayout.CENTER);

        linhaDividida.add(pVal);
        linhaDividida.add(pCvv);
        painel.add(linhaDividida);
        painel.add(Box.createVerticalStrut(8));

        painel.add(new JLabel("Bandeira:"));
        painel.add(txtBandeira);

        painel.add(Box.createVerticalGlue());
        painel.add(Box.createVerticalStrut(20));

        // Botões Salvar / Cancelar
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        BotaoArredondado btnSalvar = new BotaoArredondado("💾 Salvar Cartão", 20, COR_VERDE, 14);
        btnSalvar.addActionListener(e -> {
            String num = txtNumero.getText().trim();
            String tit = txtTitular.getText().trim();
            String val = txtValidade.getText().trim();
            String cvv = txtCVV.getText().trim();
            String band = txtBandeira.getText().trim();

            if (num.length() < 4 || tit.isEmpty() || val.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha os campos obrigatórios corretamente.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cria e adiciona o novo cartão na lista estática
            Cartao novo = new Cartao(num, tit, val, cvv, band, listaCartoesMemoria.isEmpty());
            novo.SalvarCartao();
            listaCartoesMemoria.add(novo);

            JOptionPane.showMessageDialog(this, "Cartão adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            atualizarTela();
        });

        BotaoArredondado btnCancelar = new BotaoArredondado("Cancelar", 20, new Color(160, 160, 160), 14);
        btnCancelar.addActionListener(e -> atualizarTela());

        btnRow.add(btnSalvar);
        btnRow.add(btnCancelar);
        painel.add(btnRow);

        return painel;
    }

    private JTextField criarCampoTextoForm(String dica) {
        JTextField f = new JTextField();
        f.setFont(new Font("Arial", Font.PLAIN, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return f;
    }

    private JPanel criarSecao(String titulo, String[][] pares) {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(COR_CINZA_BG);
        s.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTit = new JLabel(titulo);
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
            s.add(Box.createVerticalStrut(4));
        }
        return s;
    }

    // ─────────────────────────────────────────────────────────
    //  AÇÕES DA TELA
    // ─────────────────────────────────────────────────────────
    private void selecionarCartao(Cartao c) {
        cartaoSelecionado = c;
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = criarPainelDetalhePreenchido(c);
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }

    private void exibirFormularioCadastro() {
        cartaoSelecionado = null;
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = criarPainelFormularioCadastro();
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }

    private void definirComoPrincipal(Cartao alvo) {
        for (Cartao c : listaCartoesMemoria) {
            c.setPrincipal(c == alvo);
        }
        JOptionPane.showMessageDialog(this, "Este cartão agora é seu método de pagamento principal.");
        atualizarTela();
    }

    private void excluirCartao(Cartao alvo) {
        int r = JOptionPane.showConfirmDialog(this, "Deseja realmente remover este cartão?", "Excluir", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            listaCartoesMemoria.remove(alvo);
            // Se removeu o principal e ainda restaram cartões, define o primeiro como principal
            if (alvo.isPrincipal() && !listaCartoesMemoria.isEmpty()) {
                listaCartoesMemoria.get(0).setPrincipal(true);
            }
            atualizarTela();
        }
    }

    private void atualizarTela() {
        cartaoSelecionado = null;
        if (sist != null) {
            sist.configuraTela(new TelaCarteira(sist));
        }
    }

    private void inicializarCartoes() {
        if(listaCartoesMemoria == null || listaCartoesMemoria.isEmpty()){
            listaCartoesMemoria=Cartao.getCartoes();
        }

    }

    // ─────────────────────────────────────────────────────────
    //  MODELO INTERNO DE CARTÃO DE CRÉDITO
    // ─────────────────────────────────────────────────────────
}