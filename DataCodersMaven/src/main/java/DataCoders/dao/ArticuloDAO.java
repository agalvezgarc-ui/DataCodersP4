/**
 * Interfaz ArticuloDAO.
 *
 * Define las operaciones básicas (CRUD) que se pueden realizar
 * sobre los objetos de tipo Articulo en la base de datos.
 *
 * Forma parte del patrón de diseño DAO y permite separar
 * la lógica de acceso a datos del resto del sistema.
 */
package DataCoders.dao;

import DataCoders.modelo.Articulo;

import java.sql.SQLException;
import java.util.List;

public interface ArticuloDAO {

    void insertar(Articulo articulo);

    Articulo buscarPorCodigo(String codigo);

    List<Articulo> obtenerTodos();

    void actualizar(Articulo articulo);

    void eliminarPorCodigo(String codigo);
}