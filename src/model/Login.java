package model;

import bd.BancoDados;

/**
 * Login — representa a SESSÃO do usuário logado no sistema.
 *
 * Regras:
 *  • Os campos são ESTÁTICOS: há apenas um usuário logado por vez.
 *  • A classe é usada como repositório de sessão em memória.
 *  • O BancoDados chama Login.setarSessao() após autenticar.
 *  • As telas leem Login.GetUser(), Login.GetTipo(), Login.GetEmail().
 */
public class Login {

    // ── Sessão estática (um único usuário logado) ─────────────
    private static String email;
    private static String nome;
    private static String tipo;
    private static String localizacao;
    // Construtor mantido para compatibilidade com código existente
    public Login(String email, String user, String tipo) {
        Login.email = email;
        Login.nome  = user;
        Login.tipo  = tipo;
        Login.localizacao = localizacao;
    }

    // ── Leitura da sessão ─────────────────────────────────────
    public static String GetUser() {
        if (nome == null || nome.isEmpty()) {
            return BancoDados.GetUser();
        }
        return nome;
    }

    public static String GetEmail() {
        return email;
    }

    public static String GetTipo() {
        return tipo;
    }

    // ── Escrita da sessão (chamado pelo BancoDados) ───────────
    public static void setarSessao(String nomeParam, String emailParam, String tipoParam ) {
        nome  = nomeParam;
        email = emailParam;
        tipo  = tipoParam;
    }

    public static void limparSessao() {
        nome  = null;
        email = null;
        tipo  = null;
    }

    // ── Delegação para BancoDados (mantida para compatibilidade) ──
    public static String getLocalizacao(){
        if(localizacao == null || localizacao.isEmpty())
            localizacao =BancoDados.getLocalizacao();
            return localizacao;
    }

    public static boolean realizarLogin(String emailParam, String senhaParam) {
        return BancoDados.realizarLogin(emailParam, senhaParam);
    }

    public static boolean cadastrarUsuario(String nomeParam, String emailParam,
                                           String senhaParam, String tipoParam , String localizacaoParam) {
        return BancoDados.cadastrarUsuario(nomeParam, emailParam, senhaParam, tipoParam , localizacaoParam);
    }

    public static void inicializarBanco() {
        BancoDados.inicializarBanco();
    }

    public static boolean verificarSeEstaLogado() {
        return BancoDados.verificarSeEstaLogado();
    }

    public static void apagarCookie() {
        BancoDados.apagarCookie();
    }
}