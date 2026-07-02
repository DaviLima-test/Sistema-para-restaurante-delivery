package model;

public class Entregador  {
    private String Nome;
    private String CPF;
    private String email;
    private String Pedido;
    private int Estrelas;

    public Entregador(String email, String user, String cpf) {
        this.email = email;
        this.Nome = user;
        this.CPF = cpf;
    }

    public Entregador() {

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