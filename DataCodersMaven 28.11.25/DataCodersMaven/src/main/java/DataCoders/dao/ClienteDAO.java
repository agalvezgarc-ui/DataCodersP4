/**
 * Interfaz ClienteDAO.
 *
 * Define las operaciones básicas que se pueden realizar
 * sobre los objetos de tipo Cliente en la base de datos.
 *
 * Este interfaz forma parte del patrón de diseño DAO y permite
 * separar la lógica de acceso a datos del resto del sistema.
 */
package DataCoders.dao;

import DataCoders.modelo.Cliente;

import java.sql.SQLException;
import java.util.List;

public interface ClienteDAO {

    // INSERT
    void insertar(Cliente cliente) throws SQLException;

    // SELECT por email
    Cliente buscarPorEmail(String email) throws SQLException;

    // SELECT todos
    List<Cliente> obtenerTodos() throws SQLException;


    List<Cliente> obtenerPremium() throws SQLException;

    List<Cliente> obtenerEstandar() throws SQLException;
    // UPDATE
    void actualizar(Cliente cliente) throws SQLException;

    // DELETE por email
    void eliminarPorEmail(String email) throws SQLException;
}