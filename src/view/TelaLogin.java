package view;

import javax.swing.*;

import bd.BancoDados;
import model.Login;

import java.awt.*;
import java.util.Objects;

/**
 * Interface gráfica (View) responsável pela autenticação e controle de acesso ao sistema.
 * <p>
 * A tela é dividida horizontalmente em duas partes iguais (Grid de 1x2):
 * A metade esquerda (panelesq) exibe um banner promocional/institucional estático da plataforma.
 * A metade direita (paneldir) apresenta o formulário interativo utilizando {@link GridBagLayout},
 * contendo os campos de captura de credenciais (e-mail e senha) e os fluxos de navegação para
 * cadastro, login e encerramento do sistema.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public class TelaLogin extends JPanel {

    /** Painel lateral esquerdo destinado à exibição da identidade visual/banner. */
    private JPanel panelesq;

    /** Painel lateral direito customizado responsável por abrigar o formulário de entrada. */
    private JPanel paneldir;

    /** Subcontêiner horizontal estruturado para o alinhamento dos botões de ação na base. */
    private JPanel painelBotoes;

    /** Componente gráfico arredondado para captura do e-mail do usuário. */
    private CampoTextoArredondado email;

    /** Componente gráfico arredondado e mascarado para captura da senha do usuário. */
    private CampoSenhaArredondado senha;

    /** Rótulo textual genérico reutilizado para renderização de títulos e instruções. */
    private Texto txt;

    /** Marcador de seleção para categorização de perfil do tipo Cliente (Inativo/Reservado). */
    private CheckboxCustomizado chk_cliente;

    /** Marcador de seleção para categorização de perfil do tipo Entregador (Inativo/Reservado). */
    private CheckboxCustomizado chk_entregador;

    /** Marcador de seleção para categorização de perfil do tipo Restaurante (Inativo/Reservado). */
    private CheckboxCustomizado chk_restaurante;

    /**
     * Construtor da tela de login.
     * <p>
     * Inicializa a divisão de layouts da janela, dimensiona de forma suave a imagem do banner esquerdo,
     * monta a árvore de restrições do formulário à direita e acopla as regras de negócio associadas aos
     * gatilhos de cadastro, validação de credenciais junto ao banco e terminação de processo.
     * </p>
     */
    public TelaLogin() {

        setLayout(new GridLayout(1, 2));
        panelesq = new JPanel();
        paneldir = new PainelFormulario(Telabase.Width / 2, Telabase.Height, Color.WHITE);
        this.setBackground(Color.decode("#e96769"));
        panelesq.setBackground(Color.decode("#e96769"));

        ImageIcon banner = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/banner_delivery.jpg")));
        Image novaImg = banner.getImage().getScaledInstance(
                Telabase.Width / 2,
                Telabase.Height,
                Image.SCALE_SMOOTH
        );
        banner.setImage(novaImg);
        JLabel imagem = new JLabel(banner);
        panelesq.add(imagem);

        email = new CampoTextoArredondado(20, 20, Color.BLACK, 30);
        senha = new CampoSenhaArredondado(20, 20, Color.BLACK, 30);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 50, 10, 50);
        gbc.weightx = 1;
        gbc.gridx = 0;

        gbc.gridy = 1;
        txt = new Texto("Login");
        txt.setFont(new Font("Arial", Font.BOLD, 60));
        paneldir.add(txt, gbc);

        gbc.gridy++;
        paneldir.add(Box.createVerticalStrut(60), gbc);

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
        gbc.anchor = GridBagConstraints.CENTER;

        painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelBotoes.setOpaque(false);

        BotaoArredondado logar = new BotaoArredondado("Logar", 20, Color.decode("#c1291e"), 35);
        BotaoArredondado cadastro = new BotaoArredondado("Cadastro", 20, Color.decode("#c1291e"), 35);
        BotaoArredondado sair = new BotaoArredondado("Sair", 20, Color.decode("#c1291e"), 35);

        painelBotoes.add(sair);
        painelBotoes.add(cadastro);
        painelBotoes.add(logar);
        paneldir.add(painelBotoes, gbc);

        cadastro.addActionListener(e -> {
            TelaCadastro tc = new TelaCadastro();
            Telabase sist = (Telabase) SwingUtilities.getWindowAncestor(this);
            if (sist != null) {
                sist.configuraTela(tc);
            }
        });

        logar.addActionListener(e -> {
            String str_email = email.getText();
            String str_senha = new String(senha.getPassword());

            if (str_email.isEmpty() || str_senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos");
            } else {
                System.out.println("Requisicao enviada para o BD\n" +
                        "\nEmail:" + str_email +
                        "\nSenha:" + str_senha.length());

                if (BancoDados.realizarLogin(str_email, str_senha)) {
                    Telabase sist = (Telabase) SwingUtilities.getWindowAncestor(this);
                    if (sist != null) {
                        TelaPrincipal tl = new TelaPrincipal(sist);
                        sist.configuraTela(tl);
                    }
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