package DataCoders;

import DataCoders.modelo.Articulo;
import DataCoders.util.JPAUtil;
import jakarta.persistence.EntityManager;
import junit.framework.TestCase;

public class TestArticuloJPA extends TestCase {

    // Prueba básica: buscar un artículo por su código
    public void testBuscarArticulo() {

        // Obtener EntityManager
        EntityManager em = JPAUtil.getEntityManager();

        // Iniciar transacción (no siempre necesario, pero recomendable)
        em.getTransaction().begin();

        Articulo art = em.find(Articulo.class, "A001");

        em.getTransaction().commit();
        em.close();

        // Mostrar el artículo
        System.out.println(art);

        // Asegurarnos de que no es null
        assertNotNull("El artículo no debería ser null", art);
    }
}