package model;

import bd.BancoDados;

import java.sql.*;
import java.util.ArrayList;

public class Login{
    private  String Email;
    private  String Senha;
    private  String User;
    private  String tipo;
    ArrayList<Produto>listaCarrinho;

    /*
    public Login(String email, String senha, String user) {
        this.Email = email;
        this.Senha = senha;
        this.User = user;
        //this.tipo = new String();
    }


     */
    public Login(String email , String user,String tipo) {
        this.Email = email;
        this.User = user;
        this.tipo = tipo;
}
public  String GetUser() {
    if(User == null || User.isEmpty()) {
        return BancoDados.GetUser();
    }else{
        return User;
    }

}

    public String GetTipo() {
        return tipo;

    }
    public String GetEmail(){
        return Email;
    }

    private String MudarSenha() {

        return "Senha alterada com sucesso.";
    }

}
