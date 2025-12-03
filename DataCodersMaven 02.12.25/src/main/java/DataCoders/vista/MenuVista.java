////Observaciones: Es posible que ==Almacen en memoria== deba distribuirse entre las clases.
//No realizado: gestión de Excepciones, Excepciones personalizadas
//Error con la fecha de entrega al mostrar la lista de pedidos. La muestra como un conjunto de números y signos

package DataCoders.vista;
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
import jakarta.persistence.PersistenceException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/*public class MenuVista {
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
*/
public class MenuVista {

        public static void main(String[] args) {
            // Silenciar logs de Hibernate
            Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
            Logger.getLogger("").setLevel(Level.SEVERE);

            Datos datos = new Datos(); // Modelo
            Controlador ctrl = new Controlador(datos); // Controlador
            new MenuVista().inicio(ctrl); // Vista
        }

        void inicio(Controlador ctrl) {
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
                sc.nextLine(); // limpiar buffer

                switch (opcion) {
                    case 1 -> anadirArticulo(ctrl, sc);
                    case 2 -> mostrarArticulos(ctrl);
                    case 3 -> anadirCliente(ctrl, sc);
                    case 4 -> mostrarClientes(ctrl);
                    case 5 -> mostrarClientesEstandar(ctrl);
                    case 6 -> mostrarClientesPremium(ctrl);
                    case 7 -> anadirPedido(sc, ctrl);
                    case 8 -> eliminarPedido(sc, ctrl);
                    case 9 -> mostrarPedidosPendientes(sc,ctrl);
                    case 10 -> mostrarPedidosEnviados(sc,ctrl);
                    case 0 -> System.out.println("Programa finalizado.");
                    default -> System.out.println("Opción no válida.");
                }

            } while (opcion != 0);

