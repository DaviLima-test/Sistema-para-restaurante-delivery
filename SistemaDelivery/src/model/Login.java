package model;

public class Login {
    protected String Email;
    protected String Senha;
    protected String User;
    private static int id;

    public Login(String email, String senha, String user) {
        this.Email = email;
        this.Senha = senha;
        this.User = user;
    }

    public String GetUser() {
        return this.User;
    }

    public String MudarSenha() {
        return "Senha alterada com sucesso.";
    }

    public void ApagarConta() {
        // Implementação simplificada
    }
}