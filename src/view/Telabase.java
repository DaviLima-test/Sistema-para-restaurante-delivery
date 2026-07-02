package view;

import model.Login;
import util.RemoveEmoji;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.Objects;

public class Telabase extends JFrame {
    public static int Width;
    public static int Height;
    private int raioDoArredondamento;
    private static Login login;
    public Telabase(){
        setTitle("AiFome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        Width = Toolkit.getDefaultToolkit().getScreenSize().width;
        Height = Toolkit.getDefaultToolkit().getScreenSize().height;
        setSize(Width,Height);
        try {

           File arquivoIcone = new File("img/AIFome.png");

            if (arquivoIcone.exists()) {

                Image icone = new ImageIcon("img/AIFome.png").getImage();

                // Aplica o ícone na janela principal
                this.setIconImage(icone);
            } else {
                System.err.println("Erro: O arquivo de ícone não foi encontrado no caminho especificado.");
            }
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o ícone da aplicação: " + e.getMessage());
        }

    }
    public void configuraTela(JPanel panel){

        getContentPane().removeAll();
        getContentPane().add(panel, BorderLayout.CENTER);
        RemoveEmoji.aplicar(panel); // remove emojis de botoes e labels se o SO for Windows
        revalidate();
        repaint();

    }


    public void configuraBotao(JButton botao , Color cor){

        botao.setBackground(cor); // Vermelho
        botao.setForeground(Color.WHITE);             // Texto Branco
        botao.setFont(new Font("Arial", Font.BOLD, 16));

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);

    }

    public static Login getLogin() {
        return Telabase.login;
    }

    public static void setLogin(Login login) {
        Telabase.login = login;
    }
}
class BotaoArredondado extends JButton {
    private int raioDoArredondamento;

    public BotaoArredondado(String texto, int raio , Color cor,int tam) {
        super(texto);
        this.raioDoArredondamento = raio;

        setBackground(cor);
        setForeground(Color.WHITE);             // Texto Branco
        setFont(new Font("Arial", Font.BOLD, tam));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setRolloverEnabled(true);

    }


    // O segredo está aqui: nós redesenhamos o fundo do botão em formato oval/arredondado
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Ativa o antialiasing para a borda não ficar serrilhada (efeito "pixelado")
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Se o botão for pressionado, muda levemente a cor para dar feedback ao usuário
        if (getModel().isPressed()) {
            g2.setColor(getBackground().darker());
        } else if (getModel().isRollover()) {
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), raioDoArredondamento, raioDoArredondamento);
            g2.setColor(new Color(255, 255, 255, 50));
        } else {
            g2.setColor(getBackground());
        }

        // Desenha o fundo arredondado
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), raioDoArredondamento, raioDoArredondamento);

        g2.dispose();

        // Manda o Java desenhar o texto e o ícone por cima do fundo que criamos
        super.paintComponent(g);
    }

}
class CampoTextoArredondado extends JTextField {
    private int raio;
    private Color corBorda;
    public CampoTextoArredondado(int colunas, int raio, Color corBorda ,int tam ) {
        super(colunas);
        this.raio = raio;
        this.corBorda = corBorda;

        // Importante: Desativa o fundo e a borda padrão quadrada do Swing
        setOpaque(false);
        setBorder(new EmptyBorder(8, 12, 8, 12)); // Cria um "padding" interno pro texto não grudar na borda
        setFont(new Font("Arial", Font.PLAIN, tam));
        setCaretColor(Color.BLACK); // Cor do traço que pisca ao digitar

    }
    public void configurarTamanho(int tamanho){
        Font font=getFont();
        setFont(new Font(font.getFontName(),font.getStyle(),tamanho));
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Desenha o fundo branco (ou cinza bem claro) interno
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);

        // Desenha o contorno (a linha da borda arredondada)
        g2.setColor(corBorda);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);

        g2.dispose();

        // Permite que o Java desenhe o texto que o usuário digitou por cima
        super.paintComponent(g);
    }
}
class CampoSenhaArredondado extends JPasswordField {
    private int raio;
    private Color corBorda;

    public CampoSenhaArredondado(int colunas, int raio, Color corBorda,int tam) {
        super(colunas);
        this.raio = raio;
        this.corBorda = corBorda;

        setOpaque(false);
        setBorder(new EmptyBorder(8, 12, 8, 12)); // Padding
        setFont(new Font("Arial", Font.PLAIN, tam));

    }
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
class PainelFormulario extends JPanel {
    private int  Width;
    private int Heigth;
    private Color cor;
    public PainelFormulario(int Width,int Heigth,Color cor) {

        this.Width = Width;
        this.Heigth = Heigth;
        this.cor = cor;
        setLayout(new GridBagLayout());


        setOpaque(false);
    }

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
        g2.setColor(Color.WHITE);
        g2.setColor(this.cor);
        g2.fillRoundRect(x,y,largura,altura,raioCurva,raioCurva);
        g2.drawRoundRect(x, y, largura, altura, raioCurva, raioCurva);

