package repositorio;

import java.util.ArrayList;
import java.util.List;
import model.Produto;
import model.Pedido;

public class Dados {
	public static List<Produto> listaProdutos = new ArrayList<>();
	public static List<Pedido> listaPedidos = new ArrayList<>();
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_delivery";
    private static final String USUARIO = "root";
    private static final String SENHA = "1234";

    public static ArrayList<Produto> listaCarrinho;
}	