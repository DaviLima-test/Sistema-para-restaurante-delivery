package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Casos de teste unitários para a classe {@link Entregador}, focando na
 * transição de estados de um {@link Pedido} ao ser aceito ou recusado.
 */
class EntregadorTest {

    private Pedido criarPedidoPendente() {
        Restaurante restaurante = new Restaurante(1, "Cantina do Zé", "Rua B, 45", 4);
        Cliente cliente = new Cliente("cliente@teste.com", "Cliente Teste", "0000");
        return new Pedido(new ArrayList<>(), null, null, restaurante, cliente, null);
    }

    @Test
    @DisplayName("CT26 - Construtor deve preencher email, nome e cpf corretamente")
    void construtorDevePreencherCamposCorretamente() {
        Entregador entregador = new Entregador("joao@teste.com", "João", "123.456.789-00");

        assertEquals("joao@teste.com", entregador.getEmail());
        assertEquals("João", entregador.getNome());
        assertEquals("123.456.789-00", entregador.getCpf());
    }

    @Test
    @DisplayName("CT27 - aceitarPedido deve alterar o estado do pedido para 4 (Em Rota)")
    void aceitarPedidoDeveAlterarEstadoParaEmRota() {
        Entregador entregador = new Entregador("joao@teste.com", "João", "123.456.789-00");
        Pedido pedido = criarPedidoPendente();

        entregador.aceitarPedido(pedido);

        assertEquals(4, pedido.getEstado());
    }

    @Test
    @DisplayName("CT28 - negarPedido deve devolver o estado do pedido para 3 (Disponível para aceite)")
    void negarPedidoDeveDevolverEstadoParaDisponivel() {
        Entregador entregador = new Entregador("joao@teste.com", "João", "123.456.789-00");
        Pedido pedido = criarPedidoPendente();

        entregador.negarPedido(pedido);

        assertEquals(3, pedido.getEstado());
    }

    @Test
    @DisplayName("CT29 - setNome, setCpf, setEmail e setEstrelas devem atualizar os respectivos atributos")
    void settersDevemAtualizarAtributos() {
        Entregador entregador = new Entregador();

        entregador.setNome("Carlos");
        entregador.setCpf("999.888.777-66");
        entregador.setEmail("carlos@teste.com");
        entregador.setEstrelas(4);

        assertEquals("Carlos", entregador.getNome());
        assertEquals("999.888.777-66", entregador.getCpf());
        assertEquals("carlos@teste.com", entregador.getEmail());
        assertEquals(4, entregador.getEstrelas());
    }

    @Test
    @DisplayName("CT30 - Entregador construído sem argumentos deve iniciar com estrelas = 0")
    void construtorVazioDeveIniciarEstrelasComZero() {
        Entregador entregador = new Entregador();
        assertEquals(0, entregador.getEstrelas());
    }
}
