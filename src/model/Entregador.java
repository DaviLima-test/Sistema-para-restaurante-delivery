package model;

public class Entregador extends  Cliente {
    private String nome;
    private String cpf;
    private String email;
    private String pedido;
    private int estrelas;

    public Entregador(String email, String user, String cpf) {
        this.email = email;
        this.nome = user;
        this.cpf = cpf;
    }

    public Entregador() {
    }

    public void aceitarPedido(Pedido pedido) {
        pedido.setEstado(4); // Altera o estado do pedido para: "Em Rota"
    }

    public void negarPedido(Pedido pedido) {
        pedido.setEstado(3); // Libera de volta o pedido para o estado: "Disponível para aceite"
    }

    public void gerarRelatorio() {
        // Lógica interna encapsulada no modelo de dados
    }

    // Getters e Setters para consistência
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getEstrelas() { return estrelas; }
    public void setEstrelas(int estrelas) { this.estrelas = estrelas; }
}