package view;

import bd.BancoDados;
import model.Login;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Tela "Moderação" do painel administrativo.
 * Cada aba lista um tipo de usuário (Clientes, Restaurantes, Entregadores)
 * com ações de moderação. A aba "Admins" só é visível para o admin master
 * (predefinido pelo sistema) e permite criar/rebaixar administradores.
 */
public class TelaAdminModeracao extends TelaMenu {

    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);
    private static final Color COR_VERDE      = new Color(46, 174, 82);
    private static final Color COR_AMARELO    = new Color(255, 180, 0);
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);
    private static final Color COR_BORDA      = new Color(230, 230, 230);

    private final Telabase sist;
    private final boolean souAdminMaster;

    public TelaAdminModeracao(Telabase sist) {
        super(sist);
        this.sist = sist;
        this.souAdminMaster = "admin_master".equals(Login.GetTipo());

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

    // ─────────────────────────────────────────────────────────
    //  ABA (uma coluna do rascunho: Clientes / Rest. / Entr. / Admins)
    // ─────────────────────────────────────────────────────────
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

            // Admin master aparece fixo no topo, não pode ser banido/rebaixado por ninguém
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

    private void popularLista(JPanel lista, String tipo, boolean isAbaAdmins) {
        List<BancoDados.UsuarioAdmin> usuarios = BancoDados.listarUsuariosPorTipo(tipo);

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

    /** Recarrega o conteúdo da lista (usado após banir/promover/rebaixar). */
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

    // ─────────────────────────────────────────────────────────
    //  CARDS
    // ─────────────────────────────────────────────────────────
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

    private JPanel criarCardUsuario(BancoDados.UsuarioAdmin u, boolean isAbaAdmins, JPanel listaPai) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        // Coluna de informações
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

        // Coluna de ações — as mesmas ações se repetem em todas as abas ("||" no rascunho)
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
                if (BancoDados.definirBanimento(u.id, novoBanido)) {
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
                        if (BancoDados.rebaixarAdmin(u.id, "cliente")) {
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
                        if (BancoDados.promoverParaAdmin(u.id)) {
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

    private JButton criarBotaoAcao(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.PLAIN, 11));
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(4, 8, 4, 8));
        return btn;
    }

    // ─────────────────────────────────────────────────────────
    //  CRIAR NOVO ADMIN (só admin master)
    // ─────────────────────────────────────────────────────────
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

            if (BancoDados.criarNovoAdmin(nome, email, senha)) {
                recarregar(lista, "admin", true);
                JOptionPane.showMessageDialog(this, "Admin criado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível criar o admin (email já em uso?).", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
