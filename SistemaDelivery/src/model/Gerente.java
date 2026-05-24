package model;

public class Gerente extends Login {

    public Gerente(String email, String senha, String user) {
        super(email, senha, user);
    }

    public void AdicionarProduto(Produto produto) {
        // Lógica simplificada
    }

    public void AtualizarProduto(String nome, String descricao) {
        // Lógica simplificada
    }

    public void RemoverProdutos(Produto produto) {
        // Lógica simplificada
    }

    public void SolicitarCadastroRest(String nomeRestaurante) {
        // Lógica simplificada
    }

    public void GerarRelatorio() {
        // Lógica simplificada
    }
}