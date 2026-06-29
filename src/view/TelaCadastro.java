package view;

import javax.swing.*;

import bd.BancoDados;
import model.Login;

import java.awt.*;
import java.awt.event.ActionListener;

public class TelaCadastro extends JPanel{
    JPanel panelesq;
    JPanel paneldir;
    JPanel painelBotoes;
    JButton botao;
    CampoTextoArredondado usuario;
    CampoTextoArredondado email;
    CampoSenhaArredondado senha;
    CampoTextoArredondado localizacao;
    Texto txt;
    CheckboxCustomizado chk_cliente;
    CheckboxCustomizado chk_entregador;
    CheckboxCustomizado chk_restaurante;
    public TelaCadastro(){

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

        usuario = new CampoTextoArredondado(20,20,Color.BLACK,30);
        email  = new CampoTextoArredondado(20,20,Color.BLACK,30);
        senha = new CampoSenhaArredondado(20,20,Color.BLACK,30);
        localizacao = new CampoTextoArredondado(20,20,Color.BLACK,30);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 50, 10, 50);
        //gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        //gbc.weighty = 0.1;
        gbc.gridx = 0;

        gbc.gridy = 1;

        txt = new Texto("Cadastro");
        txt.setFont(new Font("Arial",Font.BOLD,40));
        paneldir.add(txt,gbc);
        gbc.gridy++;
        paneldir.add(Box.createVerticalStrut(40),gbc);
        //paneldir.add(Box.createVerticalStrut(30));
        txt = new Texto("Usuario:");
        txt.setFont(new Font("Arial", Font.BOLD, 30));

        paneldir.add(txt,gbc);
        gbc.gridy++;
        paneldir.add(usuario,gbc);
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

        txt = new Texto("Localização: ");
        txt.setFont(new Font("Arial",Font.BOLD,30));
        paneldir.add(txt,gbc);
        gbc.gridy++;
        paneldir.add(localizacao,gbc);
        gbc.gridy++;

        txt = new Texto("Selecione o seu perfil");
        txt.setFont(new Font("Arial", Font.BOLD, 30));
        paneldir.add(txt,gbc);
        gbc.gridy ++;

        gbc.anchor = GridBagConstraints.WEST;

        chk_cliente = new CheckboxCustomizado("CLIENTE", Color.BLUE,30,30);
        paneldir.add(chk_cliente,gbc);
        gbc.gridy++;

        chk_entregador = new CheckboxCustomizado("ENTREGADOR",Color.BLUE,30,30);
        paneldir.add(chk_entregador,gbc);
        gbc.gridy++;

        chk_restaurante = new CheckboxCustomizado("RESTAURANTE",Color.BLUE,30,30);
        paneldir.add(chk_restaurante,gbc);
        gbc.gridy++;


        gbc.anchor = GridBagConstraints.CENTER;
        painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelBotoes.setOpaque(false);
        BotaoArredondado cadastrar = new BotaoArredondado("Cadastrar",20 , Color.decode("#c1291e"),35);
        BotaoArredondado sair = new BotaoArredondado("Sair",20 , Color.decode("#c1291e"),35);
        BotaoArredondado voltar = new BotaoArredondado("Voltar",20,Color.decode("#c1291e"),35);
        painelBotoes.add(sair);
        painelBotoes.add(voltar);
        painelBotoes.add(cadastrar);
        paneldir.add(painelBotoes,gbc);
        cadastrar.setEnabled(false);

        voltar.addActionListener(e -> {
            TelaLogin tl = new TelaLogin();
            Telabase sist = (Telabase) SwingUtilities.getWindowAncestor(this);
            if(sist != null)
                sist.configuraTela(tl);
        });
        ActionListener gerenciarBotao = e -> {
            // Se QUALQUER UMA das caixas estiver marcada, ativa o botão. Senão, desativa.
            int int_cliente = (chk_cliente.isSelected())?1:0;
            int int_entregador = (chk_entregador.isSelected())?1:0;
            int int_restaurante = (chk_restaurante.isSelected())?1:0;
            int sum = int_cliente+int_restaurante+int_entregador;

            boolean temPerfilSelecionado = (chk_cliente.isSelected() ||
                    chk_entregador.isSelected() ||
                    chk_restaurante.isSelected()) && sum == 1;
            cadastrar.setEnabled(temPerfilSelecionado);
        };


        chk_cliente.addActionListener(gerenciarBotao);
        chk_entregador.addActionListener(gerenciarBotao);
        chk_restaurante.addActionListener(gerenciarBotao);


        cadastrar.addActionListener(e ->{
        String str_usuario = usuario.getText();
        String str_email=email.getText();
        String str_senha=new String(senha.getPassword());
        String str_localizacao =localizacao.getText();

            if(str_usuario.isEmpty()|| str_email.isEmpty() || str_senha.isEmpty() || str_localizacao.isEmpty() ){
                JOptionPane.showMessageDialog(this,"Por favor, preencha todos os campos");
            }else if(!str_email.contains("@") || !str_email.contains(".com")){
                JOptionPane.showMessageDialog(this,"Por favor , coloque um email valido");
            } else{
                String str_tipo = new String();
                if(chk_cliente.isSelected()){
                    str_tipo = "cliente";
                }else if(chk_entregador.isSelected()){
                    str_tipo = "entregador";

                }else if(chk_restaurante.isSelected()){
                    str_tipo = "restaurante";

                }
                    System.out.println("Requisicao enviada para o BD\n" +
                            "Usuario:"+str_usuario+
                            "\nEmail:"+str_email+
                            "\nSenha:"+str_senha.length()+
                            "\nTipo:" +str_tipo+
                            "\nLocalizacao"+str_localizacao);

                    BancoDados.cadastrarUsuario(str_usuario,str_email,str_senha,str_tipo,str_localizacao);


                    Telabase sist = (Telabase) SwingUtilities.getWindowAncestor(this);
                    if(sist != null){
                        TelaPrincipal tl = new TelaPrincipal(sist);
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

