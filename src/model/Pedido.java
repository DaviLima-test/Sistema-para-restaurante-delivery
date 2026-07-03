package model;

import bd.BancoDados;

import java.util.ArrayList;

/**
 * Pedido — modelo atualizado com dois estados independentes:
 *
 *  • Estado (entregador):  1=Pendente  2=Em rota  3=Finalizado
 *  • EstadoRestaurante:    1=Recebido  2=Em Produção  3=Pronto  4=Finalizado
 */
public class Pedido {

    private int id;
    private ArrayList<Produto> comidas;
    private String Hora_Entregue;
    private String HoraPagoComidaEntregador;
    private Cliente cliente;
    private Restaurante restaurante;
    private Entregador entregador; // Adicionado aqui
    private String formaPagamento;
    private int Estado;
    private int EstadoRestaurante;

    // Construtor atualizado incluindo o Entregador
    public Pedido(ArrayList<Produto> comidas, String horaEntrega, String horaPago,
                  Restaurante restaurante, Cliente cliente, Entregador entregador) {
        this.comidas                  = comidas != null ? comidas : new ArrayList<>();
        this.Hora_Entregue            = horaEntrega;
        this.HoraPagoComidaEntregador = horaPago;
        this.restaurante              = restaurante;
        this.cliente                  = cliente;
        this.entregador               = entregador; // Inicializado aqui
        this.Estado                   = 1;
        this.EstadoRestaurante        = 1;
    }

    public Pedido() {

    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Getter e Setter para o Entregador
    public Entregador getEntregador() { return entregador; }
    public void setEntregador(Entregador entregador) { this.entregador = entregador; }

    public int    getEstado()          { return Estado; }
    public void   setEstado(int e)     { this.Estado = e; }


    public int    getEstadoRestaurante()       { return EstadoRestaurante; }
    public void   setEstadoRestaurante(int e)  { this.EstadoRestaurante = e; }


    public ArrayList<Produto>      getComidas()        { return comidas; }
    public String      getHora_Entregue() { return Hora_Entregue; }
    public Cliente     getCliente()       { return cliente; }
    public Restaurante getRestaurante()   { return restaurante; }
    public String      getFormaPagamento(){ return formaPagamento; }
    public void        setFormaPagamento(String fp) { this.formaPagamento = fp; }

    public void setComidas(ArrayList<Produto> comidas) {
        this.comidas = comidas;
    }

    public void setHora_Entregue(String hora_Entregue) {
        Hora_Entregue = hora_Entregue;
    }

    public void setHoraPagoComidaEntregador(String horaPagoComidaEntregador) {
        HoraPagoComidaEntregador = horaPagoComidaEntregador;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public void setIdRestaurante(int idRestaurante) {
        this.restaurante = BancoDados.buscarRestaurantePorId(idRestaurante);
    }

    public void setIdCliente(int idCliente) {
        this.cliente = BancoDados.obterClientePorId(idCliente);
    }

    public void setIdEntregador(int idEntregador) {
        this.entregador = (Entregador) BancoDados.obterClientePorId(idEntregador);
    }

    public void setStatus(int status) {

    }
}