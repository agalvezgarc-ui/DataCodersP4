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
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import DataCoders.util.DBConnection;
import DataCoders.modelo.Cliente;
import DataCoders.modelo.ClienteEstandar;
import DataCoders.modelo.ClientePremium;

public class Controlador {

    // ===== Atributos de la clase =====
    private final Datos datos;
    private final MySQLArticuloDAO articuloDAO;
    private final MySQLClienteDAO clienteDAO;
    private final MySQLPedidoDAO pedidoDAO;
    //private final ClienteDAO clienteDAO;


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


    public void anadirCliente(String nombre,
                              String domicilio,
                              String nif,
                              String email,
                              int tipo,
                              int cuota) throws SQLException {
        // tipo: por ejemplo 1 = estándar, 2 = premium
        Cliente cliente;

        if (tipo == 2) {
            // Cliente premium
            ClientePremium c = new ClientePremium(nif, nombre, domicilio, email);

            // Establecemos la cuota según la lógica de negocio
            // (puedes usar 'cuota' del parámetro o fijar 0.2 directamente)
            c.setCuota(cuota); // o c.setCuota(0.2);
            cliente = c;

        } else {
            // Cliente estándar
            ClienteEstandar c = new ClienteEstandar(nif, nombre, domicilio, email);

            // Cuota 0 para estándar
            c.setCuota(cuota); // o c.setCuota(0.0);
            cliente = c;
        }

        // Guardamos el cliente usando la capa DAO (JPA/Hibernate por debajo)
        clienteDAO.insertar(cliente);
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

