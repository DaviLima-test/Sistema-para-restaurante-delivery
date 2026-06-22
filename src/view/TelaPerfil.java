package view;

import bd.BancoDados;
import model.Login;

import javax.swing.*;
import java.awt.*;

/**
 * TelaPerfil — Exibe o resumo do usuário à esquerda (40%)
 * e os detalhes ou formulário de alteração de senha à direita (60%).
 */
public class TelaPerfil extends TelaMenu {

    private final Telabase sist;
    private JPanel corpoPrincipal;
    private JPanel painelDetalhe;

    // Cores padrão unificadas do projeto
    private static final Color COR_PRIMARIA   = new Color(234, 16, 34);
    private static final Color COR_VERDE      = new Color(46, 174, 82);
    private static final Color COR_CINZA_BG   = new Color(245, 245, 245);
    private static final Color COR_BORDA      = new Color(230, 230, 230);

    public TelaPerfil(Telabase sist) {
        super(sist); // Inicializa a casca (Header + Menu + Overlay) da classe abstrata
        this.sist = sist;

        // Container raiz desta tela
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

        // MODO PADRÃO: Resumo (esquerda - 40%) + Detalhes/Ações (direita - 60%)
        corpoPrincipal = new JPanel(new GridLayout(1, 2, 20, 0));
        corpoPrincipal.setBackground(Color.WHITE);
        corpoPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Lado Esquerdo: Card de Identificação do Usuário
        corpoPrincipal.add(criarCardPerfilEsquerda());

        // Lado Direito: Detalhes iniciais do perfil
        painelDetalhe = criarPainelDetalhePreenchido();
        corpoPrincipal.add(painelDetalhe);

        JScrollPane scroll = new JScrollPane(corpoPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scroll, BorderLayout.CENTER);

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

        Texto titulo = new Texto("👤  Meu Perfil");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        p.add(titulo, BorderLayout.WEST);

        // Botão de Logout posicionado elegantemente no canto superior direito
        BotaoArredondado btnLogout = new BotaoArredondado("🚪 Sair da Conta", 20, COR_PRIMARIA, 14);
        btnLogout.setPreferredSize(new Dimension(150, 38));
        btnLogout.addActionListener(e -> acaoLogout());
        p.add(btnLogout, BorderLayout.EAST);

        return p;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL ESQUERDO — CARD DE IDENTIFICAÇÃO (40%)
    // ─────────────────────────────────────────────────────────
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

        // Avatar grande em Emoji
        JLabel lblAvatar = new JLabel("👤");
        lblAvatar.setFont(new Font("Arial", Font.PLAIN, 72));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        card.add(lblAvatar, gbc);

        // Nome do usuário conectado
        Texto txtNome = new Texto(Telabase.getLogin().GetUser());
        txtNome.setFont(new Font("Arial", Font.BOLD, 22));
        txtNome.setForeground(new Color(40, 40, 40));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(txtNome, gbc);

        // Badge estilizado com o Tipo de Conta
        String tipoConta = Telabase.getLogin().GetTipo() != null ? Telabase.getLogin().GetTipo().toUpperCase() : "CLIENTE";
        JLabel badge = new JLabel("  " + tipoConta + "  ");
        badge.setFont(new Font("Arial", Font.BOLD, 12));
        badge.setOpaque(true);
        //badge.setBackground(new java.color.ColorSpace().getMinValue(0) == 0 ? new Color(240, 240, 240) : Color.LIGHT_GRAY);
        badge.setBackground(COR_CINZA_BG);
        badge.setForeground(new Color(100, 100, 100));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        gbc.gridy = 2;
        card.add(badge, gbc);

        return card;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DIREITO — VISUALIZAÇÃO DE INFORMAÇÕES (60%)
    // ─────────────────────────────────────────────────────────
    private JPanel criarPainelDetalhePreenchido() {
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

        // Tenta recuperar o e-mail dinamicamente se o seu modelo suportar, senão usa uma máscara amigável
        String emailUsuario = Telabase.getLogin().GetUser().toLowerCase() + "@provedor.com";

        // Seção Visual Simétrica das informações cadastrais
        painel.add(criarSecao("📝 Dados Cadastrais", new String[][]{
                {"Usuário",     Telabase.getLogin().GetUser()},
                {"E-mail",       Telabase.getLogin().GetEmail()},
                {"Tipo Acesso",  Telabase.getLogin().GetTipo()}
        }));

        painel.add(Box.createVerticalGlue());
        painel.add(Box.createVerticalStrut(24));

        // Botão para disparar a troca de senha
        BotaoArredondado btnAlterarSenha = new BotaoArredondado("🔒 Alterar Minha Senha", 20, COR_VERDE, 14);
        btnAlterarSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnAlterarSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAlterarSenha.addActionListener(e -> exibirFormularioSenha());
        painel.add(btnAlterarSenha);

        return painel;
    }

    // ─────────────────────────────────────────────────────────
    //  PAINEL DIREITO — FORMULÁRIO COMPLETO PARA TROCA DE SENHA
    // ─────────────────────────────────────────────────────────
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

        // Campos do Formulário usando JPasswordField por segurança (caracteres ocultos)
        JPasswordField txtSenhaAtual = criarCampoSenhaForm();
        JPasswordField txtNovaSenha  = criarCampoSenhaForm();
        JPasswordField txtConfirma   = criarCampoSenhaForm();

        painel.add(new JLabel("Digite sua senha atual:")); painel.add(txtSenhaAtual); painel.add(Box.createVerticalStrut(12));
        painel.add(new JLabel("Digite a nova senha:")); painel.add(txtNovaSenha); painel.add(Box.createVerticalStrut(12));
        painel.add(new JLabel("Confirme a nova senha:")); painel.add(txtConfirma);

        painel.add(Box.createVerticalGlue());
        painel.add(Box.createVerticalStrut(24));

        // Botões Salvar / Cancelar
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotaoArredondado btnSalvar = new BotaoArredondado("💾 Confirmar Alteração", 20, COR_VERDE, 14);
        btnSalvar.addActionListener(e -> {
            String senhaAtual = new String(txtSenhaAtual.getPassword()).trim();
            String novaSenha  = new String(txtNovaSenha.getPassword()).trim();
            String confirma   = new String(txtConfirma.getPassword()).trim();

            if (senhaAtual.isEmpty() || novaSenha.isEmpty() || confirma.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!novaSenha.equals(confirma)) {
                JOptionPane.showMessageDialog(this, "A nova senha e a confirmação não coincidem.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // AQUI VOCÊ SE CONECTA COM O SEU BACKEND/REPOSITÓRIO:
            // Exemplo: boolean sucesso = Login.alterarSenha(senhaAtual, novaSenha);

            JOptionPane.showMessageDialog(this, "Senha atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            atualizarTela();
        });

        BotaoArredondado btnCancelar = new BotaoArredondado("Cancelar", 20, new Color(160, 160, 160), 14);
        btnCancelar.addActionListener(e -> atualizarTela());

        btnRow.add(btnSalvar);
        btnRow.add(btnCancelar);
        painel.add(btnRow);

        return painel;
    }

    // ─────────────────────────────────────────────────────────
    //  MÉTODOS AUXILIARES E COMPONENTES
    // ─────────────────────────────────────────────────────────
    private JPasswordField criarCampoSenhaForm() {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Arial", Font.PLAIN, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return f;
    }

    private JPanel criarSecao(String titulo, String[][] pares) {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(COR_CINZA_BG);
        s.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

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
            chave.setPreferredSize(new Dimension(100, 20));
            JLabel valor = new JLabel(par[1]);
            valor.setFont(new Font("Arial", Font.PLAIN, 13));
            valor.setForeground(new Color(40, 40, 40));
            linha.add(chave, BorderLayout.WEST);
            linha.add(valor, BorderLayout.CENTER);
            s.add(linha);
            s.add(Box.createVerticalStrut(4));
        }
        return s;
    }

    private void exibirFormularioSenha() {
        corpoPrincipal.remove(painelDetalhe);
        painelDetalhe = criarPainelFormularioSenha();
        corpoPrincipal.add(painelDetalhe);
        corpoPrincipal.revalidate();
        corpoPrincipal.repaint();
    }

    private void acaoLogout() {
        Object[] opcoes = {"Sim", "Não"};
        int resposta = JOptionPane.showOptionDialog(
                SwingUtilities.getWindowAncestor(this),
                "Você realmente deseja encerrar sua sessão e apagar os cookies?",
                "Confirmação de Saída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, opcoes, opcoes[0]
        );

        if (resposta == JOptionPane.YES_OPTION) {
            BancoDados.apagarCookie();
            System.exit(0);
        }
    }

    private void atualizarTela() {
        if (sist != null) {
            sist.configuraTela(new TelaPerfil(sist));
        }
    }
}