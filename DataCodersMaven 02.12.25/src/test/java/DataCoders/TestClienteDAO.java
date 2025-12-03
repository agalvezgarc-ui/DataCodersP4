package DataCoders;

import DataCoders.dao.ClienteDAO;
import DataCoders.dao.mysql.MySQLClienteDAO;
import DataCoders.modelo.Cliente;

public class TestClienteDAO {

    public static void main(String[] args) throws Exception {

        ClienteDAO dao = new MySQLClienteDAO();

        // ⚠️ Cambia este email por uno que exista en tu tabla 'cliente'
        String email = "ana@email.com";

        Cliente c = dao.buscarPorEmail(email);

        if (c == null) {
            System.out.println("❌ No se ha encontrado ningún cliente con email: " + email);
        } else {
            System.out.println("✅ Cliente encontrado a través de ClienteDAO:");
            System.out.println(c);
            System.out.println("Tipo: " + c.getTipo() +
                    ", descuento envío: " + c.getDescuentoEnvio() +
                    ", cuota: " + c.getCuota());
        }
    }
}
