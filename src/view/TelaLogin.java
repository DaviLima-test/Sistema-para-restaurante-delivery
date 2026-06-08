package view;

import javax.swing.*;
import model.Login;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import view.Telabase;
public class TelaLogin extends JPanel{
    JPanel panelesq;
    JPanel paneldir;
    JPanel painelBotoes;
    JButton botao;
    CampoTextoArredondado email;
    CampoSenhaArredondado senha;
    Texto txt;
    CheckboxCustomizado chk_cliente;
    CheckboxCustomizado chk_entregador;
    CheckboxCustomizado chk_restaurante;
    public TelaLogin(){

        setLayout(new GridLayout(1,2));
        panelesq = new JPanel();
        paneldir = new PainelFormulario(Telabase.Width/2,Telabase.Height,Color.WHITE);
        this.setBackground(Color.decode("#e96769"));
        panelesq.setBackground(Color.decode("#e96769"));
        ImageIcon banner = new ImageIcon("img/banner_delivery.jpg");

        Image novaImg = banner.getImage().getScaledInstance(
                Telabase.Width/2,
                Telabase.Height,
                Image.SCALE_SMOOTH
        );
        banner.setImage(novaImg);
        JLabel imagem = new JLabel(banner);

        panelesq.add(imagem);

        email  = new CampoTextoArredondado(20,20,Color.BLACK,50);
        senha = new CampoSenhaArredondado(20,20,Color.BLACK,50);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 50, 10, 50);
        //gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        //gbc.weighty = 0.1;
        gbc.gridx = 0;

        gbc.gridy = 1;
        txt = new Texto("Login");
        txt.setFont(new Font("Arial",Font.BOLD,60));
        paneldir.add(txt,gbc);
        gbc.gridy++;
        paneldir.add(Box.createVerticalStrut(60),gbc);
        //paneldir.add(Box.createVerticalStrut(30));
        gbc.gridy++;
        txt = new Texto("Email");
        txt.setFont(new Font("Arial", Font.BOLD, 30));
        paneldir.add(txt,gbc);
        gbc.gridy++;
        paneldir.add(email,gbc);
        gbc.gridy++;

        txt = new Texto("Senha:");
        txt.setFont(new Font("Arial", Font.BOLD, 30));
        paneldir.add(txt,gbc);
        gbc.gridy++;
        paneldir.add(senha,gbc);
        gbc.gridy++;

        gbc.anchor = GridBagConstraints.CENTER;

        painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelBotoes.setOpaque(false);
        BotaoArredondado logar = new BotaoArredondado("Logar",20 , Color.decode("#c1291e"),35);
        BotaoArredondado cadastro = new BotaoArredondado("Cadastro",20,Color.decode("#c1291e"),35);
        BotaoArredondado sair = new BotaoArredondado("Sair",20 , Color.decode("#c1291e"),35);
        painelBotoes.add(sair);
        painelBotoes.add(cadastro);
        painelBotoes.add(logar);
        paneldir.add(painelBotoes,gbc);
        cadastro.addActionListener(e -> {
            TelaCadastro tc = new TelaCadastro();
            Telabase sist = (Telabase) SwingUtilities.getWindowAncestor(this);
            if(sist != null){
                sist.configuraTela(tc);
            }
        });

        logar.addActionListener(e ->{
            String str_email=email.getText();
            String str_senha=new String(senha.getPassword());
            if(str_email.isEmpty() || str_senha.isEmpty()  ){
                JOptionPane.showMessageDialog(this,"Por favor, preencha todos os campos");
            }else{
                System.out.println("Requisicao enviada para o BD\n" +
                        "\nEmail:"+str_email+
                        "\nSenha:"+str_senha.length());
                if(Login.realizarLogin(str_email,str_senha)) {

                    TelaPrincipal tl = new TelaPrincipal();
                    Telabase sist = (Telabase) SwingUtilities.getWindowAncestor(this);
                    if (sist != null)
                        sist.configuraTela(tl);
                }
            }
        });
        sair.addActionListener(e -> {
            Object[] opcoes = {"Sim", "Não"};

            int resposta = JOptionPane.showOptionDialog(
                    this,
                    "Você realmente deseja sair?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,             // Usa o ícone padrão
                    opcoes,           // Array com os textos dos botões
                    opcoes[0]         // Botão focado por padrão (Sim)
            );

            if (resposta == JOptionPane.YES_OPTION) {
                // Código para sair...
                System.exit(0); // Fecha o programa completamente, se preferir
            }
        });
        this.add(panelesq);
        this.add(paneldir);

    }

}


