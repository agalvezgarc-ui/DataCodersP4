package DataCoders.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "articulo")  // nombre de la tabla en la base de datos
public class Articulo {

    @Id
    @Column(name = "codigo")  // clave primaria (PK)
    private String codigo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio_venta")  // nombre exacto de la columna en la BD
    private double precioVenta;

    @Column(name = "gasto_envio")   // nombre exacto de la columna en la BD
    private double gastoEnvio;

    @Column(name = "tiempo_prep_min")  // nombre exacto de la columna en la BD
    private int tiempoPrepMin;

    // Constructor vacío requerido por JPA
    public Articulo() {
    }

    // Constructor con parámetros
    public Articulo(String codigo, String descripcion,
                    double precioVenta, double gastoEnvio,
                    int tiempoPrepMin) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.gastoEnvio = gastoEnvio;
        this.tiempoPrepMin = tiempoPrepMin;
    }

    // Getters
    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public double getPrecioVenta() { return precioVenta; }
    public double getGastoEnvio() { return gastoEnvio; }
    public int getTiempoPrepMin() { return tiempoPrepMin; }

    // Setters
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }
    public void setGastoEnvio(double gastoEnvio) { this.gastoEnvio = gastoEnvio; }
    public void setTiempoPrepMin(int tiempoPrepMin) { this.tiempoPrepMin = tiempoPrepMin; }

    @Override
    public String toString() {
        return "Artículo: " + codigo + " - " + descripcion +
                ", Precio venta: " + precioVenta +
                ", Envío: " + gastoEnvio +
                ", Preparación: " + tiempoPrepMin + " min";
    }
}