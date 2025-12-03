/**
 * Interfaz PedidoDAO.
 *
 * Define las operaciones básicas (CRUD) que se pueden realizar
 * sobre los objetos de tipo Pedido en la base de datos.
 *
 * Forma parte del patrón de diseño DAO y permite separar
 * la lógica de acceso a datos del resto del sistema.
 */
package DataCoders.dao;

import DataCoders.modelo.Pedido;

import java.sql.SQLException;
import java.util.List;

public interface PedidoDAO {

    void insertar(Pedido pedido) throws SQLException;

    Pedido buscarPorNumero(String numeroPedido) throws SQLException;

    List<Pedido> obtenerTodos() throws SQLException;

    void eliminar(String numeroPedido) throws SQLException;
}