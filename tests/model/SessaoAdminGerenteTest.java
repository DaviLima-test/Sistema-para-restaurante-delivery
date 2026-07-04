package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [DEFEITO CONHECIDO] Casos de teste que documentam uma inconsistência encontrada
 * nos construtores de {@link Admin} e {@link Gerente}.
 * <p>
 * Ambas as classes têm o construtor {@code (String email, String senha, String user)},
 * mas repassam os parâmetros para a superclasse na ordem
 * {@code super(email, senha, user)}.
 * <p>
 * Só que o construtor de {@link Login} espera {@code (String email, String user, String tipo)}.
 * Ou seja, posicionalmente:
 * <ul>
 *     <li>{@code Login.nome} recebe o valor de <b>senha</b> (em vez do nome de usuário);</li>
 *     <li>{@code Login.tipo} recebe o valor de <b>user</b> (o nome de usuário, em vez do tipo/cargo).</li>
 * </ul>
 * Na prática, isso significa que, ao logar como Admin ou Gerente, {@code Login.GetUser()}
 * devolve a SENHA em texto puro no lugar do nome do usuário, e {@code Login.GetTipo()}
 * devolve o nome do usuário no lugar do tipo de conta. Isso é ao mesmo tempo um bug
 * funcional (nome/tipo trocados na tela) e um risco de segurança (exposição da senha
 * onde a tela espera exibir o nome do usuário).
 * <p>
 * Os testes abaixo comprovam o comportamento ATUAL (com o defeito). Recomenda-se
 * corrigir o construtor para {@code super(email, user, "admin")} / {@code super(email, user, "gerente")}
 * e, quando corrigido, os testes marcados como "comportamento esperado após correção"
 * (comentados) devem passar a ser usados no lugar destes.
 */
class SessaoAdminGerenteTest {

    @BeforeEach
    void limparSessaoAntes() {
        Login.limparSessao();
    }

    @AfterEach
    void limparSessaoDepois() {
        Login.limparSessao();
    }

    @Test
    @DisplayName("CT43 - [DEFEITO CONHECIDO] Construtor de Admin grava a senha como se fosse o nome de sessão")
    void construtorAdminGravaSenhaComoNomeDeSessao() {
        new Admin("admin@teste.com", "senhaSecreta123", "Administrador Master");

        assertEquals("senhaSecreta123", Login.GetUser(),
                "Comportamento atual (defeituoso): GetUser() retorna a senha, não o nome do usuário.");
        assertEquals("Administrador Master", Login.GetTipo(),
                "Comportamento atual (defeituoso): GetTipo() retorna o nome de usuário, não o tipo de conta.");

        // Comportamento esperado após a correção do bug (deve substituir os asserts acima quando corrigido):
        // assertEquals("Administrador Master", Login.GetUser());
        // assertEquals("admin", Login.GetTipo());
    }

    @Test
    @DisplayName("CT44 - [DEFEITO CONHECIDO] Construtor de Gerente grava a senha como se fosse o nome de sessão")
    void construtorGerenteGravaSenhaComoNomeDeSessao() {
        new Gerente("gerente@teste.com", "outraSenha456", "Gerente da Matriz");

        assertEquals("outraSenha456", Login.GetUser(),
                "Comportamento atual (defeituoso): GetUser() retorna a senha, não o nome do usuário.");
        assertEquals("Gerente da Matriz", Login.GetTipo(),
                "Comportamento atual (defeituoso): GetTipo() retorna o nome de usuário, não o tipo de conta.");

        // Comportamento esperado após a correção do bug (deve substituir os asserts acima quando corrigido):
        // assertEquals("Gerente da Matriz", Login.GetUser());
        // assertEquals("gerente", Login.GetTipo());
    }

    @Test
    @DisplayName("CT45 - O e-mail é o único dado repassado corretamente pelos construtores de Admin/Gerente")
    void emailEhRepassadoCorretamente() {
        new Admin("admin2@teste.com", "senha", "Nome Qualquer");
        assertEquals("admin2@teste.com", Login.GetEmail());
    }
}
