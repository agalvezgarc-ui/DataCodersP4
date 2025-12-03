package DataCoders.dao.mysql;

import DataCoders.dao.ClienteDAO;
import DataCoders.modelo.Cliente;
import DataCoders.modelo.ClienteEstandar;
import DataCoders.modelo.ClientePremium;
import DataCoders.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.sql.SQLException;
import java.util.List;

/**
 * Implementación MySQLClienteDAO usando JPA (Hibernate).
 *
 * Esta clase sustituye el acceso directo con JDBC (Connection, PreparedStatement, ResultSet)
 * por el uso de EntityManager de JPA.
 *
 * Mantiene las mismas operaciones CRUD definidas en la interfaz ClienteDAO,
 * pero delegando en el ORM la generación de las sentencias SQL.
 */
public class MySQLClienteDAO implements ClienteDAO {

    @Override
    public void insertar(Cliente cliente) throws SQLException {
        // Inserta un nuevo cliente en la BD usando JPA
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(cliente);          // INSERT en la tabla cliente
            em.getTransaction().commit();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public Cliente buscarPorEmail(String email) throws SQLException {
        // Busca un cliente por su email usando una consulta JPQL
        EntityManager em = JPAUtil.getEntityManager();

        try {
            TypedQuery<Cliente> query = em.createQuery(
                    "SELECT c FROM Cliente c WHERE c.email = :email",
                    Cliente.class
            );
            query.setParameter("email", email);

            List<Cliente> resultados = query.getResultList();
            // Si no hay ningún resultado, devolvemos null (mismo comportamiento que antes)
            return resultados.isEmpty() ? null : resultados.get(0);

        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public List<Cliente> obtenerTodos() throws SQLException {
        // Devuelve todos los clientes de la tabla cliente
        EntityManager em = JPAUtil.getEntityManager();

        try {
            TypedQuery<Cliente> query = em.createQuery(
                    "SELECT c FROM Cliente c",
                    Cliente.class
            );
            return query.getResultList();

        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Cliente> obtenerPremium() throws SQLException {
        // Devuelve únicamente los clientes de tipo ClientePremium
        EntityManager em = JPAUtil.getEntityManager();

        try {
            TypedQuery<Cliente> query = em.createQuery(
                    "SELECT c FROM Cliente c WHERE TYPE(c) = ClientePremium",
                    Cliente.class
            );
            return query.getResultList();

        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Cliente> obtenerEstandar() throws SQLException {
        // Devuelve únicamente los clientes de tipo ClienteEstandar
        EntityManager em = JPAUtil.getEntityManager();

        try {
            TypedQuery<Cliente> query = em.createQuery(
                    "SELECT c FROM Cliente c WHERE TYPE(c) = ClienteEstandar",
                    Cliente.class
            );
            return query.getResultList();

        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void actualizar(Cliente cliente) throws SQLException {
        // Actualiza los datos de un cliente existente
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(cliente);           // UPDATE basado en la clave primaria
            em.getTransaction().commit();

        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void eliminarPorEmail(String email) throws SQLException {
        // Elimina un cliente a partir de su email
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            TypedQuery<Cliente> query = em.createQuery(
                    "SELECT c FROM Cliente c WHERE c.email = :email",
                    Cliente.class
            );
            query.setParameter("email", email);
            List<Cliente> resultados = query.getResultList();

            if (!resultados.isEmpty()) {
                Cliente cliente = resultados.get(0);
                em.remove(cliente);      // DELETE
            }

            em.getTransaction().commit();

        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
