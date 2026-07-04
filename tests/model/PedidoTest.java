package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Casos de teste unitários para a classe {@link Pedido}.
 * <p>
 * Os métodos {@code setIdRestaurante}, {@code setIdCliente} e {@code setIdEntregador}
 * não são cobertos aqui pois dependem diretamente de {@code bd.BancoDados} (conexão MySQL).
 */
class PedidoTest {

    private Restaurante criarRestaurante() {
        return new Restaurante(1, "Cantina do Zé", "Rua B, 45", 4);
    }

    private Cliente criarCliente() {
        return new Cliente("cliente@teste.com", "Cliente Teste", "0000");
    }

    private Entregador criarEntregador() {
        return new Entregador("entregador@teste.com", "Entregador Teste", "111.222.333-44");
    }

    @Test
    @DisplayName("CT19 - Construtor deve iniciar Estado=1 (Pendente) e EstadoRestaurante=1 (Recebido)")
    void construtorDeveIniciarEstadosPadrao() {
        ArrayList<Produto> comidas = new ArrayList<>();
        comidas.add(new Produto(1, "X-Burguer", 18.0));

        Pedido pedido = new Pedido(comidas, null, null, criarRestaurante(), criarCliente(), criarEntregador());

        assertEquals(1, pedido.getEstado(), "Estado inicial do entregador deve ser 1 (Pendente)");
        assertEquals(1, pedido.getEstadoRestaurante(), "Estado inicial do restaurante deve ser 1 (Recebido)");
    }

    @Test
    @DisplayName("CT20 - Construtor com lista de comidas nula deve iniciar uma lista vazia (evita NullPointerException)")
    void construtorComComidasNulaDeveIniciarListaVazia() {
        Pedido pedido = new Pedido(null, null, null, criarRestaurante(), criarCliente(), criarEntregador());

        assertNotNull(pedido.getComidas());
        assertTrue(pedido.getComidas().isEmpty());
    }

    @Test
    @DisplayName("CT21 - Construtor deve associar corretamente cliente, restaurante e entregador")
    void construtorDeveAssociarEntidadesCorretamente() {
        Restaurante restaurante = criarRestaurante();
        Cliente cliente = criarCliente();
        Entregador entregador = criarEntregador();

        Pedido pedido = new Pedido(new ArrayList<>(), "19:00", "19:05", restaurante, cliente, entregador);

        assertSame(restaurante, pedido.getRestaurante());
        assertSame(cliente, pedido.getCliente());
        assertSame(entregador, pedido.getEntregador());
        assertEquals("19:00", pedido.getHora_Entregue());
    }

    @Test
    @DisplayName("CT22 - setEstado e setEstadoRestaurante devem atualizar os estados de forma independente")
    void setEstadoESetEstadoRestauranteDevemSerIndependentes() {
        Pedido pedido = new Pedido(new ArrayList<>(), null, null, criarRestaurante(), criarCliente(), criarEntregador());

        pedido.setEstado(2); // Em rota
        pedido.setEstadoRestaurante(3); // Pronto

        assertEquals(2, pedido.getEstado());
        assertEquals(3, pedido.getEstadoRestaurante());
    }

    @Test
    @DisplayName("CT23 - setId/getId e setFormaPagamento/getFormaPagamento devem funcionar corretamente")
    void setIdESetFormaPagamentoDevemFuncionar() {
        Pedido pedido = new Pedido();
        pedido.setId(42);
        pedido.setFormaPagamento("Cartão de Crédito");

        assertEquals(42, pedido.getId());
        assertEquals("Cartão de Crédito", pedido.getFormaPagamento());
    }

    @Test
    @DisplayName("CT24 - Construtor vazio (Pedido()) não inicializa a lista de comidas")
    void construtorVazioNaoInicializaListaDeComidas() {
        Pedido pedido = new Pedido();
        assertNull(pedido.getComidas(),
                "Documenta comportamento atual: Pedido() não inicializa 'comidas', diferente do construtor completo. " +
                "Risco de NullPointerException se getComidas() for iterado sem checagem prévia.");
    }

    @Test
    @DisplayName("CT25 - setComidas deve substituir completamente a lista de itens do pedido")
    void setComidasDeveSubstituirListaDeItens() {
        Pedido pedido = new Pedido(new ArrayList<>(), null, null, criarRestaurante(), criarCliente(), criarEntregador());

        ArrayList<Produto> novaLista = new ArrayList<>();
        novaLista.add(new Produto(1, "Coxinha", 6.0));
        novaLista.add(new Produto(2, "Pastel", 7.5));

        pedido.setComidas(novaLista);

        assertEquals(2, pedido.getComidas().size());
        assertEquals("Coxinha", pedido.getComidas().get(0).getNome());
    }
}
