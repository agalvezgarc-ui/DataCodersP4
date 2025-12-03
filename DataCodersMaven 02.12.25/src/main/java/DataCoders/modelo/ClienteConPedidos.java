package DataCoders.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public abstract class ClienteConPedidos extends Cliente {

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> pedidos = new ArrayList<>();

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}
