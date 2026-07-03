package view;

import javax.swing.*;
import bd.BancoDados;
import model.Login;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Interface gráfica (View) responsável pela captura, validação e persistência do
 * cadastro de novos usuários na plataforma de delivery.
 * <p>
 * A tela é dividida visualmente em duas seções principais (Layout em Grid de 1x2):
 * Um painel esquerdo que renderiza o banner institucional e um painel direito contendo
 * o formulário interativo estruturado sob {@link GridBagLayout}. O fluxo valida a
 * obrigatoriedade de campos, formato do e-mail e garante a seleção de perfil único.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public class TelaCadastro extends JPanel {

    /** Painel lateral esquerdo destinado à exibição da imagem de banner institucional. */
    JPanel panelesq;

    /** Painel lateral direito customizado que agrupa e alinha os componentes de formulário. */
    JPanel paneldir;

    /** Sub-painel inferior de fluxo alinhado para agrupar as ações de controle de saída e submissão. */
    JPanel painelBotoes;

    /** Botão genérico para ações internas de modulação (não atrelado ao fluxo principal). */
    JButton botao;

    /** Caixa de entrada de texto arredondada destinada ao nome de usuário (username). */
    CampoTextoArredondado usuario;

    /** Caixa de entrada de texto arredondada destinada ao endereço de e-mail de login. */
    CampoTextoArredondado email;

    /** Caixa de entrada de caracteres mascarados arredondada destinada à senha de acesso. */
    CampoSenhaArredondado senha;

    /** Caixa de entrada de texto arredondada destinada à localização geográfica do usuário. */
    CampoTextoArredondado localizacao;

    /** Objeto utilitário de rotulação textual estilizado para títulos e etiquetas de identificação. */
    Texto txt;

    /** Caixa de seleção customizada para o perfil do tipo Cliente. */
    CheckboxCustomizado chk_cliente;

    /** Caixa de seleção customizada para o perfil do tipo Entregador parceiro. */
    CheckboxCustomizado chk_entregador;

    /** Caixa de seleção customizada para o perfil do tipo Estabelecimento/Restaurante. */
    CheckboxCustomizado chk_restaurante;

    /**
     * Construtor padrão da tela de cadastro.
     * <p>
     * Inicializa a distribuição espacial dos subpainéis, redimensiona suavemente as dimensões
     * da imagem de banner corporativo baseado na resolução da {@link Telabase}, configura as restrições
     * e pesos do {@link GridBagConstraints}, além de vincular os listeners de eventos reativos dos botões.
     * </p>
     */
    public TelaCadastro() {

        setLayout(new GridLayout(1,2));
        panelesq = new JPanel();
        paneldir = new PainelFormulario(Telabase.Width/2, Telabase.Height, Color.WHITE);
        this.setBackground(Color.decode("#e96769"));
        panelesq.setBackground(Color.decode("#e96769"));
        ImageIcon banner = new ImageIcon("img/banner_delivery.jpg");

        // Redimensionamento estático da imagem de apresentação
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
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;

        txt = new Texto("Cadastro");
        txt.setFont(new Font("Arial", Font.BOLD, 40));
        paneldir.add(txt, gbc);
        gbc.gridy++;
        paneldir.add(Box.createVerticalStrut(40), gbc);

        txt = new Texto("Usuario:");
        txt.setFont(new Font("Arial", Font.BOLD, 30));
        paneldir.add(txt, gbc);
        gbc.gridy++;
        paneldir.add(usuario, gbc);
        gbc.gridy++;

        txt = new Texto("Email");
        txt.setFont(new Font("Arial", Font.BOLD, 30));
        paneldir.add(txt, gbc);
        gbc.gridy++;
        paneldir.add(email, gbc);
        gbc.gridy++;

        txt = new Texto("Senha:");
        txt.setFont(new Font("Arial", Font.BOLD, 30));
        paneldir.add(txt, gbc);
        gbc.gridy++;
        paneldir.add(senha, gbc);
        gbc.gridy++;

        txt = new Texto("Localização: ");
        txt.setFont(new Font("Arial", Font.BOLD, 30));
        paneldir.add(txt, gbc);
        gbc.gridy++;
        paneldir.add(localizacao, gbc);
        gbc.gridy++;

        txt = new Texto("Selecione o seu perfil");
        txt.setFont(new Font("Arial", Font.BOLD, 30));
        paneldir.add(txt, gbc);
        gbc.gridy++;

        gbc.anchor = GridBagConstraints.WEST;

        chk_cliente = new CheckboxCustomizado("CLIENTE", Color.BLUE, 30, 30);
        paneldir.add(chk_cliente, gbc);
        gbc.gridy++;

        chk_entregador = new CheckboxCustomizado("ENTREGADOR", Color.BLUE, 30, 30);
        paneldir.add(chk_entregador, gbc);
        gbc.gridy++;

        chk_restaurante = new CheckboxCustomizado("RESTAURANTE", Color.BLUE, 30, 30);
        paneldir.add(chk_restaurante, gbc);
        gbc.gridy++;

        gbc.anchor = GridBagConstraints.CENTER;
        painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelBotoes.setOpaque(false);
        BotaoArredondado cadastrar = new BotaoArredondado("Cadastrar", 20, Color.decode("#c1291e"), 35);
        BotaoArredondado sair = new BotaoArredondado("Sair", 20, Color.decode("#c1291e"), 35);
        BotaoArredondado voltar = new BotaoArredondado("Voltar", 20, Color.decode("#c1291e"), 35);
        painelBotoes.add(sair);
        painelBotoes.add(voltar);
        painelBotoes.add(cadastrar);
        paneldir.add(painelBotoes, gbc);
        cadastrar.setEnabled(false);

        // Evento para retorno à tela de Login
        voltar.addActionListener(e -> {
            TelaLogin tl = new TelaLogin();
            Telabase sist = (Telabase) SwingUtilities.getWindowAncestor(this);
            if(sist != null)
                sist.configuraTela(tl);
        });

        /**
         * Listener compartilhado encarregado de gerenciar a habilitação em tempo real do botão "Cadastrar".
         * <p>
         * Avalia a soma binária das caixas de perfil selecionadas. O botão só será ativado se o usuário
         * tiver exatamente 1 (uma) única caixa de perfil assinalada no formulário.
         * </p>
         */
        ActionListener gerenciarBotao = e -> {
            int int_cliente = (chk_cliente.isSelected()) ? 1 : 0;
            int int_entregador = (chk_entregador.isSelected()) ? 1 : 0;
            int int_restaurante = (chk_restaurante.isSelected()) ? 1 : 0;
            int sum = int_cliente + int_restaurante + int_entregador;

            boolean temPerfilSelecionado = (chk_cliente.isSelected() ||
                    chk_entregador.isSelected() ||
                    chk_restaurante.isSelected()) && sum == 1;
            cadastrar.setEnabled(temPerfilSelecionado);
        };

        chk_cliente.addActionListener(gerenciarBotao);
        chk_entregador.addActionListener(gerenciarBotao);
        chk_restaurante.addActionListener(gerenciarBotao);

        // Fluxo principal de submissão e validação do cadastro
        cadastrar.addActionListener(e -> {
            String str_usuario = usuario.getText();
            String str_email = email.getText();
            String str_senha = new String(senha.getPassword());
            String str_localizacao = localizacao.getText();

            // Cláusulas de guarda para validação de integridade dos campos
            if(str_usuario.isEmpty() || str_email.isEmpty() || str_senha.isEmpty() || str_localizacao.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos");
            } else if(!str_email.contains("@") || !str_email.contains(".com")) {
                JOptionPane.showMessageDialog(this, "Por favor, coloque um email valido");
            } else {
                String str_tipo = "";
                if(chk_cliente.isSelected()) {
                    str_tipo = "cliente";
                } else if(chk_entregador.isSelected()) {
                    str_tipo = "entregador";
                } else if(chk_restaurante.isSelected()) {
                    str_tipo = "restaurante";
                }

                System.out.println("Requisicao enviada para o BD\n" +
                        "Usuario:" + str_usuario +
                        "\nEmail:" + str_email +
                        "\nSenha:" + str_senha.length() +
                        "\nTipo:" + str_tipo +
                        "\nLocalizacao" + str_localizacao);

                // Acionamento da persistência de dados no back-end
                BancoDados.cadastrarUsuario(str_usuario, str_email, str_senha, str_tipo, str_localizacao);

                Telabase sist = (Telabase) SwingUtilities.getWindowAncestor(this);
                if(sist != null) {
                    TelaPrincipal tl = new TelaPrincipal(sist);
                    sist.configuraTela(tl);
                }
            }
        });

        // Evento de fechamento definitivo da aplicação
        sair.addActionListener(e -> {
            Object[] opcoes = {"Sim", "Não"};

            int resposta = JOptionPane.showOptionDialog(
                    this,
                    "Você realmente deseja sair?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
            );

            if (resposta == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        this.add(panelesq);
        this.add(paneldir);
    }
}