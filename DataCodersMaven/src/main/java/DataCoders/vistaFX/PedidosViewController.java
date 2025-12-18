package DataCoders.vistaFX;

import DataCoders.controlador.Controlador;
import DataCoders.excepciones.ArticuloNoDisponibleException;
import DataCoders.excepciones.ClienteNoEncontradoException;
import DataCoders.excepciones.PedidoNoCancelableException;
import DataCoders.modelo.Pedido;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PedidosViewController implements NeedsAppController {

    @FXML private TableView<Pedido> tablaPedidos;
    @FXML private TableColumn<Pedido, String> colNumero;
    @FXML private TableColumn<Pedido, String> colEmail;
    @FXML private TableColumn<Pedido, String> colCodigoArticulo;
    @FXML private TableColumn<Pedido, String> colCantidad;
    @FXML private TableColumn<Pedido, String> colPrecioUnit;
    @FXML private TableColumn<Pedido, String> colFechaEntrega;
    @FXML private TableColumn<Pedido, String> colEnviado;

    @FXML private TextField tfEmailFiltro;

    private Controlador appController;

    private enum Vista { PENDIENTES, ENVIADOS }
    private Vista vistaActual = Vista.PENDIENTES;

    @Override
    public void setAppController(Controlador controlador) {
        this.appController = controlador;
    }

    @FXML
    public void initialize() {
        colNumero.setCellValueFactory(p ->
                new SimpleStringProperty(String.valueOf(p.getValue().getNumeroPedido()))
        );

        colEmail.setCellValueFactory(p ->
                new SimpleStringProperty(
                        p.getValue().getCliente() != null ? p.getValue().getCliente().getEmail() : ""
                )
        );

        colCodigoArticulo.setCellValueFactory(p ->
                new SimpleStringProperty(
                        p.getValue().getArticulo() != null ? p.getValue().getArticulo().getCodigo() : ""
                )
        );

        colCantidad.setCellValueFactory(p ->
                new SimpleStringProperty(String.valueOf(p.getValue().getCantidad()))
        );

        colPrecioUnit.setCellValueFactory(p ->
                new SimpleStringProperty(String.valueOf(p.getValue().getPrecioUnitario()))
        );

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        colFechaEntrega.setCellValueFactory(p ->
                new SimpleStringProperty(
                        p.getValue().getFechaEntrega() != null
                                ? p.getValue().getFechaEntrega().format(fmt)
                                : ""
                )
        );

        colEnviado.setCellValueFactory(p ->
                new SimpleStringProperty(String.valueOf(p.getValue().esEnviado()))
        );
    }

    // ----------------- Navegación -----------------

    @FXML
    private void onVolverMenu() {
        SceneManager.show("main");
    }

    // ----------------- Listados -----------------

    @FXML
    private void onPendientes() {
        vistaActual = Vista.PENDIENTES;
        refrescarTabla();
    }

    @FXML
    private void onEnviados() {
        vistaActual = Vista.ENVIADOS;
        refrescarTabla();
    }

    @FXML
    private void onAplicarFiltro() {
        refrescarTabla();
    }

    @FXML
    private void onQuitarFiltro() {
        tfEmailFiltro.clear();
        refrescarTabla();
    }

    private void refrescarTabla() {
        String email = tfEmailFiltro.getText();
        if (email != null) email = email.trim();
        if (email != null && email.isEmpty()) email = null;

        List<Pedido> lista = (vistaActual == Vista.PENDIENTES)
                ? appController.obtenerPedidosPendientes(email)
                : appController.obtenerPedidosEnviados(email);

        tablaPedidos.setItems(FXCollections.observableArrayList(lista));
    }

    // ----------------- Añadir pedido -----------------

    @FXML
    private void onAnadirNuevo() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Añadir pedido");

        ButtonType guardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardar, ButtonType.CANCEL);

        TextField tfEmail = new TextField();
        TextField tfCodigo = new TextField();
        TextField tfCantidad = new TextField();
        TextField tfTiempoPrep = new TextField();
        TextField tfTiempoEnvio = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.addRow(0, new Label("Email cliente:"), tfEmail);
        grid.addRow(1, new Label("Código artículo:"), tfCodigo);
        grid.addRow(2, new Label("Cantidad:"), tfCantidad);
        grid.addRow(3, new Label("Tiempo prep (min):"), tfTiempoPrep);
        grid.addRow(4, new Label("Tiempo envío (min):"), tfTiempoEnvio);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(bt -> {
            if (bt == guardar) {

                String email = tfEmail.getText().trim();
                String cod = tfCodigo.getText().trim();
                String cantTxt = tfCantidad.getText().trim();
                String prepTxt = tfTiempoPrep.getText().trim();
                String envioTxt = tfTiempoEnvio.getText().trim();

                if (email.isEmpty() || cod.isEmpty() || cantTxt.isEmpty() || prepTxt.isEmpty() || envioTxt.isEmpty()) {
                    alerta(Alert.AlertType.WARNING, "Faltan datos", "Rellena todos los campos.");
                    return null;
                }

                int cantidad, tPrep, tEnvio;
                try {
                    cantidad = Integer.parseInt(cantTxt);
                    tPrep = Integer.parseInt(prepTxt);
                    tEnvio = Integer.parseInt(envioTxt);
                } catch (NumberFormatException e) {
                    alerta(Alert.AlertType.WARNING, "Formato incorrecto", "Cantidad y tiempos deben ser enteros.");
                    return null;
                }

                try {
                    appController.anadirPedido(email, cod, cantidad, tPrep, tEnvio);

                    // Por defecto volvemos a pendientes y refrescamos
                    vistaActual = Vista.PENDIENTES;
                    refrescarTabla();

                } catch (ClienteNoEncontradoException | ArticuloNoDisponibleException ex) {
                    alerta(Alert.AlertType.ERROR, "No se pudo crear el pedido", ex.getMessage());
                } catch (Exception ex) {
                    alerta(Alert.AlertType.ERROR, "Error inesperado", ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // ----------------- Eliminar pedido -----------------

    @FXML
    private void onEliminarPedido() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Eliminar pedido");
        dialog.setHeaderText(null);
        dialog.setContentText("Introduce el número de pedido:");

        dialog.showAndWait().ifPresent(numero -> {
            String n = numero.trim();
            if (n.isEmpty()) return;

            try {
                appController.eliminarPedido(n);
                refrescarTabla();
            } catch (PedidoNoCancelableException ex) {
                alerta(Alert.AlertType.ERROR, "No se pudo eliminar", ex.getMessage());
            } catch (Exception ex) {
                alerta(Alert.AlertType.ERROR, "Error inesperado", ex.getMessage());
            }
        });
    }

    // ----------------- Util -----------------

    private void alerta(Alert.AlertType type, String titulo, String msg) {
        Alert a = new Alert(type);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}