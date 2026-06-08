package model;

import com.mysql.cj.x.protobuf.MysqlxSql;

import java.sql.*;
import java.util.prefs.Preferences;
public class Login {
    protected static String Email;
    protected static String Senha;
    protected static String User;
    protected static String tipo;
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_delivery";
    private static final String USUARIO = "root";
    private static final String SENHA = "1234"; // Adapte para a sua senha
    private static int id;

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
public String GetUser() {
    if(User.isEmpty()) {
        if (Email.isEmpty() || Senha.isEmpty()) {
            System.out.println("Nao ha como requerir pois alguma das variáveis está sem o ngc");
            return null;
        } else {
            String sqlBuscar = "SELECT nome FROM usuarios WHERE email = ? AND senha = ?;";

            try (Connection conn = obterConexao();
                 PreparedStatement pstmt = conn.prepareStatement(sqlBuscar)) {

                pstmt.setString(1, Email);
                pstmt.setString(2, Senha);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Se encontrou o usuário, pega o nome dele do banco
                        String nomeUsuario = rs.getString("nome");
                        return nomeUsuario;
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao realizar login no MySQL: " + e.getMessage());
                return null;
            }
        }
    }else{
        return User;
    }
    return null;
    }

    public static String GetTipo() {
        return tipo;

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
                "email_usuario VARCHAR(255)," +
                "tipo_usuario VARCHAR(255)"+
                ");";

        String sqlTabelaRestaurante = "CREATE TABLE IF NOT EXISTS restaurante (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(255) NOT NULL, " +
                "localizacao VARCHAR(255) NOT NULL, " +
                "estrelas INT NOT NULL, " +
                "id_gerente INT ," +
                "CONSTRAINT fk_restaurante_gerente" +
                "    FOREIGN KEY (id_gerente) REFERENCES usuarios(id)" +
                "    ON DELETE CASCADE" +
                "    ON UPDATE CASCADE"+
                ");";


        String sqlTabelaCardapio = "CREATE TABLE IF NOT EXISTS cardapio (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome_prato VARCHAR(255) NOT NULL," +
                "preco DECIMAL(10,2) NOT NULL," +
                "id_restaurante INT," +
                "CONSTRAINT fk_cardapio_restaurante" +
                "    FOREIGN KEY (id_restaurante) REFERENCES restaurante(id)" +
                "    ON DELETE CASCADE" +
                "    ON UPDATE CASCADE"+
                ");";
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {
            //stmt.executeUpdate("DROP DATABASE IF EXISTS sistema_delivery");
            stmt.executeUpdate(sqlCriarBanco);
            stmt.executeUpdate("USE sistema_delivery");
            stmt.execute(sqlTabelaUsuarios);
            stmt.execute(sqlTabelaCookie);
            stmt.execute(sqlTabelaRestaurante);
            stmt.execute(sqlTabelaCardapio);
            // Insere a linha padrão do cookie se for a primeira vez rodando
            String sqlInsertInicial = "INSERT IGNORE INTO cookie (id, logado, nome_usuario, email_usuario, tipo_usuario) VALUES (1, 0, NULL, NULL,NULL);";
            stmt.execute(sqlInsertInicial);

            System.out.println("Banco de dados e tabelas ('usuarios' , 'cookie' , 'restaurante' e 'cardapio') verificados com sucesso!");

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
            ln.tipo = tipo;

            System.out.println("Usuário cadastrado com sucesso!");
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
            return false;
        }
    }
    public static boolean cadastrarRestaurante(String nome, String localizacao,String email ,String senha, String avaliacao) {
        String sql_find = "SELECT id FROM usuarios where email = ? AND senha = ?";
        boolean passou = false;
        String id = new String();
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql_find)) {

            pstmt.setString(1, email);
            pstmt.setString(2, senha);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getString("id");
                    passou = true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao realizar login no MySQL: " + e.getMessage());
            return  false;
        }
        if(!passou) {
            System.out.println("Usuário ou senha incorretos.");
            return false; // Retorna falso se as credenciais estiverem erradas
        }
        String sql = "INSERT INTO restaurante (nome , estrelas, avaliacao, id_gerente) VALUES (?, ?, ?, ?);";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, localizacao);
            pstmt.setString(3, avaliacao);
            pstmt.setString(4,id);
            pstmt.executeUpdate();
            System.out.println("Restaurante cadastrado com sucesso !");
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar restaurante: " + e.getMessage());
            return false;
        }
    }
    public static boolean cadastrarCardapio(String nome_prato, String preco,String nome ,String avaliacao) {
        String sql_find = "SELECT id FROM restaurante where nome = ? AND estrelas = ?";
        boolean passou = false;
        String id = new String();
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql_find)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, avaliacao);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getString("id");
                    passou = true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar restaurante Mysql: " + e.getMessage());
            return  false;
        }
        if(!passou) {
            System.out.println("Restaurante nao encontrado.");
            return false; // Retorna falso se as credenciais estiverem erradas
        }
        String sql = "INSERT INTO cardapio (nome, preco, id_restaurante) VALUES (?, ?, ?);";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, preco);
            pstmt.setString(3,id);
            pstmt.executeUpdate();
            System.out.println("Cardapio cadastrado com sucesso !");
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar cardapio: " + e.getMessage());
            return false;
        }
    }
    // 2. AUTENTICAR E SALVAR COOKIE (O método de login que sua tela vai chamar)
    public static boolean realizarLogin(String email, String senha) {

        String sqlBuscar = "SELECT nome, tipo FROM usuarios WHERE email = ? AND senha = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuscar)) {

            pstmt.setString(1, email);
            pstmt.setString(2, senha);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Se encontrou o usuário, pega o nome dele do banco
                    String nomeUsuario = rs.getString("nome");
                    String tipoUsuario = rs.getString("tipo");
                    Login  ln = new Login(email,nomeUsuario,tipoUsuario);
                    salvarCookieLogin(nomeUsuario, email,tipoUsuario);
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
    private static void salvarCookieLogin(String nome, String email,String tipo) {
        String sql = "UPDATE cookie SET logado = 1, nome_usuario = ?, email_usuario = ?, tipo_usuario = ? WHERE id = 1;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3,tipo);
            pstmt.executeUpdate();
            System.out.println("Esta salvo o cookie");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cookie: " + e.getMessage());
        }
    }


    public static boolean verificarSeEstaLogado() {
        String sql = "SELECT logado, nome_usuario, email_usuario, tipo_usuario FROM cookie WHERE id = 1;";

        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement();

             ResultSet rs = stmt.executeQuery(sql)) {


            if (rs.next()) {
                boolean estaLogado = rs.getBoolean("logado");
                System.out.println(estaLogado);
                if (estaLogado) {

                    String nomeUsuario = rs.getString("nome_usuario");
                    String emailUsuario = rs.getString("email_usuario");
                    String tipousuario = rs.getString("tipo_usuario");

                    Login ln = new Login(emailUsuario, nomeUsuario,tipousuario);
                    System.out.println("Logado aqui");
                    return true;
                }


                return false;
            }

        } catch (SQLException e) {

            System.err.println("Erro ao ler cookie no MySQL: " + e.getMessage());
            throw new RuntimeException(e);
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