package DataCoders.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    // Nombre de la unidad de persistencia definido en persistence.xml
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("DataCodersPU");

    // Devuelve un nuevo EntityManager para interactuar con la base de datos
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Cierra la fábrica de EntityManagers (se usa al final de la aplicación)
    public static void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}