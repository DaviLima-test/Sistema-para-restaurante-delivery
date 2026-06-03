package model;

public class Entregador extends Login {
    private String Nome;
    private String CPF;
    private String Pedido;
    private int Estrelas;

    public Entregador(String email, String senha, String user, String nome, String cpf) {
        super(email, senha, user);
        this.Nome = nome;
        this.CPF = cpf;
    }

    public void AceitarPedido(Pedido pedido) {
        pedido.setEstado(2); // Muda o estado para Em rota
    }

    public void NegarPedido(Pedido pedido) {
        pedido.setEstado(1); // Devolve para pendente
    }

    public void GerarRelatorio() {
        // Lógica simplificada
    }
}