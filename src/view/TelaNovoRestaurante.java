package view;

import util.RemoveEmoji;

import bd.BancoDados;
import model.Login;

import javax.swing.*;
import java.awt.*;

/**
 * TelaNovoRestaurante — Criação de restaurante pelo gerente.
 *
 * Layout (padrão TelaMenu):
 * ┌──────────────────────────────────────────────────────┐
 * │  Header (TelaMenu)                                   │
 * ├──────────────────────────────────────────────────────┤
 * │  Cabeçalho da tela                                   │
 * ├────────────────────────┬─────────────────────────────┤
 * │  Resumo / Dica (40%)   │  Formulário de criação (60%)│
 * └────────────────────────┴─────────────────────────────┘
 */
public class TelaNovoRestaurante extends TelaMenu {

    private final Telabase sist;

    private static final Color COR_PRIMARIA = new Color(234, 16, 34);
    private static final Color COR_VERDE    = new Color(46, 174, 82);
    private static final Color COR_CINZA_BG = new Color(245, 245, 245);
    private static final Color COR_BORDA    = new Color(230, 230, 230);

    // Campos do formulário (mantidos como campos para validação)
    private CampoTextoArredondado campoNome;
    private CampoTextoArredondado campoLocalizacao;
    private CampoTextoArredondado campoEstrelas;
    private CampoSenhaArredondado campoSenha;
    private JLabel lblFeedback;

    public TelaNovoRestaurante(Telabase sist) {
        super(sist);
        this.sist = sist;

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

        // Split 40/60
        JPanel corpo = new JPanel(new GridLayout(1, 2, 20, 0));
        corpo.setBackground(Color.WHITE);
        corpo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        corpo.add(criarPainelEsquerdo());
        corpo.add(criarFormulario());

        JScrollPane scroll = new JScrollPane(corpo);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scroll, BorderLayout.CENTER);

