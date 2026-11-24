/*
 * Autor original: Cristian A
 * Completado como parte del Entregable 7.
 */
package aplicaciongimnasio.modelo;

/**
 * Membresia VIP: incluye nutricionista y entrenador personal asignado.
 * Adicionalmente al precio base y a las clases, calcula el costo de las
 * sesiones con el entrenador (4 sesiones al mes por defecto).
 */
public class VIP extends Membresia {
    private static final long serialVersionUID = 1L;

    private double costoEntrenadorPorSesion;
    private double costoClasesIncluidas;
    private int sesionesEntrenadorMes = 4;
    private double costoNutricionista = 0.0;

    public VIP() {
        super();
        setTipo("VIP");
    }

    public VIP(double costoEntrenadorPorSesion, String tipo, String duracion, double precioBase,
               String fechaInicio, String fechaVencimiento, String estado,
               double porcentajeDescuentoAplicable) {
        super(tipo, duracion, precioBase, fechaInicio, fechaVencimiento, estado,
              porcentajeDescuentoAplicable);
        this.costoEntrenadorPorSesion = costoEntrenadorPorSesion;
    }

    /** VIP tambien incluye las clases grupales (al igual que Premium). */
    public void setCostoClasesIncluidas(double costoClasesIncluidas) {
        this.costoClasesIncluidas = Math.max(0.0, costoClasesIncluidas);
    }

    public double getCostoClasesIncluidas() { return costoClasesIncluidas; }

    public double getCostoEntrenadorPorSesion() { return costoEntrenadorPorSesion; }

    public void setCostoEntrenadorPorSesion(double costoEntrenadorPorSesion) {
        this.costoEntrenadorPorSesion = costoEntrenadorPorSesion;
    }

    public int getSesionesEntrenadorMes() { return sesionesEntrenadorMes; }

    public void setSesionesEntrenadorMes(int sesionesEntrenadorMes) {
        this.sesionesEntrenadorMes = Math.max(0, sesionesEntrenadorMes);
    }

    public double getCostoNutricionista() { return costoNutricionista; }

    public void setCostoNutricionista(double costoNutricionista) {
        this.costoNutricionista = Math.max(0.0, costoNutricionista);
    }

    /** Precio base + clases + sesiones de entrenador + acompanamiento nutricional. */
    @Override
    public double calcularCostoMensual() {
        return round(getPrecioBase()
                + costoClasesIncluidas
                + (costoEntrenadorPorSesion * sesionesEntrenadorMes)
                + costoNutricionista);
    }

    @Override
    public double getPorcentajeDescuentoReferido() {
        return 10.0;
    }
}
