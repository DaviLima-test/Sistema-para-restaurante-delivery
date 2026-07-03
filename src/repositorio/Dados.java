package repositorio;

import java.util.ArrayList;
import java.util.List;
import model.Produto;
import model.Pedido;

/**
 * Classe utilitária responsável por gerenciar dados temporários em memória,
 * servindo como repositório para produtos, pedidos e o carrinho de compras atual.
 * * @author Arthur,Felipe,Davi
 * @version 1.0
 */
public class Dados {

    /** Lista global de todos os produtos cadastrados no sistema. */
    public static List<Produto> listaProdutos = new ArrayList<>();

    /** Lista global contendo o histórico de pedidos efetuados. */
    public static List<Pedido> listaPedidos = new ArrayList<>();

    /** Lista dinâmica que armazena os produtos adicionados ao carrinho atual do usuário. */
    public static ArrayList<Produto> listaCarrinho = new ArrayList<>();

    /**
     * Retorna a lista completa de produtos contidos no carrinho.
     * * @return ArrayList de objetos do tipo Produto pertencentes ao carrinho.
     */
    public static ArrayList<Produto> getListaCarrinho() {
        return listaCarrinho;
    }

    /**
     * Adiciona uma unidade de um produto ao carrinho de compras corrente.
     * * @param produto O objeto Produto a ser adicionado (não deve ser nulo).
     */
    public static void adicionarProduto(Produto produto) {
        if (produto != null) {
            listaCarrinho.add(produto);
        }
    }

    /**
     * Remove uma unidade específica de um produto do carrinho de compras.
     * * @param produto O objeto Produto a ser removido da lista.
     */
    public static void removerProduto(Produto produto) {
        listaCarrinho.remove(produto);
    }

    /**
     * Limpa completamente todos os produtos contidos no carrinho de compras atual.
     * Geralmente invocado logo após a finalização bem-sucedida de um pedido.
     */
    public static void limparCarrinho() {
        listaCarrinho.clear();
    }

    /**
     * Calcula o valor financeiro total somando o preço unitário de cada item
     * presente no carrinho de compras.
     * * @return O valor acumulado total (double) de todos os produtos do carrinho.
     */
    public static double calcularTotal() {
        double total = 0;
        for (Produto p : listaCarrinho) {
            total += p.getPreco();
        }
        return total;
    }

    /**
     * Verifica se o carrinho de compras atual encontra-se completamente vazio.
     * * @return true se o carrinho não possuir nenhum item, false caso contrário.
     */
    public static boolean estaVazio() {
        return listaCarrinho.isEmpty();
    }
}