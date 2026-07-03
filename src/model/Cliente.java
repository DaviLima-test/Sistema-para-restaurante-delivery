package model;

/**
 * Representa a entidade Cliente consumidor dentro do sistema de delivery.
 * <p>
 * Concentra as informações cadastrais e financeiras do usuário comprador,
 * mapeando seu nome, e-mail de autenticação e dados do cartão principal.
 * Encapsula as regras de negócio relativas à manipulação de itens no carrinho de compras,
 * envio de pedidos, fluxos de pagamentos e avaliações de serviços.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.1
 */
public class Cliente {

    /** O número ou token representativo do cartão de crédito cadastrado pelo cliente. */
    private String Cartao;

    /** A pontuação ou classificação de reputação do cliente dentro da plataforma (padrão inicial de 5 estrelas). */
    private int Estrelas;

    /** O endereço eletrônico exclusivo que atua como identificador e login do cliente. */
    private String email;

    /** O nome completo ou alcunha de exibição do usuário consumidor. */
    private String nome;

    /**
     * Construtor completo e parametrizado para inicialização dos dados essenciais do cliente.
     * Define por padrão o nível de avaliação (reputação) do usuário com 5 estrelas.
     *
     * @param email  O e-mail de contato e autenticação do cliente.
     * @param user   O nome fantasia ou username escolhido pelo usuário.
     * @param cartao O número do cartão principal para faturamento de compras.
     */
    public Cliente(String email, String user, String cartao) {
        this.nome = user;
        this.email = email;
        this.Cartao = cartao;
        this.Estrelas = 5; // Padrão inicial
    }

    /**
     * Construtor padrão sem argumentos. Utilizado para preenchimento tardio de atributos
     * via reflexão ou mapeamentos do banco de dados (JDBC).
     */
    public Cliente() {
    }

    /**
     * Recupera o nome de exibição do cliente.
     * @return O nome do consumidor (String).
     */
    public String getNome() { return this.nome; }

    /**
     * Consolida os itens ativos no carrinho de compras e despacha a requisição de
     * um novo pedido para a cozinha do restaurante correspondente.
     */
    public void Pedir() {
        // Lógica simplificada
    }

    /**
     * Insere uma unidade de um determinado prato/insumo dentro do carrinho de compras em memória.
     * @param p O objeto {@link Produto} que será adicionado à lista de compras.
     */
    public void AdicionarNoCarrinho(Produto p) {
        // Lógica simplificada
    }

    /**
     * Remove ou desconta uma unidade de um prato específico do carrinho de compras atual.
     * @param p O objeto {@link Produto} a ser retirado da cesta de compras.
     */
    public void RemoverNoCarrinho(Produto p) {
        // Lógica simplificada
    }

    /**
     * Registra uma pontuação e uma nota avaliativa sobre a qualidade dos pratos
     * e o tempo de entrega de uma transação finalizada.
     * @param pedido O objeto {@link Pedido} que será alvo da avaliação técnica do cliente.
     */
    public void Avaliacao(Pedido pedido) {
        // Lógica simplificada
    }

    /**
     * Transmite e processa a cobrança financeira baseada no valor bruto do pedido
     * utilizando a credencial ativa do cartão configurado.
     * @param valor O montante monetário em formato double a ser debitado na transação.
     */
    public void Pagamento(Double valor) {
        // Lógica simplificada
    }

    /**
     * Abre uma tela ou consulta no sistema para monitorar em tempo real os estados
     * logísticos e operacionais dos pedidos ativos deste usuário.
     */
    public void AcompanharPedidos() {
        // Lógica simplificada
    }

    /**
     * Executa a interrupção prematura e o cancelamento de um pedido em andamento,
     * respeitando as regras e prazos de produção do restaurante.
     * @param pedido O objeto {@link Pedido} que se deseja estornar ou cancelar.
     */
    public void CancelarPedido(Pedido pedido) {
        // Lógica simplificada
    }

    /**
     * Recupera o endereço de e-mail exclusivo do cliente.
     * @return O e-mail associado à conta (String).
     */
    public String getEmail() { return email; }
}