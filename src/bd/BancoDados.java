package bd;

import model.Cartao;
import model.Login;
import view.Telabase;

import java.sql.*;
import java.util.ArrayList;

public class BancoDados {

    //protected static String Email;
    protected static String Senha;

    //protected static String tipo;


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
    public static String GetUser() {
        if(Telabase.getLogin().GetUser() == null || Telabase.getLogin().GetUser().isEmpty()) {
            if (Telabase.getLogin().GetEmail() == null || Telabase.getLogin().GetEmail().isEmpty() ||Senha == null || Senha.isEmpty()) {
                System.err.println("Nao ha como requerir pois alguma das variáveis está sem o ngc");
                return null;
            } else {
                String sqlBuscar = "SELECT nome FROM usuarios WHERE email = ? AND senha = ?;";

                try (Connection conn = obterConexao();
                     PreparedStatement pstmt = conn.prepareStatement(sqlBuscar)) {

                    pstmt.setString(1, Telabase.getLogin().GetEmail());
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
            return Telabase.getLogin().GetUser();
        }
        return null;
    }


    private String MudarSenha() {
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

        String sqlTabelCarteira = "CREATE TABLE IF NOT EXISTS cartoes (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "id_cliente INT NOT NULL, " +
                        "numero VARCHAR(16) NOT NULL, " +
                        "titular VARCHAR(100) NOT NULL, " +
                        "validade VARCHAR(7) NOT NULL, " +
                        "cvv VARCHAR(4) NOT NULL, " +
                        "bandeira VARCHAR(20) NOT NULL, " +
                        "principal BOOLEAN DEFAULT FALSE, " +
                        "CONSTRAINT fk_cartao_cliente " + // Adicionado espaço aqui
                        "FOREIGN KEY (id_cliente) REFERENCES usuarios(id) " + // Corrigido de 'clientes' para 'usuarios'
                        "ON DELETE CASCADE ON UPDATE CASCADE" +
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
        String sqlTabelaPedidos = "CREATE TABLE  IF NOT EXISTS pedidos(" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "id_prato INT," +
                "id_restaurante INT," +
                "id_cliente INT," +
                "id_entregador INT," +
                "status INT DEFAULT 1," +
                "horario_entrega VARCHAR(10)," +
                "data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "CONSTRAINT fk_pedido_cardapio" +
                "   FOREIGN KEY (id_prato) REFERENCES cardapio(id)" +
                "   ON DELETE CASCADE" +
                "   ON UPDATE CASCADE," +
                "CONSTRAINT fk_pedido_restaurante FOREIGN KEY (id_restaurante) REFERENCES restaurante(id) " +
                "ON DELETE CASCADE " +
                "ON UPDATE CASCADE," +
                "CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente) REFERENCES usuarios(id) " +
                "   ON DELETE SET NULL " +
                "ON UPDATE CASCADE," +
                "CONSTRAINT fk_pedido_entregador FOREIGN KEY (id_entregador) REFERENCES usuarios(id) " +
                "   ON DELETE SET NULL" +
                " ON UPDATE CASCADE" +
                ");";

        String urlServidor = "jdbc:mysql://localhost:3306/";
        try (Connection connServidor = DriverManager.getConnection(urlServidor, USUARIO, SENHA);
             Statement stmt = connServidor.createStatement()) {

            stmt.executeUpdate(sqlCriarBanco);
            System.out.println("Banco de dados 'sistema_delivery' verificado/criado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao criar o banco de dados: " + e.getMessage());
            return; // Se não criar o banco, não adianta continuar
        }

        // 2º PASSO: Agora que o banco existe, conecta nele normalmente para criar as tabelas
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {

            // Como a URL já aponta para sistema_delivery, não precisa do "USE sistema_delivery"
            stmt.execute(sqlTabelaUsuarios);
            stmt.execute(sqlTabelaCookie);
            stmt.execute(sqlTabelaRestaurante);
            stmt.execute(sqlTabelaCardapio);
            stmt.execute(sqlTabelaPedidos);
            stmt.execute(sqlTabelCarteira);
            // Insere a linha padrão do cookie se for a primeira vez rodando
            String sqlInsertInicial = "INSERT IGNORE INTO cookie (id, logado, nome_usuario, email_usuario, tipo_usuario) VALUES (1, 0, NULL, NULL, NULL);";
            stmt.execute(sqlInsertInicial);

            System.out.println("Tabelas verificadas/criadas com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar as tabelas: " + e.getMessage());
        }
    }


    public static boolean cadastrarUsuario(String nome, String email, String senha,String tipo) {
        String sql = "INSERT INTO usuarios (nome, email, senha, tipo) VALUES (?, ?, ?, ?);";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, senha);
            pstmt.setString(4,tipo);
            pstmt.executeUpdate();
            Telabase.setLogin(new Login(email,nome,tipo));
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
        String sql = "INSERT INTO restaurante (nome, localizacao, estrelas, id_gerente) VALUES (?, ?, ?, ?);";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, localizacao);
            pstmt.setString(3, avaliacao);   // vai para "estrelas"
            pstmt.setString(4, id);
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
                     Telabase.setLogin(new Login(email,nomeUsuario,tipoUsuario));
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

                    Telabase.setLogin(new Login(emailUsuario, nomeUsuario,tipousuario));
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
    public static boolean salvarCartao(Cartao cartao) {
        String idCliente =GetIdUsuario(Telabase.getLogin().GetUser(),Telabase.getLogin()
                .GetEmail(),Telabase.getLogin().GetTipo());
        String sql = "INSERT INTO cartoes (id_cliente, numero, titular, validade, cvv, bandeira, principal) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = BancoDados.obterConexao(); // Sua classe de conexão
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(idCliente));
            stmt.setString(2, cartao.getNumero());
            stmt.setString(3, cartao.getTitular());
            stmt.setString(4, cartao.getValidade());
            stmt.setString(5, cartao.getCvv());
            stmt.setString(6, cartao.getBandeira());
            stmt.setBoolean(7, cartao.isPrincipal()); // O Java converte o boolean automaticamente para o tipo BIT/BOOLEAN do SQL

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static String GetIdUsuario(String nome , String email , String tipo){

        String sql = "SELECT id FROM usuarios where nome = ? AND email = ? AND tipo = ? ;";


        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3,tipo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Se encontrou o usuário, pega o nome dele do banco
                    String id = rs.getString("id");
                    return id;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao realizar login no MySQL: " + e.getMessage());
            return null;
        }
        return null;
    }

