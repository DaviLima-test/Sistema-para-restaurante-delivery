/*
package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Pedido;
import repositorio.Dados;

public class TelaEntregador extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel modelo;

    public TelaEntregador() {
        setTitle("Painel de Logística - Entregas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 480);
        setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("PEDIDOS EM ROTA");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setBounds(20, 20, 300, 25);
        contentPane.add(lblTitulo);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 60, 540, 250);
        contentPane.add(scrollPane);

        modelo = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Cliente", "Endereço", "Produto", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(modelo);
        table.setRowHeight(25);
        scrollPane.setViewportView(table);

        JButton btnAcao = new JButton("ATUALIZAR STATUS / IMPRIMIR");
        btnAcao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAcao.setForeground(Color.WHITE);
        btnAcao.setBackground(new Color(234, 29, 44));
        btnAcao.setBounds(150, 330, 300, 50);
        btnAcao.addActionListener(e -> processarEntrega());
        contentPane.add(btnAcao);

        atualizarTabela();
    }

    public void atualizarTabela() {
        modelo.setRowCount(0);
        for (Pedido p : Dados.listaPedidos) {
            if (!p.getStatus().equalsIgnoreCase("Entregue") && !p.getStatus().equalsIgnoreCase("Pago")) {
                Object[] linha = {
                    p.getNomeCliente(),
                    p.getEndereco(),
                    p.getProdutoSelecionado().getNome(),
                    p.getStatus()
                };
                modelo.addRow(linha);
            }
        }
    }

    private void processarEntrega() {
        int fila = table.getSelectedRow();
        
        if (fila >= 0) {
            String nomeCliente = (String) modelo.getValueAt(fila, 0);
            
            for (Pedido p : Dados.listaPedidos) {
                if (p.getNomeCliente().equals(nomeCliente)) {
                    
                    if (p.getStatus().equals("Pendente")) {
                        p.setStatus("A caminho");
                        JOptionPane.showMessageDialog(this, "Comanda gerada para: " + nomeCliente);
                        imprimirComanda(p);
                        break;
                    } 
                    else if (p.getStatus().equals("A caminho")) {
                        p.setStatus("Pago"); 
                        JOptionPane.showMessageDialog(this, "Entrega confirmada! Valor registrado.");
                        break;
                    }
                }
            }
            atualizarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na lista.");
        }
    }

    private void imprimirComanda(Pedido p) {
        try {
            JTextArea areaTexto = new JTextArea();
            areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 10));
            
            StringBuilder txt = new StringBuilder();
            txt.append("----------------------------------------\n");
            txt.append("           COMANDA DE ENTREGA           \n");
            txt.append("----------------------------------------\n\n");
            txt.append("CLIENTE: ").append(p.getNomeCliente().toUpperCase()).append("\n");
            txt.append("ENDERECO: ").append(p.getEndereco()).append("\n");
            txt.append("----------------------------------------\n");
            txt.append("PEDIDO: ").append(p.getQuantidade()).append("x ").append(p.getProdutoSelecionado().getNome()).append("\n");
            txt.append("PAGAMENTO: ").append(p.getFormaPagamento().toUpperCase()).append("\n");
            
            String cobranca = p.getFormaPagamento().equalsIgnoreCase("Dinheiro") ? "COBRAR NO ATO" : "PAGO";
            txt.append("SITUACAO: ").append(cobranca).append("\n");
            
            txt.append("----------------------------------------\n");
            txt.append("TOTAL DO PEDIDO: R$ ").append(p.getValorTotal()).append("\n\n");
            txt.append("       OBRIGADO E BOM TRABALHO!         \n");
            txt.append("----------------------------------------\n");

            areaTexto.setText(txt.toString());
            areaTexto.print(null, null, false, null, null, false);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao imprimir comanda.");
        }
    }
}

 */