package DataCoders;

import DataCoders.modelo.Cliente;
import DataCoders.modelo.ClienteEstandar;
import DataCoders.modelo.ClientePremium;
import DataCoders.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TestClienteJPA {

    public static void main(String[] args) {

        // Obtenemos el EntityManager a través de JPAUtil
        EntityManager em = JPAUtil.getEntityManager();

        try {
            // ===========================================
            // 1) Búsqueda de un cliente por NIF con JPQL
            // ===========================================
            String nif = "12345678A";   // ⚠ Usa un NIF que exista en tu BD

            TypedQuery<Cliente> query = em.createQuery(
                    "SELECT c FROM Cliente c WHERE c.nif = :nif",
                    Cliente.class
            );
            query.setParameter("nif", nif);

            List<Cliente> resultados = query.getResultList();
            Cliente cliente = resultados.isEmpty() ? null : resultados.get(0);

            System.out.println("=== Búsqueda de cliente por NIF ===");
            System.out.println("NIF buscado: " + nif);

            if (cliente == null) {
                System.out.println("❌ No se ha encontrado ningún cliente con NIF " + nif);
            } else {
                System.out.println("✅ Cliente cargado correctamente desde la BD:");
                System.out.println(cliente);

                // Demostramos el uso de herencia (ClienteEstandar / ClientePremium)
                if (cliente instanceof ClienteEstandar) {
                    System.out.println("→ Es un ClienteEstandar con descuento envío: "
                            + cliente.getDescuentoEnvio()
                            + " y cuota: " + cliente.getCuota());
                } else if (cliente instanceof ClientePremium) {
                    System.out.println("→ Es un ClientePremium con descuento envío: "
                            + cliente.getDescuentoEnvio()
                            + " y cuota: " + cliente.getCuota());
                } else {
                    System.out.println("→ Es un tipo de cliente desconocido");
                }
            }

            // ===========================================
            // 2) Listado completo de clientes
            // ===========================================
            System.out.println("\n=== Listado completo de clientes ===");

            List<Cliente> todos = em.createQuery(
                    "SELECT c FROM Cliente c",
                    Cliente.class
            ).getResultList();

            for (Cliente c : todos) {
                System.out.println(
                        "ID=" + c.getIdCliente() +
                                ", NIF=" + c.getNif() +
                                ", Nombre=" + c.getNombre() +
                                ", Tipo=" + c.getTipo() +
                                ", Cuota=" + c.getCuota()
                );
            }

        } finally {
            // Cerramos el EntityManager
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
