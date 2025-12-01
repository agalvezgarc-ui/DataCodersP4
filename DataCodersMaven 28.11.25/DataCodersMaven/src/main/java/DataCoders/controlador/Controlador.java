//
package DataCoders.controlador;

import DataCoders.dao.mysql.MySQLArticuloDAO;
import DataCoders.dao.mysql.MySQLClienteDAO;
import DataCoders.dao.mysql.MySQLPedidoDAO;
import DataCoders.modelo.Cliente;
import DataCoders.modelo.Datos;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import DataCoders.modelo.Articulo;
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
import DataCoders.modelo.Pedido;
import java.util.stream.Collectors;

public class Controlador {

    // ===== Atributos de la clase =====
    private final Datos datos;
    private final MySQLArticuloDAO articuloDAO;
    private final MySQLClienteDAO clienteDAO;
    private final MySQLPedidoDAO pedidoDAO;

    // ===== Constructor =====
    public Controlador(Datos datos) {
        this.datos = datos;
        this.articuloDAO = new MySQLArticuloDAO();
        this.clienteDAO = new MySQLClienteDAO();
        this.pedidoDAO = new MySQLPedidoDAO();
    }

    public void anadirArticulo(String codigo, String descripcion, double precioVenta, double gastoEnvio, int tiempoPrepMin) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (CallableStatement cs = conn.prepareCall("{CALL agregararticulo(?, ?, ?, ?, ?)}")) {

                cs.setString(1, codigo);
                cs.setString(2, descripcion);
                cs.setDouble(3, precioVenta);
                cs.setDouble(4, gastoEnvio);
                cs.setInt(5, tiempoPrepMin);

                cs.executeUpdate();
                conn.commit();

                System.out.println("Artículo creado correctamente.");

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error al crear el artículo: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println("Error al conectar con la BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Articulo> obtenerTodosArticulos() throws SQLException {
        return articuloDAO.obtenerTodos();
    }


    public void anadirCliente(String nombre, String domicilio, String nif, String email, String tipoStr) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Iniciar transacción

            try (CallableStatement cs = conn.prepareCall("{CALL agregarCliente(?, ?, ?, ?, ?)}")) {

                cs.setString(1, nombre);
                cs.setString(2, domicilio);
                cs.setString(3, nif);
                cs.setString(4, email);
                cs.setString(5, tipoStr);

                cs.executeUpdate();
                conn.commit();
                System.out.println("Cliente creado correctamente.");

            } catch (SQLException e) {
                conn.rollback(); // Revertir si falla
                System.err.println("Error al crear el cliente: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println("Error al conectar con la BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== Método para obtener todos los clientes =====
    public List<Cliente> obtenerTodosClientes() throws SQLException {
        return clienteDAO.obtenerTodos();
    }

    public List<Cliente> obtenerClientesEstandar() throws SQLException {
        return clienteDAO.obtenerEstandar();
    }

    public List<Cliente> obtenerClientesPremium() throws SQLException {
        ClienteDAO dao = new MySQLClienteDAO();
        return dao.obtenerPremium();
    }

    public void anadirPedido(String emailCliente, String codigoArticulo, int cantidad, int tiempoPrep, int tiempoEnvio)
            throws ClienteNoEncontradoException, ArticuloNoDisponibleException, SQLException {

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

        // Insertar pedido usando procedimiento almacenado
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (CallableStatement cs = conn.prepareCall("{CALL agregarPedido(?, ?, ?, ?)}")) {
                cs.setString(1, cliente.getEmail());
                cs.setString(2, articulo.getCodigo());
                cs.setInt(3, cantidad);
                cs.setString(4, fechaEntregaStr);

                cs.executeUpdate();
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        }
    }

    public void eliminarPedido(String numeroPedido)
            throws PedidoNoCancelableException, SQLException {

        PedidoDAO dao = new MySQLPedidoDAO(); // tu implementación JPA

        // Llamamos directamente al DAO, que ya maneja la transacción y las excepciones
        dao.eliminar(numeroPedido);
    }

    public List<Pedido> obtenerPedidosPendientes(String emailFiltro) throws SQLException {
        PedidoDAO dao = new MySQLPedidoDAO();
        List<Pedido> pedidosBD = dao.obtenerTodos();

        // Filtrar pendientes y por cliente si se indica
        return pedidosBD.stream()
                .filter(p -> !p.esEnviado())
                .filter(p -> emailFiltro == null || p.getCliente().getEmail().equalsIgnoreCase(emailFiltro))
                .collect(Collectors.toList());
    }
    public List<Pedido> obtenerPedidosEnviados(String emailFiltro) throws SQLException {
        PedidoDAO dao = new MySQLPedidoDAO();
        List<Pedido> pedidosBD = dao.obtenerTodos();

        // Filtrar solo los enviados y por cliente si se indica
        return pedidosBD.stream()
                .filter(Pedido::esEnviado)  // solo enviados
                .filter(p -> emailFiltro == null || p.getCliente().getEmail().equalsIgnoreCase(emailFiltro))
                .collect(Collectors.toList());
    }
}

