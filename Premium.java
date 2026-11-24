/*
 * Autor original: Cristian A
 * Completado como parte del Entregable 7.
 */
package aplicaciongimnasio.modelo;

/**
 * Membresia premium: incluye clases grupales (spinning, zumba, funcional).
 * Su costo mensual es el precio base mas el costo de las clases incluidas.
 */
public class Premium extends Membresia {
    private static final long serialVersionUID = 1L;

    private double costoClasesIncluidas;

    public Premium() {
        super();
        setTipo("Premium");
    }

    public Premium(double costoClasesIncluidas, String tipo, String duracion, double precioBase,
                   String fechaInicio, String fechaVencimiento, String estado,
                   double porcentajeDescuentoAplicable) {
        super(tipo, duracion, precioBase, fechaInicio, fechaVencimiento, estado,
              porcentajeDescuentoAplicable);
        this.costoClasesIncluidas = costoClasesIncluidas;
    }

    public double getCostoClasesIncluidas() { return costoClasesIncluidas; }

    public void setCostoClasesIncluidas(double costoClasesIncluidas) {
        this.costoClasesIncluidas = costoClasesIncluidas;
    }

    /** El valor incluye el costo asociado a las clases grupales. */
    @Override
    public double calcularCostoMensual() {
        return round(getPrecioBase() + costoClasesIncluidas);
    }

    @Override
    public double getPorcentajeDescuentoReferido() {
        return 5.0;
    }
}
