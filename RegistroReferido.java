/*
 * Autor original: Cristian A
 * Completado como parte del Entregable 7.
 */
package aplicaciongimnasio.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Registro de una referencia entre afiliados. Guarda que afiliado refirio,
 * cual fue el nuevo afiliado, la fecha y si la referencia se concreto
 * (exito) con el descuento aplicado.
 */
public class RegistroReferido implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fechaReferencia;
    private boolean exitoso;

    private String codigoReferidor;
    private String codigoNuevoAfiliado;
    private double porcentajeDescuento;
    private double descuentoReferidor;
    private double descuentoNuevoAfiliado;

    public RegistroReferido() {
    }

    public RegistroReferido(String fechaReferencia, boolean exitoso) {
        this.fechaReferencia = fechaReferencia;
        this.exitoso = exitoso;
    }

    public RegistroReferido(String fechaReferencia, String codigoReferidor,
                            String codigoNuevoAfiliado, double porcentajeDescuento,
                            double descuentoReferidor, double descuentoNuevoAfiliado) {
        this.fechaReferencia = fechaReferencia;
        this.codigoReferidor = codigoReferidor;
        this.codigoNuevoAfiliado = codigoNuevoAfiliado;
        this.porcentajeDescuento = porcentajeDescuento;
        this.descuentoReferidor = descuentoReferidor;
        this.descuentoNuevoAfiliado = descuentoNuevoAfiliado;
        this.exitoso = true;
    }

    public String getFechaReferencia() { return fechaReferencia; }
    public boolean isExitoso() { return exitoso; }

    public void setFechaReferencia(String fechaReferencia) { this.fechaReferencia = fechaReferencia; }
    public void setExitoso(boolean exitoso) { this.exitoso = exitoso; }

    public String getCodigoReferidor() { return codigoReferidor; }
    public String getCodigoNuevoAfiliado() { return codigoNuevoAfiliado; }
    public double getPorcentajeDescuento() { return porcentajeDescuento; }
    public double getDescuentoReferidor() { return descuentoReferidor; }
    public double getDescuentoNuevoAfiliado() { return descuentoNuevoAfiliado; }

    /** Marca la referencia como exitosa usando la fecha actual. */
    public void marcarExitoso() {
        this.exitoso = true;
        if (fechaReferencia == null || fechaReferencia.isBlank()) {
            fechaReferencia = LocalDate.now().toString();
        }
    }

    @Override
    public String toString() {
        return (exitoso ? "Referido exitoso" : "Referido pendiente") + " | "
                + codigoReferidor + " -> " + codigoNuevoAfiliado
                + " | descuento " + porcentajeDescuento + "% | " + fechaReferencia;
    }
}
