package model;

/**
 * Pedido — modelo atualizado com dois estados independentes:
 *
 *  • Estado (entregador):  1=Pendente  2=Em rota  3=Finalizado
 *  • EstadoRestaurante:    1=Recebido  2=Em Produção  3=Pronto  4=Finalizado
 */
public class Pedido {

    private String Comida;
    private String Hora_Entregue;
    private String HoraPagoComidaEntregador;
    private Cliente    cliente;
    private Restaurante restaurante;
    private String formaPagamento;

    /** Estado do ponto de vista do ENTREGADOR */
    private int Estado;

    /** Estado do ponto de vista do RESTAURANTE */
    private int EstadoRestaurante;

    public Pedido(Produto produto, String horaEntrega, String horaPago,
                  Entregador entregador, Restaurante restaurante, Cliente cliente) {
        this.Comida                   = produto != null ? produto.getNome() : "";
        this.Hora_Entregue            = horaEntrega;
        this.HoraPagoComidaEntregador = horaPago;
        this.restaurante              = restaurante;
        this.cliente                  = cliente;
        this.Estado                   = 1; // Pendente (entregador)
        this.EstadoRestaurante        = 1; // Recebido (restaurante)
    }
    public int    getEstado()          { return Estado; }
    public void   setEstado(int e)     { this.Estado = e; }


    public int    getEstadoRestaurante()       { return EstadoRestaurante; }
    public void   setEstadoRestaurante(int e)  { this.EstadoRestaurante = e; }


    public String      getComida()        { return Comida; }
    public String      getHora_Entregue() { return Hora_Entregue; }
    public Cliente     getCliente()       { return cliente; }
    public Restaurante getRestaurante()   { return restaurante; }
    public String      getFormaPagamento(){ return formaPagamento; }
    public void        setFormaPagamento(String fp) { this.formaPagamento = fp; }
}