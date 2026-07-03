package view;

import bd.BancoDados;
import model.Login;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

/**
 * Superclasse abstrata estrutural (Template View) que estabelece a identidade visual comum
 * e os mecanismos de navegação global do sistema.
 * <p>
 * Implementa um contêiner baseado em {@link JLayeredPane} que gerencia de forma rígida três
 * níveis independentes de sobreposição através do método interceptado {@code doLayout()}:
 * </p>
 * <ul>
 * <li><b>Camada Padrão (0):</b> O contêiner da aplicação, composto pelo cabeçalho (Header) e o conteúdo injetado pela filha.</li>
 * <li><b>Camada de Paleta (100):</b> O painel translúcido (Overlay) que escurece o fundo e captura cliques externos.</li>
 * <li><b>Camada Modal (200):</b> A gaveta de navegação lateral (Barra Lateral) contendo os botões de controle de perfil.</li>
 * </ul>
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public abstract class TelaMenu extends JPanel {

    /** Flag indicadora do estado lógico de visibilidade do menu lateral retrátil. */
    protected boolean menu_aberto = false;

    /** Painel de controle de profundidade que gerencia a renderização de componentes sobrepostos. */
    private JLayeredPane camadas;

    /** Menu deslizante lateral contendo as opções e rotas de navegação do usuário. */
    private JPanel barra_lateral;

    /** Painel de fundo semitransparente ativado para criar foco visual no menu aberto. */
    private Overlay overlay;

    /** Container raiz da camada base que abriga verticalmente o cabeçalho e a área útil. */
    private JPanel conteudoApp;

    /** Referência do painel de conteúdo específico que a classe filha injetará na visualização. */
    private JPanel conteudoInterno;

    /** Instância ativa de coordenação de janelas globais do sistema. */
    protected Telabase sist;

    /** Texto explicativo temporário renderizado na caixa de pesquisa. */
    private static final String PLACEHOLDER_BUSCA = " Buscar Restaurantes e pratos ...";

    /** Campo de texto customizado e arredondado para captura dos filtros de pesquisa. */
    private CampoTextoArredondado campoBusca;

    /**
     * Construtor da base estrutural do menu.
     * <p>
     * Sobrescreve dinamicamente o método {@code doLayout()} do painel de camadas para impor dimensões
     * fixas e relativas absolutas, anulando comportamentos elásticos indesejados. Configura listeners
     * de clique no overlay e invoca a montagem dos botões de rotas.
     * </p>
     *
     * @param sist O frame base de gerenciamento global de telas {@link Telabase}.
     */
    public TelaMenu(Telabase sist) {
        this.sist = sist;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        camadas = new JLayeredPane() {
            @Override
            public void doLayout() {
                int largura = getWidth();
                int altura = getHeight();

                if (conteudoApp != null) {
                    conteudoApp.setBounds(0, 0, largura, altura);
                }
                if (overlay != null) {
                    overlay.setBounds(0, 0, largura, altura);
                }
                if (barra_lateral != null) {
                    barra_lateral.setBounds(0, 0, 250, altura);
                }
            }
        };
        add(camadas, BorderLayout.CENTER);

        conteudoApp = new JPanel(new BorderLayout());
        conteudoApp.setBackground(Color.WHITE);

        overlay = new Overlay();
        overlay.setVisible(false);
        overlay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                configurarMenu(false);
            }
        });

        barra_lateral = new JPanel();
        barra_lateral.setLayout(new BoxLayout(barra_lateral, BoxLayout.Y_AXIS));
        barra_lateral.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        barra_lateral.setBackground(Color.WHITE);
        barra_lateral.setVisible(false);

        JPanel header = criarHeader();
        conteudoApp.add(header, BorderLayout.NORTH);

        camadas.add(conteudoApp, JLayeredPane.DEFAULT_LAYER);
        camadas.add(overlay, JLayeredPane.PALETTE_LAYER);
        camadas.add(barra_lateral, JLayeredPane.MODAL_LAYER);

        iniciaMenu();
    }

    /**
     * Constrói o cabeçalho superior (Header) da aplicação contendo o botão do menu, a logo e a barra de busca.
     * Vincila listeners de foco e teclado para controle do placeholder e submissão reativa de texto.
     * * * @return Um {@link JPanel} estruturado em {@link GridBagLayout} para o topo do sistema.
     */
    private JPanel criarHeader() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(800, 80));
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 20);

        ImageIcon inc_hambuger = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/hambuger_icon.png")));
        Image novaImg = inc_hambuger.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);

        JButton bnt_hambuger = new JButton(new ImageIcon(novaImg));
        bnt_hambuger.setPreferredSize(new Dimension(50, 50));
        bnt_hambuger.setMaximumSize(new Dimension(50, 50));
        gbc.gridx = 0;
        gbc.weightx = 0.0;

        p.add(bnt_hambuger, gbc);
        bnt_hambuger.addActionListener(e -> alternarMenu());

        Texto logo = new Texto("AIFood");
        logo.setFont(new Font("Arial", Font.BOLD, 30));
        logo.setForeground(new Color(234, 16, 34));
        logo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (sist != null) {
                    sist.configuraTela(new TelaPrincipal(sist));
                }
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                logo.setForeground(new Color(240, 240, 240));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                logo.setForeground(new Color(234, 16, 34));
            }
        });
        gbc.gridx = 1;
        p.add(logo, gbc);

        campoBusca = new CampoTextoArredondado(18, 15, new Color(240, 240, 240), 30);
        campoBusca.setText(PLACEHOLDER_BUSCA);
        campoBusca.setForeground(Color.GRAY);
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoBusca.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campoBusca.getText().equals(PLACEHOLDER_BUSCA)) {
                    campoBusca.setText("");
                    campoBusca.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campoBusca.getText().isEmpty()) {
                    campoBusca.setForeground(Color.GRAY);
                    campoBusca.setText(PLACEHOLDER_BUSCA);
                    aoBuscar("");
                }
            }
        });

        campoBusca.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campoBusca.getText();
                if (texto.equals(PLACEHOLDER_BUSCA)) texto = "";
                aoBuscar(texto.trim());
            }
        });

        p.add(campoBusca, gbc);
        return p;
    }

    /**
     * Ponto de extensão assíncrono disparado sempre que há modificação no texto da barra de pesquisa.
     * <p>
     * Classes filhas que renderizam listagens filtráveis devem obrigatoriamente sobrescrever este método
     * para interceptar a string e aplicar seus filtros específicos.
     * </p>
     *
     * @param texto Cadeia de caracteres contendo o termo de pesquisa limpo.
     */
    protected void aoBuscar(String texto) {
    }

    /**
     * Acopla o painel interno de trabalho enviado pela classe filha na região central da camada base.
     * * * @param painelFilho O contêiner {@link JPanel} estruturado pela tela filha.
     */
    protected void setConteudoInterno(JPanel painelFilho) {
        this.conteudoInterno = painelFilho;
        conteudoApp.add(painelFilho, BorderLayout.CENTER);

        conteudoApp.revalidate();
        conteudoApp.repaint();
    }

    /**
     * Inicializa a esteira de opções padrão do menu lateral e injeta condicionalmente botões de
     * controle específicos baseados no nível de privilégio e tipo de perfil retornado por {@link Login}.
     */
    public void iniciaMenu() {
        adicionarItemMenu("Perfil", e -> {
            if(sist != null) {
                TelaPerfil tp = new TelaPerfil(sist);
                sist.configuraTela(tp);
            }
        });
        adicionarItemMenu("Carrinho", e -> {
            TelaCarrinho tc = new TelaCarrinho(sist);
            sist.configuraTela(tc);
        });
        adicionarItemMenu("Meus pedidos", e -> {
            TelaPedidosCliente tp = new TelaPedidosCliente(sist);
            sist.configuraTela(tp);
        });
        adicionarItemMenu("Carteira", e -> {
            TelaCarteira tc = new TelaCarteira(sist);
            sist.configuraTela(tc);
        });

        if ("restaurante".equals(Login.GetTipo())) {
            if(BancoDados.buscarRestaurantePorGerente(Login.GetEmail()) == null)
                adicionarItemMenu("Cadastrar Restaurante", e -> sist.configuraTela(new TelaNovoRestaurante(sist)));
            else
                adicionarItemMenu("Gerenciar Restaurante", e -> sist.configuraTela(new TelaGerenciarRestaurante(sist)));
            adicionarItemMenu("Pedidos", e -> sist.configuraTela(new TelaPedidosRestaurante(sist)));
        }
        if ("entregador".equals(Login.GetTipo())) {
            adicionarItemMenu("Pedidos a serem entregues", e -> {
                TelaPedidosEntregador tp = new TelaPedidosEntregador(sist);
                sist.configuraTela(tp);
            });
        }
        if ("admin".equals(Login.GetTipo()) || "admin_master".equals(Login.GetTipo())) {
            adicionarItemMenu("Painel Admin", e -> sist.configuraTela(new TelaAdminPrincipal(sist)));
        }
        adicionarItemMenu("Sair", e -> {
            Object[] opcoes = {"Sim", "Não"};
            int resposta = JOptionPane.showOptionDialog(
                    SwingUtilities.getWindowAncestor(barra_lateral),
                    "Você realmente deseja sair?", "Confirmação",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, opcoes, opcoes[0]
            );
            if (resposta == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    /**
     * Fabrica, estiliza e anexa individualmente um botão de controle à lista vertical da barra lateral.
     *
     * @param texto O rótulo descritivo a ser impresso no botão.
     * @param e     O ouvinte de ação {@link ActionListener} contendo a regra de redirecionamento de tela.
     */
    private void adicionarItemMenu(String texto, ActionListener e) {
        BotaoArredondado btn = new BotaoArredondado(texto, 25, Color.decode("#e96769"), 20);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        btn.addActionListener(e);
        barra_lateral.add(btn);
        barra_lateral.add(Box.createVerticalStrut(15));
    }

    /**
     * Inverte de forma lógica o estado atual de exibição do painel de navegação lateral.
     */
    protected void alternarMenu(){
        configurarMenu(!menu_aberto);
    }

    /**
     * Altera de forma coordenada a visibilidade das camadas de sobreposição.
     * Atualiza as propriedades de exibição do overlay e da barra de navegação, forçando a
     * árvore de componentes a reexecutar o gerenciador de renderização dinâmica de forma estável.
     *
     * @param abrir Roteia {@code true} para abrir e exibir as camadas, ou {@code false} para ocultá-las.
     */
    private void configurarMenu(boolean abrir) {
        menu_aberto = abrir;

        overlay.setVisible(menu_aberto);
        barra_lateral.setVisible(menu_aberto);

        camadas.revalidate();
        camadas.repaint();
    }
}