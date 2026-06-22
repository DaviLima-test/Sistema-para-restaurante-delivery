package view;

import model.Login;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class TelaMenu extends JPanel {
    protected boolean menu_aberto = false;
    private JLayeredPane camadas;
    private JPanel barra_lateral;
    private Overlay overlay;          // Removido o private para inicializar corretamente
    private JPanel conteudoApp;       // Container de fundo que segura o Header + Conteúdo da filha
    private JPanel conteudoInterno;   // O conteúdo específico que a classe filha vai injetar
    private Telabase sist;

    public TelaMenu(Telabase sist) {
        this.sist = sist;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. Inicializa o Painel de Camadas principal
        camadas = new JLayeredPane();
        add(camadas, BorderLayout.CENTER);

        // 2. Inicializa o Container Principal do App (Camada de Fundo)
        conteudoApp = new JPanel(new BorderLayout());
        conteudoApp.setBackground(Color.WHITE);

        // 3. Inicializa o Overlay (Camada do Meio) e adiciona o clique para fechar
        overlay = new Overlay(); // Correção: Instanciado corretamente
        overlay.setVisible(false);
        overlay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                configurarMenu(false);
            }
        });

        // 4. Inicializa a Barra Lateral (Camada de Frente)
        barra_lateral = new JPanel();
        barra_lateral.setLayout(new BoxLayout(barra_lateral, BoxLayout.Y_AXIS));
        barra_lateral.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        barra_lateral.setBackground(Color.WHITE);
        barra_lateral.setVisible(false);

        // 5. Instancia o HeaderPanel separado, passando a função de alternar o menu
        JPanel header = criarHeader();
        conteudoApp.add(header, BorderLayout.NORTH);

        // Montagem das Camadas
        camadas.add(conteudoApp, JLayeredPane.DEFAULT_LAYER);  // Camada 0
        camadas.add(overlay, JLayeredPane.PALETTE_LAYER);      // Camada 100
        camadas.add(barra_lateral, JLayeredPane.MODAL_LAYER);  // Camada 200

        // Ouvinte para garantir que os componentes preencham a tela inteira ao redimensionar
        camadas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int largura = camadas.getWidth();
                int altura = camadas.getHeight();

                conteudoApp.setBounds(0, 0, largura, altura);
                overlay.setBounds(0, 0, largura, altura);
                barra_lateral.setBounds(0, 0, 250, altura);
            }
        });

        // Ouvinte na própria tela para ajustar o Feed dinamicamente
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ajustarLarguraDoFeed();
            }
        });
        // ... Dentro do construtor de TelaMenu, logo após os métodos camadas.add(...) ...

