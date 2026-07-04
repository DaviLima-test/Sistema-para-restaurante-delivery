package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Casos de teste unitários para a classe {@link Login}.
 * <p>
 * ATENÇÃO: {@code Login} guarda o estado da sessão em campos {@code static},
 * compartilhados por toda a JVM. Por isso, cada teste limpa a sessão antes
 * e depois de rodar, evitando que um teste "vaze" estado para o próximo
 * (importante inclusive ao rodar em paralelo com outras classes de teste).
 * <p>
 * Métodos que dependem de {@code bd.BancoDados} (realizarLogin, cadastrarUsuario,
 * inicializarBanco, verificarSeEstaLogado, apagarCookie) exigem uma conexão MySQL
 * real e são tratados como testes de integração — não estão cobertos aqui.
 */
class LoginTest {

    @BeforeEach
    void limparSessaoAntes() {
        Login.limparSessao();
    }

    @AfterEach
    void limparSessaoDepois() {
        Login.limparSessao();
    }

    @Test
    @DisplayName("CT39 - setarSessao deve tornar os dados do usuário disponíveis globalmente")
    void setarSessaoDeveDisponibilizarDadosGlobalmente() {
        Login.setarSessao("Fulano", "fulano@teste.com", "cliente");

        assertEquals("Fulano", Login.GetUser());
        assertEquals("fulano@teste.com", Login.GetEmail());
        assertEquals("cliente", Login.GetTipo());
    }

    @Test
    @DisplayName("CT40 - limparSessao deve zerar nome, email e tipo do usuário logado")
    void limparSessaoDeveZerarDadosDoUsuario() {
        Login.setarSessao("Fulano", "fulano@teste.com", "cliente");
        Login.limparSessao();

        assertNull(Login.GetEmail());
        assertNull(Login.GetTipo());
        // GetUser() com nome nulo tenta um fallback via BancoDados/Telabase - ver CT41.
    }

    @Test
    @DisplayName("CT41 - [DEFEITO CONHECIDO] GetUser() lança NullPointerException quando não há sessão nem tela ativa")
    void getUserSemSessaoLancaNullPointerException() {
        Login.limparSessao();

        // BancoDados.GetUser() acessa Telabase.getLogin().GetUser() sem checar se
        // Telabase.getLogin() é nulo. Em um cenário de teste (ou de uso da API sem
        // nunca ter aberto a tela principal), isso resulta em NullPointerException,
        // o que deveria ser tratado com uma verificação de nulidade adicional.
        assertThrows(NullPointerException.class, Login::GetUser,
                "Este teste documenta um defeito: GetUser() deveria devolver null/mensagem amigável, " +
                "e não estourar NullPointerException, quando chamado sem sessão e sem Telabase ativa.");
    }

    @Test
    @DisplayName("CT42 - setarSessao deve sobrescrever uma sessão anterior corretamente")
    void setarSessaoDeveSobrescreverSessaoAnterior() {
        Login.setarSessao("Primeiro", "primeiro@teste.com", "cliente");
        Login.setarSessao("Segundo", "segundo@teste.com", "restaurante");

        assertEquals("Segundo", Login.GetUser());
        assertEquals("segundo@teste.com", Login.GetEmail());
        assertEquals("restaurante", Login.GetTipo());
    }
}
