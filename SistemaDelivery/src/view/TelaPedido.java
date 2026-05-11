package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.*;
import repositorio.Dados;
import java.awt.*;

public class TelaPedido extends JFrame {

    private JPanel contentPane;
    private JTextField txtCliente, txtEndereco, txtQuantidade;
    private JComboBox<String> cbProdutos, cbPagamento;
    private JLabel lblTotal;
    private double taxaEntrega = 7.00;

    public TelaPedido() {
        setTitle("Lançar Novo Pedido");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 520);
        setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Componentes da Interface
        adicionarRotulo("Nome do Cliente:", 20);
        txtCliente = adicionarCampo(40);

        adicionarRotulo("Endereço de Entrega:", 80);
        txtEndereco = adicionarCampo(100);

        adicionarRotulo("Selecione o Produto:", 140);
        cbProdutos = new JComboBox<>();
        cbProdutos.setBounds(30, 160, 360, 25);
        
        // Carrega produtos cadastrados
        for (Produto p : Dados.listaProdutos) {
            cbProdutos.addItem(p.getNome());
        }
        contentPane.add(cbProdutos);

        adicionarRotulo("Quantidade:", 200);
        txtQuantidade = new JTextField("1");
        txtQuantidade.setBounds(30, 220, 60, 25);
        contentPane.add(txtQuantidade);

        adicionarRotulo("Forma de Pagamento:", 200);
        cbPagamento = new JComboBox<>(new String[] {"Pix", "Cartão", "Dinheiro"});
        cbPagamento.setBounds(150, 220, 240, 25);
        contentPane.add(cbPagamento);

        lblTotal = new JLabel("Total com Entrega: R$ 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setBounds(30, 280, 300, 20);
        contentPane.add(lblTotal);

        JButton btnFinalizar = new JButton("CONFIRMAR PEDIDO");
        btnFinalizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFinalizar.setBackground(new Color(234, 29, 44)); 
        btnFinalizar.setForeground(Color.WHITE);
        btnFinalizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnFinalizar.setBounds(80, 350, 280, 50);
        btnFinalizar.addActionListener(e -> finalizarPedido());
        contentPane.add(btnFinalizar);
    }

    private void finalizarPedido() {
        try {
            int selecao = cbProdutos.getSelectedIndex();
            if (selecao < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um produto no cardápio.");
                return;
            }

            Produto p = Dados.listaProdutos.get(selecao);
            int qtd = Integer.parseInt(txtQuantidade.getText());
            String pagamento = (String) cbPagamento.getSelectedItem();
            
            // Define status com base no pagamento
            String status = "Pendente";
            if (pagamento.equals("Pix") || pagamento.equals("Cartão")) {
                status = "Pago";
            }

            Pedido novoPedido = new Pedido(
                txtCliente.getText(), 
                txtEndereco.getText(), 
                p, 
                qtd, 
                pagamento, 
                taxaEntrega, 
                status
            );

            if (pagamento.equals("Pix")) {
                exibirPainelPix(novoPedido.getValorTotal());
            }

            Dados.listaPedidos.add(novoPedido);

            JOptionPane.showMessageDialog(this, "Pedido registrado!\nStatus: " + status);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe uma quantidade válida.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar: " + ex.getMessage());
        }
    }

    private void exibirPainelPix(double valorTotal) {
        try {
            java.io.File file = new java.io.File("img/qrcode.jpeg");
            if (file.exists()) {
                ImageIcon img = new ImageIcon(file.getAbsolutePath());
                Image redimensionada = img.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                
                JOptionPane.showMessageDialog(this, 
                    "Valor a pagar: R$ " + valorTotal, 
                    "Pagamento Pix", 
                    JOptionPane.PLAIN_MESSAGE, 
                    new ImageIcon(redimensionada));
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem.");
        }
    }

    private void adicionarRotulo(String texto, int y) {
        JLabel label = new JLabel(texto);
        label.setBounds(30, y, 200, 14);
        contentPane.add(label);
    }

    private JTextField adicionarCampo(int y) {
        JTextField campo = new JTextField();
        campo.setBounds(30, y, 360, 25);
        contentPane.add(campo);
        return campo;
    }
}