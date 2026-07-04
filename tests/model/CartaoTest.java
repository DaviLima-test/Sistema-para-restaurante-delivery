package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Casos de teste unitários para a classe {@link Cartao}.
 * <p>
 * Foco na sanitização de dados feita em {@link Cartao#adequarDadosDoCartao()}
 * (chamada automaticamente pelo construtor) e no mascaramento de exibição
 * em {@link Cartao#getQuatroUltimosDigitos()}. Métodos que dependem do banco
 * de dados (getCartoes, GetPrincipal, SalvarCartao) não são cobertos aqui,
 * por exigirem uma conexão MySQL real (ver testes de integração).
 */
class CartaoTest {

    @Test
    @DisplayName("CT09 - Cartão válido deve manter os dados após sanitização")
    void cartaoValidoMantemDadosAposSanitizacao() {
        Cartao cartao = new Cartao("1234567890123456", "JOAO DA SILVA", "12/2030", "123", "Visa", true);

        assertEquals("1234567890123456", cartao.getNumero());
        assertEquals("JOAO DA SILVA", cartao.getTitular());
        assertEquals("12/2030", cartao.getValidade());
        assertEquals("123", cartao.getCvv());
        assertEquals("Visa", cartao.getBandeira());
        assertTrue(cartao.isPrincipal());
    }

    @Test
    @DisplayName("CT10 - Número com espaços deve ser sanitizado (espaços removidos)")
    void numeroComEspacosDeveSerSanitizado() {
        Cartao cartao = new Cartao("1234 5678 9012 3456", "Maria", "01/2029", "321", "Mastercard", false);
        assertEquals("1234567890123456", cartao.getNumero());
    }

    @Test
    @DisplayName("CT11 - Número acima de 16 dígitos deve ser truncado para 16 caracteres")
    void numeroAcimaDoLimiteDeveSerTruncado() {
        Cartao cartao = new Cartao("12345678901234567890", "Maria", "01/2029", "321", "Elo", false);
        assertEquals(16, cartao.getNumero().length());
        assertEquals("1234567890123456", cartao.getNumero());
    }

    @Test
    @DisplayName("CT12 - Bandeira acima de 20 caracteres deve ser truncada")
    void bandeiraAcimaDoLimiteDeveSerTruncada() {
        String bandeiraLonga = "BandeiraComNomeMuitoMuitoGrande"; // 31 caracteres
        Cartao cartao = new Cartao("1111222233334444", "Titular", "05/2028", "999", bandeiraLonga, false);
        assertEquals(20, cartao.getBandeira().length());
    }

    @Test
    @DisplayName("CT13 - CVV e validade com espaços nas bordas devem ser aparados (trim)")
    void cvvEValidadeDevemSerAparados() {
        Cartao cartao = new Cartao("1111222233334444", "Titular", "  12/2031  ", "  456  ", "Visa", false);
        assertEquals("12/2031", cartao.getValidade());
        assertEquals("456", cartao.getCvv());
    }

    @Test
    @DisplayName("CT14 - Campos nulos não devem lançar exceção durante a sanitização")
    void camposNulosNaoDevemLancarExcecao() {
        assertDoesNotThrow(() -> {
            Cartao cartao = new Cartao(null, "Titular", null, null, null, false);
            assertNull(cartao.getNumero());
            assertNull(cartao.getValidade());
            assertNull(cartao.getCvv());
            assertNull(cartao.getBandeira());
        });
    }

    @Test
    @DisplayName("CT15 - getQuatroUltimosDigitos deve retornar os 4 últimos dígitos do número")
    void getQuatroUltimosDigitosRetornaUltimosDigitos() {
        Cartao cartao = new Cartao("1111222233334444", "Titular", "05/2028", "999", "Visa", false);
        assertEquals("4444", cartao.getQuatroUltimosDigitos());
    }

    @Test
    @DisplayName("CT16 - getQuatroUltimosDigitos deve retornar '0000' quando número é nulo")
    void getQuatroUltimosDigitosRetorna0000QuandoNumeroNulo() {
        Cartao cartao = new Cartao(null, "Titular", "05/2028", "999", "Visa", false);
        assertEquals("0000", cartao.getQuatroUltimosDigitos());
    }

    @Test
    @DisplayName("CT17 - getQuatroUltimosDigitos deve retornar '0000' quando número tem menos de 4 dígitos")
    void getQuatroUltimosDigitosRetorna0000QuandoNumeroCurto() {
        Cartao cartao = new Cartao("12", "Titular", "05/2028", "999", "Visa", false);
        assertEquals("0000", cartao.getQuatroUltimosDigitos());
    }

    @Test
    @DisplayName("CT18 - setPrincipal deve alterar o estado de cartão principal")
    void setPrincipalDeveAlterarEstado() {
        Cartao cartao = new Cartao("1111222233334444", "Titular", "05/2028", "999", "Visa", false);
        assertFalse(cartao.isPrincipal());

        cartao.setPrincipal(true);
        assertTrue(cartao.isPrincipal());
    }
}
