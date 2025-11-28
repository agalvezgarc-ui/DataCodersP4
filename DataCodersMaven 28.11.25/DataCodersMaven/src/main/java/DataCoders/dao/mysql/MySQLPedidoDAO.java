package DataCoders.dao.mysql;

import DataCoders.dao.PedidoDAO;
import DataCoders.modelo.Pedido;
import DataCoders.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.sql.SQLException;
import java.util.List;

/**
 * Implementación MySQLPedidoDAO usando JPA (Hibernate).
 *
 * Esta clase implementa los métodos definidos en la interfaz PedidoDAO
 * utilizando EntityManager en lugar de JDBC directo.
 *
 * Incluye operaciones básicas sobre la tabla 'pedido' y delega en el ORM
 * la carga de las relaciones con Cliente y Articulo.
 */
public class MySQLPedidoDAO implements PedidoDAO {

    @Override
    public void insertar(Pedido pedido) throws SQLException {
        // Inserta un nuevo pedido en la BD usando JPA.
        // El número de pedido (numero_pedido) se genera automáticamente (IDENTITY).
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(pedido);   // INSERT en la tabla pedido
            em.getTransaction().commit();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public Pedido buscarPorNumero(String numeroPedido) throws SQLException {
        // Busca un pedido por su número (clave primaria).
        EntityManager em = JPAUtil.getEntityManager();

        try {
            int num = Integer.parseInt(numeroPedido);
            return em.find(Pedido.class, num);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public List<Pedido> obtenerTodos() throws SQLException {
        // Devuelve todos los pedidos almacenados en la BD.
        EntityManager em = JPAUtil.getEntityManager();

        try {
            TypedQuery<Pedido> query =
                    em.createQuery("SELECT p FROM Pedido p", Pedido.class);
            return query.getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void eliminar(String numeroPedido) throws SQLException {
        // Elimina un pedido a partir de su número (clave primaria).
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            int num = Integer.parseInt(numeroPedido);
            Pedido pedido = em.find(Pedido.class, num);
            if (pedido != null) {
                em.remove(pedido);    // DELETE en la tabla pedido
            }

            em.getTransaction().commit();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
