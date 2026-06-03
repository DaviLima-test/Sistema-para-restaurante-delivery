package model;

import com.mysql.cj.x.protobuf.MysqlxSql;

import java.sql.*;
import java.util.prefs.Preferences;
public class Login {
    protected String Email;
    protected String Senha;
    protected String User;
    protected String tipo;
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_delivery";
    private static final String USUARIO = "root";
    private static final String SENHA = "1234"; // Adapte para a sua senha
    private static int id;

    public Login(String email, String senha, String user) {
        this.Email = email;
        this.Senha = senha;
        this.User = user;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String GetUser() {
        return this.User;
    }

    public String MudarSenha() {
        return "Senha alterada com sucesso.";
    }


    private static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }


    public static void inicializarBanco() {

        String sqlCriarBanco = "CREATE DATABASE IF NOT EXISTS sistema_delivery";

        // Nova tabela para cadastrar os usuários do sistema
        String sqlTabelaUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) UNIQUE NOT NULL, " +
                "senha VARCHAR(255) NOT NULL," +
                "tipo VARCHAR(255) NOT NULL"+
                ");";


        String sqlTabelaCookie = "CREATE TABLE IF NOT EXISTS cookie (" +
                "id INT PRIMARY KEY, " +
                "logado TINYINT DEFAULT 0, " +
                "nome_usuario VARCHAR(255), " +
                "email_usuario VARCHAR(255)" +
                ");";

        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {
            //stmt.executeUpdate("DROP DATABASE IF EXISTS sistema_delivery");
            stmt.executeUpdate(sqlCriarBanco);
            stmt.executeUpdate("USE sistema_delivery");
            stmt.execute(sqlTabelaUsuarios);
            stmt.execute(sqlTabelaCookie);

            // Insere a linha padrão do cookie se for a primeira vez rodando
            String sqlInsertInicial = "INSERT IGNORE INTO cookie (id, logado, nome_usuario, email_usuario) VALUES (1, 0, NULL, NULL);";
            stmt.execute(sqlInsertInicial);

            System.out.println("Banco de dados e tabelas ('usuarios' e 'cookie') verificados com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro no processo MySQL ao inicializar: " + e.getMessage());
        }
    }


    public static boolean cadastrarUsuario(String nome, String email, String senha,String tipo) {
        String sql = "INSERT INTO usuarios (nome, email, senha, tipo) VALUES (?, ?, ?, ?);";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, senha); // Em sistemas reais, aplique criptografia aqui
            pstmt.setString(4,tipo);
            pstmt.executeUpdate();
            Login ln = new Login(email,senha,nome);
            ln.setTipo(tipo);
            System.out.println("Usuário cadastrado com sucesso!");
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
            return false;
        }
    }

    // 2. AUTENTICAR E SALVAR COOKIE (O método de login que sua tela vai chamar)
    public static boolean realizarLogin(String email, String senha) {
        // Busca na tabela de usuários se existe a combinação exata de e-mail e senha
        String sqlBuscar = "SELECT nome FROM usuarios WHERE email = ? AND senha = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuscar)) {

            pstmt.setString(1, email);
            pstmt.setString(2, senha);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Se encontrou o usuário, pega o nome dele do banco
                    String nomeUsuario = rs.getString("nome");

                    // Salva que ele está logado na tabela cookie
                    salvarCookieLogin(nomeUsuario, email);
                    System.out.println("Login realizado com sucesso para: " + nomeUsuario);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao realizar login no MySQL: " + e.getMessage());
        }

        System.out.println("Usuário ou senha incorretos.");
        return false; // Retorna falso se as credenciais estiverem erradas
    }

    // Método auxiliar privado (usado internamente pelo realizarLogin)
    private static void salvarCookieLogin(String nome, String email) {
        String sql = "UPDATE cookie SET logado = 1, nome_usuario = ?, email_usuario = ? WHERE id = 1;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cookie: " + e.getMessage());
        }
    }


    public static boolean verificarSeEstaLogado() {
        String sql = "SELECT logado FROM cookie WHERE id = 1;";

        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getBoolean("logado");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler cookie no MySQL: " + e.getMessage());
        }
        return false;
    }

    // 4. APAGAR COOKIE (Logout)
    public static void apagarCookie() {
        String sql = "UPDATE cookie SET logado = 0, nome_usuario = NULL, email_usuario = NULL WHERE id = 1;";

        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("Cookie deletado do MySQL (Logout efetuado).");

        } catch (SQLException e) {
            System.err.println("Erro ao apagar cookie no MySQL: " + e.getMessage());
        }
    }
}