package DataCoders.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "tipo",
        discriminatorType = DiscriminatorType.STRING,
        length = 20
)
public abstract class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")   // PK autoincremental en la BD
    private Integer idCliente;

    @Column(name = "nif", unique = true)
    private String nif;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "domicilio")
    private String domicilio;

    @Column(name = "email")
    private String email;

    @Column(name = "cuota")
    private double cuota;

    // Constructor vacío requerido por JPA
    public Cliente() {
    }

    // Constructor de conveniencia (no incluimos idCliente porque lo genera la BD)
    public Cliente(String nif, String nombre, String domicilio, String email) {
        this.nif = nif;
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.email = email;
    }

    // Getter solo lectura para idCliente (lo asigna JPA)
    public Integer getIdCliente() {
        return idCliente;
    }

    // Getters y setters
    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Getter y setter añadidos para exponer la cuota como atributo persistente.
    // Antes el tipo de cliente (estándar/premium) se gestionaba solo con lógica interna,
    // pero al usar JPA necesitamos poder leer y modificar la cuota desde otros
    // componentes (DAO/servicio) y mantenerla sincronizada con la columna 'cuota' de la BD.
    public double getCuota() { return cuota; }
    public void setCuota(double cuota) { this.cuota = cuota; }

    @Override
    public String toString() {
        return "Cliente{" +
                "nif='" + nif + '\'' +
                ", nombre='" + nombre + '\'' +
                ", domicilio='" + domicilio + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public abstract double getDescuentoEnvio();// Este método no tiene cuerpo aquí porque cada tipo de cliente (estándar o premium) tendrá su propio descuento.
    public abstract String getTipo();

}
