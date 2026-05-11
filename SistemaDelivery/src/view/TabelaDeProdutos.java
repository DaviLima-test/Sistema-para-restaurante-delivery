package view;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import repositorio.Dados;
import model.Produto;

public class TabelaDeProdutos extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel modelo;
    private JTextField txtPesquisa;

    public TabelaDeProdutos() {
        setTitle("Gestão de Cardápio");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 500);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // --- Título ---
        JLabel lblTitulo = new JLabel("GESTÃO DE PRODUTOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(234, 29, 44));
        lblTitulo.setBounds(30, 10, 300, 30);
        contentPane.add(lblTitulo);

        // --- Campo de Pesquisa ---
        JLabel lblIcone = new JLabel("Pesquisar:"); 
        lblIcone.setBounds(30, 50, 100, 25);
        contentPane.add(lblIcone);

        txtPesquisa = new JTextField();
        txtPesquisa.setBounds(115, 50, 390, 25);
        contentPane.add(txtPesquisa);
        
        // Listener para filtrar a tabela enquanto digita
        txtPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                atualizarTabela(txtPesquisa.getText());
            }
        });

        // --- Tabela de Dados ---
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(30, 90, 525, 250);
        contentPane.add(scrollPane);

        modelo = new DefaultTableModel(
            new Object[][] {}, 
            new String[] { "ID", "Nome do Produto", "Preço" }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(modelo);
        table.setRowHeight(30);
        
        // Alinhamento das colunas
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        
        scrollPane.setViewportView(table);

        // --- Botões de Ação ---
        JButton btnRemover = new JButton("REMOVER ITEM");
        btnRemover.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRemover.setBackground(new Color(234, 29, 44));
        btnRemover.setForeground(Color.WHITE);
        btnRemover.setBounds(30, 360, 150, 40);
        btnRemover.addActionListener(e -> removerProduto());
        contentPane.add(btnRemover);

        JButton btnAtualizar = new JButton("ATUALIZAR");
        btnAtualizar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAtualizar.setBackground(new Color(80, 80, 80));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setBounds(215, 360, 150, 40);
        btnAtualizar.addActionListener(e -> atualizarTabela(""));
        contentPane.add(btnAtualizar);

        JButton btnFechar = new JButton("FECHAR");
        btnFechar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnFechar.setBounds(405, 360, 150, 40);
        btnFechar.addActionListener(e -> dispose());
        contentPane.add(btnFechar);

        atualizarTabela("");
    }

    // Atualiza os dados exibidos na tabela
    public void atualizarTabela(String filtro) {
        modelo.setRowCount(0);
        for (Produto p : Dados.listaProdutos) {
            String nome = p.getNome().toLowerCase();
            String busca = filtro.toLowerCase();

            if (filtro.isEmpty() || nome.contains(busca)) {
                modelo.addRow(new Object[] { 
                    p.getId(), 
                    p.getNome().toUpperCase(), 
                    "R$ " + p.getPreco() 
                });
            }
        }
    }

    // Método para remover o produto selecionado da lista
    private void removerProduto() {
        int linha = table.getSelectedRow();
        
        if (linha >= 0) {
            int idParaRemover = (int) modelo.getValueAt(linha, 0);
            String nome = (String) modelo.getValueAt(linha, 1);

            int confirmacao = JOptionPane.showConfirmDialog(this, 
                "Deseja excluir o item: " + nome + "?", 
                "Confirmação", JOptionPane.YES_NO_OPTION);

            if (confirmacao == JOptionPane.YES_OPTION) {
                // Percorre a lista para encontrar o ID e remover
                for (int i = 0; i < Dados.listaProdutos.size(); i++) {
                    if (Dados.listaProdutos.get(i).getId() == idParaRemover) {
                        Dados.listaProdutos.remove(i);
                        break; 
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Produto excluído!");
                atualizarTabela(txtPesquisa.getText()); 
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.");
        }
    }
}