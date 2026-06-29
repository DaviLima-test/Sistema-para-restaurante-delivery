package repositorio;

import java.util.ArrayList;
import java.util.List;
import model.Produto;
import model.Pedido;

public class Dados {
	public static List<Produto> listaProdutos = new ArrayList<>();
	public static List<Pedido> listaPedidos = new ArrayList<>();
    public static ArrayList<Produto> listaCarrinho = new ArrayList<>();
    //private static final ArrayList<Produto> listaCarrinho = new ArrayList<>();

    // Retorna a lista completa caso alguma função precise ler
    public static ArrayList<Produto> getListaCarrinho() {
        return listaCarrinho;
    }

    // Adiciona um item ao carrinho
    public static void adicionarProduto(Produto produto) {
        if (produto != null) {
            listaCarrinho.add(produto);
        }
    }

    // Remove uma unidade do item do carrinho
    public static void removerProduto(Produto produto) {
        listaCarrinho.remove(produto);
    }

    // Limpa o carrinho por completo (usado após finalizar o pedido)
    public static void limparCarrinho() {
        listaCarrinho.clear();
    }

    // Calcula o valor total somando o preço de cada item da lista
    public static double calcularTotal() {
        double total = 0;
        for (Produto p : listaCarrinho) {
            total += p.getPreco();
        }
        return total;
    }

    // Verifica se o carrinho está completamente vazio
    public static boolean estaVazio() {
        return listaCarrinho.isEmpty();
    }
}	