    public static ArrayList<Cartao> GetCartoes(){
        String id =GetIdUsuario(Telabase.getLogin().GetUser(),Telabase.getLogin()
                .GetEmail(),Telabase.getLogin().GetTipo());
        ArrayList<Cartao> cartoes = new ArrayList<>();

        String sql = "SELECT numero, titular, validade, cvv, bandeira, principal FROM cartoes WHERE id_cliente = ?;";


        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String numero = rs.getString("numero");
                    String titular = rs.getString("titular");
                    String validade = rs.getString("validade");
                    String cvv = rs.getString("cvv");
                    String bandeira = rs.getString("bandeira");
                    boolean principal = rs.getBoolean("principal");
                    cartoes.add(new Cartao(numero, titular, validade, cvv, bandeira, principal));
                }
                return cartoes;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao realizar login no MySQL: " + e.getMessage());
            return null;
        }

    }
    public static Cartao GetCartaoPrincipal() {
        String id = GetIdUsuario(Telabase.getLogin().GetUser(), Telabase.getLogin()
                .GetEmail(), Telabase.getLogin().GetTipo());
        String sql = "SELECT numero, titular, validade, cvv, bandeira, principal FROM cartoes WHERE id_cliente = ? AND principal = 1;";


        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String numero = rs.getString("numero");
                    String titular = rs.getString("titular");
                    String validade = rs.getString("validade");
                    String cvv = rs.getString("cvv");
                    String bandeira = rs.getString("bandeira");
                    boolean principal = rs.getBoolean("principal");

                    // Retorna o cartão preenchido com sucesso
                    return new Cartao(numero, titular, validade, cvv, bandeira, principal);
                }
                }
        } catch (SQLException e) {
            System.err.println("Erro ao realizar login no MySQL: " + e.getMessage());
            return null;
        }

        return null;
    }



}