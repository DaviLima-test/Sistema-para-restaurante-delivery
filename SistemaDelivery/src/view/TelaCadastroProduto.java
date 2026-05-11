package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import repositorio.Dados; 
import model.Produto;     

public class TelaCadastroProduto extends JFrame {

    private JPanel contentPane;
    private JTextField txtNome;
    private JTextField txtPreco;
    private JTextField txtDescricao;

    public TelaCadastroProduto() {
        setTitle("Cadastro de Novo Produto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(150, 150, 450, 500);
        setLocationRelativeTo(null); 
        
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Cabeçalho
        JPanel header = new JPanel();
        header.setBackground(new Color(234, 29, 44));
        header.setBounds(0, 0, 450, 60);
        header.setLayout(null);
        contentPane.add(header);

        JLabel lblTitulo = new JLabel("NOVO PRODUTO");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(0, 15, 450, 30);
        header.add(lblTitulo);

        // Formulário
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        lblNome.setBounds(50, 80, 200, 20);
        contentPane.add(lblNome);

        txtNome = new JTextField();
        txtNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNome.setBounds(50, 105, 330, 35);
        contentPane.add(txtNome);

        JLabel lblPreco = new JLabel("Preço (R$):");
        lblPreco.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        lblPreco.setBounds(50, 160, 200, 20);
        contentPane.add(lblPreco);

        txtPreco = new JTextField();
        txtPreco.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPreco.setBounds(50, 185, 330, 35);
        contentPane.add(txtPreco);

        JLabel lblDescricao = new JLabel("Descrição:");
        lblDescricao.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        lblDescricao.setBounds(50, 240, 200, 20);
        contentPane.add(lblDescricao);

        txtDescricao = new JTextField();
        txtDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescricao.setBounds(50, 265, 330, 35);
        contentPane.add(txtDescricao);

        // Botão de Ação
        JButton btnSalvar = new JButton("SALVAR PRODUTO");
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setBackground(new Color(234, 29, 44));
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalvar.setBounds(50, 360, 330, 50);
        btnSalvar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                salvar();
            }
        });
        contentPane.add(btnSalvar);
    }

    private void salvar() {
        try {
            String nome = txtNome.getText();
            String precoTexto = txtPreco.getText().replace(",", "."); 
            String descricao = txtDescricao.getText();

            // Validação básica
            if (nome.isEmpty() || precoTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha nome e preço.");
                return;
            }

            double preco = Double.parseDouble(precoTexto);

            // Instancia o modelo
            Produto p = new Produto();
            p.setNome(nome);
            p.setPreco(preco);
            p.setDescricao(descricao);
            
            // Gera ID automático
            p.setId(Dados.listaProdutos.size() + 1);

            // Adiciona no repositório
            Dados.listaProdutos.add(p);

            JOptionPane.showMessageDialog(this, "Sucesso! Produto cadastrado.");
            dispose(); 
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido no campo preço.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }
}