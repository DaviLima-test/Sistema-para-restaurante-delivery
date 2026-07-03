package model;

import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import bd.BancoDados;

import static bd.BancoDados.obterValorPedidoNoBanco;

/**
 * Classe que representa a entidade Restaurante no sistema.
 * Concentra as informações cadastrais e encapsula toda a lógica de negócio
 * relacionada ao gerenciamento do cardápio e faturamento de pedidos.
 * * @author Arthur, Felipe, Davi
 * @version 1.1
 */
public class Restaurante {

    private int id;
    private String nome;
    private String localizacao;
    private int estrelas;
    private List<Produto> cardapio = new ArrayList<>();

    /**
     * Construtor parametrizado para inicialização completa do restaurante.
     * * @param id          O identificador único do restaurante.
     * @param nome        O nome fantasia do estabelecimento.
     * @param localizacao O endereço do restaurante.
     * @param estrelas    A classificação em estrelas do restaurante.
     */
    public Restaurante(int id, String nome, String localizacao, int estrelas) {
        this.id = id;
        this.nome = nome;
        this.estrelas = estrelas;
        this.localizacao = localizacao;
    }

    /**
     * Construtor padrão sem argumentos.
     */
    public Restaurante() {
    }


    /**
     * Gera um arquivo de texto (.txt) atuando como a comanda do pedido,
     * detalhando o faturamento e o lucro obtido pelo restaurante.
     * * @param idPedido O identificador do pedido para geração do fechamento.
     */
    public void GerarComanda(int idPedido) {
        double valorTotal = obterValorPedidoNoBanco(idPedido);

        String nomeArquivo = "comanda_restaurante_pedido_" + idPedido + ".txt";

        try (FileWriter writer = new FileWriter(nomeArquivo)) {
            writer.write("==================================================\n");
            writer.write("           EXTRATO DE COMANDA - RELESTRATO        \n");
            writer.write("==================================================\n");
            writer.write("Restaurante: " + this.nome + "\n");
            writer.write("Localização: " + this.localizacao + "\n");
            writer.write("ID do Pedido: " + idPedido + "\n");
            writer.write("--------------------------------------------------\n");
            writer.write(String.format("Valor Total do Pedido: R$ %.2f\n", valorTotal));
            writer.write(String.format("Lucro Total do Restaurante: R$ %.2f\n", valorTotal));
            writer.write("==================================================\n");

            System.out.println("Comanda exportada com sucesso: " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao gerar o arquivo de texto da comanda: " + e.getMessage());
        }
    }

    /**
     * Sincroniza e carrega a lista de produtos do cardápio diretamente do banco de dados.
     */
    public void carregarCardapio() {
        try {
            this.cardapio = BancoDados.getCardapioPorRestaurante(this.id);
            if (this.cardapio == null) {
                this.cardapio = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar cardápio: " + e.getMessage());
            this.cardapio = new ArrayList<>();
        }
    }

    /**
     * Adiciona um novo prato à lista em memória e efetua o registo no banco de dados.
     * * @param nomeProduto Nome do prato a ser inserido.
     * @param preco       Preço de venda do prato.
     * @return true se a operação foi concluída com sucesso, false caso contrário.
     */
    public boolean adicionarPrato(String nomeProduto, double preco) {
        Produto novo = new Produto(nomeProduto, preco);
        this.cardapio.add(novo);
        try {
            BancoDados.cadastrarCardapio(nomeProduto, String.valueOf(preco), this.nome, this.localizacao);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao registar prato no banco: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza as informações de um prato existente no cardápio e sincroniza com o banco de dados.
     * * @param produto   O objeto Produto a ser editado.
     * @param novoNome  O novo nome a ser atribuído.
     * @param novoPreco O novo preço a ser atribuído.
     */
    public void atualizarPrato(Produto produto, String novoNome, double novoPreco) {
        if (produto != null) {
            produto.setNome(novoNome);
            produto.setPreco(novoPreco);
            BancoDados.atualizarCardapio(produto.getCodigo(), novoNome, String.valueOf(novoPreco));
        }
    }

    /**
     * Remove um prato específico do cardápio e elimina o seu registo no banco de dados.
     * * @param produto O objeto Produto a ser removido.
     */
    public void removerPrato(Produto produto) {
        if (produto != null) {
            this.cardapio.remove(produto);
            BancoDados.removerPrato(produto.getCodigo());
        }
    }

    public void LancarPedido(String item, String cliente) {
    }

    public String getNome() {
        return nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public int getEstrelas() {
        return estrelas;
    }

    public int getId() {
        return id;
    }

    public List<Produto> getCardapio() {
        return cardapio;
    }
}