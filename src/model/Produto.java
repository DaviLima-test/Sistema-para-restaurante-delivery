package model;

public class Produto {

    private int codigo;
    private String nome;
    private String descricao;
    private double preco;
    private String restaurante;

    public Produto(int codigo, String nome ,double preco, String restaurante){
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
        this.restaurante = restaurante;
    }
    public Produto(int codigo, String nome, String descricao, double preco) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

    public Produto(String nome,double preco) {
        this.nome = nome;
        this.preco = preco;
        this.descricao = "";
    }

    public Produto(int codigo, String nome, double preco) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = "";
        this.preco = preco;
    }

    public Produto() {

    }


    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getRestaurante(){return  this.restaurante;}
    @Override
    public String toString() {
        return nome;
    }
}