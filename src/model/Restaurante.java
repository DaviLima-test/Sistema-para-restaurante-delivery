package model;

public class Restaurante {
    private int id;
    private String nome;
    private String Localizacao;
    private int Estrelas;

    public Restaurante(int id ,String nome , String localizacao , int estrelas ){
        this.id = id;
        this.nome = nome;
        this.Estrelas = estrelas;
        this.Localizacao = localizacao;
    }
    public void LancarPedido(String item, String cliente) {
        // Lógica simplificada
    }

    public void GerarComanda() {
        // Lógica simplificada
    }

    public String getNome() {
        return nome;
    }

    public String getLocalizacao() {
        return Localizacao;
    }

    public int getEstrelas() {
        return Estrelas;
    }

    public int getId() {return id;}
}