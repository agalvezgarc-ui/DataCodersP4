package DataCoders.vistaFX;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

import DataCoders.modelo.Datos;
import DataCoders.controlador.Controlador;

public class SceneManager {

    private static Stage stage;
    private static Controlador appController;

    private static final Map<String, String> routes = Map.of(
            "main", "/fxml/MainView.fxml",
            "clientes", "/fxml/ClientesView.fxml",
            "articulos", "/fxml/ArticulosView.fxml",
            "pedidos", "/fxml/PedidosView.fxml"
    );

    public static void init(Stage primaryStage, Controlador controlador) {
        stage = primaryStage;
        appController = controlador;
    }

    public static void show(String route) {
        try {
            String fxml = routes.get(route);

            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxml));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof NeedsAppController needs) {
                needs.setAppController(appController);
            }

            stage.setScene(new Scene(root, 800, 600));
            stage.show();

        } catch (IOException ex) {
            throw new RuntimeException("No se pudo cargar la vista: " + route, ex);
        }
    }
}