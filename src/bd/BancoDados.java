package bd;

import model.*;
import view.Telabase;

import java.sql.*;
import java.util.ArrayList;

public class BancoDados {

    protected static String Senha;
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_delivery";
    private static final String USUARIO = "root";
    private static final String SENHA = "1234"; // Adapte para a sua senha
    private static int id;
    private static void DeletarBancodados(){

        String sqlCriarBanco = "DROP DATABASE IF EXISTS sistema_delivery";

        String urlServidor = "jdbc:mysql://localhost:3306/";

        try (Connection connServidor = DriverManager.getConnection(urlServidor, USUARIO, SENHA);

             Statement stmt = connServidor.createStatement()) {


            stmt.executeUpdate(sqlCriarBanco);

            System.out.println("Banco de dados 'sistema_delivery' verificado/criado com sucesso!");


        } catch (SQLException e) {

            System.err.println("Erro ao criar o banco de dados: " + e.getMessage());

            return; // Se não criar o banco, não adianta continuar

        }

    }
    public static String GetUser() {
        if(Telabase.getLogin().GetUser() == null || Telabase.getLogin().GetUser().isEmpty()) {
            if (Telabase.getLogin().GetEmail() == null || Telabase.getLogin().GetEmail().isEmpty() || Senha == null || Senha.isEmpty()) {
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
                            return rs.getString("nome");
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Erro ao realizar login no MySQL: " + e.getMessage());
                    return null;
                }
            }
        } else {
            return Telabase.getLogin().GetUser();
        }
        return null;
    }

    // --- NOVO MÉTODO GETTER PARA A LOCALIZAÇÃO ---
    public static String getLocalizacao() {
        // Primeiro tenta pegar do Cookie do Banco de Dados (caso esteja logado)
        String sql = "SELECT localizacao_usuario FROM cookie WHERE id = 1 AND logado = 1;";
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String localizacao = rs.getString("localizacao_usuario");
                if (localizacao != null && !localizacao.isEmpty()) {
                    return localizacao;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter localização do cookie: " + e.getMessage());
        }

        // Caso não ache no cookie, mas exista um usuário na Telabase, busca direto na tabela de usuários
        if (Telabase.getLogin() != null && Telabase.getLogin().GetEmail() != null) {
            String sqlUsuario = "SELECT localizacao FROM usuarios WHERE email = ?;";
            try (Connection conn = obterConexao();
                 PreparedStatement pstmt = conn.prepareStatement(sqlUsuario)) {

                pstmt.setString(1, Telabase.getLogin().GetEmail());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("localizacao");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao buscar localização do usuário no banco: " + e.getMessage());
            }
        }

        return null; // Retorna null se não encontrar em lugar nenhum
    }

    private String MudarSenha() {
        return "Senha alterada com sucesso.";
    }

    private static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void inicializarBanco() {
        //DeletarBancodados();
        String sqlCriarBanco = "CREATE DATABASE IF NOT EXISTS sistema_delivery";

        String sqlTabelaUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) UNIQUE NOT NULL, " +
                "senha VARCHAR(255) NOT NULL," +
                "tipo VARCHAR(255) NOT NULL," +
                "localizacao VARCHAR(255) NOT NULL"+
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
                "CONSTRAINT fk_cartao_cliente " +
                "FOREIGN KEY (id_cliente) REFERENCES usuarios(id) " +
                "ON DELETE CASCADE ON UPDATE CASCADE" +
                ");";

        // ALTERADO: Adicionada a coluna 'localizacao_usuario' na tabela cookie
        String sqlTabelaCookie = "CREATE TABLE IF NOT EXISTS cookie (" +
                "id INT PRIMARY KEY, " +
                "logado TINYINT DEFAULT 0, " +
                "nome_usuario VARCHAR(255), " +
                "email_usuario VARCHAR(255)," +
                "tipo_usuario VARCHAR(255)," +
                "localizacao_usuario VARCHAR(255)" +
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

        String sqlTabelaPedidos = "CREATE TABLE IF NOT EXISTS pedidos(" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "id_restaurante INT," +
                "id_cliente INT," +
                "id_entregador INT," +
                "status INT DEFAULT 1," +
                "horario_entrega VARCHAR(10)," +
                "data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "CONSTRAINT fk_pedido_restaurante FOREIGN KEY (id_restaurante) REFERENCES restaurante(id) " +
                "   ON DELETE CASCADE ON UPDATE CASCADE," +
                "CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente) REFERENCES usuarios(id) " +
                "   ON DELETE SET NULL ON UPDATE CASCADE," +
                "CONSTRAINT fk_pedido_entregador FOREIGN KEY (id_entregador) REFERENCES usuarios(id) " +
                "   ON DELETE SET NULL ON UPDATE CASCADE" +
                ");";
        String sqlTabelaItensPedidos = "CREATE TABLE IF NOT EXISTS itens_pedido(" +
                "id_pedido INT," +
                "id_prato INT," +
                "quantidade INT DEFAULT 1," +
                "PRIMARY KEY (id_pedido, id_prato)," +
                "CONSTRAINT fk_itens_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id) " +
                "   ON DELETE CASCADE ON UPDATE CASCADE," +
                "CONSTRAINT fk_itens_cardapio FOREIGN KEY (id_prato) REFERENCES cardapio(id) " +
                "   ON DELETE CASCADE ON UPDATE CASCADE" +
                ");";

        String urlServidor = "jdbc:mysql://localhost:3306/";
        try (Connection connServidor = DriverManager.getConnection(urlServidor, USUARIO, SENHA);
             Statement stmt = connServidor.createStatement()) {

            stmt.executeUpdate(sqlCriarBanco);
            System.out.println("Banco de dados 'sistema_delivery' verificado/criado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao criar o banco de dados: " + e.getMessage());
            return;
        }

        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlTabelaUsuarios);
            stmt.execute(sqlTabelaCookie);
            stmt.execute(sqlTabelaRestaurante);
            stmt.execute(sqlTabelaCardapio);
            stmt.execute(sqlTabelaPedidos);
            stmt.execute(sqlTabelCarteira);
            stmt.execute(sqlTabelaItensPedidos);
            // ALTERADO: Adicionado um campo NULL a mais para a nova coluna no INSERT IGNORE
            String sqlInsertInicial = "INSERT IGNORE INTO cookie (id, logado, nome_usuario, email_usuario, tipo_usuario, localizacao_usuario) VALUES (1, 0, NULL, NULL, NULL, NULL);";
            stmt.execute(sqlInsertInicial);

            System.out.println("Tabelas verificadas/criadas com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar as tabelas: " + e.getMessage());
        }
    }
    public static boolean cadastrarUsuario(String nome, String email, String senha, String tipo, String localizacao) {
        String sql = "INSERT INTO usuarios (nome, email, senha, tipo, localizacao) VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, senha);
            pstmt.setString(4, tipo);
            pstmt.setString(5, localizacao);
            pstmt.executeUpdate();

            Telabase.setLogin(new Login(email, nome, tipo));


            //salvarCookieLogin(nome, email, tipo, localizacao);

            System.out.println("Usuário cadastrado com sucesso!");
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
            return false;
        }
    }

    // ALTERADO: Agora busca também a 'localizacao' e passa para o método do cookie

    public static boolean realizarLogin(String email, String senha) {
        String sqlBuscar = "SELECT nome, tipo, localizacao FROM usuarios WHERE email = ? AND senha = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuscar)) {

            pstmt.setString(1, email);
            pstmt.setString(2, senha);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nomeUsuario = rs.getString("nome");
                    String tipoUsuario = rs.getString("tipo");
                    String localizacaoUsuario = rs.getString("localizacao"); // Captura a localização

                    Telabase.setLogin(new Login(email, nomeUsuario, tipoUsuario));
                    // Passa a localização capturada para salvar no cookie
                    salvarCookieLogin(nomeUsuario, email, tipoUsuario, localizacaoUsuario);

                    System.out.println("Login realizado com sucesso para: " + nomeUsuario);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao realizar login no MySQL: " + e.getMessage());
        }

        System.out.println("Usuário ou senha incorretos.");
        return false;
    }
    // ALTERADO: Adicionado parâmetro 'localizacao' e alterada a Query SQL para atualizar o cookie correspondente

    private static void salvarCookieLogin(String nome, String email, String tipo, String localizacao) {
        String sql = "UPDATE cookie SET logado = 1, nome_usuario = ?, email_usuario = ?, tipo_usuario = ?, localizacao_usuario = ? WHERE id = 1;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, tipo);
            pstmt.setString(4, localizacao); // Salva a localização na tabela cookie
            pstmt.executeUpdate();
            System.out.println("Esta salvo o cookie com a localização.");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cookie: " + e.getMessage());
        }
    }
    // ALTERADO: Adicionado 'localizacao_usuario' no SELECT do cookie

    public static boolean verificarSeEstaLogado() {
        String sql = "SELECT logado, nome_usuario, email_usuario, tipo_usuario, localizacao_usuario FROM cookie WHERE id = 1;";

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
                    String localizacaousuario = rs.getString("localizacao_usuario");

                    Telabase.setLogin(new Login(emailUsuario, nomeUsuario, tipousuario));
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

    // ALTERADO: Também limpa o campo de localização ao deslogar
    public static void apagarCookie() {
        String sql = "UPDATE cookie SET logado = 0, nome_usuario = NULL, email_usuario = NULL, tipo_usuario = NULL, localizacao_usuario = NULL WHERE id = 1;";

        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("Cookie deletado do MySQL (Logout efetuado).");

        } catch (SQLException e) {
            System.err.println("Erro ao apagar cookie no MySQL: " + e.getMessage());
        }
    }

    // --- OS DEMAIS MÉTODOS CONTINUAM IGUAIS ---
    public static boolean cadastrarRestaurante(String nome, String localizacao, String email, String senha, String avaliacao) {
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
            return false;
        }
        if(!passou) {
            System.out.println("Usuário ou senha incorretos.");
            return false;
        }
        String sql = "INSERT INTO restaurante (nome, localizacao, estrelas, id_gerente) VALUES (?, ?, ?, ?);";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, localizacao);
            pstmt.setString(3, avaliacao);
            pstmt.setString(4, id);
            pstmt.executeUpdate();
            System.out.println("Restaurante cadastrado com sucesso !");
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar restaurante: " + e.getMessage());
            return false;
        }
    }
    public static boolean atualizarCardapio(int codigo, String novoNome, String novoPreco) {
        // Atualiza direto pelo ID do prato (codigo)
        String sql = "UPDATE cardapio SET nome_prato = ?, preco = ? WHERE id = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, novoNome);
            pstmt.setString(2, novoPreco);
            pstmt.setInt(3, codigo); // Passa o código direto aqui

            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Prato com código " + codigo + " atualizado com sucesso!");
                return true;
            } else {
                System.out.println("Nenhum prato foi encontrado com o código " + codigo);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar o cardápio pelo código: " + e.getMessage());
            return false;
        }
    }
    public static boolean cadastrarCardapio(String nome_prato, String preco, String nomeRestaurante, String localizacao) {
        // Agora busca por nome e localização, em vez de estrelas
        String sql_find = "SELECT id FROM restaurante WHERE nome = ? AND localizacao = ?";
        boolean passou = false;
        String id = "";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql_find)) {

            pstmt.setString(1, nomeRestaurante);
            pstmt.setString(2, localizacao);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getString("id");
                    passou = true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar restaurante Mysql: " + e.getMessage());
            return false;
        }

        if (!passou) {
            System.out.println("Restaurante nao encontrado com essa localização.");
            return false;
        }

        String sql = "INSERT INTO cardapio (nome_prato, preco, id_restaurante) VALUES (?, ?, ?);";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome_prato);
            pstmt.setString(2, preco);
            pstmt.setString(3, id);
            pstmt.executeUpdate();
            System.out.println("Cardapio cadastrado com sucesso !");
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar cardapio: " + e.getMessage());
            return false;
        }
    }
    public static Restaurante buscarRestaurantePorNome(String nomeBuscado) {
        String sql = "SELECT id, nome, localizacao, estrelas FROM restaurante WHERE nome = ? LIMIT 1;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Define o nome na consulta SQL com segurança
            pstmt.setString(1, nomeBuscado);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Se encontrou o restaurante no banco
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String localizacao = rs.getString("localizacao");
                    int estrelas = rs.getInt("estrelas");

                    // Cria e retorna o objeto usando exatamente o seu construtor
                    return new Restaurante(id, nome, localizacao, estrelas);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar restaurante pelo nome '" + nomeBuscado + "': " + e.getMessage());
        }

        // Retorna null caso o restaurante não exista no banco ou ocorra um erro
        return null;
    }
    public static Restaurante buscarRestaurantePorId(int idBuscado) {
        String sql = "SELECT id, nome, localizacao, estrelas FROM restaurante WHERE id = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Define o ID na consulta (setInt ao invés de setString)
            pstmt.setInt(1, idBuscado);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String localizacao = rs.getString("localizacao");
                    int estrelas = rs.getInt("estrelas");

                    // Instancia e retorna o objeto usando o seu construtor
                    return new Restaurante(id, nome, localizacao, estrelas);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar restaurante pelo ID " + idBuscado + ": " + e.getMessage());
        }

        return null;
    }
    public static ArrayList<Produto> getCardapioPorRestaurante(int idRestaurante) {
        ArrayList<Produto> produtos = new ArrayList<>();
        // Filtra direto no banco apenas os pratos daquele restaurante
        String sql = "SELECT id, nome_prato, preco FROM cardapio WHERE id_restaurante = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idRestaurante); // Define qual restaurante buscar

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int codigo = rs.getInt("id");
                    String nomePrato = rs.getString("nome_prato");
                    double preco = rs.getDouble("preco");

                    Produto produto = new Produto(codigo, nomePrato, preco,buscarRestaurantePorId(idRestaurante));
                    produtos.add(produto);
                }
            }
            return produtos;

        } catch (SQLException e) {
            System.err.println("Erro ao buscar cardápio do restaurante " + idRestaurante + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    public static boolean removerPrato(int codigo) {
        // Deleta o prato diretamente usando a chave primária (id)
        String sql = "DELETE FROM cardapio WHERE id = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, codigo);

            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Prato com código " + codigo + " removido com sucesso!");
                return true;
            } else {
                System.out.println("Nenhum prato foi encontrado com o código " + codigo);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao remover o prato do cardápio: " + e.getMessage());
            return false;
        }
    }
    public static ArrayList<Produto> getPratos(){
            ArrayList<Produto> produtos = new ArrayList<>();
            // Filtra direto no banco apenas os pratos daquele restaurante
            String sql = "SELECT c.id, nome_prato, preco , nome FROM cardapio " +
                    "AS c JOIN restaurante ON c.id_restaurante = restaurante.id;";

            try (Connection conn = obterConexao();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int codigo = rs.getInt("id");
                        String nomePrato = rs.getString("nome_prato");
                        double preco = rs.getDouble("preco");
                        String restaurante = rs.getString("nome");
                        Produto produto = new Produto(codigo, nomePrato, preco,buscarRestaurantePorNome(restaurante));
                        produtos.add(produto);
                    }
                }
                return produtos;

            } catch (SQLException e) {
                System.err.println("Erro ao buscar todos os pratos " +  e.getMessage());
                return new ArrayList<>();
            }
    }
    // (cancelamento de pedido feito via cancelarPedidoNoBanco(int idPedido), mais abaixo)
    public static String[] buscarRestaurantePorGerente(String emailGerente) {

        if (emailGerente == null || emailGerente.isEmpty()) {
            System.err.println("[BD] buscarRestaurantePorGerente: email nulo ou vazio.");
            return null;
        }

        String sql =
                "SELECT r.id, r.nome, r.localizacao, r.estrelas " +
                        "FROM restaurante r " +
                        "INNER JOIN usuarios u ON r.id_gerente = u.id " +
                        "WHERE u.email = ? " +
                        "LIMIT 1";                       // um gerente → um restaurante

        try (Connection conn = obterConexao();
             PreparedStatement p = conn.prepareStatement(sql)) {

            p.setString(1, emailGerente);

            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            rs.getString("id"),
                            rs.getString("nome"),
                            rs.getString("localizacao"),
                            rs.getString("estrelas")
                    };
                }
            }

        } catch (SQLException e) {
            System.err.println("[BD] Erro ao buscar restaurante do gerente: " + e.getMessage());
        }

        return null;   // gerente não tem restaurante ainda
    }

    public static ArrayList<Restaurante> getRestaurantes() {
        String sql = "SELECT r.id, r.nome, r.localizacao, r.estrelas " +
                "FROM restaurante r ";
        ArrayList<Restaurante> restaurantes = new ArrayList<>();
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String localizacao = rs.getString("localizacao");
                    int estrelas = rs.getInt("estrelas");

                    Restaurante restaurante = new Restaurante(id,nome,localizacao,estrelas);
                    restaurantes.add(restaurante);
                }
            }
            return restaurantes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        public static boolean salvarCartao(Cartao cartao) {
        String idCliente = GetIdUsuario(Telabase.getLogin().GetUser(), Telabase.getLogin().GetEmail(), Telabase.getLogin().GetTipo());
        String sql = "INSERT INTO cartoes (id_cliente, numero, titular, validade, cvv, bandeira, principal) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = BancoDados.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(idCliente));
            stmt.setString(2, cartao.getNumero());
            stmt.setString(3, cartao.getTitular());
            stmt.setString(4, cartao.getValidade());
            stmt.setString(5, cartao.getCvv());
            stmt.setString(6, cartao.getBandeira());
            stmt.setBoolean(7, cartao.isPrincipal());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String GetIdUsuario(String nome, String email, String tipo) {
        String sql = "SELECT id FROM usuarios where nome = ? AND email = ? AND tipo = ? ;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, tipo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao realizar login no MySQL: " + e.getMessage());
            return null;
        }
        return null;
    }

    public static ArrayList<Cartao> GetCartoes() {
        String id = GetIdUsuario(Telabase.getLogin().GetUser(), Telabase.getLogin().GetEmail(), Telabase.getLogin().GetTipo());
        ArrayList<Cartao> cartoes = new ArrayList<>();
        String sql = "SELECT numero, titular, validade, cvv, bandeira, principal FROM cartoes WHERE id_cliente = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) { // Alterado para while para pegar múltiplos cartões caso existam
                    String numero = rs.getString("numero");
                    String titular = rs.getString("titular");
                    String validade = rs.getString("validade");
                    String cvv = rs.getString("cvv");
                    String bandeira = rs.getString("bandeira");
                    boolean principal = rs.getBoolean("principal");
                    cartoes.add(new Cartao(numero, titular, validade, cvv, bandeira, principal));
                    System.out.println("Cartão pego"+bandeira + titular);
                }
                return cartoes;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao realizar login no MySQL: " + e.getMessage());
            return null;
        }
    }

    public static Cartao GetCartaoPrincipal() {
        String id = GetIdUsuario(Telabase.getLogin().GetUser(), Telabase.getLogin().GetEmail(), Telabase.getLogin().GetTipo());
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

                    return new Cartao(numero, titular, validade, cvv, bandeira, principal);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao realizar login no MySQL: " + e.getMessage());
            return null;
        }
        return null;
    }
    public static boolean criarPedido(ArrayList<Produto> carrinho, int idRestaurante) {
        if (carrinho == null || carrinho.isEmpty()) {
            System.err.println("[BD] Erro: Tentativa de criar pedido com carrinho vazio.");
            return false;
        }

        // 1. Recupera o ID do cliente logado no sistema
        String idClienteStr = GetIdUsuario(Telabase.getLogin().GetUser(), Telabase.getLogin().GetEmail(), Telabase.getLogin().GetTipo());
        if (idClienteStr == null) {
            System.err.println("[BD] Erro: Não foi possível identificar o usuário logado.");
            return false;
        }
        int idCliente = Integer.parseInt(idClienteStr);

        // SQL 1: Insere apenas os dados gerais do pedido (Sem o id_prato)
        String sqlPedido = "INSERT INTO pedidos (id_restaurante, id_cliente, status) VALUES (?, ?, 1);";

        // SQL 2: Insere os itens vinculando-os ao ID do pedido gerado
        //        ON DUPLICATE KEY UPDATE garante segurança extra caso o mesmo prato apareça mais de uma vez
        String sqlItens = "INSERT INTO itens_pedido (id_pedido, id_prato, quantidade) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantidade = quantidade + VALUES(quantidade);";

        try (Connection conn = obterConexao()) {
            // Desativa o auto-commit para garantir atomicidade (ou entra tudo ou nada)
            conn.setAutoCommit(false);

            // PreparedStatement.RETURN_GENERATED_KEYS serve para podermos pegar o ID que o MySQL acabou de gerar
            try (PreparedStatement pstmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement pstmtItens = conn.prepareStatement(sqlItens)) {

                // --- PASSO 1: Inserir o Pedido Principal ---
                pstmtPedido.setInt(1, idRestaurante);
                pstmtPedido.setInt(2, idCliente);
                pstmtPedido.executeUpdate();

                // Recupera o ID gerado para o pedido
                int idPedidoGerado = -1;
                try (ResultSet generatedKeys = pstmtPedido.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idPedidoGerado = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Falha ao obter o ID do pedido gerado.");
                    }
                }

                // --- PASSO 2: Inserir os Itens do Carrinho ---
                // Agrupa produtos repetidos (mesmo prato clicado mais de uma vez) somando a quantidade,
                // pois a tabela itens_pedido tem chave primária composta (id_pedido, id_prato)
                java.util.LinkedHashMap<Integer, Integer> quantidadesPorPrato = new java.util.LinkedHashMap<>();
                for (Produto produto : carrinho) {
                    quantidadesPorPrato.merge(produto.getCodigo(), 1, Integer::sum);
                }

                for (java.util.Map.Entry<Integer, Integer> item : quantidadesPorPrato.entrySet()) {
                    pstmtItens.setInt(1, idPedidoGerado);
                    pstmtItens.setInt(2, item.getKey());   // id_prato
                    pstmtItens.setInt(3, item.getValue()); // quantidade somada
                    pstmtItens.addBatch(); // Adiciona ao lote
                }


                pstmtItens.executeBatch();


                conn.commit();
                System.out.println("[BD] Pedido #" + idPedidoGerado + " registrado com sucesso! Itens: " + carrinho.size());
                return true;

            } catch (SQLException e) {
                // Se falhar o pedido ou os itens, desfaz tudo o que foi feito nesta tentativa
                conn.rollback();
                System.err.println("[BD] Erro na transação. Operação cancelada: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("[BD] Erro de conexão ao tentar criar pedido: " + e.getMessage());
            return false;
        }
    }
    // ─────────────────────────────────────────────────────────
    //  PEDIDOS — Estados unificados (coluna única 'status'):
    //  1 = Recebido | 2 = Em Produção | 3 = Pronto p/ Entrega
    //  4 = Em Rota  | 5 = Entregue    | (cancelado = linha removida)
    // ─────────────────────────────────────────────────────────
    private static final String SQL_BASE_PEDIDOS =
            "SELECT p.id AS id_pedido, p.status AS status_pedido, p.horario_entrega, " +
            "p.id_restaurante, r.nome AS nome_restaurante, r.localizacao AS loc_restaurante, " +
            "p.id_cliente, uc.nome AS nome_cliente, uc.email AS email_cliente, " +
            "p.id_entregador, ue.nome AS nome_entregador, ue.email AS email_entregador, " +
            "c.id AS id_prato, c.nome_prato AS nome_prato " +
            "FROM pedidos p " +
            "JOIN itens_pedido i ON p.id = i.id_pedido " +
            "JOIN cardapio c ON i.id_prato = c.id " +
            "LEFT JOIN restaurante r ON p.id_restaurante = r.id " +
            "LEFT JOIN usuarios uc ON p.id_cliente = uc.id " +
            "LEFT JOIN usuarios ue ON p.id_entregador = ue.id ";

    private static ArrayList<Pedido> montarPedidos(PreparedStatement pstmt) throws SQLException {
        ArrayList<Pedido> listaPedidos = new ArrayList<>();

        try (ResultSet rs = pstmt.executeQuery()) {
            Pedido pedidoAtual = null;
            int idPedidoAnterior = -1;

            while (rs.next()) {
                int idPedido = rs.getInt("id_pedido");

                if (idPedido != idPedidoAnterior) {
                    Cliente cliente = new Cliente(
                            rs.getString("email_cliente"),
                            rs.getString("nome_cliente"),
                            ""
                    );

                    Restaurante restaurante = new Restaurante(
                            rs.getInt("id_restaurante"),
                            rs.getString("nome_restaurante"),
                            rs.getString("loc_restaurante"),
                            0
                    );

                    Entregador entregador = null;
                    rs.getInt("id_entregador");
                    if (!rs.wasNull()) {
                        entregador = new Entregador(
                                rs.getString("email_entregador"),
                                rs.getString("nome_entregador"),
                                ""
                        );
                    }

                    ArrayList<Produto> produtosDoPedido = new ArrayList<>();

                    pedidoAtual = new Pedido(
                            produtosDoPedido,
                            rs.getString("horario_entrega"),
                            null,
                            restaurante,
                            cliente,
                            entregador
                    );

                    pedidoAtual.setId(idPedido);
                    pedidoAtual.setEstado(rs.getInt("status_pedido"));
                    listaPedidos.add(pedidoAtual);
                    idPedidoAnterior = idPedido;
                }

                Produto produto = new Produto();
                produto.setCodigo(rs.getInt("id_prato"));
                produto.setNome(rs.getString("nome_prato"));

                if (pedidoAtual != null) {
                    pedidoAtual.getComidas().add(produto);
                }
            }
        }

        return listaPedidos;
    }

    /** Retorna o ID do usuário atualmente logado no sistema, ou -1 se não encontrado. */
    public static int obterIdUsuarioLogado() {
        if (Telabase.getLogin() == null) return -1;
        String idStr = GetIdUsuario(Telabase.getLogin().GetUser(), Telabase.getLogin().GetEmail(), Telabase.getLogin().GetTipo());
        if (idStr == null) return -1;
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Pedidos de um cliente específico (todos os estados, mais recentes primeiro). */
    public static ArrayList<Pedido> obterPedidosPorCliente(int idCliente) {
        String sql = SQL_BASE_PEDIDOS + "WHERE p.id_cliente = ? ORDER BY p.id DESC;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            return montarPedidos(pstmt);
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao buscar pedidos do cliente: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /** Pedidos ativos (ainda não entregues) de um restaurante. */
    public static ArrayList<Pedido> obterPedidosPorRestaurante(int idRestaurante) {
        String sql = SQL_BASE_PEDIDOS + "WHERE p.id_restaurante = ? AND p.status < 4 ORDER BY p.id DESC;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idRestaurante);
            return montarPedidos(pstmt);
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao buscar pedidos do restaurante: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /** Pedidos prontos aguardando algum entregador aceitar (ainda sem entregador). */
    public static ArrayList<Pedido> obterPedidosDisponiveisEntrega() {
        String sql = SQL_BASE_PEDIDOS + "WHERE p.status IN (2, 3) AND p.id_entregador IS NULL ORDER BY p.status DESC, p.id DESC;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            return montarPedidos(pstmt);
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao buscar pedidos disponíveis para entrega: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /** Pedido que o entregador logado está entregando no momento (status 4), ou null. */
    public static Pedido obterPedidoAtivoEntregador(int idEntregador) {
        String sql = SQL_BASE_PEDIDOS + "WHERE p.status = 4 AND p.id_entregador = ? ORDER BY p.id DESC LIMIT 1;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEntregador);
            ArrayList<Pedido> lista = montarPedidos(pstmt);
            return lista.isEmpty() ? null : lista.get(0);
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao buscar pedido ativo do entregador: " + e.getMessage());
            return null;
        }
    }

    /** Atualiza o status de um pedido específico pelo seu ID. */
    public static boolean atualizarEstadoPedido(int idPedido, int novoEstado) {
        String sql = "UPDATE pedidos SET status = ? WHERE id = ?;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, novoEstado);
            pstmt.setInt(2, idPedido);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao atualizar estado do pedido: " + e.getMessage());
            return false;
        }
    }

    /** Um entregador aceita um pedido pronto (operação atômica: só aceita se ainda estiver livre). */
    public static boolean aceitarEntrega(int idPedido, int idEntregador) {
        String sql = "UPDATE pedidos SET status = 4, id_entregador = ? WHERE id = ? AND status = 3 AND id_entregador IS NULL;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEntregador);
            pstmt.setInt(2, idPedido);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao aceitar entrega: " + e.getMessage());
            return false;
        }
    }

    /** Entregador desiste da corrida: pedido volta a ficar disponível (status 3, sem entregador). */
    public static boolean liberarEntrega(int idPedido) {
        String sql = "UPDATE pedidos SET status = 3, id_entregador = NULL WHERE id = ?;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPedido);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao liberar entrega: " + e.getMessage());
            return false;
        }
    }

    /** Cancela (remove) um pedido específico pelo seu ID. */
    public static boolean cancelarPedidoNoBanco(int idPedido) {
        String sql = "DELETE FROM pedidos WHERE id = ?;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPedido);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao cancelar pedido: " + e.getMessage());
            return false;
        }
    }
}