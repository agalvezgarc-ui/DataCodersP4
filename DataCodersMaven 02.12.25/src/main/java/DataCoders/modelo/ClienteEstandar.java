package DataCoders.modelo;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ESTANDAR")
public class ClienteEstandar extends Cliente {
    public ClienteEstandar() {}
    public ClienteEstandar(String nif, String nombre,
                           String domicilio, String email) {
        super(nif, nombre, domicilio, email);
        setCuota(0.0);
    }

    @Override
    public double getDescuentoEnvio() { return 0.0; }

    @Override
    public String getTipo() { return "ESTANDAR"; }
}
