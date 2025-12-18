package DataCoders.vistaFX;

import DataCoders.controlador.Controlador;
import DataCoders.modelo.Articulo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import DataCoders.vistaFX.SceneManager;


import java.util.List;

public class ArticulosViewController implements NeedsAppController {

    @FXML private TableView<Articulo> tablaArticulos;
    @FXML private TableColumn<Articulo, String> colCodigo;
    @FXML private TableColumn<Articulo, String> colDescripcion;
    @FXML private TableColumn<Articulo, String> colPrecioVenta;
    @FXML private TableColumn<Articulo, String> colGastoEnvio;
    @FXML private TableColumn<Articulo, String> colTiempoPrep;

    private Controlador appController;

    @Override
    public void setAppController(Controlador controlador) {
        this.appController = controlador;
    }

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));
        colPrecioVenta.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPrecioVenta())));
        colGastoEnvio.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getGastoEnvio())));
        colTiempoPrep.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getTiempoPrepMin())));
    }

    @FXML
    private void onMostrarLista() {
        List<Articulo> lista = appController.obtenerTodosArticulos();
        tablaArticulos.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    private void onAnadirNuevo() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Añadir artículo");

        ButtonType guardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardar, ButtonType.CANCEL);

        TextField tfCodigo = new TextField();
        TextField tfDescripcion = new TextField();
        TextField tfPrecioVenta = new TextField();
        TextField tfGastoEnvio = new TextField();
        TextField tfTiempoPrep = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.addRow(0, new Label("Código:"), tfCodigo);
        grid.addRow(1, new Label("Descripción:"), tfDescripcion);
        grid.addRow(2, new Label("Precio venta:"), tfPrecioVenta);
        grid.addRow(3, new Label("Gasto envío:"), tfGastoEnvio);
        grid.addRow(4, new Label("Tiempo prep (min):"), tfTiempoPrep);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(bt -> {
            if (bt == guardar) {
                String codigo = tfCodigo.getText().trim();
                String desc = tfDescripcion.getText().trim();
                String precioTxt = tfPrecioVenta.getText().trim();
                String gastoTxt = tfGastoEnvio.getText().trim();
                String tiempoTxt = tfTiempoPrep.getText().trim();

                if (codigo.isEmpty() || desc.isEmpty() || precioTxt.isEmpty() || gastoTxt.isEmpty() || tiempoTxt.isEmpty()) {
                    alerta(Alert.AlertType.WARNING, "Faltan datos", "Rellena todos los campos.");
                    return null;
                }

                double precio, gasto;
                int tiempo;
                try {
                    precio = Double.parseDouble(precioTxt);
                    gasto = Double.parseDouble(gastoTxt);
                    tiempo = Integer.parseInt(tiempoTxt);
                } catch (NumberFormatException e) {
                    alerta(Alert.AlertType.WARNING, "Formato incorrecto", "Precio/Gasto deben ser números y Tiempo un entero.");
                    return null;
                }

                appController.anadirArticulo(codigo, desc, precio, gasto, tiempo);

                // refrescar
                onMostrarLista();
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void alerta(Alert.AlertType type, String titulo, String msg) {
        Alert a = new Alert(type);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    @FXML
    private void onVolverMenu() {
        SceneManager.show("main");
    }
}
