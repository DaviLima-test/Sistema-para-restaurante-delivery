package model;

public class Produto {
    public String nome;
    public String descricao;
    private int codigo;

    public Produto(String nome, String descricao, int codigo) {
        this.nome = nome;
        this.descricao = descricao;
        this.codigo = codigo;
    }
}