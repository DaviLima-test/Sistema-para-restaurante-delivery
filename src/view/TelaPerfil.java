package view;

import util.RemoveEmoji;
import bd.BancoDados;
import model.Login;

import javax.swing.*;
import java.awt.*;

/**
 * Interface gráfica (View) encarregada de exibir e gerenciar o perfil do usuário logado.
 * <p>
 * O layout distribui os componentes em duas colunas centrais equilibradas por um {@link GridLayout}:
 * A coluna esquerda (40%) monta o resumo de identificação do usuário e dados resumidos de seu estabelecimento.
 * A coluna direita (60%) renderiza dinamicamente seções de dados cadastrais detalhados e, caso o perfil logado
 * pertença à categoria de "restaurante", consome a fachada {@link BancoDados} para listar ou alertar a ausência
 * do estabelecimento associado ao e-mail corporativo.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public class TelaPerfil extends TelaMenu {

    /** Instância ativa do frame de coordenação global de telas. */
    private final Telabase sist;

    /** Contêiner estrutural central encarregado de organizar as colunas de dados. */
    private JPanel corpoPrincipal;

    /** Painel reativo lateral direito focado na renderização do faturamento e dados cadastrais. */
    private JPanel painelDetalhe;

    /** Cor vermelha corporativa aplicada em botões de destaque, encerramento e títulos secundários. */
    private static final Color COR_PRIMARIA = new Color(234, 16, 34);

    /** Tonalidade verde para confirmações e estados de conclusão física de tarefas. */
    private static final Color COR_VERDE    = new Color(46, 174, 82);

    /** Cor cinza neutra para panos de fundo de contêineres e placeholders informativos. */
    private static final Color COR_CINZA_BG = new Color(245, 245, 245);

    /** Cor sutil e padronizada para pintura de contornos e divisórias de componentes. */
    private static final Color COR_BORDA    = new Color(230, 230, 230);

    /** Vetor de Strings contendo o mapeamento de dados do restaurante [id, nome, localizacao, estrelas]. */
    private final String[] dadosRestaurante;

    /**
     * Construtor da tela de visualização e edição de perfil.
     * <p>
     * Avalia de forma prévia se o tipo de usuário ativo corresponde a um perfil de restaurante para efetuar
     * a busca persistente correspondente. Inicializa a barra de rolagem principal e acopla os cartões de dados.
     * </p>
     *
     * @param sist O frame base de gerenciamento global de telas {@link Telabase}.
     */
    public TelaPerfil(Telabase sist) {
        super(sist);
        this.sist = sist;

        if ("restaurante".equalsIgnoreCase(Login.GetTipo())) {
            dadosRestaurante = BancoDados.buscarRestaurantePorGerente(Login.GetEmail());
        } else {
            dadosRestaurante = null;
        }

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

        corpoPrincipal = new JPanel(new GridLayout(1, 2, 20, 0));
        corpoPrincipal.setBackground(Color.WHITE);
        corpoPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        corpoPrincipal.add(criarCardPerfilEsquerda());

        painelDetalhe = criarPainelDetalheDireito();
        corpoPrincipal.add(painelDetalhe);

        JScrollPane scroll = new JScrollPane(corpoPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scroll, BorderLayout.CENTER);

        setConteudoInterno(container);
    }

    /**
     * Cria e preenche a barra superior informativa da interface.
     * * * @return Um {@link JPanel} estruturado contendo o título da tela e o botão de logout.
     */
    private JPanel criarCabecalho() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(16, 30, 16, 30)
        ));

        Texto titulo = new Texto("👤  Meu Perfil");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        p.add(titulo, BorderLayout.WEST);

        BotaoArredondado btnLogout = new BotaoArredondado("🚪 Sair da Conta", 20, COR_PRIMARIA, 14);
        btnLogout.setPreferredSize(new Dimension(150, 38));
        btnLogout.addActionListener(e -> acaoLogout());
        p.add(btnLogout, BorderLayout.EAST);

        return p;
    }

    /**
     * Cria a seção de resumo de perfil localizada na coluna esquerda.
     * Renderiza o avatar genérico, nome de usuário, cargo e anexa condicionalmente metadados básicos do restaurante.
     * * * @return Um {@link JPanel} estruturado sob o gerenciador {@link GridBagLayout}.
     */
    private JPanel criarCardPerfilEsquerda() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(30, 20, 30, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        JLabel lblAvatar = new JLabel("👤");
        lblAvatar.setFont(new Font("Arial", Font.PLAIN, 72));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        card.add(lblAvatar, gbc);

        String nomeUsuario = Login.GetUser() != null ? Login.GetUser() : "Usuário";
        Texto txtNome = new Texto(nomeUsuario);
        txtNome.setFont(new Font("Arial", Font.BOLD, 22));
        txtNome.setForeground(new Color(40, 40, 40));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(txtNome, gbc);

        String tipoConta = Login.GetTipo() != null ? Login.GetTipo().toUpperCase() : "CLIENTE";
        JLabel badge = new JLabel("  " + tipoConta + "  ");
        badge.setFont(new Font("Arial", Font.BOLD, 12));
        badge.setOpaque(true);
        badge.setBackground(COR_CINZA_BG);
        badge.setForeground(new Color(100, 100, 100));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        gbc.gridy = 2;
        card.add(badge, gbc);

        if (dadosRestaurante != null) {
            gbc.gridy = 3;
            gbc.insets = new Insets(16, 0, 0, 0);

            JPanel cardRest = new JPanel(new GridBagLayout());
            cardRest.setBackground(new Color(255, 247, 247));
            cardRest.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 200, 200), 1, true),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
            ));

            GridBagConstraints g2 = new GridBagConstraints();
            g2.gridx = 0; g2.anchor = GridBagConstraints.CENTER;

            JLabel iconeRest = new JLabel("🏪");
            iconeRest.setFont(new Font("Arial", Font.PLAIN, 26));
            g2.gridy = 0; g2.insets = new Insets(0, 0, 4, 0);
            cardRest.add(iconeRest, g2);

            JLabel lblNomeRest = new JLabel(dadosRestaurante[1]);
            lblNomeRest.setFont(new Font("Arial", Font.BOLD, 13));
            lblNomeRest.setForeground(COR_PRIMARIA);
            g2.gridy = 1; g2.insets = new Insets(0, 0, 2, 0);
            cardRest.add(lblNomeRest, g2);

            JLabel lblLocRest = new JLabel(dadosRestaurante[2]);
            lblLocRest.setFont(new Font("Arial", Font.PLAIN, 11));
            lblLocRest.setForeground(Color.GRAY);
            g2.gridy = 2;
            cardRest.add(lblLocRest, g2);

            card.add(cardRest, gbc);
        }

        return card;
    }

    /**
     * Instancia o contêiner padrão da coluna direita preenchendo as caixas de metadados.
     * Mapeia credenciais de e-mail e bifurca a exibição dependendo do nível de permissão corporativa.
     * * * @return O painel formatado em {@link BoxLayout}.
     */
    private JPanel criarPainelDetalheDireito() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel lblTitulo = new JLabel("Informações da Conta");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 30));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(20));

        String emailExibido = Login.GetEmail() != null ? Login.GetEmail()
                : (Login.GetUser() != null ? Login.GetUser().toLowerCase() + "@provedor.com" : "—");

        painel.add(criarSecao("📝 Dados Cadastrais", new String[][]{
                {"Usuário",     Login.GetUser() != null  ? Login.GetUser()  : "—"},
                {"E-mail",      emailExibido},
                {"Tipo Acesso", Login.GetTipo() != null  ? Login.GetTipo()  : "—"}
        }));
        painel.add(Box.createVerticalStrut(16));

        if ("restaurante".equalsIgnoreCase(Login.GetTipo())) {
            painel.add(criarSecaoRestaurante());
            painel.add(Box.createVerticalStrut(16));
        }

        painel.add(Box.createVerticalGlue());
        painel.add(Box.createVerticalStrut(20));

        BotaoArredondado btnSenha = new BotaoArredondado("🔒 Alterar Minha Senha", 20, COR_VERDE, 14);
        btnSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSenha.addActionListener(e -> exibirFormularioSenha());
        painel.add(btnSenha);

        return painel;
    }

    /**
     * Fabrica dinamicamente o painel de status do restaurante com base na consulta prévia do banco.
     * Retorna uma seção detalhada se encontrado, ou um card de alerta amarelo caso não existam registros.
     * * * @return O {@link JPanel} de controle situacional formatado.
     */
    private JPanel criarSecaoRestaurante() {
        if (dadosRestaurante != null) {
            String estrelas = gerarEstrelas(dadosRestaurante[3]);

            JPanel secao = criarSecao("🏪 Meu Restaurante", new String[][]{
                    {"Nome",        dadosRestaurante[1]},
                    {"Endereço",    dadosRestaurante[2]},
                    {"Avaliação",   estrelas},
            });
            JPanel wrapper = new JPanel();
            wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
            wrapper.setOpaque(false);
            wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrapper.add(secao);
            wrapper.add(Box.createVerticalStrut(12));
            return wrapper;

        } else {
            JPanel secaoVazia = new JPanel();
            secaoVazia.setLayout(new BoxLayout(secaoVazia, BoxLayout.Y_AXIS));
            secaoVazia.setBackground(new Color(255, 252, 240));
            secaoVazia.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 220, 100), 1, true),
                    BorderFactory.createEmptyBorder(16, 16, 16, 16)
            ));
            secaoVazia.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblTit = new JLabel("🏪  Meu Restaurante");
            lblTit.setFont(new Font("Arial", Font.BOLD, 13));
            lblTit.setForeground(new Color(133, 100, 4));
            lblTit.setAlignmentX(Component.LEFT_ALIGNMENT);
            secaoVazia.add(lblTit);
            secaoVazia.add(Box.createVerticalStrut(8));

            JLabel lblMsg = new JLabel(
                    "<html><div style='width:260px;color:#666'>" +
                            "Você ainda não cadastrou nenhum restaurante.<br>" +
                            "Crie o seu agora para começar a receber pedidos." +
                            "</div></html>"
            );
            lblMsg.setFont(new Font("Arial", Font.PLAIN, 13));
            lblMsg.setAlignmentX(Component.LEFT_ALIGNMENT);
            secaoVazia.add(lblMsg);
            secaoVazia.add(Box.createVerticalStrut(14));
            return secaoVazia;
        }
    }

    /**
     * Gera e estrutura o formulário interativo focado na alteração das credenciais de acesso (senha).
     * Mapeia validações de consistência e igualdade de caracteres inseridos.
     * * * @return O painel do formulário montado.
     */
    private JPanel criarPainelFormularioSenha() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel lblTitulo = new JLabel("Segurança — Atualizar Senha");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 30));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(20));

        JPasswordField txtSenhaAtual = criarCampoSenhaForm();
        JPasswordField txtNovaSenha  = criarCampoSenhaForm();
        JPasswordField txtConfirma   = criarCampoSenhaForm();

        painel.add(rotulo("Digite sua senha atual:"));  painel.add(txtSenhaAtual); painel.add(Box.createVerticalStrut(12));
        painel.add(rotulo("Digite a nova senha:"));     painel.add(txtNovaSenha);  painel.add(Box.createVerticalStrut(12));
        painel.add(rotulo("Confirme a nova senha:"));   painel.add(txtConfirma);

        painel.add(Box.createVerticalGlue());
        painel.add(Box.createVerticalStrut(24));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotaoArredondado btnSalvar = new BotaoArredondado("💾 Confirmar Alteração", 20, COR_VERDE, 14);
        btnSalvar.addActionListener(e -> {
            String sa = new String(txtSenhaAtual.getPassword()).trim();
            String sn = new String(txtNovaSenha.getPassword()).trim();
            String sc = new String(txtConfirma.getPassword()).trim();

            if (sa.isEmpty() || sn.isEmpty() || sc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!sn.equals(sc)) {
                JOptionPane.showMessageDialog(this, "A nova senha e a confirmação não coincidem.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Senha updated com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            atualizarTela();
        });

        BotaoArredondado btnCancelar = new BotaoArredondado("Cancelar", 20, new Color(160, 160, 160), 14);
        btnCancelar.addActionListener(e -> atualizarTela());

        btnRow.add(btnSalvar);
        btnRow.add(btnCancelar);
        painel.add(btnRow);

        return painel;
    }

    /**
     * Fabrica uma subseção visual formatada em pares de chave/valor para exibição limpa de metadados.
     * * * @param titulo O título da seção (ex: "📝 Dados Cadastrais").
     * @param pares  Matriz bi-dimensional de Strings contendo as linhas formatadas.
     * @return O painel contendo o layout da seção.
     */
    private JPanel criarSecao(String titulo, String[][] pares) {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(COR_CINZA_BG);
        s.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        int h = 45 + (pares.length * 26);
        s.setPreferredSize(new Dimension(320, h));
        s.setMinimumSize(new Dimension(250, h));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Arial", Font.BOLD, 13));
        lblTit.setForeground(COR_PRIMARIA);
        s.add(lblTit);
        s.add(Box.createVerticalStrut(8));

        for (String[] par : pares) {
            JPanel linha = new JPanel(new BorderLayout(8, 0));
            linha.setOpaque(false);
            JLabel chave = new JLabel(par[0] + ":");
            chave.setFont(new Font("Arial", Font.BOLD, 13));
            chave.setForeground(new Color(80, 80, 80));
            chave.setPreferredSize(new Dimension(100, 22));
            JLabel valor = new JLabel(par[1]);
            valor.setFont(new Font("Arial", Font.PLAIN, 13));
            valor.setForeground(new Color(40, 40, 40));
            linha.add(chave, BorderLayout.WEST);
            linha.add(valor, BorderLayout.CENTER);
            s.add(linha);
            s.add(Box.createVerticalStrut(5));
        }
        return s;
    }

    /**
     * Fabrica e padroniza campos mascarados de senha aplicados em formulários.
     * * * @return A instância ajustada de {@link JPasswordField}.
     */
    private JPasswordField criarCampoSenhaForm() {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Arial", Font.PLAIN, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    /**
     * Instancia rótulos textuais customizados para legendas de inputs.
     * * * @param texto A string descritiva.
     * @return O {@link JLabel} configurado.
     */
    private JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(new Color(60, 60, 60));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    /**
     * Transpõe o valor numérico bruto de classificação do estabelecimento em uma cadeia de caracteres estelar.
     * * * @param qtdStr A string correspondente ao dígito numérico (ex: "4").
     * @return Uma string formatada (ex: "★★★★☆"), ou um hífen em caso de falha de conversão.
     */
    private String gerarEstrelas(String qtdStr) {
        try {
            int qtd = Integer.parseInt(qtdStr);
            qtd = Math.max(0, Math.min(5, qtd));
            return "*".repeat(qtd) + "".repeat(5 - qtd);
        } catch (NumberFormatException e) {
            return "—";
        }
    }

    /**
     * Substitui o contêiner cadastral da direita e injeta os campos interativos de senha.
     * Aplica sanitizações através da classe utilitária {@link RemoveEmoji}.
     */
    private void exibirFormularioSenha() {
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = criarPainelFormularioSenha();
        RemoveEmoji.aplicar(painelDetalhe);
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }

    /**
     * Limpa os cookies temporários locais mapeados por {@link Login} e encerra por completo o processo.
     */
    private void acaoLogout() {
        Object[] opcoes = {"Sim", "Não"};
        int resposta = JOptionPane.showOptionDialog(
                SwingUtilities.getWindowAncestor(this),
                "Você realmente deseja encerrar sua sessão?",
                "Confirmação de Saída",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opcoes, opcoes[0]
        );
        if (resposta == JOptionPane.YES_OPTION) {
            Login.apagarCookie();
            System.exit(0);
        }
    }

    /**
     * Recarrega por completo a árvore estrutural da janela injetando uma nova instância limpa.
     */
    private void atualizarTela() {
        if (sist != null) sist.configuraTela(new TelaPerfil(sist));
    }
}