package DataCoders.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")  // nombre de la tabla en la BD
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero_pedido")   // PK autoincremental en la BD
    private Integer numeroPedido;

    // Muchos pedidos pueden pertenecer a un mismo cliente
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    // FK hacia cliente.id_cliente (clave primaria de Cliente)
    private Cliente cliente;

    // Muchos pedidos pueden ser del mismo artículo
    @ManyToOne
    @JoinColumn(name = "codigo_articulo", referencedColumnName = "codigo", nullable = false)
    // FK hacia articulo.codigo
    private Articulo articulo;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "precio_unitario")
    private double precioUnitario;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    // Constructor vacío requerido por JPA
    public Pedido() {
    }

    // Constructor de conveniencia: no incluye numeroPedido porque lo genera la BD
    public Pedido(Cliente cliente, Articulo articulo,
                  int cantidad, double precioUnitario, LocalDateTime fechaEntrega) {
        this.cliente = cliente;
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.fechaEntrega = fechaEntrega;
    }

    // --- Getters y setters ---

    // Solo lectura: el número de pedido lo asigna la BD (IDENTITY)
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

    // === Lógica de negocio (idéntica a tu versión original) ===

    // Calcula el importe total del pedido: productos + gastos de envío
    public double calcularImporte() {
        return (precioUnitario * cantidad) + calcularGastoEnvio();
    }

    // Calcula los gastos de envío aplicando el descuento del cliente (0.0 estándar, 0.20 premium)
    public double calcularGastoEnvio() {
        double descuento = cliente.getDescuentoEnvio(); // 0.0 estándar, 0.20 premium
        double base = articulo.getGastoEnvio();
        return base * (1 - descuento);
    }

    // Indica si el pedido ya ha sido enviado (fecha de entrega en el pasado)
    public boolean esEnviado() {
        return fechaEntrega != null && LocalDateTime.now().isAfter(fechaEntrega);
    }

    // Un pedido es cancelable mientras no haya sido enviado
    public boolean esCancelable() {
        return !esEnviado();
    }

    @Override
    public String toString() {
        return "Pedido " +
                "numero=" + numeroPedido +
                ", cliente=" + (cliente != null ? cliente.getEmail() : "null") +
                ", articulo=" + (articulo != null ? articulo.getCodigo() : "null") +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", fechaEntrega=" + fechaEntrega;
    }
}