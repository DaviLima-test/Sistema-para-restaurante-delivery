package view;

import bd.BancoDados;
import model.Admin;
import model.Login;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Interface gráfica de Moderação e Auditoria de Contas pertencente ao painel do administrador.
 * <p>
 * Apresenta uma disposição baseada em abas dinâmicas divididas por categorias operacionais
 * (Clientes, Restaurantes, Entregadores, e opcionalmente Administradores). Fornece ferramentas
 * em tempo real para visualização de dados cadastrais, aplicação/remoção de sanções de banimento,
 * além de permitir a promoção e rebaixamento de usuários controlados pela instância {@link Admin}.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public class TelaAdminModeracao extends TelaMenu {

    /** Cor vermelha padrão associada a ações de restrição e banimento de contas. */
    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);

    /** Cor verde associada a operações seguras, criação de registros e liberação (desbanir). */
    private static final Color COR_VERDE      = new Color(46, 174, 82);

    /** Cor amarela comemorativa aplicada estritamente no destaque visual do Admin Master. */
    private static final Color COR_AMARELO    = new Color(255, 180, 0);

    /** Cor cinza neutra clara para estilização de fundos secundários. */
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);

    /** Cor sutil para pintura de linhas limítrofes (divisores). */
    private static final Color COR_BORDA      = new Color(230, 230, 230);

    /** Frame controlador centralizador de navegação {@link Telabase}. */
    private final Telabase sist;

    /** Flag condicional que valida se as permissões ativas pertencem ao administrador master nativo. */
    private final boolean souAdminMaster;

    /** Objeto operacional de domínio que unifica os métodos de negócios em back-end para moderação. */
    private final Admin adminOperador;

    /**
     * Construtor padrão para montagem estrutural da tela de moderação de usuários.
     * Mapeia os níveis de acesso e inicializa as abas parametrizadas de acordo com as permissões do perfil.
     *
     * @param sist O frame base de gerenciamento global de telas {@link Telabase}.
     */
    public TelaAdminModeracao(Telabase sist) {
        super(sist);
        this.sist = sist;
        this.souAdminMaster = "admin_master".equals(Login.GetTipo());
        this.adminOperador = new Admin(Login.GetEmail(), "", Login.GetUser());

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Arial", Font.BOLD, 13));
        abas.addTab("Clientes", construirAba("cliente", false));
        abas.addTab("Restaurantes", construirAba("restaurante", false));
        abas.addTab("Entregadores", construirAba("entregador", false));
        if (souAdminMaster) {
            abas.addTab("Admins", construirAba("admin", true));
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 30, 16, 30));
        wrapper.add(abas, BorderLayout.CENTER);

        container.add(wrapper, BorderLayout.CENTER);
        setConteudoInterno(container);
    }

    /**
     * Cria e estiliza o componente textual do cabeçalho da área de moderação.
     * @return Um {@link JPanel} configurado com títulos descritivos.
     */
    private JPanel criarCabecalho() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(16, 30, 16, 30)
        ));

        JPanel titulos = new JPanel();
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        titulos.setOpaque(false);

        Texto titulo = new Texto("🛡  Moderação de Usuários");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        titulos.add(titulo);

        JLabel sub = new JLabel("Clientes, Restaurantes, Entregadores" + (souAdminMaster ? " e Admins" : ""));
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);
        titulos.add(sub);

        p.add(titulos, BorderLayout.WEST);
        return p;
    }

    /**
     * Constrói dinamicamente a estrutura interna de rolagem de uma aba com base na categoria informada.
     *
     * @param tipo         A string literal de filtro do usuário (Ex: 'cliente').
     * @param isAbaAdmins  Flag indicando se o escopo de construção pertence à listagem restrita de administradores.
     * @return Um {@link JPanel} estruturado com barras de rolagem prontas.
     */
    private JPanel construirAba(String tipo, boolean isAbaAdmins) {
        JPanel abaRaiz = new JPanel(new BorderLayout());
        abaRaiz.setBackground(Color.WHITE);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);
        lista.setBorder(BorderFactory.createEmptyBorder(12, 4, 12, 4));

        if (isAbaAdmins) {
            JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            topo.setOpaque(false);
            topo.setAlignmentX(Component.LEFT_ALIGNMENT);
            BotaoArredondado btnCriar = new BotaoArredondado("+  Criar novo admin", 20, COR_VERDE, 13);
            btnCriar.addActionListener(e -> abrirFormularioNovoAdmin(lista));
            topo.add(btnCriar);
            lista.add(topo);
            lista.add(Box.createVerticalStrut(14));

            lista.add(criarCardAdminMaster());
            lista.add(Box.createVerticalStrut(10));
        }

        popularLista(lista, tipo, isAbaAdmins);

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        abaRaiz.add(scroll, BorderLayout.CENTER);

        return abaRaiz;
    }

    /**
     * Interage com o objeto {@link Admin} para ler a listagem do banco e injetar
     * os cards gráficos correspondentes na janela do painel.
     *
     * @param lista       O container interno onde os elementos visuais serão inseridos.
     * @param tipo        A string descritiva da categoria que serve como filtro para consulta.
     * @param isAbaAdmins Flag indicativa da natureza de exibição da aba.
     */
    private void popularLista(JPanel lista, String tipo, boolean isAbaAdmins) {
        List<BancoDados.UsuarioAdmin> usuarios = adminOperador.listarUsuariosParaModeracao(tipo);

        if (usuarios.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum usuário deste tipo cadastrado.");
            vazio.setFont(new Font("Arial", Font.ITALIC, 13));
            vazio.setForeground(Color.GRAY);
            vazio.setAlignmentX(Component.LEFT_ALIGNMENT);
            lista.add(vazio);
            return;
        }

        for (BancoDados.UsuarioAdmin u : usuarios) {
            lista.add(criarCardUsuario(u, isAbaAdmins, lista));
            lista.add(Box.createVerticalStrut(10));
        }
    }

    /**
     * Executa a limpeza estrutural e solicita uma nova varredura de dados ao back-end.
     * Invocado de forma reativa para atualizar a tela após ações de banimento ou promoções de perfil.
     *
     * @param lista       O container gerenciador de layouts a ser reconstruído.
     * @param tipo        A categoria alvo da recarga.
     * @param isAbaAdmins Flag definindo o escopo das abas de administração.
     */
    private void recarregar(JPanel lista, String tipo, boolean isAbaAdmins) {
        lista.removeAll();
        if (isAbaAdmins) {
            JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            topo.setOpaque(false);
            BotaoArredondado btnCriar = new BotaoArredondado("+  Criar novo admin", 20, COR_VERDE, 13);
            btnCriar.addActionListener(e -> abrirFormularioNovoAdmin(lista));
            topo.add(btnCriar);
            lista.add(topo);
            lista.add(Box.createVerticalStrut(14));
            lista.add(criarCardAdminMaster());
            lista.add(Box.createVerticalStrut(10));
        }
        popularLista(lista, tipo, isAbaAdmins);
        lista.revalidate();
        lista.repaint();
    }

    /**
     * Fabrica um card gráfico estático e imutável para representação do Admin Master no topo da aba.
     * @return Um {@link JPanel} contendo o layout formatado com insígnias douradas.
     */
    private JPanel criarCardAdminMaster() {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(255, 249, 230));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_AMARELO, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JPanel infos = new JPanel();
        infos.setOpaque(false);
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));

        JLabel nome = new JLabel("👑 Admin Master");
        nome.setFont(new Font("Arial", Font.BOLD, 14));
        infos.add(nome);

        JLabel detalhe = new JLabel("Predefinido pelo sistema — sempre existe. Pode criar, banir e monitorar outros admins.");
        detalhe.setFont(new Font("Arial", Font.PLAIN, 11));
        detalhe.setForeground(Color.GRAY);
        infos.add(detalhe);

        card.add(infos, BorderLayout.CENTER);
        return card;
    }

    /**
     * Constrói e anexa os componentes internos de um card de usuário individual.
     * Mapeia de forma autônoma os eventos das ações do botão "Banir/Desbanir", "Promover" e "Rebaixar"
     * redirecionando-as para a classe de back-end {@link Admin}.
     *
     * @param u           O modelo representativo contendo os dados brutos recuperados do usuário.
     * @param isAbaAdmins Flag controladora de contexto para exibição de botões de cargos.
     * @param listaPai    Referência ao painel pai para invocação das rotinas de atualização após mutações.
     * @return Um {@link JPanel} completamente estruturado e operante.
     */
    private JPanel criarCardUsuario(BancoDados.UsuarioAdmin u, boolean isAbaAdmins, JPanel listaPai) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JPanel infos = new JPanel();
        infos.setOpaque(false);
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));

        JLabel nome = new JLabel(u.nome + (u.banido ? "   🚫 BANIDO" : ""));
        nome.setFont(new Font("Arial", Font.BOLD, 14));
        nome.setForeground(u.banido ? COR_PRIMARIA : new Color(30, 30, 30));
        infos.add(nome);

        JLabel email = new JLabel(u.email + "   •   " + (u.localizacao == null ? "-" : u.localizacao));
        email.setFont(new Font("Arial", Font.PLAIN, 12));
        email.setForeground(Color.GRAY);
        infos.add(email);

        card.add(infos, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        acoes.setOpaque(false);

        JButton btnDenuncias = criarBotaoAcao("Ver denúncias");
        btnDenuncias.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Nenhuma denúncia registrada para " + u.nome + ".\n" +
                        "(Funcionalidade completa de denúncias ainda não implementada.)",
                "Denúncias", JOptionPane.INFORMATION_MESSAGE));
        acoes.add(btnDenuncias);

        JButton btnAdvertir = criarBotaoAcao("Advertir");
        btnAdvertir.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                    "Enviar advertência para " + u.nome + "?\n(Ação opcional — apenas um aviso, não bane a conta.)",
                    "Advertir usuário", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Advertência registrada para " + u.nome + ".");
            }
        });
        acoes.add(btnAdvertir);

        JButton btnBanir = criarBotaoAcao(u.banido ? "Desbanir" : "Banir");
        btnBanir.setForeground(u.banido ? COR_VERDE : COR_PRIMARIA);
        btnBanir.addActionListener(e -> {
            boolean novoBanido = !u.banido;
            String acao = novoBanido ? "banir" : "desbanir";
            int r = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja " + acao + " a conta de " + u.nome + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (r == JOptionPane.YES_OPTION) {
                if (adminOperador.definirBanimentoConta(u.id, novoBanido)) {
                    recarregar(listaPai, u.tipo, isAbaAdmins);
                } else {
                    JOptionPane.showMessageDialog(this, "Não foi possível atualizar o usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        acoes.add(btnBanir);

        if (souAdminMaster) {
            if (isAbaAdmins) {
                JButton btnRebaixar = criarBotaoAcao("Rebaixar");
                btnRebaixar.addActionListener(e -> {
                    int r = JOptionPane.showConfirmDialog(this,
                            "Remover privilégios de admin de " + u.nome + "?\nA conta voltará a ser um cliente comum.",
                            "Rebaixar admin", JOptionPane.YES_NO_OPTION);
                    if (r == JOptionPane.YES_OPTION) {
                        if (adminOperador.rebaixarAdministrador(u.id)) {
                            recarregar(listaPai, "admin", true);
                        } else {
                            JOptionPane.showMessageDialog(this, "Não foi possível rebaixar este usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                acoes.add(btnRebaixar);
            } else {
                JButton btnPromover = criarBotaoAcao("Tornar Admin");
                btnPromover.addActionListener(e -> {
                    int r = JOptionPane.showConfirmDialog(this,
                            "Tornar " + u.nome + " um administrador padrão?",
                            "Promover a Admin", JOptionPane.YES_NO_OPTION);
                    if (r == JOptionPane.YES_OPTION) {
                        if (adminOperador.promoverUsuarioParaAdmin(u.id)) {
                            recarregar(listaPai, u.tipo, isAbaAdmins);
                            JOptionPane.showMessageDialog(this, u.nome + " agora é um admin.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Não foi possível promover este usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                acoes.add(btnPromover);
            }
        }

        card.add(acoes, BorderLayout.EAST);
        return card;
    }

    /**
     * Helper padronizador para renderização uniforme dos pequenos botões retangulares de ações.
     *
     * @param texto A String literal que será fixada no corpo de clique do botão.
     * @return Um componente operacional do tipo {@link JButton}.
     */
    private JButton criarBotaoAcao(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.PLAIN, 11));
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(4, 8, 4, 8));
        return btn;
    }

    /**
     * Invoca e constrói de forma flutuante uma caixa modal parametrizada com caixas de preenchimento.
     * Utilizado exclusivamente por administradores master para gerar credenciais síncronas de novos administradores
     * por meio do método `criarNovoAdministrador`.
     *
     * @param lista O container de listagem visível associado para acionamento de repinturas automáticas.
     */
    private void abrirFormularioNovoAdmin(JPanel lista) {
        JTextField campoNome = new JTextField();
        JTextField campoEmail = new JTextField();
        JPasswordField campoSenha = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
        form.add(new JLabel("Nome:"));
        form.add(campoNome);
        form.add(new JLabel("Email (login):"));
        form.add(campoEmail);
        form.add(new JLabel("Senha:"));
        form.add(campoSenha);

        int r = JOptionPane.showConfirmDialog(this, form, "Criar novo admin",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (r == JOptionPane.OK_OPTION) {
            String nome = campoNome.getText().trim();
            String email = campoEmail.getText().trim();
            String senha = new String(campoSenha.getPassword()).trim();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (adminOperador.criarNovoAdministrador(nome, email, senha)) {
                recarregar(lista, "admin", true);
                JOptionPane.showMessageDialog(this, "Admin criado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível criar o admin (email já em uso?).", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}