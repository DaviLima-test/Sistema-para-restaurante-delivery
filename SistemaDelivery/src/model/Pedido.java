package model;

public class Pedido {
	private String nomeCliente;
	private String endereco;
	private Produto produtoSelecionado;
	private int quantidade;
	private String formaPagamento;
	private double taxaEntrega;
	private double valorTotal;
	private String status; // "Pendente", "A caminho", "Entregue"
	public Pedido(String nomeCliente, String endereco, Produto produtoSelecionado, int quantidade, String formaPagamento, double taxaEntrega, String status) {
	    this.nomeCliente = nomeCliente;
	    this.endereco = endereco;
	    this.produtoSelecionado = produtoSelecionado;
	    this.quantidade = quantidade;
	    this.formaPagamento = formaPagamento;
	    this.taxaEntrega = taxaEntrega;
	    this.status = status;
	    this.valorTotal = (produtoSelecionado.getPreco() * quantidade) + taxaEntrega;
	    
	}
	
	public String getNomeCliente() { return nomeCliente; }
	public String getEndereco() { return endereco; }
	public Produto getProdutoSelecionado() { return produtoSelecionado; }
	public int getQuantidade() { return quantidade; }
	public String getFormaPagamento() { return formaPagamento; }
	public double getTaxaEntrega() { return taxaEntrega; }
	public double getValorTotal() { return valorTotal; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
}