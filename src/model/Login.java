package model;

import bd.BancoDados;

/**
 * Representa a controle de SESSÃO ativa do usuário autenticado no sistema.
 * <p>
 * Esta classe atua como um repositório centralizado em memória utilizando o padrão de
 * escopo estático, garantindo que exista apenas uma credencial ativa trafegando por vez
 * entre os ciclos de navegação das interfaces Swing.
 * </p>
 * <p>
 * <b>Fluxo de Regras:</b>
 * </p>
 * <ul>
 * <li>O componente {@link bd.BancoDados} injeta as credenciais via {@link #setarSessao} após a validação no banco.</li>
 * <li>As telas de visualização consomem de forma global via {@link #GetUser()}, {@link #GetTipo()} e {@link #GetEmail()}.</li>
 * </ul>
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public class Login {

    /** O endereço eletrônico (e-mail) associado à conta autenticada na sessão. */
    private static String email;

    /** O nome descritivo ou alcunha (username) do perfil atualmente conectado. */
    private static String nome;

    /** A categoria ou nível de permissão hierárquica do usuário (Ex: 'cliente', 'restaurante', 'entregador'). */
    private static String tipo;

    /** A coordenada literal ou endereço residencial vinculado ao usuário logado. */
    private static String localizacao;

    /**
     * Construtor parametrizado padrão mantido estritamente para fins de retrocompatibilidade
     * com rotinas legadas de instanciação em memória.
     *
     * @param email O endereço eletrônico do usuário.
     * @param user  O nome de exibição do usuário.
     * @param tipo  O nível de acesso atribuído ao perfil.
     */
    public Login(String email, String user, String tipo) {
        Login.email = email;
        Login.nome  = user;
        Login.tipo  = tipo;
        Login.localizacao = localizacao;
    }

    /**
     * Recupera de forma global o nome do usuário logado na sessão ativa.
     * <p>Caso o dado em memória esteja corrompido ou nulo, realiza um fallback buscando
     * o cookie/estado persistido diretamente no repositório de persistência.</p>
     * @return O nome do usuário autenticado (String).
     */
    public static String GetUser() {
        if (nome == null || nome.isEmpty()) {
            return BancoDados.GetUser();
        }
        return nome;
    }

    /**
     * Recupera o e-mail associado à conta presente na sessão.
     * @return O endereço de e-mail ativo (String).
     */
    public static String GetEmail() {
        return email;
    }

    /**
     * Recupera o tipo ou permissão de acesso do perfil autenticado.
     * @return String identificando o nível de acesso (Ex: 'cliente', 'admin').
     */
    public static String GetTipo() {
        return tipo;
    }

    /**
     * Aloca e injeta em memória os dados do usuário autenticado no sistema, definindo a sessão.
     * Método invocado estrategicamente pela classe de persistência após o sucesso no login.
     *
     * @param nomeParam  Nome cadastrado do usuário que logou.
     * @param emailParam E-mail correspondente à conta validada.
     * @param tipoParam  Nível hierárquico concedido ao registro.
     */
    public static void setarSessao(String nomeParam, String emailParam, String tipoParam ) {
        nome  = nomeParam;
        email = emailParam;
        tipo  = tipoParam;
    }

    /**
     * Efetua a limpeza completa das variáveis estáticas em memória,
     * desautenticando o perfil e invalidando a sessão corrente (Logout).
     */
    public static void limparSessao() {
        nome  = null;
        email = null;
        tipo  = null;
    }

    /**
     * Recupera o endereço de localização cadastrado para o perfil em sessão.
     * <p>Caso a propriedade esteja nula, delega a leitura síncrona dos arquivos ou
     * tabelas para a classe {@link bd.BancoDados}.</p>
     * @return O endereço completo de localização (String).
     */
    public static String getLocalizacao(){
        if(localizacao == null || localizacao.isEmpty())
            localizacao = BancoDados.getLocalizacao();
        return localizacao;
    }

    /**
     * Repassa a requisição de autenticação para o método de controle de persistência.
     *
     * @param emailParam O e-mail informado no formulário de login.
     * @param senhaParam A senha em texto puro informada na interface visual.
     * @return true caso as credenciais coincidam no banco de dados, false caso contrário.
     */
    public static boolean realizarLogin(String emailParam, String senhaParam) {
        return BancoDados.realizarLogin(emailParam, senhaParam);
    }

    /**
     * Repassa os parâmetros capturados na tela para registrar uma nova conta no banco de dados.
     *
     * @param nomeParam        O nome completo do indivíduo ou empresa.
     * @param emailParam       O e-mail único que servirá de login.
     * @param senhaParam       A senha associada de acesso para criptografia/gravação.
     * @param tipoParam        A string de categoria operacional do usuário.
     * @param localizacaoParam O endereço cadastral de entrega ou sede.
     * @return true se o registro foi criado com sucesso no banco, false se houver duplicidade ou falha.
     */
    public static boolean cadastrarUsuario(String nomeParam, String emailParam,
                                           String senhaParam, String tipoParam , String localizacaoParam) {
        return BancoDados.cadastrarUsuario(nomeParam, emailParam, senhaParam, tipoParam , localizacaoParam);
    }

    /**
     * Inicializa a estrutura de tabelas, relacionamentos e dados nativos de semente (seed)
     * delegando a execução para a camada de infraestrutura.
     */
    public static void inicializarBanco() {
        BancoDados.inicializarBanco();
    }

    /**
     * Verifica e valida se há algum rastro ou persistência de estado ativo (cookies/cache)
     * indicando uma sessão previamente válida.
     * @return true se houver estado logado ativo, false caso contrário.
     */
    public static boolean verificarSeEstaLogado() {
        return BancoDados.verificarSeEstaLogado();
    }

    /**
     * Executa a destruição física do arquivo temporário ou cookie de autenticação local automatizada,
     * impedindo o login automático em inicializações futuras.
     */
    public static void apagarCookie() {
        BancoDados.apagarCookie();
    }
}