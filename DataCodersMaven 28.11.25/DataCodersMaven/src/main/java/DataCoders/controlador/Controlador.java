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

    public List<Articulo> obtenerTodosArticulos()throws SQLException {
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
}