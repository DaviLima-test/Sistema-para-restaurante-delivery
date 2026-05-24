package model;

public class Cliente extends Login {
    private String Cartao;
    private int Estrelas;

    public Cliente(String email, String senha, String user, String cartao) {
        super(email, senha, user);
        this.Cartao = cartao;
        this.Estrelas = 5; // Padrão inicial
    }

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
}