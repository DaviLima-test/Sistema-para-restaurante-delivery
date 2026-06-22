package model;

import bd.BancoDados;
import view.Telabase;

import javax.sql.rowset.spi.TransactionalWriter;
import java.util.ArrayList;

public class Cartao {
    private String numero;
    private String titular;
    private String validade;
    private String cvv;
    private String bandeira;
    private boolean principal;

    public Cartao(String numero, String titular, String validade, String cvv, String bandeira, boolean principal) {
        this.numero = numero;
        this.titular = titular;
        this.validade = validade;
        this.cvv = cvv;
        this.bandeira = bandeira;
        this.principal = principal;
        adequarDadosDoCartao();
        BancoDados.salvarCartao(this);
    }

    public String getQuatroUltimosDigitos() {
        if (numero == null || numero.length() < 4) return "0000";
        return numero.substring(numero.length() - 4);
    }
    public static ArrayList<Cartao> getCartoes(){
        return  BancoDados.GetCartoes();
    }

    public void adequarDadosDoCartao() {


        if (this.numero != null) {
            this.numero = this.numero.trim().replace(" ", "");

            this.numero = this.numero.substring(0, Math.min(this.numero.length(), 16));
        }

        if (this.validade != null) {
            this.validade = this.validade.trim();

            this.validade = this.validade.substring(0, Math.min(this.validade.length(), 7));
        }

        if (this.cvv != null) {
            this.cvv = this.cvv.trim();

            this.cvv = this.cvv.substring(0, Math.min(this.cvv.length(), 4));
        }

        if (this.bandeira != null) {
            this.bandeira = this.bandeira.trim();

            this.bandeira = this.bandeira.substring(0, Math.min(this.bandeira.length(), 20));
        }
    }

    public String getNumero() { return numero; }
    public String getTitular() { return titular; }
    public String getValidade() { return validade; }
    public String getCvv() { return cvv; }
    public String getBandeira() { return bandeira; }
    public boolean isPrincipal() { return principal; }
    public void setPrincipal(boolean principal) { this.principal = principal; }
    public static Cartao GetPrincipal(){
        return BancoDados.GetCartaoPrincipal();
    }

}
