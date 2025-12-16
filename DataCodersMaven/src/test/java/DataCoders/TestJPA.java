package DataCoders;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestJPA {
    public static void main(String[] args) {

        var emf = Persistence.createEntityManagerFactory("DataCodersPU");
        var em = emf.createEntityManager();

        System.out.println("✅ JPA conectado correctamente");

        em.close();
        emf.close();
    }
}