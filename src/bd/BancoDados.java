package bd;

import model.*;
import view.Telabase;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * Camada de persistência (Data Access Object - DAO) e controle de infraestrutura de dados.
 * <p>
 * Centraliza toda a comunicação por meio do driver JDBC do MySQL para o ecossistema do
 * sistema de delivery. Gerencia o ciclo de vida das tabelas relacionais, controle transacional
 * de pedidos complexos, manipulação de estados do usuário (autenticação, cookies locais e banimentos)
 * e métricas consolidadas para relatórios gerenciais da plataforma.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public class BancoDados {

    /** Armazenamento volátil da senha utilizada no contexto da sessão corrente. */
    protected static String Senha;

    /** String de conexão para o banco de dados MySQL local. */
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_delivery";

    /** Credencial padrão de usuário do servidor de banco de dados. */
    private static final String USUARIO = "root";

    /** Credencial padrão de senha do servidor de banco de dados. */
    private static final String SENHA = "1234";

    /** Identificador de referência global da instância atual. */
    private static int id;

    /**
     * Remove de forma definitiva e em cascata o banco de dados principal do sistema.
     */
    private static void DeletarBancodados() {
        String sqlCriarBanco = "DROP DATABASE IF EXISTS sistema_delivery";
        String urlServidor = "jdbc:mysql://localhost:3306/";

        try (Connection connServidor = DriverManager.getConnection(urlServidor, USUARIO, SENHA);
             Statement stmt = connServidor.createStatement()) {

            stmt.executeUpdate(sqlCriarBanco);
            System.out.println("Banco de dados 'sistema_delivery' verificado/criado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao criar o banco de dados: " + e.getMessage());
        }
    }

    /**
     * Recupera o nome de exibição do usuário ativo com base nos dados de contexto ou credenciais mapeadas.
     * * @return Nome do usuário caso autenticado com sucesso, ou null se houver inconsistência de credenciais.
     */
    public static String GetUser() {
        if (Telabase.getLogin().GetUser() == null || Telabase.getLogin().GetUser().isEmpty()) {
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

    /**
     * Recupera a localização física registrada para o usuário atual, validando primeiramente
     * a tabela temporária de sessão (cookie) e subsequentemente os dados permanentes cadastrais.
     * * @return Endereço mapeado do usuário ou null se não houver registros localizados.
     */
    public static String getLocalizacao() {
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
        return null;
    }

    /**
     * Mapeamento interno de retorno de estado para redefinições de segurança de credenciais.
     * * @return Mensagem textual indicativa do sucesso da redefinição de segurança.
     */
    private String MudarSenha() {
        return "Senha alterada com sucesso.";
    }

    /**
     * Abre uma nova sessão ativa de comunicação com o banco de dados por meio dos parâmetros de conexão configurados.
     * * @return Uma instância de {@link Connection} pronta para processamento.
     * @throws SQLException Se houver falha de autenticação ou rede com o servidor de dados.
     */
    private static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    /**
     * Consulta o valor total de um determinado pedido diretamente no banco de dados.
     * Faz a junção dos itens com o cardápio para somar os valores reais.
     * * @param idPedido O identificador do pedido a ser consultado.
     * @return O valor total do pedido (double), ou 0.0 em caso de falha ou não encontrado.
     */
    public static double obterValorPedidoNoBanco(int idPedido) {
        String sql = "SELECT SUM(c.preco * ip.quantidade) AS total_calculado " +
                "FROM itens_pedido ip " +
                "INNER JOIN cardapio c ON ip.id_prato = c.id " +
                "WHERE ip.id_pedido = ?;";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sistema_delivery", "root", "1234");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPedido);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_calculado");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar valor do pedido no banco: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Inicializa o banco de dados gerando todo o esquema físico (DDL) caso ele não exista.
     * Cria as tabelas estruturais de usuários, cartões, cookies de sessão, restaurantes, cardápios,
     * pedidos e itens associados, injetando cargas de registros iniciais e contas administrativas padrão.
     */
    public static void inicializarBanco() {
        String sqlCriarBanco = "CREATE DATABASE IF NOT EXISTS sistema_delivery";

        String sqlTabelaUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) UNIQUE NOT NULL, " +
                "senha VARCHAR(255) NOT NULL," +
                "tipo VARCHAR(255) NOT NULL," +
                "localizacao VARCHAR(255) NOT NULL,"+
                "banido TINYINT DEFAULT 0" +
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

            String sqlAdminMaster = "INSERT IGNORE INTO usuarios (nome, email, senha, tipo, localizacao, banido) " +
                    "VALUES ('Admin', 'admin', '123', 'admin_master', 'Sede AIFood', 0);";
            stmt.execute(sqlAdminMaster);

            String sqlInsertInicial = "INSERT IGNORE INTO cookie (id, logado, nome_usuario, email_usuario, tipo_usuario, localizacao_usuario) VALUES (1, 0, NULL, NULL, NULL, NULL);";
            stmt.execute(sqlInsertInicial);

            System.out.println("Tabelas verificadas/criadas com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar as tabelas: " + e.getMessage());
        }
    }

    /**
     * Efetua o registro permanente de uma nova entidade de usuário na base de dados,
     * validando regras de restrição de unicidade para o nome de exibição e e-mail corporativo.
     * * @param nome Atributo de exibição do usuário.
     * @param email Chave única de e-mail e login de acesso.
     * @param senha Senha de autenticação associada.
     * @param tipo Categoria do perfil de acesso (Ex: cliente, gerente, entregador, admin).
     * @param localizacao Localização ou ponto geográfico para entregas e operações.
     * @return True se a inclusão for executada com sucesso, ou false caso infrinja regras ou falhe no driver.
     */
    public static boolean cadastrarUsuario(String nome, String email, String senha, String tipo, String localizacao) {
        String sqlCheckNome = "SELECT COUNT(*) FROM usuarios WHERE nome = ?;";
        String sqlCheckEmail = "SELECT COUNT(*) FROM usuarios WHERE email = ?;";
        String sqlInsert = "INSERT INTO usuarios (nome, email, senha, tipo, localizacao) VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = obterConexao()) {

            try (PreparedStatement pstmtCheckNome = conn.prepareStatement(sqlCheckNome)) {
                pstmtCheckNome.setString(1, nome);
                try (ResultSet rs = pstmtCheckNome.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(null, "Este nome de usuário já está em uso!", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }

            try (PreparedStatement pstmtCheckEmail = conn.prepareStatement(sqlCheckEmail)) {
                pstmtCheckEmail.setString(1, email);
                try (ResultSet rs = pstmtCheckEmail.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(null, "Este e-mail já está cadastrado!", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                pstmt.setString(1, nome);
                pstmt.setString(2, email);
                pstmt.setString(3, senha);
                pstmt.setString(4, tipo);
                pstmt.setString(5, localizacao);
                pstmt.executeUpdate();

                Telabase.setLogin(new Login(email, nome, tipo));
                JOptionPane.showMessageDialog(null, "Usuário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar usuário: " + e.getMessage(), "Erro no Banco", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Processa a validação cadastral de login, validando status de banimento e credenciais do usuário.
     * * @param email E-mail de identificação de login do usuário.
     * @param senha Senha informada para verificação relacional.
     * @return True se o usuário possuir credenciais válidas e não estiver banido, false caso contrário.
     */
    public static boolean realizarLogin(String email, String senha) {
        String sqlBuscar = "SELECT nome, tipo, localizacao, banido FROM usuarios WHERE email = ? AND senha = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuscar)) {

            pstmt.setString(1, email);
            pstmt.setString(2, senha);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getBoolean("banido")) {
                        System.out.println("Conta banida. Login recusado para: " + email);
                        return false;
                    }
                    String nomeUsuario = rs.getString("nome");
                    String tipoUsuario = rs.getString("tipo");
                    String localizacaoUsuario = rs.getString("localizacao");

                    Telabase.setLogin(new Login(email, nomeUsuario, tipoUsuario));
                    salvarCookieLogin(nomeUsuario, email, tipoUsuario, localizacaoUsuario);

                    System.out.println("Login realizado com sucesso para: " + nomeUsuario);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao realizar login no MySQL: " + e.getMessage());
        }
        JOptionPane.showMessageDialog(null,"Usuário ou senha incorretos.");
        return false;
    }

    /**
     * Registra o estado persistente de sessão local na tabela de cookies para manutenção do usuário logado.
     * * @param nome Nome do usuário autenticado.
     * @param email E-mail associado à conta autenticada.
     * @param tipo Perfil de nível de privilégio do usuário.
     * @param localizacao Localização cadastrada do usuário logado.
     */
    private static void salvarCookieLogin(String nome, String email, String tipo, String localizacao) {
        String sql = "UPDATE cookie SET logado = 1, nome_usuario = ?, email_usuario = ?, tipo_usuario = ?, localizacao_usuario = ? WHERE id = 1;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, tipo);
            pstmt.setString(4, localizacao);
            pstmt.executeUpdate();
            System.out.println("Esta salvo o cookie com a localização.");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cookie: " + e.getMessage());
        }
    }

    /**
     * Analisa o estado de retenção de cookies de login da sessão anterior para restabelecimento
     * automático de sessão no contexto da aplicação ao reiniciá-la.
     * * @return True se houver um cookie válido e ativo com status ativo, false caso contrário.
     */
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

    /**
     * Recupera um cliente do banco de dados utilizando apenas o seu identificador (ID).
     * Instancia o objeto utilizando o construtor parametrizado da classe Cliente.
     * * @param idCliente O identificador único do cliente na tabela usuarios.
     * @return Um objeto {@link model.Cliente} preenchido, ou null caso não seja localizado.
     */
    public static Cliente obterClientePorId(int idCliente) {
        String sqlUsuario = "SELECT nome, email FROM usuarios WHERE id = ? AND tipo = 'cliente';";
        String sqlCartao = "SELECT numero FROM cartoes WHERE id_cliente = ? ORDER BY principal DESC LIMIT 1;";

        String nome = null;
        String email = null;
        String cartao = "Não informado";

        try (Connection conn = obterConexao()) {

            try (PreparedStatement pstmtUsr = conn.prepareStatement(sqlUsuario)) {
                pstmtUsr.setInt(1, idCliente);
                try (ResultSet rsUsr = pstmtUsr.executeQuery()) {
                    if (rsUsr.next()) {
                        nome = rsUsr.getString("nome");
                        email = rsUsr.getString("email");
                    } else {
                        return null;
                    }
                }
            }

            try (PreparedStatement pstmtCard = conn.prepareStatement(sqlCartao)) {
                pstmtCard.setInt(1, idCliente);
                try (ResultSet rsCard = pstmtCard.executeQuery()) {
                    if (rsCard.next()) {
                        cartao = rsCard.getString("numero");
                    }
                }
            }

            return new model.Cliente(email, nome, cartao);

        } catch (SQLException e) {
            System.err.println("[BD] Erro ao obter cliente pelo ID #" + idCliente + ": " + e.getMessage());
        }

        return null;
    }

    /**
     * Limpa de forma irrestrita o registro de sessão ativo na tabela local de cookies,
     * invalidando dados de contexto e efetuando o logout lógico da aplicação.
     */
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

    /**
     * Efetua o registro de um novo estabelecimento do tipo restaurante associado a um
     * usuário com perfil de gerente verificado.
     * * @param email E-mail do gerente responsável legal pelo restaurante.
     * @param senha Senha do gerente para validação transacional e autenticação.
     * @param nome Nome de marca fantasia do novo restaurante.
     * @param localizacao Localização da cozinha ou loja física para despacho.
     * @param avaliacao Avaliação qualitativa de estrelas inicial.
     * @return True se a transação for concluída com sucesso, false caso ocorra falha de segurança ou consistência.
     */
    public static boolean cadastrarRestaurante(String email, String senha, String nome, String localizacao, String avaliacao) {
        String sql_find = "SELECT id FROM usuarios WHERE email = ? AND senha = ?;";
        String sqlCheckRestaurante = "SELECT COUNT(*) FROM restaurante WHERE nome = ? AND localizacao = ?;";
        String sql = "INSERT INTO restaurante (nome, localizacao, estrelas, id_gerente) VALUES (?, ?, ?, ?);";

        boolean passou = false;
        String id = "";

        try (Connection conn = obterConexao()) {

            try (PreparedStatement pstmt = conn.prepareStatement(sql_find)) {
                pstmt.setString(1, email);
                pstmt.setString(2, senha);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        id = rs.getString("id");
                        passou = true;
                    }
                }
            }

            if (!passou) {
                JOptionPane.showMessageDialog(null, "Usuário ou senha do gerente incorretos.", "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheckRestaurante)) {
                pstmtCheck.setString(1, nome);
                pstmtCheck.setString(2, localizacao);
                try (ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(null, "Já existe um restaurante com esse nome nesta localização!", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nome);
                pstmt.setString(2, localizacao);
                pstmt.setString(3, avaliacao);
                pstmt.setString(4, id);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Restaurante cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao processar operação: " + e.getMessage(), "Erro no Banco", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Atualiza os dados de registro (descrição e valor monetário) de um item específico no catálogo.
     * Sanatiza e padroniza a separação decimal flutuante padrão antes do envio ao banco.
     * * @param codigo Identificador de registro (PK) do prato na tabela cardapio.
     * @param novoNome Novo nome descritivo a ser aplicado ao prato.
     * @param novoPreco String contendo o preço a ser parseado e atualizado.
     * @return True se uma ou mais linhas forem atualizadas, false caso ocorra falha.
     */
    public static boolean atualizarCardapio(int codigo, String novoNome, String novoPreco) {
        String sql = "UPDATE cardapio SET nome_prato = ?, preco = ? WHERE id = ?;";

        String precoSanitizado = novoPreco != null ? novoPreco.replace(",", ".") : "0.00";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, novoNome);
            pstmt.setString(2, precoSanitizado);
            pstmt.setInt(3, codigo);

            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar o cardápio pelo código: " + e.getMessage());
            return false;
        }
    }

    /**
     * Vincula e insere um novo prato no cardápio associativo de um restaurante específico por localização.
     * Sanatiza a pontuação decimal de strings numéricas de entrada para o padrão internacional.
     * * @param nome_prato Nome identificador do prato/produto.
     * @param preco Valor de venda ao consumidor final.
     * @param nomeRestaurante Nome comercial do restaurante proprietário do cardápio.
     * @param localizacao Localização exata da unidade operacional do restaurante.
     * @return True se o restaurante for identificado e o item adicionado com sucesso, false caso contrário.
     */
    public static boolean cadastrarCardapio(String nome_prato, String preco, String nomeRestaurante, String localizacao) {
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

        String precoSanitizado = preco != null ? preco.replace(",", ".") : "0.00";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome_prato);
            pstmt.setString(2, precoSanitizado);
            pstmt.setString(3, id);
            pstmt.executeUpdate();
            System.out.println("Cardapio cadastrado com sucesso !");
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar cardapio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Executa a busca estruturada por um restaurante utilizando como filtro seu nome de marca fantasia.
     * * @param nomeBuscado String contendo o nome do estabelecimento.
     * @return Um modelo preenchido de {@link Restaurante} ou null se não houver correspondências.
     */
    public static Restaurante buscarRestaurantePorNome(String nomeBuscado) {
        String sql = "SELECT id, nome, localizacao, estrelas FROM restaurante WHERE nome = ? LIMIT 1;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nomeBuscado);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String localizacao = rs.getString("localizacao");
                    int estrelas = rs.getInt("estrelas");

                    return new Restaurante(id, nome, localizacao, estrelas);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar restaurante pelo nome '" + nomeBuscado + "': " + e.getMessage());
        }
        return null;
    }

    /**
     * Recupera do banco de dados o objeto estruturado de um restaurante a partir do seu identificador numérico sequencial.
     * * @param idBuscado Chave primária (ID) do restaurante.
     * @return Um modelo instanciado de {@link Restaurante} ou null caso não exista o ID.
     */
    public static Restaurante buscarRestaurantePorId(int idBuscado) {
        String sql = "SELECT id, nome, localizacao, estrelas FROM restaurante WHERE id = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idBuscado);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String localizacao = rs.getString("localizacao");
                    int estrelas = rs.getInt("estrelas");

                    return new Restaurante(id, nome, localizacao, estrelas);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar restaurante pelo ID " + idBuscado + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Mapeia todos os produtos em catálogo vinculados estritamente ao código chave de um restaurante parceiro.
     * * @param idRestaurante Código identificador único do estabelecimento.
     * @return Uma lista tipada de {@link Produto} contendo os itens ativos, ou uma lista vazia em caso de falha.
     */
    public static ArrayList<Produto> getCardapioPorRestaurante(int idRestaurante) {
        ArrayList<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome_prato, preco FROM cardapio WHERE id_restaurante = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idRestaurante);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int codigo = rs.getInt("id");
                    String nomePrato = rs.getString("nome_prato");
                    double preco = rs.getDouble("preco");

                    Produto produto = new Produto(codigo, nomePrato, preco, buscarRestaurantePorId(idRestaurante));
                    produtos.add(produto);
                }
            }
            return produtos;

        } catch (SQLException e) {
            System.err.println("Erro ao buscar cardápio do restaurante " + idRestaurante + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Remove fisicamente do banco de dados um prato do cardápio por meio de sua chave primária de identificação.
     * * @param codigo Código numérico identificador do prato (ID).
     * @return True se a remoção for bem-sucedida, false se nenhuma linha for afetada ou o comando falhar.
     */
    public static boolean removerPrato(int codigo) {
        String sql = "DELETE FROM cardapio WHERE id = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, codigo);
            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao remover o prato do cardápio: " + e.getMessage());
            return false;
        }
    }

    /**
     * Consulta o banco de dados e retorna uma lista consolidada de todos os pratos cadastrados,
     * executando junções estruturadas (JOIN) para obter os dados de seus respectivos restaurantes.
     * * @return Uma lista completa com os objetos {@link Produto} instanciados do banco.
     */
    public static ArrayList<Produto> getPratos() {
        ArrayList<Produto> produtos = new ArrayList<>();
        String sql = "SELECT c.id, nome_prato, preco, nome FROM cardapio " +
                "AS c JOIN restaurante ON c.id_restaurante = restaurante.id;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int codigo = rs.getInt("id");
                    String nomePrato = rs.getString("nome_prato");
                    double preco = rs.getDouble("preco");
                    String restaurante = rs.getString("nome");
                    Produto produto = new Produto(codigo, nomePrato, preco, buscarRestaurantePorNome(restaurante));
                    produtos.add(produto);
                }
            }
            return produtos;

        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os pratos " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Executa a busca estruturada dos dados informativos do restaurante gerenciado por um e-mail de gerente fornecido.
     * * @param emailGerente Endereço eletrônico chave do gerente administrador.
     * @return Array de Strings contendo as informações indexadas do restaurante [id, nome, localizacao, estrelas] ou null se não localizado.
     */
    public static String[] buscarRestaurantePorGerente(String emailGerente) {
        if (emailGerente == null || emailGerente.isEmpty()) {
            System.err.println("[BD] buscarRestaurantePorGerente: email nulo ou vazio.");
            return null;
        }

        String sql = "SELECT r.id, r.nome, r.localizacao, r.estrelas FROM restaurante r " +
                "INNER JOIN usuarios u ON r.id_gerente = u.id WHERE u.email = ? LIMIT 1";

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
        return null;
    }

    /**
     * Varre e coleta a totalidade de estabelecimentos comerciais (restaurantes) operacionais registrados na base de dados.
     * * @return Uma lista do tipo {@link ArrayList} contendo todos os objetos de mapeamento de restaurantes.
     */
    public static ArrayList<Restaurante> getRestaurantes() {
        String sql = "SELECT r.id, r.nome, r.localizacao, r.estrelas FROM restaurante r ";
        ArrayList<Restaurante> restaurants = new ArrayList<>();
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String localizacao = rs.getString("localizacao");
                    int estrelas = rs.getInt("estrelas");

                    Restaurante restaurante = new Restaurante(id, nome, localizacao, estrelas);
                    restaurants.add(restaurante);
                }
            }
            return restaurants;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Salva o registro de dados financeiros e de faturamento de um novo cartão de crédito associado a um cliente.
     * * @param cartao Entidade modelo contendo dados validados do cartão.
     * @return True se a operação for realizada e refletida na base de dados, false caso ocorra falha.
     */
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

    /**
     * Resolve de forma reversa e recupera a chave primária (ID) numérica do usuário
     * cruzando dados de login compostos de escopo de sessão.
     * * @param nome Nome textual de cadastro.
     * @param email E-mail único de indexação.
     * @param tipo Perfil ou categoria funcional da conta.
     * @return ID numérico formatado como String ou null caso não haja correspondência exata.
     */
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

    /**
     * Agrupa e retorna toda a coleção de cartões vinculados à carteira do usuário autenticado no sistema.
     * * @return Lista com os cartões localizados na tabela relacional ou null se houver falhas.
     */
    public static ArrayList<Cartao> GetCartoes() {
        String id = GetIdUsuario(Telabase.getLogin().GetUser(), Telabase.getLogin().GetEmail(), Telabase.getLogin().GetTipo());
        ArrayList<Cartao> cartoes = new ArrayList<>();
        String sql = "SELECT numero, titular, validade, cvv, bandeira, principal FROM cartoes WHERE id_cliente = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
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

    /**
     * Filtra e localiza o cartão financeiro marcado com flag de preferência primária (principal) para o usuário.
     * * @return Cartao prioritário configurado para cobrança automatizada ou null se inexistente.
     */
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

    /**
     * Consolida e cria um pedido dentro de uma transação isolada com segurança ACID.
     * Mapeia e agrupa a quantidade de itens duplicados vindos do carrinho para inseri-los em lote (Batch).
     * Realiza Rollback automático em caso de erros e inconsistências durante a persistência.
     * * @param carrinho Coleção contendo os produtos selecionados e adicionados pelo cliente.
     * @param idRestaurante Código de registro do restaurante destino do pedido.
     * @return True se a transação for fechada (Committed) com total sucesso, false caso ocorra algum erro operacional.
     */
    public static boolean criarPedido(ArrayList<Produto> carrinho, int idRestaurante) {
        if (carrinho == null || carrinho.isEmpty()) {
            System.err.println("[BD] Erro: Tentativa de criar pedido com carrinho vazio.");
            return false;
        }

        String idClienteStr = GetIdUsuario(Telabase.getLogin().GetUser(), Telabase.getLogin().GetEmail(), Telabase.getLogin().GetTipo());
        if (idClienteStr == null) {
            System.err.println("[BD] Erro: Não foi possível identificar o usuário logado.");
            return false;
        }
        int idCliente = Integer.parseInt(idClienteStr);

        String sqlPedido = "INSERT INTO pedidos (id_restaurante, id_cliente, status) VALUES (?, ?, 1);";
        String sqlItens = "INSERT INTO itens_pedido (id_pedido, id_prato, quantidade) VALUES (?, ?, ?);";

        try (Connection conn = obterConexao()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPedido.setInt(1, idRestaurante);
                pstmtPedido.setInt(2, idCliente);
                pstmtPedido.executeUpdate();

                int idPedidoGerado = -1;
                try (ResultSet generatedKeys = pstmtPedido.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idPedidoGerado = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Falha ao obter o ID do pedido gerado.");
                    }
                }

                java.util.HashMap<Integer, Integer> quantidadesPorPrato = new java.util.HashMap<>();
                for (Produto produto : carrinho) {
                    quantidadesPorPrato.merge(produto.getCodigo(), 1, Integer::sum);
                }

                try (PreparedStatement pstmtItens = conn.prepareStatement(sqlItens)) {
                    for (java.util.Map.Entry<Integer, Integer> item : quantidadesPorPrato.entrySet()) {
                        pstmtItens.setInt(1, idPedidoGerado);
                        pstmtItens.setInt(2, item.getKey());
                        pstmtItens.setInt(3, item.getValue());
                        pstmtItens.addBatch();
                    }
                    pstmtItens.executeBatch();
                }

                conn.commit();
                System.out.println("[BD] Sucesso! Pedido #" + idPedidoGerado + " criado com " + quantidadesPorPrato.size() + " itens únicos.");
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("[BD] Erro na transação. Operação cancelada: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("[BD] Erro de conexão ao tentar criar pedido: " + e.getMessage());
            return false;
        }
    }

    /** Clausula base compartilhada contendo estruturas de projecoes e joins para mapeamento de dados relacionais complexos de pedidos. */
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

    /**
     * Extrai, converte e monta de forma iterativa objetos de modelo {@link Pedido} agregando os sub-itens
     * mapeados das linhas de resultado de uma consulta do banco.
     * * @param pstmt PreparedStatement configurado pronto para ser executado.
     * @return Uma lista estruturada de Pedidos agregados com seus respectivos itens.
     * @throws SQLException Caso ocorram erros no cursor de leitura do ResultSet.
     */
    private static ArrayList<Pedido> montarPedidos(PreparedStatement pstmt) throws SQLException {
        ArrayList<Pedido> listaPedidos = new ArrayList<>();

        try (ResultSet rs = pstmt.executeQuery()) {
            Pedido pedidoAtual = null;
            int idPedidoAnterior = -1;

            while (rs.next()) {
                int idPedido = rs.getInt("id_pedido");

                if (idPedido != idPedidoAnterior) {
                    Cliente cliente = new Cliente(rs.getString("email_cliente"), rs.getString("nome_cliente"), "");
                    Restaurante restaurante = new Restaurante(rs.getInt("id_restaurante"), rs.getString("nome_restaurante"), rs.getString("loc_restaurante"), 0);

                    Entregador entregador = null;
                    rs.getInt("id_entregador");
                    if (!rs.wasNull()) {
                        entregador = new Entregador(rs.getString("email_entregador"), rs.getString("nome_entregador"), "");
                    }

                    ArrayList<Produto> produtosDoPedido = new ArrayList<>();
                    pedidoAtual = new Pedido(produtosDoPedido, rs.getString("horario_entrega"), null, restaurante, cliente, entregador);
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

    /**
     * Devolve de forma imediata o ID inteiro do usuário ativo na sessão atual da aplicação.
     * * @return O ID do usuário ativo ou -1 se não houver sessões válidas mapeadas.
     */
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

    /**
     * Localiza todos os pedidos realizados por um cliente ordenados de forma decrescente a partir do histórico de compras.
     * * @param idCliente Identificador único (ID) do cliente.
     * @return Lista contendo os pedidos vinculados localizados.
     */
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

    /**
     * Mapeia os dados resumidos de pedidos atribuídos para um restaurante específico.
     * * @param idRestaurante Identificador (ID) do restaurante proprietário.
     * @return Lista contendo os pedidos vinculados mapeados.
     */
    public static ArrayList<model.Pedido> obterPedidosPorRestaurante(int idRestaurante) {
        java.util.LinkedHashMap<Integer, model.Pedido> map = new java.util.LinkedHashMap<>();

        String sql = "SELECT p.id, p.id_restaurante, p.id_cliente, p.id_entregador, p.status, p.horario_entrega, " +
                "c.id AS prod_id, c.nome_prato AS prod_nome, c.preco AS prod_preco, ip.quantidade " +
                "FROM pedidos p " +
                "LEFT JOIN itens_pedido ip ON p.id = ip.id_pedido " +
                "LEFT JOIN cardapio c ON ip.id_prato = c.id " +
                "WHERE p.id_restaurante = ?;";

        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idRestaurante);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idPedido = rs.getInt("id");
                    model.Pedido pedido = map.get(idPedido);

                    if (pedido == null) {
                        pedido = new model.Pedido();
                        pedido.setId(idPedido);
                        pedido.setIdRestaurante(rs.getInt("id_restaurante"));
                        pedido.setIdCliente(rs.getInt("id_cliente"));
                        pedido.setIdEntregador(rs.getInt("id_entregador"));
                        pedido.setEstado(rs.getInt("status"));
                        pedido.setHora_Entregue(rs.getString("horario_entrega"));
                        pedido.setComidas(new ArrayList<>());
                        map.put(idPedido, pedido);
                    }

                    int prodId = rs.getInt("prod_id");
                    if (prodId > 0) {
                        model.Produto produto = new model.Produto();
                        produto.setCodigo(prodId);
                        produto.setNome(rs.getString("prod_nome"));
                        produto.setPreco(rs.getDouble("prod_preco"));

                        int quantidade = rs.getInt("quantidade");
                        for (int i = 0; i < quantidade; i++) {
                            pedido.getComidas().add(produto);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao obter pedidos do restaurante #" + idRestaurante + ": " + e.getMessage());
        }

        return new ArrayList<>(map.values());
    }

    /**
     * Busca na base de dados por todos os pedidos aguardando aceitação de frete e que
     * se encontrem com estados válidos para entrega e sem entregador associado.
     * * @return Lista contendo pedidos aptos e livres para entrega no sistema.
     */
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

    /**
     * Recupera o registro do pedido atualmente sob responsabilidade de transporte (Status em rota) por um entregador específico.
     * * @param idEntregador Código identificador (ID) do parceiro entregador.
     * @return Entidade {@link Pedido} ativa em andamento ou null se não houver fretes ativos pendentes.
     */
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

    /**
     * Agrupa o histórico retroativo completo de entregas finalizadas e concluídas com sucesso
     * por um profissional logado.
     * * @param idEntregador Código identificador único (ID) do entregador.
     * @return Histórico de entregas concluídas associadas.
     */
    public static ArrayList<Pedido> obterPedidosConcluidosPorEntregador(int idEntregador) {
        String sql = SQL_BASE_PEDIDOS + "WHERE p.status = 5 AND p.id_entregador = ? ORDER BY p.id DESC;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEntregador);
            return montarPedidos(pstmt);
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao buscar histórico de entregas concluídas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Altera o estado do fluxo de controle operacional de um pedido.
     * * @param idPedido ID numérico do pedido.
     * @param novoEstado Novo código indicativo de status desejado.
     * @return True se a alteração for realizada, false caso contrário.
     */
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

    /**
     * Vincula a entrega de um pedido com status disponível a um entregador e altera o estado do pedido.
     * * @param idPedido ID numérico do pedido.
     * @param idEntregador Código identificador (ID) do entregador.
     * @return True se o pedido for reservado com sucesso, false caso contrário.
     */
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

    /**
     * Desvincula o entregador atual de um pedido e redefine o estado dele para aguardando coleta.
     * * @param idPedido Código do pedido a ser liberado.
     * @return True se a redefinição for aplicada, false caso contrário.
     */
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

    /**
     * Remove fisicamente a tupla de registro de um pedido da tabela do banco de dados.
     * * @param idPedido ID numérico do pedido.
     * @return True se o pedido for deletado da base de dados, false caso contrário.
     */
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

    /**
     * Conta a quantidade total de usuários registrados com base em uma categoria de perfil de acesso.
     * * @param tipo Categoria ou nível de perfil de acesso (Ex: cliente, entregador).
     * @return Contagem agregada (int) de usuários pertencentes a essa categoria.
     */
    public static int contarUsuariosPorTipo(String tipo) {
        String sql = "SELECT COUNT(*) AS total FROM usuarios WHERE tipo = ?;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tipo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao contar usuários por tipo: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Calcula o total consolidado de contas administrativas ativas na plataforma.
     * * @return Soma total de administradores comuns e administradores master.
     */
    public static int contarTotalAdmins() {
        return contarUsuariosPorTipo("admin") + contarUsuariosPorTipo("admin_master");
    }

    /**
     * Realiza a contagem total de todos os estabelecimentos comerciais cadastrados na plataforma.
     * * @return Volume numérico total de lojas registradas na tabela restaurante.
     */
    public static int contarTotalRestaurantesCadastrados() {
        String sql = "SELECT COUNT(*) AS total FROM restaurante;";
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao contar restaurantes: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Retorna a quantidade total de pedidos realizados na plataforma até o momento.
     * * @return Volume numérico total de registros de pedidos na tabela.
     */
    public static int contarTotalPedidos() {
        String sql = "SELECT COUNT(*) AS total FROM pedidos;";
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao cadastrar restaurantes: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Calcula o faturamento financeiro total bruto transacionado por meio do processamento e
     * soma de todos os itens de pedidos vendidos no sistema.
     * * @return Valor flutuante consolidado do faturamento bruto da plataforma.
     */
    public static double calcularFaturamentoTotal() {
        String sql = "SELECT COALESCE(SUM(c.preco * ip.quantidade), 0) AS total " +
                "FROM itens_pedido ip JOIN cardapio c ON ip.id_prato = c.id;";
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao calcular faturamento: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Classe utilitária interna (Data Transfer Object - DTO) para representação estruturada de
     * dados cadastrais de usuários nos painéis administrativos.
     */
    public static class UsuarioAdmin {
        public final int id;
        public final String nome;
        public final String email;
        public final String localizacao;
        public final String tipo;
        public final boolean banido;

        public UsuarioAdmin(int id, String nome, String email, String localizacao, String tipo, boolean banido) {
            this.id = id;
            this.nome = nome;
            this.email = email;
            this.localizacao = localizacao;
            this.tipo = tipo;
            this.banido = banido;
        }
    }

    /**
     * Monta uma lista detalhada de usuários com base em uma categoria de perfil de acesso para listagens administrativas.
     * * @param tipo Categoria ou nível de perfil de acesso.
     * @return Lista contendo objetos {@link UsuarioAdmin} mapeados.
     */
    public static ArrayList<UsuarioAdmin> listarUsuariosPorTipo(String tipo) {
        ArrayList<UsuarioAdmin> lista = new ArrayList<>();
        String sql = "SELECT id, nome, email, localizacao, tipo, banido FROM usuarios WHERE tipo = ? ORDER BY id DESC;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tipo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new UsuarioAdmin(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("email"),
                            rs.getString("localizacao"),
                            rs.getString("tipo"),
                            rs.getBoolean("banido")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao listar usuários por tipo: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Altera o status lógico de banimento de um usuário, impedindo ou permitindo o login no sistema.
     * * @param idUsuario Identificador único (ID) do usuário.
     * @param banir Flag booleana definindo o estado de banimento (true para bloquear, false para liberar).
     * @return True se a alteração for realizada com sucesso, false caso contrário.
     */
    public static boolean definirBanimento(int idUsuario, boolean banir) {
        String sql = "UPDATE usuarios SET banido = ? WHERE id = ?;";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, banir);
            pstmt.setInt(2, idUsuario);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao (des)banir usuário: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cria e cadastra uma nova conta com privilégios de administrador comum no sistema.
     * * @param nome Nome do novo administrador.
     * @param email E-mail único corporativo.
     * @param senha Senha de acesso.
     * @return True se a operação for concluída com sucesso, false caso contrário.
     */
    public static boolean criarNovoAdmin(String nome, String email, String senha) {
        return cadastrarUsuario(nome, email, senha, "admin", "-");
    }

    /**
     * Altera a categoria de privilégios de um usuário comum na base de dados para o nível de administrador.
     * * @param idUsuario Identificador único (ID) do usuário.
     * @return True se a promoção for aplicada com sucesso, false caso contrário.
     */
    public static boolean promoverParaAdmin(int idUsuario) {
        String sql = "UPDATE usuarios SET tipo = 'admin' WHERE id = ? AND tipo <> 'admin_master';";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao promover usuário a admin: " + e.getMessage());
            return false;
        }
    }

    /**
     * Altera e rebaixa o perfil de acesso de um usuário administrador para uma nova categoria padrão definida.
     * * @param idUsuario Identificador único (ID) do administrador.
     * @param novoTipo Nova categoria ou nível de perfil de acesso de destino (Ex: cliente).
     * @return True se o rebaixamento de perfil for aplicado com sucesso, false caso contrário.
     */
    public static boolean rebaixarAdmin(int idUsuario, String novoTipo) {
        String sql = "UPDATE usuarios SET tipo = ? WHERE id = ? AND tipo = 'admin';";
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, novoTipo);
            pstmt.setInt(2, idUsuario);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao rebaixar admin: " + e.getMessage());
            return false;
        }
    }
}