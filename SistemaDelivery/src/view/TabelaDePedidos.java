package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Pedido;
import repositorio.Dados;
import java.text.MessageFormat;

public class TabelaDePedidos extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel modelo;
	private JLabel lblTotalPix, lblTotalCartao, lblTotalDinheiro, lblTotalGeral;

	public TabelaDePedidos() {
		setTitle("Relatório de Vendas");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 750, 550);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBackground(new Color(245, 245, 245));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		// Listagem de Pedidos
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 20, 690, 300);
		contentPane.add(scrollPane);

		modelo = new DefaultTableModel(
			new Object[][] {},
			new String[] {"Cliente", "Total", "Pagamento", "Status"}
		) {
			@Override
			public boolean isCellEditable(int row, int column) { return false; }
		};

		table = new JTable(modelo);
		table.setRowHeight(30);
		table.getTableHeader().setBackground(new Color(234, 29, 44));
		table.getTableHeader().setForeground(Color.WHITE);
		scrollPane.setViewportView(table);

		// Painel de Resumo Financeiro
		JPanel painelSoma = new JPanel();
		painelSoma.setBackground(Color.WHITE);
		painelSoma.setBorder(BorderFactory.createTitledBorder("Faturamento (Apenas pedidos pagos)"));
		painelSoma.setBounds(20, 335, 690, 100);
		painelSoma.setLayout(new GridLayout(2, 2, 10, 10));
		contentPane.add(painelSoma);

		lblTotalPix = new JLabel("Total Pix: R$ 0.00");
		lblTotalCartao = new JLabel("Total Cartão: R$ 0.00");
		lblTotalDinheiro = new JLabel("Total Dinheiro: R$ 0.00");
		lblTotalGeral = new JLabel("FATURAMENTO TOTAL: R$ 0.00");
		lblTotalGeral.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblTotalGeral.setForeground(new Color(234, 29, 44));

		painelSoma.add(lblTotalPix);
		painelSoma.add(lblTotalCartao);
		painelSoma.add(lblTotalDinheiro);
		painelSoma.add(lblTotalGeral);

		// Ação de exportação
		JButton btnPDF = new JButton("GERAR DOCUMENTO PDF");
		btnPDF.setBounds(20, 450, 690, 40);
		btnPDF.setBackground(new Color(50, 50, 50));
		btnPDF.setForeground(Color.WHITE);
		btnPDF.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnPDF.addActionListener(e -> gerarPdf());
		contentPane.add(btnPDF);
		
		atualizarTabela();
	}

	public void atualizarTabela() {
		modelo.setRowCount(0);
		double somaPix = 0;
		double somaCartao = 0;
		double somaDinheiro = 0;

		for (Pedido p : Dados.listaPedidos) {
			Object[] linha = {
				p.getNomeCliente(),
				"R$ " + p.getValorTotal(),
				p.getFormaPagamento(),
				p.getStatus()
			};
			modelo.addRow(linha);

			// Soma apenas o que já foi confirmado como Pago
			if (p.getStatus() != null && p.getStatus().equalsIgnoreCase("Pago")) {
				double valor = p.getValorTotal();
				String forma = p.getFormaPagamento().toLowerCase();
				
				if (forma.contains("pix")) {
					somaPix += valor;
				} else if (forma.contains("cart")) {
					somaCartao += valor;
				} else if (forma.contains("dinheiro")) {
					somaDinheiro += valor;
				}
			}
		}

		lblTotalPix.setText("Total Pix: R$ " + somaPix);
		lblTotalCartao.setText("Total Cartão: R$ " + somaCartao);
		lblTotalDinheiro.setText("Total Dinheiro: R$ " + somaDinheiro);
		lblTotalGeral.setText("FATURAMENTO TOTAL: R$ " + (somaPix + somaCartao + somaDinheiro));
	}

	private void gerarPdf() {
	    try {
	        String infoFaturamento = lblTotalGeral.getText();
	        
	        MessageFormat cabecalho = new MessageFormat("Relatório de Vendas - Delivery");
	        MessageFormat rodape = new MessageFormat(infoFaturamento + " | Página {0}");
	        
	        // Abre a caixa de diálogo de impressão do sistema
	        boolean sucesso = table.print(JTable.PrintMode.FIT_WIDTH, cabecalho, rodape);
	        
	        if (sucesso) {
	            JOptionPane.showMessageDialog(this, "Relatório enviado para impressão/PDF.");
	        }
	    } catch (Exception e) {
	        JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage());
	    }
	}
}