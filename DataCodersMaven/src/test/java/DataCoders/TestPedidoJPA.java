package DataCoders;

import DataCoders.modelo.Articulo;
import DataCoders.modelo.Cliente;
import DataCoders.modelo.Pedido;
import DataCoders.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

public class TestPedidoJPA {

    public static void main(String[] args) {

        // Obtenemos el EntityManager a través de JPAUtil
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            // ⚠ Usa un cliente y un artículo que EXISTAN en tu BD
            // Cliente: ahora se identifica por id_cliente (PK autoincremental)
            int idCliente = 1;              // cambia por un id_cliente real de tu tabla 'cliente'
            String codigoArticulo = "A001"; // cambia por un código real de tu tabla 'articulo'

            Cliente cliente = em.find(Cliente.class, idCliente);
            Articulo articulo = em.find(Articulo.class, codigoArticulo);

            if (cliente == null || articulo == null) {
                System.out.println("❌ Cliente o artículo no encontrados. Revisa idCliente / codigoArticulo.");
                em.getTransaction().rollback();
                return;
            }

            // Creamos un nuevo pedido (numero_pedido lo genera la BD)
            Pedido pedido = new Pedido(
                    cliente,
                    articulo,
                    2,                                  // cantidad
                    articulo.getPrecioVenta(),         // precioUnitario
                    LocalDateTime.now().plusMinutes(30) // fechaEntrega (para probar esEnviado/esCancelable)
            );

            em.persist(pedido);  // INSERT
            em.getTransaction().commit();

            Integer numeroGenerado = pedido.getNumeroPedido();

            System.out.println("✅ Pedido guardado con numero_pedido = " + numeroGenerado);

            // ---------------------------------------
            // Volvemos a leer el pedido desde la BD
            // ---------------------------------------
            Pedido pedidoBD = em.find(Pedido.class, numeroGenerado);

            System.out.println("\n=== Pedido leído desde la BD ===");
            System.out.println(pedidoBD);
            System.out.println("Cliente: " + pedidoBD.getCliente().getEmail());
            System.out.println("Artículo: " + pedidoBD.getArticulo().getCodigo());
            System.out.println("Cantidad: " + pedidoBD.getCantidad());
            System.out.println("Precio unitario: " + pedidoBD.getPrecioUnitario());
            System.out.println("Importe total: " + pedidoBD.calcularImporte());
            System.out.println("¿Enviado? " + pedidoBD.esEnviado());
            System.out.println("¿Cancelable? " + pedidoBD.esCancelable());

        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
