package model;

public class TesteDriver {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Sucesso! O Java encontrou o driver do MySQL.");
        } catch (Exception e) {
            System.out.println("❌ Erro: O Java ainda não sabe onde está o arquivo .jar.");
            e.printStackTrace();
        }
    }
}