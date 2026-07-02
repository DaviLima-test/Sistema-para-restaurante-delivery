package model;

public class Cliente {
    private String Cartao;
    private int Estrelas;
    private String email;
    private String nome;

    public Cliente(String email, String user, String cartao) {

        this.nome = user;
        this.email = email;
        this.Cartao = cartao;
        this.Estrelas = 5; // Padrão inicial
    }

    public Cliente() {

    }

    public String getNome() { return this.nome; }

    public void Pedir() {
        // Lógica simplificada
    }

    public void AdicionarNoCarrinho(Produto p) {
        // Lógica simplificada
    }

    public void RemoverNoCarrinho(Produto p) {
        // Lógica simplificada
    }

    public void Avaliacao(Pedido pedido) {
        // Lógica simplificada
    }

    public void Pagamento(Double valor) {
        // Lógica simplificada
    }

    public void AcompanharPedidos() {
        // Lógica simplificada
    }

    public void CancelarPedido(Pedido pedido) {
        // Lógica simplificada
    }

    public String getEmail() {return email;  }
}