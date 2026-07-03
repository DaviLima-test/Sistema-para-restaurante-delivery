package model;

/**
 * Representa um item ou prato comercializável (Produto) pertencente ao cardápio de um estabelecimento.
 * <p>
 * Concentra os dados comerciais da mercadoria como identificador numérico, nome fantasia,
 * descrição complementar de ingredientes e valor monetário de venda, além do vínculo com o
 * restaurante de origem.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.1
 */
public class Produto {

    /** Código identificador exclusivo e primário do produto gerado pelo banco de dados. */
    private int codigo;

    /** Nome descritivo ou título do prato exibido para o consumidor final (Ex: X-Salada). */
    private String nome;

    /** Detalhes adicionais do prato, contendo informações sobre ingredientes ou composição. */
    private String descricao;

    /** Preço de venda praticado para o item no formato double monetário. */
    private double preco;

    /** Instância do estabelecimento comercial (Restaurante) proprietário deste produto no cardápio. */
    private Restaurante restaurante;

    /**
     * Construtor parametrizado para associação direta de um produto a um objeto Restaurante.
     *
     * @param codigo      O identificador numérico exclusivo do prato.
     * @param nome        O nome de exibição comercial do prato.
     * @param preco       O valor estipulado para a venda da unidade.
     * @param restaurante O objeto modelo {@link Restaurante} que fabrica este prato.
     */
    public Produto(int codigo, String nome ,double preco, Restaurante restaurante){
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
        this.restaurante = restaurante;
    }

    /**
     * Construtor abrangente utilizado para inicialização do item com detalhamento de ingredientes.
     *
     * @param codigo    O identificador numérico do prato.
     * @param nome      O nome do prato.
     * @param descricao Texto livre detalhando os componentes do alimento.
     * @param preco     O preço unitário de venda.
     */
    public Produto(int codigo, String nome, String descricao, double preco) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

    /**
     * Construtor simplificado para cadastro ágil em memória.
     * Define uma string vazia como descrição padrão.
     *
     * @param nome  O nome identificador do prato.
     * @param preco O preço unitário do item.
     */
    public Produto(String nome,double preco) {
        this.nome = nome;
        this.preco = preco;
        this.descricao = "";
    }

    /**
     * Construtor parametrizado padrão para manipulação rápida via persistência JDBC.
     * Define uma string vazia como descrição padrão.
     *
     * @param codigo O identificador numérico cadastrado no banco.
     * @param nome   O nome do prato.
     * @param preco  O preço unitário associado.
     */
    public Produto(int codigo, String nome, double preco) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = "";
        this.preco = preco;
    }

    /**
     * Construtor padrão sem argumentos. Utilizado para instanciação dinâmica ou
     * mapeamento tardio de propriedades via reflexão.
     */
    public Produto() {
    }

    /**
     * Construtor de compatibilidade utilizado para acoplamento textual de dados legados do banco.
     *
     * @param codigo      O identificador exclusivo do produto.
     * @param nomePrato   O nome descritivo da comida.
     * @param preco       O valor de precificação.
     * @param restaurante Nome literal ou informação textual referente ao estabelecimento (armazenamento omitido).
     */
    public Produto(int codigo, String nomePrato, double preco, String restaurante) {
        this.codigo = codigo;
        this.nome = nomePrato;
        this.preco = preco;
    }

    /**
     * Recupera o código identificador numérico do produto.
     * @return O ID numérico do produto (int).
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Atribui ou altera o identificador primário do produto.
     * @param codigo O novo código de identificação.
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * Recupera o nome de exibição comercial do prato.
     * @return String contendo o nome do item.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Altera o nome de exibição comercial do prato.
     * @param nome O novo nome a ser aplicado ao item.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Recupera a descrição literal contendo os ingredientes ou detalhes do produto.
     * @return String contendo a descrição técnica.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Define ou atualiza os detalhes textuais relativos à composição do prato.
     * @param descricao Texto detalhado de ingredientes.
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Recupera o preço de comercialização unitário do prato.
     * @return O preço de venda (double).
     */
    public double getPreco() {
        return preco;
    }

    /**
     * Atualiza o valor monetário cobrado pela aquisição unitária do item.
     * @param preco O novo valor de comercialização do produto.
     */
    public void setPreco(double preco) {
        this.preco = preco;
    }

    /**
     * Obtém o objeto completo do estabelecimento comercial vinculado a este prato.
     * @return A instância de {@link Restaurante} associada.
     */
    public Restaurante getRestaurante(){
        return this.restaurante;
    }

    /**
     * Retorna a representação textual direta da classe, simplificando a exibição
     * em elementos de listagem visual da interface Swing (ex: JList ou JComboBox).
     * @return O próprio nome do produto.
     */
    @Override
    public String toString() {
        return nome;
    }
}