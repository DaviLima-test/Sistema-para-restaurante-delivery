package repositorio;


import java.sql.*;

public class SessaoBanco {
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_delivery?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String SENHA = "1234"; // Adapte para a sua senha

    private static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    // 1. INICIALIZAR (Usando a sua estrutura corrigida)
    public static void inicializarBanco() {
        String sqlTabela = "CREATE TABLE IF NOT EXISTS cookie (" +
                "id INT PRIMARY KEY, " +
                "logado TINYINT DEFAULT 0, " +
                "nome_usuario VARCHAR(255), " +
                "email_usuario VARCHAR(255)" +
                ");";

        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlTabela);

            // Insere a linha padrão com ID 1 se for a primeira vez rodando
            String sqlInsertInicial = "INSERT IGNORE INTO cookie (id, logado, nome_usuario, email_usuario) VALUES (1, 0, NULL, NULL);";
            stmt.execute(sqlInsertInicial);

            System.out.println("Tabela 'cookie' padronizada e verificada no MySQL!");

        } catch (SQLException e) {
            System.err.println("Erro ao iniciar a tabela no MySQL: " + e.getMessage());
        }
    }

    // 2. SALVAR COOKIE (Adicionado nome e e-mail)
    public static void salvarCookieLogin(String nome, String email) {
        String sql = "UPDATE cookie SET logado = 1, nome_usuario = ?, email_usuario = ? WHERE id = 1;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
            System.out.println("Cookie atualizado no MySQL!");

        } catch (SQLException e) {
            System.err.println("Erro ao salvar cookie no MySQL: " + e.getMessage());
        }
    }

    // 3. VERIFICAR SE ESTÁ LOGADO
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
            System.out.println("Cookie deletado do MySQL.");

        } catch (SQLException e) {
            System.err.println("Erro ao apagar cookie no MySQL: " + e.getMessage());
        }
    }
}