        g2.dispose();
        super.paintComponent(g);
    }
}
class Texto extends JLabel{

    public Texto(String texto  ){
        super(texto);

        setHorizontalAlignment(SwingConstants.CENTER);

    }
}
class CheckboxCustomizado extends JCheckBox {
    private Color corMarcado;
    private int tamanhoQuadrado;
    private int tamanhoFonte;

    // O construtor agora pede o tamanho do quadrado e da fonte
    public CheckboxCustomizado(String texto, Color corMarcado, int tamanhoQuadrado, int tamanhoFonte) {
        super(texto);
        this.corMarcado = corMarcado;
        this.tamanhoQuadrado = tamanhoQuadrado;
        this.tamanhoFonte = tamanhoFonte;

        setOpaque(false); // Transparente para o retângulo branco de fundo
        setFocusPainted(false); // Remove o tracejado ao clicar
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor de mãozinha

        // Define a fonte fixa passada na chamada
        setFont(new Font("Arial", Font.PLAIN, tamanhoFonte));

        // Remove o ícone padrão do Java para usarmos o nosso desenho
        setIcon(new ImageIcon());

        // Ajusta o espaço para o texto não ficar em cima do quadrado (tamanho + margem de 12px)
        setBorder(BorderFactory.createEmptyBorder(0, tamanhoQuadrado + 12, 0, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calcula a posição para o quadrado ficar centralizado verticalmente com o texto
        int x = 5;
        int y = (getHeight() - tamanhoQuadrado) / 2;
        int raioCurva = tamanhoQuadrado / 3; // Curvatura proporcional ao tamanho escolhido

        if (isSelected()) {
            // Estado Marcado: Preenche com a cor escolhida
            g2.setColor(corMarcado);
            g2.fillRoundRect(x, y, tamanhoQuadrado, tamanhoQuadrado, raioCurva, raioCurva);

            // Desenha o "V" branco proporcional ao tamanho do quadrado
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(tamanhoQuadrado * 0.12f)); // Espessura da linha proporcional

            g2.drawLine(x + (int) (tamanhoQuadrado * 0.22), y + (int) (tamanhoQuadrado * 0.5),
                    x + (int) (tamanhoQuadrado * 0.44), y + (int) (tamanhoQuadrado * 0.72));
            g2.drawLine(x + (int) (tamanhoQuadrado * 0.44), y + (int) (tamanhoQuadrado * 0.72),
                    x + (int) (tamanhoQuadrado * 0.77), y + (int) (tamanhoQuadrado * 0.27));
        } else {
            // Estado Desmarcado: Quadrado branco com borda cinza
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
class CardRestaurante extends JPanel {

    public CardRestaurante(String nome, String nota, String avaliacao ,String emj) {
        setLayout(new BorderLayout(15, 0));
        setBackground(Color.WHITE);
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // Define uma altura fixa para o card e largura flexível
        setPreferredSize(new Dimension(400, 80)); // Largura fixa de 320px para caber vários na tela lateralmente
        setMaximumSize(new Dimension(400, 80));

        // Borda suave ao redor do card para parecer um bloco separado
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 240, 240), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // 1. "FOTO" DO RESTAURANTE (Substituída por um quadrado cinza arredondado/ícone)
        JPanel emoji = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); // Círculo/Quadrado suave
                g2.dispose();
            }
        };
        emoji.setPreferredSize(new Dimension(60, 60));
        add(emoji, BorderLayout.WEST);

        JLabel lblEmoji = new JLabel(emj);
        lblEmoji.setFont(new Font("Arial",Font.PLAIN,28));
        emoji.add(lblEmoji);

        // 2. INFORMAÇÕES (Nome, Nota, Tempo)
        JPanel infos = new JPanel(new GridLayout(2, 1, 0, 5));
        infos.setOpaque(false);

        JLabel lblNome = new JLabel(nome);
        lblNome.setFont(new Font("Arial", Font.BOLD, 16));


        // Detalhes em cinza (Nota, Tempo, Frete) igual ao app
        JLabel lblDetalhes;
        if(Objects.equals(emj, "🏪")) {
            int numNota = Integer.parseInt(avaliacao);
            avaliacao = "⭐".repeat(numNota);
            lblDetalhes = new JLabel("⭐ " + nota + " • " +avaliacao );
        }else{
            lblDetalhes = new JLabel("R$" + nota + " • " + avaliacao);
        }

        lblDetalhes.setFont(new Font("Arial", Font.PLAIN, 13));
        lblDetalhes.setForeground(Color.GRAY);

        infos.add(lblNome);
        infos.add(lblDetalhes);

        add(infos, BorderLayout.CENTER);

        // Transforma o card todo em um botão clicável visualmente
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


}
class Overlay extends JPanel {

    public Overlay() {
        setOpaque(false);
        setVisible(false);
        // Bloqueia cliques do mouse no resto do cinza para o usuário não clicar no feed sem querer
        //addMouseListener(new MouseAdapter() {});
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();


        g2.setColor(new Color(40, 40, 40, 160));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose();
    }
}