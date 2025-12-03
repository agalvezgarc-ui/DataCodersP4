//
package DataCoders.controlador;

import DataCoders.dao.mysql.MySQLArticuloDAO;
import DataCoders.dao.mysql.MySQLClienteDAO;
import DataCoders.dao.mysql.MySQLPedidoDAO;
import DataCoders.modelo.*;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.util.List;
import DataCoders.util.DBConnection;
import DataCoders.dao.ClienteDAO;
import DataCoders.excepciones.ClienteNoEncontradoException;
import DataCoders.excepciones.ArticuloNoDisponibleException;
import DataCoders.excepciones.PedidoNoCancelableException;
import DataCoders.dao.ArticuloDAO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import DataCoders.dao.PedidoDAO;
import DataCoders.dao.mysql.MySQLPedidoDAO;

import java.util.stream.Collectors;
import jakarta.persistence.PersistenceException;

public class Controlador {

    // ===== Atributos de la clase ===== //AQUÍ TENEMOS QUE LLAMAR A LA INTERFAZ NO A LA IMPLEMENTACIÓN
    private final Datos datos;
    private final ArticuloDAO articuloDAO;
    private final ClienteDAO clienteDAO;
    private final PedidoDAO pedidoDAO;

    // ===== Constructor =====
    public Controlador(Datos datos) {
        this.datos = datos;
        this.articuloDAO = new MySQLArticuloDAO();
        this.clienteDAO = new MySQLClienteDAO();
        this.pedidoDAO = new MySQLPedidoDAO();
    }

    public void anadirArticulo(String codigo, String descripcion, double precioVenta, double gastoEnvio, int tiempoPrepMin) {
        //creamos entidad articulo con los datos de la vista
        Articulo articulo = new Articulo(
                codigo,
                descripcion,
                precioVenta,
                gastoEnvio,
                tiempoPrepMin
        );
        try {
            articuloDAO.insertar(articulo);
            System.out.println("Artículo creado correctamente.");
        } catch (jakarta.persistence.EntityExistsException e) {
            // PK duplicada (código de artículo ya existe)
            System.err.println("Error: Ya existe un artículo con el código " + codigo);
        }
    }

    public List<Articulo> obtenerTodosArticulos(){
        return articuloDAO.obtenerTodos();
    }

    public void anadirCliente(String nombre, String domicilio, String nif, String email, String tipoStr) {
        Cliente cliente;
        if ("PREMIUM".equalsIgnoreCase(tipoStr) || "Premium".equalsIgnoreCase(tipoStr)) {
            cliente = new ClientePremium(nif, nombre, domicilio, email);
        } else {
            cliente = new ClienteEstandar(nif, nombre, domicilio, email);
        }
        try{
            clienteDAO.insertar(cliente);
            System.out.println("Cliente creado correctamente.");
        } catch (PersistenceException e){
            System.err.println("Error al crear el cliente. ");
            System.err.println("Detalles: " + e.getMessage());
        }
    }


    // ===== Método para obtener todos los clientes =====
    public List<Cliente> obtenerTodosClientes(){
        return clienteDAO.obtenerTodos();
    }

    public List<Cliente> obtenerClientesEstandar(){
        return clienteDAO.obtenerEstandar();
    }

    public List<Cliente> obtenerClientesPremium(){
        ClienteDAO dao = new MySQLClienteDAO();
        return dao.obtenerPremium();
    }

    public void anadirPedido(String emailCliente, String codigoArticulo, int cantidad, int tiempoPrep, int tiempoEnvio)
            throws ClienteNoEncontradoException, ArticuloNoDisponibleException{

        ClienteDAO clienteDAO = new MySQLClienteDAO();
        ArticuloDAO articuloDAO = new MySQLArticuloDAO();

        // Buscar cliente
        Cliente cliente = clienteDAO.buscarPorEmail(emailCliente);
        if (cliente == null) {
            throw new ClienteNoEncontradoException(
                    "El cliente con email '" + emailCliente + "' no existe."
            );
        }

        // Buscar artículo
        Articulo articulo = articuloDAO.buscarPorCodigo(codigoArticulo);
        if (articulo == null) {
            throw new ArticuloNoDisponibleException(
                    "El artículo con código '" + codigoArticulo + "' no está disponible."
            );
        }

        // Calcular fecha de entrega usando tiempo de preparación y envío
        LocalDateTime fechaEntrega = LocalDateTime.now().plusMinutes(tiempoPrep + tiempoEnvio);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaEntregaStr = fechaEntrega.format(formatter);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setArticulo(articulo);
        pedido.setCantidad(cantidad);
        pedido.setFechaEntrega(fechaEntrega);


        // 5. Guardar el pedido con JPA/Hibernate
        try {
            pedidoDAO.insertar(pedido);
            System.out.println("Pedido creado correctamente.");
        } catch (PersistenceException e) {
            System.err.println("Error al crear el pedido.");
            System.err.println("Detalles: " + e.getMessage());
        }
    }

    public void eliminarPedido(String numeroPedido)
            throws PedidoNoCancelableException {

        try {
            pedidoDAO.eliminar(numeroPedido);
            System.out.println("Pedido eliminado correctamente.");
        } catch (PersistenceException e) {
            throw new PedidoNoCancelableException(
                    "No se pudo cancelar el pedido " + numeroPedido + ": " + e.getMessage()
            );
        }
    }

    public List<Pedido> obtenerPedidosPendientes(String emailFiltro){
        PedidoDAO dao = new MySQLPedidoDAO();
        List<Pedido> pedidosBD = dao.obtenerTodos();

        // Filtrar pendientes y por cliente si se indica
        return pedidosBD.stream()
                .filter(p -> !p.esEnviado())
                .filter(p -> emailFiltro == null || p.getCliente().getEmail().equalsIgnoreCase(emailFiltro))
                .collect(Collectors.toList());
    }
    public List<Pedido> obtenerPedidosEnviados(String emailFiltro){
        PedidoDAO dao = new MySQLPedidoDAO();
        List<Pedido> pedidosBD = dao.obtenerTodos();

        // Filtrar solo los enviados y por cliente si se indica
        return pedidosBD.stream()
                .filter(Pedido::esEnviado)  // solo enviados
                .filter(p -> emailFiltro == null || p.getCliente().getEmail().equalsIgnoreCase(emailFiltro))
                .collect(Collectors.toList());
    }
}