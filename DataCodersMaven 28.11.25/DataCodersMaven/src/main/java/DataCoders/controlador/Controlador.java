//
package DataCoders.controlador;

import java.util.ArrayList;
import DataCoders.modelo.*;

public class Controlador {
    private final Datos datos;

    public Controlador(Datos datos) { this.datos = datos; }

    // Artículos
    public void addArticulo(Articulo a) { datos.addArticulo(a); }
    public ArrayList<Articulo> getArticulos() { return datos.getArticulos(); }
    public Articulo findArticuloByCodigo(String codigo) { return datos.findArticuloByCodigo(codigo); }

    // Clientes
    public void addCliente(Cliente c) { datos.addCliente(c); }
    public ArrayList<Cliente> getClientes() { return datos.getClientes(); }
    public Cliente findClienteByEmail(String email) { return datos.findClienteByEmail(email); }

    // Pedidos
    public void addPedido(Pedido p) { datos.addPedido(p); }
    public ArrayList<Pedido> getPedidos() { return datos.getPedidos(); }
}
