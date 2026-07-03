package view;

import model.Login;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.Objects;
import util.*;

/**
 * Janela principal (JFrame) base do ecossistema do sistema de delivery "AiFome".
 * <p>
 * Atua como o container mestre responsável por gerenciar a troca dinâmica de telas (paineis),
 * dimensionar a interface de acordo com a resolução do monitor do usuário e aplicar patches
 * visuais como a remoção de emojis em sistemas operacionais Windows.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 * @see javax.swing.JFrame
 */
public class Telabase extends JFrame {

    /** Largura total da tela capturada dinamicamente com base no monitor do usuário. */
    public static int Width;

    /** Altura total da tela capturada dinamicamente com base no monitor do usuário. */
    public static int Height;

    /** Atributo de raio de arredondamento reservado para customizações futuras da janela. */
    private int raioDoArredondamento;

    /** Estado global da sessão contendo as informações do usuário autenticado no sistema. */
    private static Login login;

    /**
     * Construtor padrão da Telabase.
     * <p>
     * Inicializa as propriedades do JFrame, maximiza a janela ocupando todo o monitor disponível,
     * define o layout como {@link BorderLayout} e tenta carregar de forma segura o ícone institucional
     * a partir do ClassLoader para garantir compatibilidade dentro e fora de arquivos JAR.
     * </p>
     */
    public Telabase(){
        setTitle("AiFome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        Width = Toolkit.getDefaultToolkit().getScreenSize().width;
        Height = Toolkit.getDefaultToolkit().getScreenSize().height;
        setSize(Width,Height);
        try {
            java.net.URL urlIcone = getClass().getClassLoader().getResource("img/AIFome.png");
            if (urlIcone != null) {
                Image icone = new ImageIcon(urlIcone).getImage();
                this.setIconImage(icone);
            } else {
                System.err.println("Erro: O arquivo de ícone não foi encontrado no caminho especificado.");
            }
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o ícone da aplicação: " + e.getMessage());
        }
    }

    /**
     * Substitui o conteúdo atual da janela por um novo painel de visualização de forma fluida.
     * <p>
     * Este método limpa o content pane antigo, acopla o novo componente centralizado, dispara o
     * utilitário {@link RemoveEmoji} para mitigar bugs de fontes "tofu boxes" no Windows e força
     * o redimensionamento/redesenho da árvore de componentes Swing.
     * </p>
     * * @param panel O {@link JPanel} contendo a interface gráfica da tela de destino.
     */
    public void configuraTela(JPanel panel){
        getContentPane().removeAll();
        getContentPane().add(panel, BorderLayout.CENTER);
        RemoveEmoji.aplicar(panel);
        revalidate();
        repaint();
    }

    /**
     * Aplica uma estilização padronizada (padrão Flat/Material) a um botão genérico do Swing.
     * * @param botao O componente {@link JButton} que receberá as alterações visuais.
     * @param cor A cor de fundo principal de destaque para o botão.
     */
    public void configuraBotao(JButton botao , Color cor){
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Arial", Font.BOLD, 16));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
    }

    /**
     * Recupera os dados de login do usuário atualmente autenticado na sessão global.
     * * @return O objeto {@link Login} preenchido, ou {@code null} se nenhum usuário estiver logado.
     */
    public static Login getLogin() {
        return Telabase.login;
    }

    /**
     * Define ou atualiza os dados da sessão do usuário autenticado no ecossistema da aplicação.
     * * @param login O objeto {@link Login} contendo as novas credenciais e privilégios de acesso.
     */
    public static void setLogin(Login login) {
        Telabase.login = login;
    }
}

/**
 * Componente customizado de botão que renderiza suas bordas e fundo de forma arredondada.
 * Utiliza recursos do {@link Graphics2D} com suavização de serrilhado (Anti-aliasing).
 */
class BotaoArredondado extends JButton {

    /** O raio em pixels utilizado para realizar a curvatura dos cantos do botão. */
    private int raioDoArredondamento;

