package view;

import model.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TelaPerfil extends TelaMenu {

    public TelaPerfil(Telabase sist){
        super(sist); // 1. Inicializa a casca (Header + Menu + Overlay) da classe abstrata


        JPanel container = new JPanel();
        container.setBackground(Color.WHITE);
        container.setLayout(new BorderLayout());


        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        container.add(panel, BorderLayout.CENTER);


        panel.add(Box.createVerticalStrut(30));


        PainelFormulario panel2 = new PainelFormulario(500, 400, Color.red);
        panel2.setPreferredSize(new Dimension(500, 400));
        panel2.setMaximumSize(new Dimension(500, 400));


        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(panel2);


        String user = Login.GetUser();
        String tipo = Login.GetTipo();


        Texto txtNome = new Texto(user);
        txtNome.setFont(new Font("Arial", Font.BOLD, 30));
        txtNome.setForeground(Color.WHITE); // Se o fundo é vermelho, texto branco destaca mais

// CORREÇÃO 2: Como panel2 é FlowLayout, adicionamos o texto normalmente sem o 'gbc'
        panel2.add(txtNome);

// Espaçamento entre o painel vermelho e as informações abaixo dele
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

// Texto do Tipo de Conta
        Texto txtTipo = new Texto("Tipo de conta: " + tipo);
        txtTipo.setFont(new Font("Arial", Font.BOLD, 20));
        txtTipo.setAlignmentX(Component.CENTER_ALIGNMENT); // Alinhamento correto
        panel.add(txtTipo);

// Espaçamento entre o texto e o botão de logout
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

// Botão de Logout
        BotaoArredondado bnt_logout = getBotaoArredondado(); // Assume que você tem esse método criado
        bnt_logout.setAlignmentX(Component.CENTER_ALIGNMENT); // Alinhamento correto
        panel.add(bnt_logout);

// Mágica final: Envia toda essa interface para o centro da TelaMenu
        setConteudoInterno(container);
//        add(container);
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
}
