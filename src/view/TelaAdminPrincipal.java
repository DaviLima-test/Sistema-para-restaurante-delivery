package view;

import bd.BancoDados;
import model.Admin;
import model.Login;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Interface gráfica (Dashboard Principal) destinada ao monitoramento e controle estatístico por administradores.
 * <p>
 * Apresenta indicadores consolidados de desempenho como contagem de usuários segmentados, total de pedidos e
 * faturamento bruto global da plataforma. Oferece pontos de entrada para a tela de moderação avançada e para
 * a emissão de relatórios consolidados em formato físico (.txt).
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public class TelaAdminPrincipal extends TelaMenu {

    /** Cor vermelha corporativa adotada para realce de elementos informativos e títulos de alta relevância. */
    private static final Color COR_PRIMARIA = new Color(234, 16, 34);

    /** Cor verde destinada à estilização de dados e valores monetários positivos (faturamento). */
    private static final Color COR_VERDE    = new Color(46, 174, 82);

    /** Cor de borda clara para delimitação sutil dos painéis internos de estatísticas. */
    private static final Color COR_BORDA    = new Color(230, 230, 230);

    /** Cor cinza neutra de fundo aplicada aos cards de exibição de dados numéricos (stats). */
    private static final Color COR_CINZA_BG = new Color(245, 245, 245);

    /** Referência ao coordenador de navegação e troca de telas do sistema. */
    private final Telabase sist;

    /** Instância ativa da classe de domínio do back-end para execução síncrona das operações administrativas. */
    private final Admin adminCorporativo;

    /**
     * Construtor da janela principal do painel de administração.
     * Inicializa os componentes visuais Swing e cria o objeto operacional {@link Admin} associado à sessão atual.
     *
     * @param sist O frame base de gerenciamento global de telas {@link Telabase}.
     */
    public TelaAdminPrincipal(Telabase sist) {
        super(sist);
        this.sist = sist;
        this.adminCorporativo = new Admin(Login.GetEmail(), "", Login.GetUser());

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(criarCabecalho(), BorderLayout.NORTH);

        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBackground(Color.WHITE);
        corpo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        corpo.add(criarSecaoStats());
        corpo.add(Box.createVerticalStrut(24));
        corpo.add(criarSecaoAcoes());

        JScrollPane scroll = new JScrollPane(corpo);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scroll, BorderLayout.CENTER);

        setConteudoInterno(container);
    }

    /**
     * Fabrica o painel superior da tela, exibindo o título institucional e as credenciais do operador da sessão.
     * @return Um {@link JPanel} estruturado contendo as etiquetas textuais identificadoras.
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

        Texto titulo = new Texto("🛡  Painel Administrativo");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(new Color(30, 30, 30));
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        titulos.add(titulo);

        JLabel sub = new JLabel("Logado como: " + Login.GetUser() + " (" + rotuloTipo(Login.GetTipo()) + ")");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);
        titulos.add(sub);

        p.add(titulos, BorderLayout.WEST);
        return p;
    }

    /**
     * Mapeia de forma amigável a string crua do banco de dados para um rótulo formatado de exibição.
     * @param tipo A string de permissão original ('admin' ou 'admin_master').
     * @return O texto amigável legível para o usuário final.
     */
    private String rotuloTipo(String tipo) {
        if ("admin_master".equals(tipo)) return "Admin Master";
        if ("admin".equals(tipo)) return "Admin";
        return tipo;
    }

    /**
     * Compõe e agrupa as seções visuais contendo os cartões estatísticos de usuários e movimentação financeira.
     * Consome as contagens por meio do objeto back-end {@link Admin}.
     * @return Um {@link JPanel} contendo as malhas (grids) de contagem populadas.
     */
    private JPanel criarSecaoStats() {
        JPanel secao = new JPanel();
        secao.setLayout(new BoxLayout(secao, BoxLayout.Y_AXIS));
        secao.setBackground(Color.WHITE);
        secao.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Stats");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        secao.add(titulo);

        JLabel sub1 = new JLabel("Usuários");
        sub1.setFont(new Font("Arial", Font.BOLD, 13));
        sub1.setForeground(Color.GRAY);
        sub1.setAlignmentX(Component.LEFT_ALIGNMENT);
        secao.add(Box.createVerticalStrut(10));
        secao.add(sub1);
        secao.add(Box.createVerticalStrut(8));

        JPanel linhaUsuarios = new JPanel(new GridLayout(1, 4, 12, 0));
        linhaUsuarios.setOpaque(false);
        linhaUsuarios.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaUsuarios.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        linhaUsuarios.add(criarCardStat("Novos Clientes", String.valueOf(adminCorporativo.obterContagemClientes()), COR_PRIMARIA));
        linhaUsuarios.add(criarCardStat("Novos Restaurantes", String.valueOf(adminCorporativo.obterContagemRestaurantes()), COR_PRIMARIA));
        linhaUsuarios.add(criarCardStat("Novos Entregadores", String.valueOf(adminCorporativo.obterContagemEntregadores()), COR_PRIMARIA));
        linhaUsuarios.add(criarCardStat("Admins", String.valueOf(adminCorporativo.obterContagemAdmins()), COR_PRIMARIA));
        secao.add(linhaUsuarios);

        JLabel sub2 = new JLabel("Pedidos");
        sub2.setFont(new Font("Arial", Font.BOLD, 13));
        sub2.setForeground(Color.GRAY);
        sub2.setAlignmentX(Component.LEFT_ALIGNMENT);
        secao.add(Box.createVerticalStrut(18));
        secao.add(sub2);
        secao.add(Box.createVerticalStrut(8));

        JPanel linhaPedidos = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaPedidos.setOpaque(false);
        linhaPedidos.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaPedidos.setMaximumSize(new Dimension(600, 90));
        linhaPedidos.add(criarCardStat("Vendas (pedidos)", String.valueOf(adminCorporativo.obterTotalPedidos()), COR_VERDE));
        linhaPedidos.add(criarCardStat("Faturamento", String.format("R$ %.2f", adminCorporativo.obterFaturamentoTotal()), COR_VERDE));
        secao.add(linhaPedidos);

        return secao;
    }

    /**
     * Helper visual para a construção padronizada de um card individual de métricas (caixa cinza).
     *
     * @param rotulo   O descritivo do dado monitorado.
     * @param valor    O dado numérico convertido em formato de String.
     * @param corValor A tonalidade cromática a ser aplicada no texto do valor principal.
     * @return Um container estruturado contendo o card formatado.
     */
    private JPanel criarCardStat(String rotulo, String valor, Color corValor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COR_CINZA_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.BOLD, 22));
        lblValor.setForeground(corValor);
        card.add(lblValor);

        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblRotulo.setForeground(Color.GRAY);
        card.add(lblRotulo);

        return card;
    }

    /**
     * Desenha a área de botões operacionais da tela (redirecionamento de moderação e exportação).
     * @return Um {@link JPanel} contendo as ações engatadas.
     */
    private JPanel criarSecaoAcoes() {
        JPanel secao = new JPanel();
        secao.setLayout(new BoxLayout(secao, BoxLayout.Y_AXIS));
        secao.setBackground(Color.WHITE);
        secao.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Área de trabalho");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        secao.add(titulo);
        secao.add(Box.createVerticalStrut(12));

        JPanel linhaBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        linhaBotoes.setOpaque(false);
        linhaBotoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotaoArredondado btnUsuarios = new BotaoArredondado("👥  Gerenciar Usuários", 20, COR_PRIMARIA, 14);
        btnUsuarios.setPreferredSize(new Dimension(220, 44));
        btnUsuarios.addActionListener(e -> sist.configuraTela(new TelaAdminModeracao(sist)));
        linhaBotoes.add(btnUsuarios);

        BotaoArredondado btnRelatorio = new BotaoArredondado("📄  Gerar Relatório (.txt)", 20, new Color(90, 90, 90), 14);
        btnRelatorio.setPreferredSize(new Dimension(220, 44));
        btnRelatorio.addActionListener(e -> gerarRelatorio());
        linhaBotoes.add(btnRelatorio);

        secao.add(linhaBotoes);
        return secao;
    }

    /**
     * Aciona um diálogo seletor de arquivos físico, compila as estatísticas recuperadas do
     * back-end ativo e realiza a escrita persistente de um relatório analítico textual (.txt).
     */
    private void gerarRelatorio() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Escolher local para salvar o relatório");
        chooser.setSelectedFile(new java.io.File("relatorio_aifood.txt"));

        int resultado = chooser.showSaveDialog(SwingUtilities.getWindowAncestor(this));
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        java.io.File arquivo = chooser.getSelectedFile();

        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        StringBuilder sb = new StringBuilder();
        sb.append("RELATÓRIO AIFOOD — ").append(dataHora).append("\n");
        sb.append("Gerado por: ").append(Login.GetUser()).append(" (").append(rotuloTipo(Login.GetTipo())).append(")\n");
        sb.append("----------------------------------------\n\n");
        sb.append("USUÁRIOS\n");
        sb.append("  Clientes:      ").append(adminCorporativo.obterContagemClientes()).append("\n");
        sb.append("  Restaurantes:  ").append(adminCorporativo.obterContagemRestaurantes()).append("\n");
        sb.append("  Entregadores:  ").append(adminCorporativo.obterContagemEntregadores()).append("\n");
        sb.append("  Admins:        ").append(adminCorporativo.obterContagemAdmins()).append("\n\n");
        sb.append("PEDIDOS\n");
        sb.append("  Total de pedidos: ").append(adminCorporativo.obterTotalPedidos()).append("\n");
        sb.append(String.format("  Faturamento total: R$ %.2f%n", adminCorporativo.obterFaturamentoTotal()));

        try (FileWriter fw = new FileWriter(arquivo)) {
            fw.write(sb.toString());
            JOptionPane.showMessageDialog(this, "Relatório salvo em:\n" + arquivo.getAbsolutePath(),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar relatório: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}