    /**
     * Construtor para criação de um botão arredondado personalizado.
     * * @param texto Mensagem de texto exibida no centro do botão.
     * @param raio Grau de curvatura dos cantos em pixels.
     * @param cor Cor de fundo padrão do botão no estado estático.
     * @param tam Tamanho da fonte tipográfica Arial em negrito.
     */
    public BotaoArredondado(String texto, int raio , Color cor, int tam) {
        super(texto);
        this.raioDoArredondamento = raio;
        setBackground(cor);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, tam));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setRolloverEnabled(true);
    }

    /**
     * Intercepta a rotina de pintura do ciclo do Swing para desenhar a geometria arredondada.
     * Fornece efeitos visuais para os estados dinâmicos "Pressionado" e "Hover" (Rollover).
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2.setColor(getBackground().darker());
        } else if (getModel().isRollover()) {
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), raioDoArredondamento, raioDoArredondamento);
            g2.setColor(new Color(255, 255, 255, 50));
        } else {
            g2.setColor(getBackground());
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), raioDoArredondamento, raioDoArredondamento);
        g2.dispose();
        super.paintComponent(g);
    }
}

/**
 * Caixa de texto customizada para captura de dados alfanuméricos com cantos arredondados.
 * Remove os preenchimentos quadrados padrão e insere espaçamento (padding) interno de segurança.
 */
class CampoTextoArredondado extends JTextField {

    /** O raio de curvatura dos cantos do campo em pixels. */
    private int raio;

    /** A cor aplicada à linha limítrofe do contorno. */
    private Color corBorda;

    /**
     * Inicializa um campo de texto arredondado.
     * * @param colunas Quantidade sugerida de largura em caracteres.
     * @param raio Grau de curvatura dos cantos em pixels.
     * @param corBorda Cor da linha externa reguladora da borda.
     * @param tam Tamanho da fonte textual interna.
     */
    public CampoTextoArredondado(int colunas, int raio, Color corBorda, int tam) {
        super(colunas);
        this.raio = raio;
        this.corBorda = corBorda;
        setOpaque(false);
        setBorder(new EmptyBorder(8, 12, 8, 12));
        setFont(new Font("Arial", Font.PLAIN, tam));
        setCaretColor(Color.BLACK);
    }

    /**
     * Redimensiona de forma dinâmica o tamanho do corpo da fonte sem alterar suas outras propriedades.
     * * @param tamanho O novo tamanho numérico da fonte.
     */
    public void configurarTamanho(int tamanho){
        Font font=getFont();
        setFont(new Font(font.getFontName(),font.getStyle(),tamanho));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
        g2.setColor(corBorda);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
        g2.dispose();
        super.paintComponent(g);
    }
}

/**
 * Caixa de texto mascarada para captura segura de senhas com acabamento arredondado.
 * Oculta os caracteres digitados protegendo a confidencialidade do usuário.
 */
class CampoSenhaArredondado extends JPasswordField {

    /** O raio de curvatura dos cantos do campo de senha em pixels. */
    private int raio;

    /** A cor aplicada à linha limítrofe do contorno. */
    private Color corBorda;

    /**
     * Inicializa um campo de senha arredondado.
     * * @param colunas Quantidade sugerida de largura em caracteres.
     * @param raio Grau de curvatura dos cantos em pixels.
     * @param corBorda Cor da linha externa reguladora da borda.
     * @param tam Tamanho da fonte interna dos caracteres/máscara.
     */
    public CampoSenhaArredondado(int colunas, int raio, Color corBorda, int tam) {
        super(colunas);
        this.raio = raio;
        this.corBorda = corBorda;
        setOpaque(false);
        setBorder(new EmptyBorder(8, 12, 8, 12));
        setFont(new Font("Arial", Font.PLAIN, tam));
    }

    /**
     * Redimensiona de forma dinâmica o tamanho do corpo da fonte da máscara da senha.
     * * @param tamanho O novo tamanho numérico da fonte.
     */
    public void configurarTamanho(int tamanho){
        Font font=getFont();
        setFont(new Font(font.getFontName(),font.getStyle(),tamanho));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
        g2.setColor(corBorda);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
        g2.dispose();
        super.paintComponent(g);
    }
}

