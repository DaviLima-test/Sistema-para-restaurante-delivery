package view;

import javax.swing.*;
import java.awt.*;

public abstract class Telabase extends JFrame {
    protected static int Width;
    protected static int Height;
    private int raioDoArredondamento;

    public Telabase(){
        setTitle("AiFome - Inicial");
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
    public void configuraBotao(JButton botao){

        botao.setBackground(Color.decode("#EA1D2C")); // Vermelho
        botao.setForeground(Color.WHITE);             // Texto Branco
        botao.setFont(new Font("Arial", Font.BOLD, 16));

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);

    }
    public void arrendondarbotao(String texto , int raio){
      BotaoArredondado botao = new BotaoArredondado(texto ,raio);

    }



        // Construtor: recebe o texto e o nível de arredondamento (ex: 20 ou 30)

}
class BotaoArredondado extends JButton {
    private int raioDoArredondamento;

    // Construtor: recebe o texto e o nível de arredondamento (ex: 20 ou 30)
    public BotaoArredondado(String texto, int raio) {
        super(texto);
        this.raioDoArredondamento = raio;

        // Configurações obrigatórias para o arredondamento funcionar limpo
        setContentAreaFilled(false);
        setFocusPainted(false);      // Remove a auréola azul feia
        setBorderPainted(false);     // Desativa a borda quadrada do Windows
        setOpaque(false);
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
            g2.setColor(getBackground().brighter()); // Efeito hover integrado!
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