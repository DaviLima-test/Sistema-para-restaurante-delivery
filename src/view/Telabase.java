package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public abstract class Telabase extends JFrame {
    protected static int Width;
    protected static int Height;
    private int raioDoArredondamento;

    public Telabase(){
        setTitle("AiFome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        Width = Toolkit.getDefaultToolkit().getScreenSize().width;
        Height = Toolkit.getDefaultToolkit().getScreenSize().height;
        setSize(Width,Height);
    }
    public void configuraTela(JPanel panel){
        setContentPane(panel);
        setVisible(true);
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
}
class BotaoArredondado extends JButton {
    private int raioDoArredondamento;

    public BotaoArredondado(String texto, int raio , Color cor,int tam) {
        super(texto);
        this.raioDoArredondamento = raio;

        setBackground(cor);
        setForeground(Color.WHITE);             // Texto Branco
        setFont(new Font("Arial", Font.BOLD, tam));

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

    public PainelFormulario() {
        // Define o layout vertical para as opções
        setLayout(new GridBagLayout());

        // Torna o painel transparente para o fundo da janela aparecer nas bordas externos do retângulo
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // Ativa o antialiasing para as bordas do retângulo ficarem suaves
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Define a margem (espaço) entre a borda da tela e o retângulo branco
        int margem = 50;

        int x = margem;
        int y = margem;
        int largura = getWidth() - (margem * 2);
        int altura = getHeight() - (margem * 2) ;
        int raioCurva = 25; // Define o quão arredondado será o retângulo

        // 1. Desenha a sombra ou preenchimento do retângulo branco
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x, y, largura, altura, raioCurva, raioCurva);


        g2.setColor(new Color(230, 230, 230));
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