/**
 * Painel customizado estruturado em {@link GridBagLayout} para organizar e renderizar o fundo
 * dos formulários centrais (como Login e Cadastro) com uma caixa flutuante arredondada e margens de proteção.
 */
class PainelFormulario extends JPanel {

    /** Largura de referência destinada ao cálculo da caixa interna. */
    private int Width;

    /** Altura de referência destinada ao cálculo da caixa interna. */
    private int Heigth;

    /** Cor de preenchimento interno do bloco de formulários. */
    private Color cor;

    /**
     * Inicializa o painel organizador do formulário.
     * * @param Width Largura base de cálculo disponível para o painel.
     * @param Heigth Altura base de cálculo disponível para o painel.
     * @param cor Cor a ser estampada no fundo do contêiner arredondado.
     */
    public PainelFormulario(int Width, int Heigth, Color cor) {
        this.Width = Width;
        this.Heigth = Heigth;
        this.cor = cor;
        setLayout(new GridBagLayout());
        setOpaque(false);
    }

    /**
     * Desenha um cartão/bloco retangular flutuante arredondado centralizado aplicando uma
     * margem fixa de 50 pixels em relação ao tamanho total do painel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int margem = 50;
        int x = margem;
        int y = margem;
        int largura = this.Width - (margem * 2);
        int altura = this.Heigth - (margem * 2) ;
        int raioCurva = 25;

        g2.setColor(this.cor);
        g2.fillRoundRect(x, y, largura, altura, raioCurva, raioCurva);
        g2.drawRoundRect(x, y, largura, altura, raioCurva, raioCurva);
        g2.dispose();
        super.paintComponent(g);
    }
}

/**
 * Rótulo de texto simplificado baseado em {@link JLabel} configurado por padrão
 * com alinhamento horizontal centralizado automático para títulos e instruções.
 */
class Texto extends JLabel{

    /**
     * Cria um rótulo de texto centralizado.
     * * @param texto A string de texto que será estampada.
     */
    public Texto(String texto){
        super(texto);
        setHorizontalAlignment(SwingConstants.CENTER);
    }
}

/**
 * Caixa de seleção (JCheckBox) com identidade visual customizada e vetorizada.
 * <p>
 * Abandona o visual tradicional do Java para renderizar um quadrado plano moderno que,
 * quando marcado, executa o desenho geométrico de um sinal de confirmação (check/V) dinâmico
 * e proporcional ao tamanho definido.
 * </p>
 */
class CheckboxCustomizado extends JCheckBox {

    /** A cor de preenchimento interna do quadrado quando o estado for marcado. */
    private Color corMarcado;

    /** Tamanho em pixels das arestas do quadrado da caixa de marcação. */
    private int tamanhoQuadrado;

    /** Tamanho numérico para a tipografia da legenda do campo. */
    private int tamanhoFonte;

