/*
 * Creado para el Entregable 7: configuracion administrativa de membresias.
 */
package aplicaciongimnasio.modelo;

import java.io.Serializable;

/**
 * Parametros configurables por el administrador para cada tipo de membresia:
 * precio base, costo de clases, costo de entrenador y porcentaje de descuento
 * por referido. Centraliza los valores que antes estaban dispersos.
 */
public class ParametrosMembresia implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String tipo;
    private double precioBase;
    private double costoClases;
    private double costoEntrenadorSesion;
    private double porcentajeDescuentoReferido;

    public ParametrosMembresia(String tipo, double precioBase, double costoClases,
                               double costoEntrenadorSesion, double porcentajeDescuentoReferido) {
        this.tipo = tipo;
        this.precioBase = precioBase;
        this.costoClases = costoClases;
        this.costoEntrenadorSesion = costoEntrenadorSesion;
        this.porcentajeDescuentoReferido = porcentajeDescuentoReferido;
    }

    public String getTipo() { return tipo; }
    public double getPrecioBase() { return precioBase; }
    public double getCostoClases() { return costoClases; }
    public double getCostoEntrenadorSesion() { return costoEntrenadorSesion; }
    public double getPorcentajeDescuentoReferido() { return porcentajeDescuentoReferido; }

    public void setPrecioBase(double precioBase) { this.precioBase = Math.max(0, precioBase); }
    public void setCostoClases(double costoClases) { this.costoClases = Math.max(0, costoClases); }
    public void setCostoEntrenadorSesion(double v) { this.costoEntrenadorSesion = Math.max(0, v); }
    public void setPorcentajeDescuentoReferido(double p) {
        this.porcentajeDescuentoReferido = Math.min(100, Math.max(0, p));
    }

    /** Fabrica una membresia concreta segun el tipo configurado. */
    public Membresia crearMembresia() {
        String hoy = java.time.LocalDate.now().toString();
        String fin = java.time.LocalDate.now().plusMonths(1).toString();
        Membresia m;
        switch (tipo) {
            case "Premium":
                m = new Premium(costoClases, tipo, "Mensual", precioBase, hoy, fin,
                        Membresia.ESTADO_ACTIVA, porcentajeDescuentoReferido);
                break;
            case "VIP":
                VIP vip = new VIP(costoEntrenadorSesion, tipo, "Mensual", precioBase, hoy, fin,
                        Membresia.ESTADO_ACTIVA, porcentajeDescuentoReferido);
                vip.setCostoClasesIncluidas(costoClases);
                m = vip;
                break;
            default:
                m = new Basica(tipo, "Mensual", precioBase, hoy, fin,
                        Membresia.ESTADO_ACTIVA, porcentajeDescuentoReferido);
        }
        return m;
    }
}