        // Padrão correto: apenas setConteudoInterno, sem add()
        setConteudoInterno(container);
    }

    // ─────────────────────────────────────────────────────────
    //  CABEÇALHO
    // ─────────────────────────────────────────────────────────
    private JPanel criarCabecalho() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(16, 30, 16, 30)
        ));

        Texto titulo = new Texto("🏪  Cadastrar Novo Restaurante");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        p.add(titulo, BorderLayout.WEST);

        BotaoArredondado btnVoltar = new BotaoArredondado("← Voltar", 20, new Color(160, 160, 160), 14);
        btnVoltar.setPreferredSize(new Dimension(110, 38));
        btnVoltar.addActionListener(e -> sist.configuraTela(new TelaPrincipal(sist)));
        p.add(btnVoltar, BorderLayout.EAST);

        return p;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL ESQUERDO — Informativo / Dica
    // ─────────────────────────────────────────────────────────
    private JPanel criarPainelEsquerdo() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(COR_CINZA_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(30, 24, 30, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 14, 0);

        // Ícone grande
        JLabel icone = new JLabel("🏪");
        icone.setFont(new Font("Arial", Font.PLAIN, 64));
        icone.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(icone, gbc);

        // Título
        JLabel titulo = new JLabel("Seu Restaurante no AIFood");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(new Color(30, 30, 30));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(12, 0, 20, 0);
        card.add(titulo, gbc);

        // Dicas
        String[] dicas = {
            "✅  Seu restaurante ficará visível para todos os clientes",
            "✅  Gerencie o cardápio depois do cadastro",
            "✅  Acompanhe os pedidos em tempo real",
            "✅  Você pode editar os dados depois"
        };

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        for (String dica : dicas) {
            JLabel lblDica = new JLabel("<html><div style='width:200px'>" + dica + "</div></html>");
            lblDica.setFont(new Font("Arial", Font.PLAIN, 13));
            lblDica.setForeground(new Color(60, 60, 60));
            gbc.gridy++;
            card.add(lblDica, gbc);
        }

        // Empurrão pra cima
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        card.add(new JPanel() {{ setOpaque(false); }}, gbc);

        return card;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DIREITO — Formulário
    // ─────────────────────────────────────────────────────────
    private JPanel criarFormulario() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(28, 28, 28, 28)
        ));

        // ─ Título do form
        JLabel lblTitulo = new JLabel("Informações do Restaurante");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 30));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(6));

        JLabel lblSub = new JLabel("Preencha todos os campos para cadastrar.");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblSub);
        painel.add(Box.createVerticalStrut(24));

        // ─ Campos
        campoNome        = new CampoTextoArredondado(20, 16, COR_BORDA, 15);
        campoLocalizacao = new CampoTextoArredondado(20, 16, COR_BORDA, 15);
        campoEstrelas    = new CampoTextoArredondado(5, 16, COR_BORDA, 15);
        campoSenha       = new CampoSenhaArredondado(20, 16, COR_BORDA, 15);

        painel.add(criarGrupoCampo("Nome do Restaurante *", campoNome, "Ex: Central Burger & Cia"));
        painel.add(Box.createVerticalStrut(16));
        painel.add(criarGrupoCampo("Endereço / Localização *", campoLocalizacao, "Ex: Av. Paulista, 1000 - São Paulo"));
        painel.add(Box.createVerticalStrut(16));
        painel.add(criarGrupoCampo("Confirme sua senha *", campoSenha, "Sua senha de acesso ao sistema"));
        painel.add(Box.createVerticalStrut(28));

        // ─ Label de feedback (erros / sucesso)
        lblFeedback = new JLabel(" ");
        lblFeedback.setFont(new Font("Arial", Font.BOLD, 13));
        lblFeedback.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblFeedback);
        painel.add(Box.createVerticalStrut(12));

        // ─ Botão Cadastrar
        BotaoArredondado btnCadastrar = new BotaoArredondado("🏪  Cadastrar Restaurante", 20, COR_PRIMARIA, 15);
        btnCadastrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnCadastrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCadastrar.addActionListener(e -> tentarCadastrar());
        painel.add(btnCadastrar);
        painel.add(Box.createVerticalStrut(12));

        // ─ Link para ir direto ao gerenciador (se já tiver restaurante)
        JLabel lblLink = new JLabel("<html><u>Já tenho um restaurante → Ir para o Gerenciador</u></html>");
        lblLink.setFont(new Font("Arial", Font.PLAIN, 13));
        lblLink.setForeground(COR_PRIMARIA);
        lblLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                sist.configuraTela(new TelaGerenciarRestaurante(sist));
            }
        });
        painel.add(lblLink);

        // Empurrão pra cima
        painel.add(Box.createVerticalGlue());

        return painel;
    }

    /** Monta label + campo + hint em um bloco vertical alinhado */
    private JPanel criarGrupoCampo(String label, JComponent campo, String hint) {
        JPanel grupo = new JPanel();
        grupo.setLayout(new BoxLayout(grupo, BoxLayout.Y_AXIS));
        grupo.setOpaque(false);
        grupo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(new Color(50, 50, 50));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        grupo.add(lbl);
        grupo.add(Box.createVerticalStrut(5));

        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        grupo.add(campo);
        grupo.add(Box.createVerticalStrut(4));

        JLabel lblHint = new JLabel(hint);
        lblHint.setFont(new Font("Arial", Font.PLAIN, 11));
        lblHint.setForeground(new Color(160, 160, 160));
        lblHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        grupo.add(lblHint);

        return grupo;
    }

    // ─────────────────────────────────────────────────────────
    //  LÓGICA DE CADASTRO
    // ─────────────────────────────────────────────────────────
    private void tentarCadastrar() {
        String nome        = campoNome.getText().trim();
        String localizacao = campoLocalizacao.getText().trim();
        String senha       = new String(campoSenha.getPassword()).trim();

        // Validação
        if (nome.isEmpty() || localizacao.isEmpty()  || senha.isEmpty()) {
            setFeedback("⚠  Preencha todos os campos obrigatórios.", COR_PRIMARIA);
            return;
        }
        // Email do gerente vem da sessão ativa
        String emailGerente = Login.GetEmail();
        if (emailGerente == null || emailGerente.isEmpty()) {
            setFeedback("⚠  Sessão inválida. Faça login novamente.", COR_PRIMARIA);
            return;
        }

        setFeedback("⏳  Cadastrando...", Color.GRAY);

        boolean ok = BancoDados.cadastrarRestaurante(emailGerente, senha, nome,localizacao,"5");

        if (ok) {
            setFeedback("✅  Restaurante cadastrado com sucesso!", COR_VERDE);
            // Navega para o gerenciador após 800ms
            Timer timer = new Timer(800, ev -> sist.configuraTela(new TelaGerenciarRestaurante(sist)));
            timer.setRepeats(false);
            timer.start();
        } else {
            setFeedback("❌  Falha no cadastro. Verifique sua senha ou contate o suporte.", COR_PRIMARIA);
        }
    }

    private void setFeedback(String msg, Color cor) {
        lblFeedback.setText(RemoveEmoji.texto(msg));
        lblFeedback.setForeground(cor);
    }
}