    /**
     * Constrói uma caixa de seleção estilizada e escalável.
     * * @param texto Legenda descritiva exposta ao lado do checkbox.
     * @param corMarcado Cor temática adotada no preenchimento do estado selecionado.
     * @param tamanhoQuadrado Dimensão em pixels do quadrado de controle (largura/altura).
     * @param tamanhoFonte Tamanho em pontos para a fonte Arial do texto associado.
     */
    public CheckboxCustomizado(String texto, Color corMarcado, int tamanhoQuadrado, int tamanhoFonte) {
        super(texto);
        this.corMarcado = corMarcado;
        this.tamanhoQuadrado = tamanhoQuadrado;
        this.tamanhoFonte = tamanhoFonte;

        setOpaque(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setFont(new Font("Arial", Font.PLAIN, tamanhoFonte));
        setIcon(new ImageIcon()); // Descarta o ícone nativo
        setBorder(BorderFactory.createEmptyBorder(0, tamanhoQuadrado + 12, 0, 0));
    }

    /**
     * Executa a renderização matemática do componente. Se o estado estiver desmarcado, desenha
     * uma borda cinza sutil; se marcado, renderiza o preenchimento colorido e traça as duas linhas
     * do vetor "V" usando frações proporcionais ao {@code tamanhoQuadrado}.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = 5;
        int y = (getHeight() - tamanhoQuadrado) / 2;
        int raioCurva = tamanhoQuadrado / 3;

        if (isSelected()) {
            g2.setColor(corMarcado);
            g2.fillRoundRect(x, y, tamanhoQuadrado, tamanhoQuadrado, raioCurva, raioCurva);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(tamanhoQuadrado * 0.12f));

            g2.drawLine(x + (int) (tamanhoQuadrado * 0.22), y + (int) (tamanhoQuadrado * 0.5),
                    x + (int) (tamanhoQuadrado * 0.44), y + (int) (tamanhoQuadrado * 0.72));
            g2.drawLine(x + (int) (tamanhoQuadrado * 0.44), y + (int) (tamanhoQuadrado * 0.72),
                    x + (int) (tamanhoQuadrado * 0.77), y + (int) (tamanhoQuadrado * 0.27));
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x, y, tamanhoQuadrado, tamanhoQuadrado, raioCurva, raioCurva);

            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, tamanhoQuadrado, tamanhoQuadrado, raioCurva, raioCurva);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}

/**
 * Card visual em formato de bloco horizontal que sumariza os dados de um restaurante cadastrado.
 * <p>
 * Renderiza uma área de imagem arredondada simulada (via Emoji), o título do estabelecimento,
 * avaliações por estrelas baseadas em strings matemáticas ou formatações de taxas financeiras.
 * </p>
 */
class CardRestaurante extends JPanel {

    /**
     * Constrói um cartão informativo clicável para exibição em listas de feeds de restaurantes.
     * * @param nome Título fantasia do restaurante.
     * @param nota Nota numérica de pontuação ou o valor base da taxa de entrega em texto.
     * @param avaliacao Número bruto representativo da contagem de estrelas ou texto de tempo estimado.
     * @param emj Caractere de emoji representativo do ícone de categoria (ex: "🏪").
     */
    public CardRestaurante(String nome, String nota, String avaliacao ,String emj) {
        setLayout(new BorderLayout(15, 0));
        setBackground(Color.WHITE);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setPreferredSize(new Dimension(400, 80));
        setMaximumSize(new Dimension(400, 80));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 240, 240), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JPanel emoji = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        emoji.setPreferredSize(new Dimension(60, 60));
        add(emoji, BorderLayout.WEST);

        JLabel lblEmoji = new JLabel(emj);
        lblEmoji.setFont(new Font("Arial",Font.PLAIN,28));
        emoji.add(lblEmoji);

        JPanel infos = new JPanel(new GridLayout(2, 1, 0, 5));
        infos.setOpaque(false);

        JLabel lblNome = new JLabel(nome);
        lblNome.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblDetalhes;
        if(Objects.equals(emj, "🏪")) {
            int numNota = Integer.parseInt(avaliacao);
            avaliacao = "⭐".repeat(numNota);
            lblDetalhes = new JLabel("⭐ " + nota + " • " + avaliacao );
        } else {
            lblDetalhes = new JLabel("R$" + nota + " • " + avaliacao);
        }

        lblDetalhes.setFont(new Font("Arial", Font.PLAIN, 13));
        lblDetalhes.setForeground(Color.GRAY);

        infos.add(lblNome);
        infos.add(lblDetalhes);
        add(infos, BorderLayout.CENTER);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}

/**
 * Painel de sobreposição (Overlay) translúcido e escurecido utilizado para criar o efeito visual
 * de foco (Modal/Dimmer) sobre a interface principal, impedindo cliques em botões de fundo enquanto
 * um menu suspenso ou caixa de diálogo customizada estiver visível em primeiro plano.
 */
class Overlay extends JPanel {

    /**
     * Construtor do painel de sobreposição bloqueador.
     */
    public Overlay() {
        setOpaque(false);
        setVisible(false);
    }

    /**
     * Pinta uma camada retangular preenchendo toda a extensão do componente com uma cor cinza escura
     * contendo um nível de transparência Alpha controlado (160 de 255).
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(40, 40, 40, 160));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}