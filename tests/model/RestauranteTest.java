package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Casos de teste unitários para a classe {@link Restaurante}.
 * <p>
 * Os métodos {@code carregarCardapio()}, {@code atualizarPrato()}, {@code removerPrato()}
 * e {@code GerarComanda()} não são cobertos aqui por dependerem de {@code bd.BancoDados}
 * (conexão MySQL) ou de escrita em disco. {@code adicionarPrato()} é parcialmente coberto,
 * pois adiciona o item em memória antes de tentar persistir no banco.
 */
class RestauranteTest {

    @Test
    @DisplayName("CT31 - Construtor completo deve preencher todos os atributos")
    void construtorCompletoDevePreencherAtributos() {
        Restaurante restaurante = new Restaurante(7, "Pizzaria Bella", "Av. Central, 100", 5);

        assertEquals(7, restaurante.getId());
        assertEquals("Pizzaria Bella", restaurante.getNome());
        assertEquals("Av. Central, 100", restaurante.getLocalizacao());
        assertEquals(5, restaurante.getEstrelas());
    }

    @Test
    @DisplayName("CT32 - Construtor vazio deve iniciar o cardápio como uma lista vazia (não nula)")
    void construtorVazioDeveIniciarCardapioVazio() {
        Restaurante restaurante = new Restaurante();

        assertNotNull(restaurante.getCardapio());
        assertTrue(restaurante.getCardapio().isEmpty());
    }

    @Test
    @DisplayName("CT33 - adicionarPrato deve inserir o produto na lista em memória mesmo sem conexão com o banco")
    void adicionarPratoDeveInserirNaListaEmMemoria() {
        Restaurante restaurante = new Restaurante(1, "Restaurante Teste", "Rua Teste, 1", 3);

        // Sem um banco MySQL disponível, espera-se retorno "false" (falha ao persistir),
        // mas o item deve permanecer na lista em memória (this.cardapio.add ocorre antes do try/catch).
        restaurante.adicionarPrato("Combo Teste", 25.0);

        assertEquals(1, restaurante.getCardapio().size());
        assertEquals("Combo Teste", restaurante.getCardapio().get(0).getNome());
    }

    @Test
    @DisplayName("CT34 - LancarPedido não deve lançar exceção ao ser chamado (método atualmente vazio)")
    void lancarPedidoNaoDeveLancarExcecao() {
        Restaurante restaurante = new Restaurante(1, "Restaurante Teste", "Rua Teste, 1", 3);
        assertDoesNotThrow(() -> restaurante.LancarPedido("X-Salada", "Cliente Teste"));
    }
}
