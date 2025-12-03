package DataCoders.modelo;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PREMIUM")
public class ClientePremium extends Cliente {
    public ClientePremium() {}
    public ClientePremium(String nif, String nombre,
                          String domicilio, String email) {
        super(nif, nombre, domicilio, email);
        setCuota(0.2);
    }

    @Override
    public double getDescuentoEnvio() { return 0.2; }

    @Override
    public String getTipo() { return "PREMIUM"; }
}