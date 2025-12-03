package DataCoders.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("Estandar")
public class ClienteEstandar extends ClienteConPedidos {

    public ClienteEstandar() {
        super();
        setCuota(0.0); // cuota estándar
    }

    public ClienteEstandar(String nif, String nombre, String domicilio, String email) {
        super(nif, nombre, domicilio, email);
        setCuota(0.0);
    }

    @Override
    public double getDescuentoEnvio() {
        return 0.0; // sin descuento
    }

    @Override
    public String getTipo() {
        return "Estandar";
    }
}
