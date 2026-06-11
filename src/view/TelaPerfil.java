package view;

import model.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TelaPerfil extends JPanel {

    public TelaPerfil(){

        this.setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        String user=Login.GetUser();
        String tipo=Login.GetTipo();
        add(criaHeader(),BorderLayout.NORTH);
        Texto txt = new Texto("Usuario:"+user);
        txt.setFont(new Font("Arial", Font.BOLD, 30));
        txt.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(txt);

        txt = new Texto("Tipo de conta:"+tipo);
        txt.setFont(new Font("Arial", Font.BOLD, 30));
        txt.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(txt);
        add(panel,BorderLayout.CENTER);
        BotaoArredondado bnt_logout = getBotaoArredondado();
        panel.add(bnt_logout);
    }


    private BotaoArredondado getBotaoArredondado() {
        BotaoArredondado bnt_logout = new BotaoArredondado("Logout(APAGA O COOKIE DE SESSAO)",20,Color.RED,20 );
        bnt_logout.addActionListener(e ->{
                Object[] opcoes = {"Sim", "Não"};
                int resposta = JOptionPane.showOptionDialog(
                        SwingUtilities.getWindowAncestor(this), // Pai real da janela
                        "Você realmente deseja apagar os cookies?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, opcoes, opcoes[0]
                );

                if (resposta == JOptionPane.YES_OPTION) {
                    Login.apagarCookie();
                    System.exit(0);

            }
        });
        return bnt_logout;
    }

    public JPanel criaHeader(){
    JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(800, 80));

        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(0, 20, 0, 20);
    gbc.gridx = 0;
    gbc.weightx = 0.0;
        JButton btnLogo = new JButton("AIFood");
        btnLogo.setFont(new Font("Arial", Font.BOLD, 30));
        btnLogo.setForeground(new Color(234, 16, 34)); // Vermelho iFood

        // Deixa o botão com aparência de texto (sem bordas ou fundo de botão)
        btnLogo.setBorderPainted(false);
        btnLogo.setContentAreaFilled(false);
        btnLogo.setFocusPainted(false);
        btnLogo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Ação do Botão
        btnLogo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Botão AIFood clicado! Tentando mudar de tela...");

                // Pega a janela base a partir do próprio botão clicado
                Window ancestral = SwingUtilities.getWindowAncestor(btnLogo);

                if (ancestral instanceof Telabase) {
                    Telabase sist = (Telabase) ancestral;

                    // Instancia a nova tela
                    TelaPrincipal tl = new TelaPrincipal();

                    // Chama a função da Telabase
                    sist.configuraTela(tl);
                } else {
                    System.out.println("Erro: Ancestor não é uma Telabase ou é nulo!");
                }
            }
        });

        gbc.gridx = 1;
        p.add(btnLogo, gbc);




    CampoTextoArredondado busca = new CampoTextoArredondado(18, 15, new Color(240, 240, 240),30);
        busca.setText(" Buscar Restaurantes e pratos ...");
        busca.setForeground(Color.GRAY);
    gbc.gridx = 2;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
        busca.addFocusListener(new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
            if(busca.getText().equals(" Buscar Restaurantes e pratos ...")){
                busca.setText("");
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if(busca.getText().isEmpty()){
                busca.setText(" Buscar Restaurantes e pratos ...");
            }
        }
    });
        p.add(busca, gbc);

        return p;
}
}
