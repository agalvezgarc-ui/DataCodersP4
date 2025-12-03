//
package DataCoders.modelo;

import java.util.ArrayList;

public class Datos {
    private final ArrayList<Articulo> articulos = new ArrayList<>();
    private final ArrayList<Cliente>  clientes  = new ArrayList<>();
    private final ArrayList<Pedido>   pedidos   = new ArrayList<>();

    // Artículos
    public void addArticulo(Articulo a) { articulos.add(a); }
    public ArrayList<Articulo> getArticulos() { return articulos; }
    public Articulo findArticuloByCodigo(String codigo) {
        for (Articulo a : articulos) if (a.getCodigo().equalsIgnoreCase(codigo)) return a;
        return null;
    }

    // Clientes
    public void addCliente(Cliente c) { clientes.add(c); }
    public ArrayList<Cliente> getClientes() { return clientes; }
    public Cliente findClienteByEmail(String email) {
        for (Cliente c : clientes) if (c.getEmail().equalsIgnoreCase(email)) return c;
        return null;
    }

    // Pedidos
    public void addPedido(Pedido p) { pedidos.add(p); }
    public ArrayList<Pedido> getPedidos() { return pedidos; }
}
