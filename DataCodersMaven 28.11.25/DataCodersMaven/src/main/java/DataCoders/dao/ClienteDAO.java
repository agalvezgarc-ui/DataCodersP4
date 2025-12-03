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

import java.util.List;

public interface ClienteDAO {

    // INSERT
    void insertar(Cliente cliente);

    // SELECT por email
    Cliente buscarPorEmail(String email);

    // SELECT todos
    List<Cliente> obtenerTodos();


    List<Cliente> obtenerPremium();

    List<Cliente> obtenerEstandar();
    // UPDATE
    void actualizar(Cliente cliente);

    // DELETE por email
    void eliminarPorEmail(String email);
}