// Força o tamanho inicial baseado nas variáveis estáticas da Telabase
        int w = view.Telabase.Width;
        int h = view.Telabase.Height;

        conteudoApp.setBounds(0, 0, w, h);
        overlay.setBounds(0, 0, w, h);
        barra_lateral.setBounds(0, 0, 250, h);
        iniciaMenu();

    }


    private JPanel criarHeader() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(800, 80));
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 20);

        ImageIcon inc_hambuger = new ImageIcon("img/hambuger_icon.png");
        Image novaImg = inc_hambuger.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);

        JButton bnt_hambuger = new JButton(new ImageIcon(novaImg));
        bnt_hambuger.setPreferredSize(new Dimension(50, 50));
        bnt_hambuger.setMaximumSize(new Dimension(50, 50));
        gbc.gridx = 0;
        gbc.weightx = 0.0;

        p.add(bnt_hambuger, gbc);

        // Aciona a função da própria classe
        bnt_hambuger.addActionListener(e -> alternarMenu());

        Texto logo = new Texto("AIFood");
        logo.setFont(new Font("Arial", Font.BOLD, 30));
        logo.setForeground(new Color(234, 16, 34));
        logo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // --- COLOQUE A SUA AÇÃO AQUI ---
                System.out.println("O usuário clicou no nome: " + Login.GetUser());
                if (sist != null) {
                    sist.configuraTela(new TelaPrincipal(sist)); // Abre a tela de perfil
                }
            }

            // OPCIONAL: Se quiser fazer o texto mudar de cor ao passar o mouse por cima (efeito hover)
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                logo.setForeground(new Color(240, 240, 240)); // Fica um cinza claro
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                logo.setForeground(new Color(234, 16, 34)); // Volta a ser branco puro
            }
        });
        gbc.gridx = 1;
        p.add(logo, gbc);

        CampoTextoArredondado busca = new CampoTextoArredondado(18, 15, new Color(240, 240, 240), 30);
        busca.setText(" Buscar Restaurantes e pratos ...");
        busca.setForeground(Color.GRAY);
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        busca.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (busca.getText().equals(" Buscar Restaurantes e pratos ...")) {
                    busca.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (busca.getText().isEmpty()) {
                    busca.setText(" Buscar Restaurantes e pratos ...");
                }
            }
        });

        p.add(busca, gbc);
        return p;
    }
        protected void setConteudoInterno(JPanel painelFilho) {
        this.conteudoInterno = painelFilho;
        conteudoApp.add(painelFilho, BorderLayout.CENTER);
        ajustarLarguraDoFeed();
    }

    public void iniciaMenu() {
        adicionarItemMenu("Perfil", e -> {
            if(sist != null) {
                System.out.println("Passo pela sist aqui");
                TelaPerfil tp = new TelaPerfil(sist);
                sist.configuraTela(tp);
            }
        });
        adicionarItemMenu("Carrinho", e -> {

        });
        adicionarItemMenu("Meus pedidos", e -> {/* Ação dos Pedidos */});
        adicionarItemMenu("Carteira", e -> {
            TelaCarteira tc = new TelaCarteira(sist);
            sist.configuraTela(tc);
        });

        if ("restaurante".equals(Login.GetTipo())) {
            adicionarItemMenu("Gerenciar restaurante", e -> {/* Ação do Restaurante */});
        }
        if ("entregador".equals(Login.GetTipo())) {
            adicionarItemMenu("Pedidos a serem entregues", e -> {
                TelaPedidosEntregador tp = new TelaPedidosEntregador(sist);
                sist.configuraTela(tp);
            });
        }
        adicionarItemMenu("Sair", e -> {
            Object[] opcoes = {"Sim", "Não"};
            int resposta = JOptionPane.showOptionDialog(
                    SwingUtilities.getWindowAncestor(barra_lateral),
                    "Você realmente deseja sair?", "Confirmação",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, opcoes, opcoes[0]
            );
            if (resposta == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    private void adicionarItemMenu(String texto, ActionListener e) {
        BotaoArredondado btn = new BotaoArredondado(texto, 25, Color.decode("#e96769"), 20);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        btn.addActionListener(e);
        barra_lateral.add(btn);
        barra_lateral.add(Box.createVerticalStrut(15));
    }

    protected void alternarMenu(){
        configurarMenu(!menu_aberto);
    }

    private void configurarMenu(boolean abrir) {
        menu_aberto = abrir;

        overlay.setVisible(menu_aberto);
        if (menu_aberto) {
            barra_lateral.setPreferredSize(new Dimension(250, 0));
            barra_lateral.setVisible(true);
        } else {
            barra_lateral.setPreferredSize(new Dimension(0, 0));
            barra_lateral.setVisible(false);
        }

        ajustarLarguraDoFeed();

        Container paneltop = SwingUtilities.getWindowAncestor(this);
        if (paneltop != null) {
            paneltop.revalidate();
            paneltop.repaint();
        }
        camadas.repaint();
    }


    private void ajustarLarguraDoFeed() {
        // Proteção contra NullPointerException caso a classe filha ainda não tenha injetado o painel
        if (conteudoInterno == null) return;

        int larguraDisponivel = Telabase.Width;
        if (menu_aberto) {
            //larguraDisponivel -= 250;
        }
        //larguraDisponivel -= 20;

        int alturaAtual = conteudoInterno.getPreferredSize().height;
        conteudoInterno.setPreferredSize(new Dimension(larguraDisponivel, alturaAtual));
        conteudoInterno.revalidate();
    }

}