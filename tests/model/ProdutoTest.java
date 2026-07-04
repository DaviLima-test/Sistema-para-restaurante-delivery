package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Casos de teste unitários para a classe {@link Produto}.
 * <p>
 * Cobre os cinco construtores sobrecarregados, os getters/setters e o
 * comportamento de {@link Produto#toString()}, usado pela interface Swing
 * para listar produtos em JComboBox/JList.
 */
class ProdutoTest {

    @Test
    @DisplayName("CT01 - Construtor completo com Restaurante deve preencher todos os campos")
    void construtorComRestauranteDevePreencherCampos() {
        Restaurante restaurante = new Restaurante(1, "Burger House", "Rua A, 123", 5);
        Produto produto = new Produto(10, "X-Salada", 19.90, restaurante);

        assertEquals(10, produto.getCodigo());
        assertEquals("X-Salada", produto.getNome());
        assertEquals(19.90, produto.getPreco(), 0.0001);
        assertSame(restaurante, produto.getRestaurante());
    }

    @Test
    @DisplayName("CT02 - Construtor com descrição deve preencher nome, descrição e preço")
    void construtorComDescricaoDevePreencherCampos() {
        Produto produto = new Produto(11, "X-Bacon", "Pão, carne, bacon e queijo", 22.50);

        assertEquals(11, produto.getCodigo());
        assertEquals("X-Bacon", produto.getNome());
        assertEquals("Pão, carne, bacon e queijo", produto.getDescricao());
        assertEquals(22.50, produto.getPreco(), 0.0001);
        assertNull(produto.getRestaurante(), "Este construtor não associa Restaurante");
    }

    @Test
    @DisplayName("CT03 - Construtor simplificado (nome, preco) deve iniciar descrição vazia")
    void construtorSimplificadoDeveIniciarDescricaoVazia() {
        Produto produto = new Produto("Refrigerante Lata", 6.0);

        assertEquals("Refrigerante Lata", produto.getNome());
        assertEquals(6.0, produto.getPreco(), 0.0001);
        assertEquals("", produto.getDescricao());
    }

    @Test
    @DisplayName("CT04 - Construtor (codigo, nome, preco) deve iniciar descrição vazia")
    void construtorComCodigoENomeDeveIniciarDescricaoVazia() {
        Produto produto = new Produto(20, "Batata Frita", 12.0);

        assertEquals(20, produto.getCodigo());
        assertEquals("Batata Frita", produto.getNome());
        assertEquals("", produto.getDescricao());
    }

    @Test
    @DisplayName("CT05 - Construtor de compatibilidade (restaurante como String) não deve associar objeto Restaurante")
    void construtorDeCompatibilidadeNaoAssociaRestauranteObjeto() {
        Produto produto = new Produto(30, "Suco Natural", 8.5, "Restaurante Legado");

        assertEquals(30, produto.getCodigo());
        assertEquals("Suco Natural", produto.getNome());
        assertEquals(8.5, produto.getPreco(), 0.0001);
        assertNull(produto.getRestaurante(),
                "Documenta comportamento atual: o nome do restaurante recebido como String é descartado");
    }

    @Test
    @DisplayName("CT06 - Construtor vazio deve permitir preenchimento posterior via setters")
    void construtorVazioDevePermitirSetters() {
        Produto produto = new Produto();

        produto.setCodigo(99);
        produto.setNome("Pizza Grande");
        produto.setDescricao("Mussarela e tomate");
        produto.setPreco(45.0);

        assertEquals(99, produto.getCodigo());
        assertEquals("Pizza Grande", produto.getNome());
        assertEquals("Mussarela e tomate", produto.getDescricao());
        assertEquals(45.0, produto.getPreco(), 0.0001);
    }

    @Test
    @DisplayName("CT07 - toString() deve retornar apenas o nome do produto (usado em JList/JComboBox)")
    void toStringDeveRetornarApenasNome() {
        Produto produto = new Produto(1, "Milkshake", 15.0);
        assertEquals("Milkshake", produto.toString());
    }

    @Test
    @DisplayName("CT08 - setPreco deve aceitar valores zero e negativos (sem validação atual)")
    void setPrecoAceitaValoresInvalidosSemValidacao() {
        Produto produto = new Produto(1, "Item Teste", 10.0);
        produto.setPreco(-5.0);
        assertEquals(-5.0, produto.getPreco(), 0.0001,
                "Documenta ausência de validação de preço negativo na camada de modelo");
    }
}