            sc.close();
        }


    private void anadirArticulo(Controlador ctrl, Scanner sc) {
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
        sc.nextLine(); // limpiar buffer

        // Llamamos al controlador con los datos
        ctrl.anadirArticulo(codigo, descripcion, precioVenta, gastoEnvio, tiempoPrepMin);
    }

    private void mostrarArticulos(Controlador ctrl) {
        try {
            System.out.println("\n--- Lista de Artículos ---");
            List<Articulo> articulos = ctrl.obtenerTodosArticulos();
            for (Articulo art : articulos) {
                System.out.println(
                        art.getCodigo() + " | " +
                                art.getDescripcion() + " | " +
                                art.getPrecioVenta() + " € | envío " +
                                art.getGastoEnvio() + " € | prep: " +
                                art.getTiempoPrepMin() + " min"
                );
            }
        } catch (PersistenceException e) {
            System.err.println("Error al obtener los artículos");
            System.err.println("Detalles: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado.");
            System.err.println("Detalles: " + e.getMessage());
        }
    }


    private void anadirCliente(Controlador ctrl,Scanner sc) {
        System.out.println("\n--- Añadir Cliente ---");

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

        // >>> aquí calculamos la cuota según el tipo seleccionado
        int cuota = (tipo == 2) ? 20 : 0;   // 2 = Premium - 20, 1 = Estándar - 0

        try {
            ctrl.anadirCliente(nombre, domicilio, nif, email, tipo, cuota);
            System.out.println("Cliente creado correctamente.");

        } catch (jakarta.persistence.PersistenceException e) {
            System.err.println("Error al guardar el cliente usando Hibernate.");
            System.err.println("Detalles: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("Error inesperado.");
            System.err.println("Detalles: " + e.getMessage());
        }
    }
    private void mostrarClientes(Controlador ctrl) {
        System.out.println("\n--- Lista de Clientes ---");

        try {
            List<Cliente> clientes = ctrl.obtenerTodosClientes();

            System.out.println("=== CLIENTES EN BBDD ===");
            for (Cliente c : clientes) {
                System.out.println(c.getNombre() + " - " + c.getEmail() + " _ " + c.getTipo());
            }
        } catch (PersistenceException e) {
            System.err.println("Error al obtener los clientes con Hibernate.");
            System.err.println("Detalles: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado.");
            System.err.println("Detalles: " + e.getMessage());
        }
    }

    private void mostrarClientesEstandar(Controlador ctrl) {
        System.out.println("\n--- Clientes Estándar ---");

        try {
            List<Cliente> clientes = ctrl.obtenerClientesEstandar();

            System.out.println("=== CLIENTES ESTÁNDAR EN BBDD ===");
            for (Cliente c : clientes) {
                System.out.println(c.getNombre() + " - " + c.getEmail() + " - " + c.getTipo());
            }

        } catch (PersistenceException e) {
            System.err.println("Error al obtener los clientes estándar con Hibernate.");
            System.err.println("Detalles: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    private void mostrarClientesPremium(Controlador ctrl) {
        System.out.println("\n--- Clientes Premium ---");

        try {
            List<Cliente> clientes = ctrl.obtenerClientesPremium();

            System.out.println("=== CLIENTES PREMIUM EN BBDD ===");

            for (Cliente c : clientes) {
                System.out.println(
                        c.getNombre() + " - " +
                                c.getEmail() + " _ " +
                                c.getTipo()
                );
            }
        } catch (PersistenceException e) {
            System.err.println(" Error al obtener los clientes premium con Hibernate.");
            System.err.println("Detalles: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }


    private void anadirPedido(Scanner sc, Controlador ctrl) {
        System.out.println("\n--- Añadir Pedido ---");

        System.out.print("Email del cliente: ");
        String email = sc.nextLine();

        System.out.print("Código del artículo: ");
        String codigo = sc.nextLine();

        System.out.print("Cantidad: ");
        int cantidad = sc.nextInt();
        sc.nextLine(); // limpiar buffer

        System.out.print("Tiempo de preparación del artículo (minutos): ");
        int tiempoPreparacion = sc.nextInt();
        sc.nextLine(); // limpiar buffer

        System.out.print("Tiempo de envío (minutos): ");
        int tiempoEnvio = sc.nextInt();
        sc.nextLine(); // limpiar buffer

        try {
            ctrl.anadirPedido(email, codigo, cantidad, tiempoPreparacion, tiempoEnvio);
            System.out.println("Pedido añadido correctamente.");
        } catch (ClienteNoEncontradoException | ArticuloNoDisponibleException e) {
            System.err.println("Cliente no encontrado " + e.getMessage());

        } catch (PersistenceException e) {
            System.err.println("Error en la base de datos");
            System.err.println("Detalles: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }




    private void eliminarPedido(Scanner sc, Controlador ctrl) {
        System.out.println("\n--- Eliminar Pedido ---");

        System.out.print("Número de pedido: ");
        String numero = sc.nextLine().trim();

        try {
            ctrl.eliminarPedido(numero);
            System.out.println("✅ Pedido eliminado correctamente.");
        } catch (PedidoNoCancelableException e) {
            System.err.println("Pedido no cancelable " + e.getMessage());

        } catch (PersistenceException e) {
            System.err.println("Error en la base de datosc.");
            System.err.println("Detalles: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }




    private void mostrarPedidosPendientes(Scanner sc, Controlador ctrl) {
        System.out.println("\n--- Pedidos pendientes ---");
        System.out.print("¿Deseas filtrar por cliente? (s/n): ");
        String resp = sc.nextLine().trim();

        String emailFiltro = null;
        if (resp.equalsIgnoreCase("s")) {
            System.out.print("Introduce el email del cliente: ");
            emailFiltro = sc.nextLine().trim();
        }

        try {
            List<Pedido> pedidos = ctrl.obtenerPedidosPendientes(emailFiltro);

            if (pedidos.isEmpty()) {
                System.out.println("No hay pedidos pendientes con ese criterio.");
            } else {
                pedidos.forEach(System.out::println); // usa toString() de Pedido
            }

        } catch (PersistenceException e) {
            System.err.println("Error al obtener los pedidos con Hibernate.");
            System.err.println("Detalles: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    private void mostrarPedidosEnviados(Scanner sc, Controlador ctrl) {
        System.out.println("\n--- Pedidos enviados ---");
        System.out.print("¿Deseas filtrar por cliente? (s/n): ");
        String resp = sc.nextLine().trim();

        String emailFiltro = null;
        if (resp.equalsIgnoreCase("s")) {
            System.out.print("Introduce el email del cliente: ");
            emailFiltro = sc.nextLine().trim();
        }

        try {
            List<Pedido> pedidos = ctrl.obtenerPedidosEnviados(emailFiltro);

            if (pedidos.isEmpty()) {
                System.out.println("No hay pedidos enviados con ese criterio.");
            } else {
                pedidos.forEach(System.out::println); // usa toString() de Pedido
            }

        } catch (PersistenceException e) {
            System.err.println("Error al obtener los pedidos enviados c.");
            System.err.println("Detalles: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

}