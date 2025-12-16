package DataCoders.vistaFX;

import DataCoders.controlador.Controlador;
import DataCoders.modelo.Datos;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        Datos datos = new Datos(); // Modelo
        Controlador controlador = new Controlador(datos); // Controlador
        SceneManager.init(stage, controlador);
        stage.setTitle("DataCoders - Producto 5");
        SceneManager.show("main");
    }

    public static void main(String[] args) {
        launch(args);
    }
}