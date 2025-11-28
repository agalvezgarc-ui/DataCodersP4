////Observaciones: Es posible que ==Almacen en memoria== deba distribuirse entre las clases.
//No realizado: gestión de Excepciones, Excepciones personalizadas
//Error con la fecha de entrega al mostrar la lista de pedidos. La muestra como un conjunto de números y signos

package DataCoders.vista;//

import DataCoders.controlador.Controlador;
import DataCoders.dao.ArticuloDAO;
import DataCoders.dao.ClienteDAO;
import DataCoders.dao.PedidoDAO;
import DataCoders.dao.mysql.MySQLArticuloDAO;
import DataCoders.dao.mysql.MySQLClienteDAO;
import DataCoders.dao.mysql.MySQLPedidoDAO;
import DataCoders.excepciones.ArticuloNoDisponibleException;
import DataCoders.excepciones.ClienteNoEncontradoException;
import DataCoders.excepciones.PedidoNoCancelableException;
import DataCoders.modelo.*;
import DataCoders.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuVista {
    // === Almacén en memoria ===
    private final ArrayList<Articulo> articulos = new ArrayList<>();
    private final ArrayList<Cliente> clientes = new ArrayList<>();
    private final ArrayList<Pedido> pedidos = new ArrayList<>();

    public static void main(String[] args) {
        Datos datos = new Datos();                  // Modelo (almacenamiento de listas)
        Controlador ctrl = new Controlador(datos);  // Puente entre Vista y Modelo
        new MenuVista().inicio(ctrl);               // Ejecución del menú
    }

    void inicio(Controlador ctrl) {
           try (Connection conn = DBConnection.getConnection()) {
                System.out.println(" Conexión exitosa a la base de datos!");
            } catch (SQLException e) {
                System.out.println(" Error de conexión:");
                e.printStackTrace();
            }



        Scanner sc = new Scanner(System.in);
        int opcion;
        do {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Añadir Artículo");
            System.out.println("2. Mostrar Artículos");
            System.out.println("3. Añadir Cliente");
            System.out.println("4. Mostrar Clientes");
            System.out.println("5. Mostrar Clientes Estándar");
            System.out.println("6. Mostrar Clientes Premium");
            System.out.println("7. Añadir Pedido");
            System.out.println("8. Eliminar Pedido");
            System.out.println("9. Mostrar Pedidos pendientes");
            System.out.println("10. Mostrar Pedidos enviados");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            opcion = sc.nextInt();
            sc.nextLine(); // limpiar el buffer
        try {
            switch (opcion) {
                case 1:
                    anadirArticulo(sc);
                    break;
                case 2:
                    mostrarArticulos();
                    break;
                case 3:
                    anadirCliente(sc);
                    break;
                case 4:
                    mostrarClientes();
                    break;
                case 5:
                    mostrarClientesEstandar();
                    break;
                case 6:
                    mostrarClientesPremium();
                    break;
                case 7:
                    anadirPedido(sc);
                    break;
                case 8:
                    eliminarPedido(sc);
                    break;
                case 9:
                    mostrarPedidosPendientes(sc);
                    break;
                case 10:
                    mostrarPedidosEnviados(sc);
                    break;
                case 0:
                    System.out.println("Programa finalizado.");
                    break;
                default:
                    System.out.println("Opción no válida. Intenta de nuevo.");
            }
        }   catch (ClienteNoEncontradoException | ArticuloNoDisponibleException | PedidoNoCancelableException e){
            System.out.println("!" + e.getMessage());
        }   catch (Exception e){
            System.out.println("Error inesperado: " + e.getMessage());
        }
        } while (opcion != 0);

        sc.close();
    }

    private void anadirArticulo(Scanner sc) {

        ArticuloDAO dao = new MySQLArticuloDAO();

            System.out.println("\n--- Añadir Artículo ---");

            System.out.print("Código: ");
            String codigo = sc.nextLine();

            System.out.print("Descripción: ");
            String descripcion = sc.nextLine();

            System.out.print("Precio de venta (€): ");
            double precioVenta = sc.nextDouble();

            System.out.print("Gasto de envío (€): ");
            double gastoEnvio = sc.nextDouble();

            System.out.print("Tiempo de preparación (minutos): ");
            int tiempoPrepMin = sc.nextInt();
            sc.nextLine(); // limpiar el buffer

           // Articulo nuevo = new Articulo(codigo, descripcion, precioVenta, gastoEnvio, tiempoPrepMin);
            //articulos.add(nuevo);

            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false); // Iniciar transacción

                try (CallableStatement cs = conn.prepareCall("{CALL agregararticulo(?, ?, ?, ? ,?)}")) {
                    List<Articulo> articulos = dao.obtenerTodos();
                    cs.setString(1, codigo);      // 1. Email del cliente
                    cs.setString(2, descripcion);    // 2. Código del artículo
                    cs.setDouble(3, precioVenta);                   // 3. Cantidad
                    cs.setDouble(4, gastoEnvio);         // 4. Fecha entrega (formato yyyy-MM-dd HH:mm:ss)
                    cs.setDouble(5, tiempoPrepMin);

                    cs.executeUpdate();
                    conn.commit();
                    System.out.println("articulo creado correctamente.");


                } catch (SQLException e) {
                    conn.rollback(); // Revertir si falla
                    System.err.println("Error al crear el articulo: " + e.getMessage());
                    e.printStackTrace();
                }

            } catch (SQLException e) {
                System.err.println("Error al conectar con la BD: " + e.getMessage());
                e.printStackTrace();
            }
    }

    private void mostrarArticulos() {
        ArticuloDAO dao = new MySQLArticuloDAO();
        try {
        System.out.println("\n--- Lista de Artículos ---");
            List<Articulo> articulos = dao.obtenerTodos();
            for (Articulo art : articulos) {
                System.out.println(
                        art.getCodigo() + " | " +
                                art.getDescripcion() + " | " +
                                art.getPrecioVenta() + " € | envío " +
                                art.getGastoEnvio() + " € | prep: " +
                                art.getTiempoPrepMin() + " min"
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void anadirCliente(Scanner sc) {
        System.out.println("\n--- Añadir Cliente ---");

        ClienteDAO dao = new MySQLClienteDAO();


        System.out.print("Nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Domicilio: ");
        String domicilio = sc.nextLine();

        System.out.print("NIF: ");
        String nif = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Tipo de cliente (1 = Estándar, 2 = Premium): ");
        int tipo = sc.nextInt();
        sc.nextLine(); // limpiar buffer

        // Convertir a string según el tipo
        String tipoStr = (tipo == 2) ? "Premium" : "Estándar";



        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Iniciar transacción

            try (CallableStatement cs = conn.prepareCall("{CALL agregarCliente(?, ?, ?, ? ,?)}")) {
                List<Cliente> articulos = dao.obtenerTodos();
                cs.setString(1, nombre);      // 1. Email del cliente
                cs.setString(2, domicilio);    // 2. Código del artículo
                cs.setString(3, nif);                   // 3. Cantidad
                cs.setString(4, email);         // 4. Fecha entrega (formato yyyy-MM-dd HH:mm:ss)
                cs.setString(5, tipoStr);

                cs.executeUpdate();
                conn.commit();
                System.out.println("articulo creado correctamente.");


            } catch (SQLException e) {
                conn.rollback(); // Revertir si falla
                System.err.println("Error al crear el cliente: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println("Error al conectar con la BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarClientes() {

        System.out.println("\n--- Lista de Clientes ---");

        ClienteDAO dao = new MySQLClienteDAO();

        try {
            List<Cliente> clientes = dao.obtenerTodos();

                System.out.println("=== CLIENTES EN BBDD ===");
                for (Cliente c : clientes) {
                    System.out.println(c.getNombre() + " - " + c.getEmail() + " _ " + c.getTipo());
                }
        } catch (SQLException e){
                e.printStackTrace();
                }
    }

    private void mostrarClientesEstandar() {
        System.out.println("\n--- Clientes Estándar ---");
        ClienteDAO dao = new MySQLClienteDAO();
        try {
            List<Cliente> clientes = dao.obtenerEstandar();
            for (Cliente c : clientes) {
                if (c instanceof ClienteEstandar) {
                    System.out.println(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void mostrarClientesPremium() {
        System.out.println("\n--- Clientes Premium ---");
        ClienteDAO dao = new MySQLClienteDAO();
        try {
            List<Cliente> clientes = dao.obtenerPremium();

            System.out.println("=== CLIENTES EN BBDD ===");

            for (Cliente c : clientes) {
                System.out.println(c.getNombre() + " - " + c.getEmail() + " _ " + c.getTipo());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void anadirPedido(Scanner sc)
            throws ClienteNoEncontradoException, ArticuloNoDisponibleException {
        ClienteDAO dao = new MySQLClienteDAO();
        //declaramos articulo para evitar eror resolve
        Articulo articulo = null;
        Cliente cliente = null;
        PedidoDAO dao3 = new MySQLPedidoDAO();
        System.out.println("\n--- Añadir Pedido ---");

        //busca por email
        System.out.print("Email del cliente: ");
        String email = sc.nextLine();

        try {
            cliente = dao.buscarPorEmail(email);

            if (cliente != null) {
                System.out.println("\nCliente encontrado:");
                System.out.println("Nombre: " + cliente.getNombre());
                System.out.println("Email: " + cliente.getEmail());
                System.out.println("Tipo: " + cliente.getTipo());
            } else {
                System.out.println(" No existe ningún cliente con ese email.");
            }

        } catch (SQLException e) {
            System.err.println(" Error al buscar el cliente: " + e.getMessage());
        }

        // Cliente


       /* Cliente cliente = buscarPorCampo(clientes, Cliente::getEmail, email);
        if (cliente == null) {
            System.out.println("No existe el cliente con ese email.");
            System.out.print("¿Deseas añadirlo ahora? (s/n): ");
            String respuesta = sc.nextLine();
            if (respuesta.equalsIgnoreCase("s")) {
                anadirCliente(sc);
                cliente = buscarPorCampo(clientes, Cliente::getEmail, email);
                if (cliente == null) {
                    throw new ClienteNoEncontradoException("No se pudo crear/encontrar el cliente: " + email);
                }
            } else {
                throw new ClienteNoEncontradoException("Pedido cancelado: cliente no existe.");
            }
        }
*/
        //imprimimos articulos para elejir un codigo
        mostrarArticulos();

        // Buscar artículo por código
        ArticuloDAO dao1 = new MySQLArticuloDAO();
        System.out.print("Código del artículo a comprar: ");
        String codigo = sc.nextLine().trim();
        try {
            articulo = dao1.buscarPorCodigo(codigo);

            if (articulo != null) {
                System.out.print("articulo elejido: ");
                    System.out.println(
                            articulo.getCodigo() + " | " +
                                    articulo.getDescripcion() + " | " +
                                    articulo.getPrecioVenta() + " € | envío " +
                                    articulo.getGastoEnvio() + " € | prep: " +
                                    articulo.getTiempoPrepMin() + " min"
                    );

            } else {
                System.out.println("⚠️ No existe ningún articulo con ese codigo.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar el articulo: " + e.getMessage());
        }

        /*Articulo articulo = buscarPorCampo(articulos, Articulo::getCodigo, codigo);
        if (articulo == null) {
            throw new ArticuloNoDisponibleException(
                    "El artículo con código '" + codigo + "' no está disponible o no existe."
            );
        }
*/
        // Cantidad
        System.out.print("Cantidad: ");
        int cantidad = sc.nextInt();
        sc.nextLine(); // limpiar buffer


       // Para simplificar, fecha de entrega = ahora + tiempo preparación
        LocalDateTime fechaEntrega = LocalDateTime.now().plusMinutes(articulo.getTiempoPrepMin());

        // Formatear fecha para MySQL
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaEntregaStr = fechaEntrega.format(formatter);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Iniciar transacción

            try (CallableStatement cs = conn.prepareCall("{CALL agregarPedido(?, ?, ?, ?)}")) {

                cs.setString(1, cliente.getEmail());      // 1. Email del cliente
                cs.setString(2, articulo.getCodigo());    // 2. Código del artículo
                cs.setInt(3, cantidad);                   // 3. Cantidad
                cs.setString(4, fechaEntregaStr);         // 4. Fecha entrega (formato yyyy-MM-dd HH:mm:ss)

                cs.executeUpdate();
                conn.commit();
                System.out.println("Pedido añadido correctamente.");


            } catch (SQLException e) {
                conn.rollback(); // Revertir si falla
                System.err.println("Error al crear el pedido: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }

        //Pedido nuevo = new Pedido(numeroPedido, cliente, articulo, cantidad,
               // articulo.getPrecioVenta(), fechaEntrega);

       // pedidos.add(nuevo);
        /*try {
            dao3.insertar(nuevo);
            System.out.println("Pedido añadido correctamente");
        } catch (SQLException e) {
            System.err.println(" Error al insertar el pedido: " + e.getMessage());
        }*/

    }




    private void eliminarPedido(Scanner sc) throws PedidoNoCancelableException {
        System.out.println("\n--- Eliminar Pedido ---");
        System.out.print("Número de pedido: ");
        String numero = sc.nextLine().trim();

        PedidoDAO dao = new MySQLPedidoDAO();

        try {
            // 1) Buscar el pedido en la BBDD
            Pedido encontrado = dao.buscarPorNumero(numero);

            if (encontrado == null) {
                System.out.println("No se ha encontrado ningún pedido con ese número.");
                return;
            }

            // 2) Comprobar si ya está enviado
            if (encontrado.esEnviado()) {
                throw new PedidoNoCancelableException(
                        "No se puede eliminar: el pedido ya está enviado."
                );
            }

            // 3) Eliminar de la BBDD
            dao.eliminar(numero);
            System.out.println("Pedido eliminado correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar el pedido: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarPedidosPendientes(Scanner sc) {
        System.out.println("\n--- Pedidos pendientes ---");
        System.out.print("¿Deseas filtrar por cliente? (s/n): ");
        String resp = sc.nextLine().trim();

        String emailFiltro = null;
        if (resp.equalsIgnoreCase("s")) {
            System.out.print("Introduce el email del cliente: ");
            emailFiltro = sc.nextLine().trim();
        }

        PedidoDAO dao = new MySQLPedidoDAO();

        try {
            List<Pedido> pedidosBD = dao.obtenerTodos();

            boolean hayResultados = false;

            for (Pedido p : pedidosBD) {
                boolean coincideCliente = (emailFiltro == null) ||
                        p.getCliente().getEmail().equalsIgnoreCase(emailFiltro);

                if (!p.esEnviado() && coincideCliente) {
                    System.out.println(p);   // usa toString() de Pedido
                    hayResultados = true;
                }
            }

            if (!hayResultados) {
                System.out.println("No hay pedidos pendientes con ese criterio.");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los pedidos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarPedidosEnviados(Scanner sc) {
        System.out.println("\n--- Pedidos enviados ---");
        System.out.print("¿Deseas filtrar por cliente? (s/n): ");
        String resp = sc.nextLine().trim();

        String emailFiltro = null;
        if (resp.equalsIgnoreCase("s")) {
            System.out.print("Introduce el email del cliente: ");
            emailFiltro = sc.nextLine().trim();
        }

        PedidoDAO dao = new MySQLPedidoDAO();

        try {
            List<Pedido> pedidosBD = dao.obtenerTodos();

            boolean hayResultados = false;

            for (Pedido p : pedidosBD) {
                boolean coincideCliente = (emailFiltro == null) ||
                        p.getCliente().getEmail().equalsIgnoreCase(emailFiltro);

                if (p.esEnviado() && coincideCliente) {
                    System.out.println(p);
                    hayResultados = true;
                }
            }

            if (!hayResultados) {
                System.out.println("No hay pedidos enviados con ese criterio.");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los pedidos: " + e.getMessage());
            e.printStackTrace();
        }
    }

}