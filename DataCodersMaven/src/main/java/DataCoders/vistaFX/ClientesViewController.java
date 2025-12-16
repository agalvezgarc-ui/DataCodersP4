package DataCoders.vistaFX;

import DataCoders.controlador.Controlador;
import DataCoders.modelo.Cliente;
import DataCoders.modelo.ClienteEstandar;
import DataCoders.modelo.ClientePremium;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.util.List;

public class ClientesViewController implements NeedsAppController {

    private Controlador controlador;

    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, String> colCliente;

    @FXML private TableColumn<Cliente, String> colId;
    @FXML private TableColumn<Cliente, String> colNif;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTipo;
    @FXML private TableColumn<Cliente, String> colEmail;

    @Override
    public void setAppController(Controlador controlador) {
        this.controlador = controlador;

        colId.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getId()))
        );

        colNif.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getNif())
        );

        colNombre.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getNombre())
        );

        colTipo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTipo())
        );

        colEmail.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEmail())
        );

        reload();
    }

    @FXML
    public void goBack() {
        SceneManager.show("main");
    }

    @FXML
    public void reload() {
        try {
            List<Cliente> clientes = controlador.obtenerTodosClientes();
            tblClientes.getItems().setAll(clientes);
        } catch (Exception ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("No se pudieron cargar los clientes");
            a.setContentText(ex.getMessage());
            a.showAndWait();
        }
    }

    @FXML
    public void filtrarTodos() {
        cargarLista(controlador.obtenerTodosClientes());
    }

    @FXML
    public void filtrarEstandar() {
        cargarLista(controlador.obtenerClientesEstandar());
    }

    @FXML
    public void filtrarPremium() {
        cargarLista(controlador.obtenerClientesPremium());
    }

    private void cargarLista(List<Cliente> clientes) {
        tblClientes.getItems().setAll(clientes);
    }

    @FXML
    public void anadirCliente() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Añadir cliente");
        dialog.setHeaderText(null);

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        // Formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        TextField tfNif = new TextField();
        TextField tfNombre = new TextField();
        TextField tfDomicilio = new TextField();
        TextField tfEmail = new TextField();

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Estandar", "Premium");
        cbTipo.getSelectionModel().selectFirst();

        grid.addRow(0, new Label("NIF:"), tfNif);
        grid.addRow(1, new Label("Nombre:"), tfNombre);
        grid.addRow(2, new Label("Domicilio:"), tfDomicilio);
        grid.addRow(3, new Label("Email:"), tfEmail);
        grid.addRow(4, new Label("Tipo:"), cbTipo);

        dialog.getDialogPane().setContent(grid);


        dialog.showAndWait().ifPresent(result -> {
            if (result == btnGuardar) {
                try {
                    String nif = tfNif.getText().trim();
                    String nombre = tfNombre.getText().trim();
                    String domicilio = tfDomicilio.getText().trim();
                    String email = tfEmail.getText().trim();

                    if (nif.isEmpty() || nombre.isEmpty()) {
                        Alert a = new Alert(Alert.AlertType.WARNING);
                        a.setTitle("Atención");
                        a.setHeaderText("Faltan datos");
                        a.setContentText("NIF y Nombre son obligatorios.");
                        a.showAndWait();
                        return;
                    }

// Tipo (ajusta los valores según tu proyecto)
                    int tipo = cbTipo.getValue().equalsIgnoreCase("Premium") ? 2 : 1;

// Cuota (ajusta la lógica según tu proyecto)
                    int cuota = (tipo == 2) ? 20 : 0;

                    controlador.anadirCliente(nombre, domicilio, nif, email, tipo, cuota);

                    // 3) Сообщение и обновление таблицы
                    Alert ok = new Alert(Alert.AlertType.INFORMATION);
                    ok.setTitle("OK");
                    ok.setHeaderText("Cliente añadido");
                    ok.setContentText(nombre);
                    ok.showAndWait();


                } catch (Exception ex) {
                    Alert err = new Alert(Alert.AlertType.ERROR);
                    err.setTitle("Error");
                    err.setHeaderText("No se pudo añadir el cliente");
                    err.setContentText(ex.getMessage());
                    err.showAndWait();
                }
            }
        });
    }


}