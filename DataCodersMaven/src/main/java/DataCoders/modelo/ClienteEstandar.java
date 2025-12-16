package DataCoders.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("ESTANDAR")
public class ClienteEstandar extends ClienteConPedidos {

    // =========================
    // Constructores
    // =========================
    public ClienteEstandar() {
        super();
        setCuota(0.0); // cuota estándar
    }

    public ClienteEstandar(String nif, String nombre, String domicilio, String email) {
        super(nif, nombre, domicilio, email);
        setCuota(0.0); // cuota estándar
    }

    // =========================
    // Métodos sobrescritos
    // =========================
    @Override
    public double getDescuentoEnvio() {
        return 0.0; // los clientes estándar no tienen descuento
    }

    @Override
    public String getTipo() {
        return "Estandar";
    }
}