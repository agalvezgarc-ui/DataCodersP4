package DataCoders.servicio;

import DataCoders.excepciones.ArticuloNoDisponibleException;
import DataCoders.excepciones.ClienteNoEncontradoException;
import DataCoders.modelo.Articulo;
import DataCoders.modelo.Cliente;
import DataCoders.modelo.Pedido;

import java.time.LocalDateTime;
import java.util.List;

public class ServicioPedidos {
    private final List<Articulo> articulos;
    private final List<Cliente> clientes;
    private final List<Pedido> pedidos;

    // El servicio trabaja sobre  listas en memoria (estructuras dinámicas)
    public ServicioPedidos(List<Articulo> articulos, List<Cliente> clientes, List<Pedido> pedidos) {
        this.articulos = articulos;
        this.clientes = clientes;
        this.pedidos = pedidos;
    }

    // Test 1 (lógica de negocio): crear pedido
    public Pedido crearPedido(String emailCliente, String codigoArticulo, int cantidad,
                              String numeroPedido, LocalDateTime ahora)
            throws ClienteNoEncontradoException, ArticuloNoDisponibleException {

        Cliente cliente = clientes.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(emailCliente))
                .findFirst()
                .orElseThrow(() -> new ClienteNoEncontradoException(
                        "No existe el cliente con email: " + emailCliente));

        Articulo articulo = articulos.stream()
                .filter(a -> a.getCodigo().equalsIgnoreCase(codigoArticulo))
                .findFirst()
                .orElseThrow(() -> new ArticuloNoDisponibleException(
                        "Artículo no disponible: " + codigoArticulo));

        LocalDateTime entrega = ahora.plusMinutes(articulo.getTiempoPrepMin());

        Pedido p = new Pedido(cliente, articulo, cantidad,
                articulo.getPrecioVenta(), entrega);
        pedidos.add(p);
        return p;
    }


}
