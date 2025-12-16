package DataCoders.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public abstract class ClienteConPedidos extends Cliente {

    @Column(name = "cuota")
    private double cuota;   // ← campo cuota

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> pedidos = new ArrayList<>();

    // =========================
    // Constructores
    // =========================
    public ClienteConPedidos() {
        super();
    }

    public ClienteConPedidos(String nif, String nombre, String domicilio, String email) {
        super(nif, nombre, domicilio, email);
    }

    // =========================
    // pedidos
    // =========================
    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public void addPedido(Pedido pedido) {
        if (!pedidos.contains(pedido)) {
            pedidos.add(pedido);
            pedido.setCliente(this);
        }
    }

    public void removePedido(Pedido pedido) {
        if (pedidos.remove(pedido)) {
            pedido.setCliente(null);
        }
    }

    // =========================
    // cuota
    // =========================
    public double getCuota() {
        return cuota;
    }

    public void setCuota(double cuota) {
        this.cuota = cuota;
    }
}