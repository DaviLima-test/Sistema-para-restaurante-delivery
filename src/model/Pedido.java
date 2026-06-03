package model;

public class Pedido {
    private String Comida;
    private String Hora_Entregue;
    private String HoraPagoComidaEntregador;
    private int Estado;

  
    public Pedido(Produto produto, String string1, String string2, Entregador entregador, Restaurante restaurante, Cliente cliente) {
        this.Comida = produto.nome;
        this.Hora_Entregue = string1;
        this.HoraPagoComidaEntregador = string2;
        this.Estado = 1; // 1 = Pendente, 2 = Em rota, 3 = Finalizado
    }

    public int getEstado() {
        return Estado;
    }

    public void setEstado(int estado) {
        this.Estado = estado;
    }

    public String getComida() {
        return Comida;
    }
}