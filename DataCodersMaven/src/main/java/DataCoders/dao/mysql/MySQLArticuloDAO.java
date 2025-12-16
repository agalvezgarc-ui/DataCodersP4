package DataCoders.dao.mysql;

import DataCoders.dao.ArticuloDAO;
import DataCoders.modelo.Articulo;
import DataCoders.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * Implementación MySQLArticuloDAO usando JPA (Hibernate).
 *
 * Esta clase implementa los métodos definidos en la interfaz ArticuloDAO
 * utilizando la API de JPA para interactuar con la base de datos.
 *
 * Ya no se usan conexiones JDBC directas ni sentencias SQL manuales.
 */
public class MySQLArticuloDAO implements ArticuloDAO {

    @Override
    public void insertar(Articulo articulo){
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(articulo);  // INSERT en la tabla articulo
            em.getTransaction().commit();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public Articulo buscarPorCodigo(String codigo){
        EntityManager em = JPAUtil.getEntityManager();

        try {
            // Búsqueda por clave primaria
            return em.find(Articulo.class, codigo);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public List<Articulo> obtenerTodos(){
        EntityManager em = JPAUtil.getEntityManager();

        try {
            // Consulta JPQL: devuelve todos los artículos
            TypedQuery<Articulo> query =
                    em.createQuery("SELECT a FROM Articulo a", Articulo.class);
            return query.getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void actualizar(Articulo articulo){
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(articulo);  // UPDATE
            em.getTransaction().commit();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void eliminarPorCodigo(String codigo){
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            Articulo articulo = em.find(Articulo.class, codigo);
            if (articulo != null) {
                em.remove(articulo);  // DELETE
            }
            em.getTransaction().commit();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
