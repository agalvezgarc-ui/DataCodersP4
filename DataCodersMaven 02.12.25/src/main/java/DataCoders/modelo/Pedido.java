package DataCoders.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero_pedido")
    private Integer numeroPedido;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "codigo_articulo", referencedColumnName = "codigo", nullable = false)
    private Articulo articulo;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "precio_unitario")
    private double precioUnitario;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    // Constructor vacío requerido por JPA
    public Pedido() {}

    // Constructor sin PK (lo genera la BD)
    public Pedido(Cliente cliente, Articulo articulo, int cantidad,
                  double precioUnitario, LocalDateTime fechaEntrega) {
        this.cliente = cliente;
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.fechaEntrega = fechaEntrega;
    }

    // ==========================
    // Getters & Setters
    // ==========================

    public Integer getNumeroPedido() {
        return numeroPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    // ==========================
    // Lógica del negocio
    // ==========================

    public double calcularImporte() {
        return (precioUnitario * cantidad) + calcularGastoEnvio();
    }

    public double calcularGastoEnvio() {
        double descuento = cliente.getDescuentoEnvio();
        double base = articulo.getGastoEnvio();
        return base * (1 - descuento);
    }

    public boolean esEnviado() {
        return fechaEntrega != null &&
                LocalDateTime.now().isAfter(fechaEntrega);
    }

    public boolean esCancelable() {
        return !esEnviado();
    }

    // ==========================
    // Métodos utilitarios pedidos
    // ==========================

    /**
     * Añadir un pedido a un cliente (útil si Cliente tiene lista de pedidos).
     */
    public void registrarEnCliente() {
        if (cliente != null) {
            cliente.getPedidos().add(this);
        }
    }

    /**
     * Eliminar un pedido desde el cliente (si existe).
     */
    public void eliminarDeCliente() {
        if (cliente != null) {
            cliente.getPedidos().remove(this);
        }
    }

    @Override
    public String toString() {
        return "Pedido {" +
                "numero=" + numeroPedido +
                ", cliente=" + (cliente != null ? cliente.getEmail() : "null") +
                ", articulo=" + (articulo != null ? articulo.getCodigo() : "null") +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", fechaEntrega=" + fechaEntrega +
                '}';
    }
}
