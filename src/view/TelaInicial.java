package view;

import javax.swing.*;
import model.Login;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TelaInicial extends Telabase{
    JPanel panel1;
    JPanel panelesq;
    JPanel paneldir;
    JPanel painelBotoes;
    JButton botao;
    CampoTextoArredondado usuario;
    CampoTextoArredondado email;
    CampoSenhaArredondado senha;
    Texto txt;
    CheckboxCustomizado chk_cliente;
    CheckboxCustomizado chk_entregador;
    CheckboxCustomizado chk_restaurante;
    public TelaInicial(){
        super();
        panel1 = new JPanel(new GridLayout(1,2));
        panelesq = new JPanel();
        paneldir = new PainelFormulario();
        panel1.setBackground(Color.decode("#e96769"));
        panelesq.setBackground(Color.decode("#e96769"));
        ImageIcon banner = new ImageIcon("img/banner_delivery.jpg");

        JLabel imagem = new JLabel(banner);

        panelesq.add(imagem);

        usuario = new CampoTextoArredondado(20,20,Color.BLACK,50);
        email  = new CampoTextoArredondado(20,20,Color.BLACK,50);
        senha = new CampoSenhaArredondado(20,20,Color.BLACK,50);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 50, 10, 50);
        //gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        //gbc.weighty = 0.1;
        gbc.gridx = 0;

        gbc.gridy = 1;

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

        painelBotoes.add(sair);
        painelBotoes.add(cadastrar);
        paneldir.add(painelBotoes,gbc);
        panelesq.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Garante que o painel já tem tamanho na tela
                if (panelesq.getWidth() > 0 && panelesq.getHeight() > 0) {

                    // Pega a imagem pura do ImageIcon
                    Image img = banner.getImage();

                    // Redimensiona a imagem para preencher o painel suavemente
                    Image novaImg = img.getScaledInstance(
                            panelesq.getWidth(),
                            panelesq.getHeight(),
                            Image.SCALE_SMOOTH
                    );


                    imagem.setIcon(new ImageIcon(novaImg));
                }
            }
        });
        sair.addActionListener(e -> {
            System.exit(0);
        });
        cadastrar.setEnabled(false);


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
            if(str_usuario.isEmpty()|| str_email.isEmpty() || str_senha.isEmpty()  ){
                JOptionPane.showMessageDialog(this,"Por favor, preencha todos os campos");
            }else{
                String str_tipo = new String();
                if(chk_cliente.isSelected()){
                    str_tipo = "cliente";
                }else if(chk_entregador.isSelected()){
                    str_tipo = "entregador";

                }else if(chk_restaurante.isSelected()){
                    str_tipo = "restaurante";

                }
                    Login.cadastrarUsuario(str_usuario,str_email,str_senha,str_tipo);
                    Login.realizarLogin(str_email,str_senha);
                    TelaLogin tl = new TelaLogin();
                    tl.setVisible(true);
                    dispose();
            }
        });
        panel1.add(panelesq);
        panel1.add(paneldir);
        configuraTela(panel1);
    }

}

