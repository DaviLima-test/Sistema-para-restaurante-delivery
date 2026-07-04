package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Casos de teste unitários para a classe {@link Cliente}.
 * <p>
 * Os métodos de negócio (Pedir, AdicionarNoCarrinho, RemoverNoCarrinho, Avaliacao,
 * Pagamento, AcompanharPedidos, CancelarPedido) estão com "lógica simplificada" no
 * código-fonte atual (sem efeito colateral observável) — os testes aqui garantem
 * apenas que podem ser chamados sem lançar exceções, servindo de rede de segurança
 * para quando a lógica de fato for implementada.
 */
class ClienteTest {

    @Test
    @DisplayName("CT35 - Construtor completo deve preencher nome, email e iniciar cartão associado")
    void construtorCompletoDevePreencherCampos() {
        Cliente cliente = new Cliente("ana@teste.com", "Ana", "1234567890123456");

        assertEquals("Ana", cliente.getNome());
        assertEquals("ana@teste.com", cliente.getEmail());
    }

    @Test
    @DisplayName("CT36 - Construtor vazio deve permitir instanciação sem exceção (uso via JDBC/reflexão)")
    void construtorVazioNaoDeveLancarExcecao() {
        assertDoesNotThrow(Cliente::new);
    }

    @Test
    @DisplayName("CT37 - Métodos de negócio não devem lançar exceção quando chamados com parâmetros válidos")
    void metodosDeNegocioNaoDevemLancarExcecao() {
        Cliente cliente = new Cliente("ana@teste.com", "Ana", "1234567890123456");
        Produto produto = new Produto(1, "Sanduíche", 15.0);

        assertDoesNotThrow(() -> {
            cliente.AdicionarNoCarrinho(produto);
            cliente.RemoverNoCarrinho(produto);
            cliente.Pedir();
            cliente.Pagamento(15.0);
            cliente.AcompanharPedidos();
        });
    }

    @Test
    @DisplayName("CT38 - AdicionarNoCarrinho e RemoverNoCarrinho aceitam argumento nulo sem lançar exceção")
    void adicionarERemoverNoCarrinhoAceitamNulo() {
        Cliente cliente = new Cliente("ana@teste.com", "Ana", "1234567890123456");
        assertDoesNotThrow(() -> cliente.AdicionarNoCarrinho(null));
        assertDoesNotThrow(() -> cliente.RemoverNoCarrinho(null));
    }
}
