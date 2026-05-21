package Model;

import java.util.Date;

public class Pedido {
    private int id;
    private Date data;
    private double total;
    private Cliente cliente;
    private Produto produto;
    private String formaPagamento;
    private double desconto;

    public Pedido() {}

    public Pedido(int id, Date data, double total, Cliente cliente, Produto produto, String formaPagamento, double desconto) {
        this.id = id;
        this.data = data;
        this.total = total;
        this.cliente = cliente;
        this.produto = produto;
        this.formaPagamento = formaPagamento;
        this.desconto = desconto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public double getDesconto() { return desconto; }
    public void setDesconto(double desconto) { this.desconto = desconto; }
}