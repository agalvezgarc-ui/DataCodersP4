package DataCoders.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("Premium")
public class ClientePremium extends ClienteConPedidos {

    public ClientePremium() {
        super();
        setCuota(50.0); // cuota premium
    }

    public ClientePremium(String nif, String nombre, String domicilio, String email) {
        super(nif, nombre, domicilio, email);
        setCuota(50.0);
    }

    @Override
    public double getDescuentoEnvio() {
        return 0.20; // 20% de descuento en gastos de envío
    }

    @Override
    public String getTipo() {
        return "Premium";
    }
}
