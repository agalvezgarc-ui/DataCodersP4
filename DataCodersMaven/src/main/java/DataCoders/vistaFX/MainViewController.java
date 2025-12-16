package DataCoders.vistaFX;

import javafx.event.ActionEvent;

public class MainViewController {

    public void goClientes(ActionEvent e) {
        SceneManager.show("clientes");
    }

    public void goArticulos(ActionEvent e) {
        SceneManager.show("articulos");
    }

    public void goPedidos(ActionEvent e) {
        SceneManager.show("pedidos");
    }
}
