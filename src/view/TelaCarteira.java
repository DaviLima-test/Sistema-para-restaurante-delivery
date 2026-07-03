package view;

import util.RemoveEmoji;
import model.Cartao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface gráfica (View) destinada ao gerenciamento de métodos de pagamento do cliente.
 * <p>
 * Apresenta uma arquitetura dividida em duas colunas através de uma malha (Grid de 1x2):
 * A coluna da esquerda (40%) renderiza a listagem com barra de rolagem de cartões cadastrados e
 * seus respectivos status (Principal/Secundário), enquanto a coluna da direita (60%) atua de forma
 * reativa como um contêiner dinâmico, alternando entre um informativo padrão, a visualização de
 * detalhes do item selecionado ou o formulário interativo de cadastro.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public class TelaCarteira extends TelaMenu {

    /** Instância ativa de coordenação e navegação de janelas globais do sistema. */
    private final Telabase sist;

    /** Painel lateral direito reativo para exibição de detalhes, placeholders ou formulários. */
    private JPanel painelDetalhe;

    /** Container central estruturado para abrigar a distribuição das seções na tela. */
    private JPanel corpoPrincipal;

    /** Referência da entidade temporariamente selecionada pelo usuário para auditoria. */
    private Cartao cartaoSelecionado;

    /** Lista estática que gerencia e centraliza os registros em cache volátil na memória. */
    private static List<Cartao> listaCartoesMemoria;

    /** Tonalidade vermelha corporativa para realces visuais e sinalizações de ações críticas. */
    private static final Color COR_PRIMARIA = new Color(234, 16, 34);

    /** Tonalidade verde associada a confirmações, adições bem-sucedidas e status ativos. */
    private static final Color COR_VERDE = new Color(46, 174, 82);

    /** Cor cinza de fundo neutro para demarcação de sub-seções internas. */
    private static final Color COR_CINZA_BG = new Color(245, 245, 245);

    /** Cor sutil padronizada para pintura de contornos e bordas de componentes. */
    private static final Color COR_BORDA = new Color(230, 230, 230);

    /** Cor sutil aplicada como realce de fundo durante eventos de passagem do cursor mouse (Hover). */
    private static final Color COR_CARD_HOVER = new Color(255, 242, 242);

    /**
     * Construtor da tela de carteira de pagamentos.
     * <p>
     * Verifica e aciona a inicialização da massa de dados em cache, cria os componentes estruturais,
     * define o lado direito em estado inicial de placeholder e acopla o scroll vertical suave.
     * </p>
     *
     * @param sist O frame base de gerenciamento global de telas {@link Telabase}.
     */
    public TelaCarteira(Telabase sist) {
        super(sist);
        this.sist = sist;

        if (listaCartoesMemoria == null) {
            inicializarCartoes();
        }

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
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scroll, BorderLayout.CENTER);

        setConteudoInterno(container);
    }

    /**
     * Fabrica o painel superior da tela com o título da seção e o gatilho de adição de novos cartões.
     * * @return Um {@link JPanel} de cabeçalho configurado.
     */
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

        BotaoArredondado btnAdicionar = new BotaoArredondado("+ Adicionar Cartão", 20, COR_VERDE, 14);
        btnAdicionar.setPreferredSize(new Dimension(170, 38));
        btnAdicionar.addActionListener(e -> exibirFormularioCadastro());
        p.add(btnAdicionar, BorderLayout.EAST);

        return p;
    }

    /**
     * Monta a subseção esquerda que lista os cartões, tratando o fluxo visual caso a carteira esteja vazia.
     * * @return Um {@link JPanel} contendo as caixas de cartões empilhadas.
     */
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

    /**
     * Constrói individualmente o card interativo de um cartão utilizando o gerenciador {@link GridBagLayout}.
     * Incorpora listeners dedicados para detecção de cliques e efeito visual reativo de Hover.
     *
     * @param c A instância de {@link Cartao} cujos dados estruturais preencherão o card.
     * @return Um {@link JPanel} estilizado e interativo.
     */
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

        JLabel lblIcon = new JLabel("💳");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 14);
        card.add(lblIcon, gbc);

        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 3, 0);

        JLabel lblNome = new JLabel("•••• •••• •••• " + c.getQuatroUltimosDigitos());
        lblNome.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(lblNome, gbc);

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

    /**
     * Fabrica o painel explicativo inicial que instrui o usuário a selecionar ou criar um item.
     * * @return Um {@link JPanel} centralizado com textos informativos em HTML.
     */
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

    /**
     * Instancia o bloco descritivo detalhado exibindo as credenciais estruturadas do cartão selecionado.
     * Anexa em sua base as operações reativas de exclusão e definição de prioridade de cobrança.
     *
     * @param c A entidade {@link Cartao} ativa cujas propriedades preencherão os rótulos.
     * @return Um {@link JPanel} estruturado contendo as informações e os botões de controle.
     */
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

        painel.add(criarSecao("💳  Dados do Método de Pagamento", new String[][]{
                {"Bandeira", c.getBandeira().toUpperCase()},
                {"Número", "•••• •••• •••• " + c.getQuatroUltimosDigitos()},
                {"Titular", c.getTitular().toUpperCase()},
                {"Validade", c.getValidade()},
                {"Status", c.isPrincipal() ? "Método Principal de Cobrança" : "Opcional"}
        }));

        painel.add(Box.createVerticalGlue());
        painel.add(Box.createVerticalStrut(24));

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

    /**
     * Constrói o painel de formulário capturador interativo contendo os campos de entrada de texto.
     * Vincula a lógica de submissão, verificação de obrigatoriedade de dados e persistência via back-end.
     *
     * @return Um {@link JPanel} estruturado com caixas de entrada de dados e gatilhos salvadores.
     */
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

    /**
     * Helper visual para a padronização e estilização de bordas internas dos campos do formulário.
     *
     * @param dica Texto auxiliar (Dica) que parametriza as propriedades iniciais do campo.
     * @return Um componente operacional configurado do tipo {@link JTextField}.
     */
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

    /**
     * Constrói sub-caixas estruturadas de listagem cinza para emparelhamento visual de pares chave/valor.
     *
     * @param titulo Rótulo superior descritivo do bloco analítico.
     * @param pares  Matriz contendo as tuplas organizadas com o descritor e seu respectivo dado.
     * @return Um {@link JPanel} contendo as linhas formatadas.
     */
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

    /**
     * Atualiza dinamicamente o estado do lado direito injetando o painel de detalhes do cartão selecionado.
     * Executa de forma acoplada o utilitário {@link RemoveEmoji} para assegurar a renderização de fontes nativas.
     *
     * @param c A entidade {@link Cartao} alvo da injeção visual.
     */
    private void selecionarCartao(Cartao c) {
        cartaoSelecionado = c;
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = criarPainelDetalhePreenchido(c);
        RemoveEmoji.aplicar(painelDetalhe);
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }

    /**
     * Altera reativamente o escopo do lado direito para renderizar o formulário interativo de captura.
     */
    private void exibirFormularioCadastro() {
        cartaoSelecionado = null;
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = criarPainelFormularioCadastro();
        RemoveEmoji.aplicar(painelDetalhe);
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }

    /**
     * Percorre a lista local para alterar de forma lógica as permissões e definir o elemento principal da conta.
     *
     * @param alvo A entidade {@link Cartao} que receberá o destaque de cobrança padrão.
     */
    private void definirComoPrincipal(Cartao alvo) {
        for (Cartao c : listaCartoesMemoria) {
            c.setPrincipal(c == alvo);
        }
        JOptionPane.showMessageDialog(this, "Este cartão agora é seu método de pagamento principal.");
        atualizarTela();
    }

    /**
     * Promove a exclusão lógica do objeto, reordenando as prioridades de faturamento padrão caso necessário.
     *
     * @param alvo O objeto {@link Cartao} a ser expelido da listagem.
     */
    private void excluirCartao(Cartao alvo) {
        int r = JOptionPane.showConfirmDialog(this, "Deseja realmente remover este cartão?", "Excluir", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            listaCartoesMemoria.remove(alvo);
            if (alvo.isPrincipal() && !listaCartoesMemoria.isEmpty()) {
                listaCartoesMemoria.get(0).setPrincipal(true);
            }
            atualizarTela();
        }
    }

    /**
     * Força a reconstrução estrutural completa do frame instanciando uma nova View para repintar os componentes.
     */
    private void atualizarTela() {
        cartaoSelecionado = null;
        if (sist != null) {
            sist.configuraTela(new TelaCarteira(sist));
        }
    }

    /**
     * Faz a leitura do back-end para carregar os registros persistidos em cache no ciclo de vida da sessão.
     */
    private void inicializarCartoes() {
        if(listaCartoesMemoria == null || listaCartoesMemoria.isEmpty()){
            listaCartoesMemoria = Cartao.getCartoes();
        }
    }
}