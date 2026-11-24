/*
 * Autor original: Cristian A
 * Completado como parte del Entregable 7.
 */
package aplicaciongimnasio.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Clase abstracta "Membresia". Define el estado y el contrato comun de todas
 * las membresias (fecha de inicio, vencimiento, estado, descuento aplicable),
 * pero delega en las subclases concretas el calculo del costo y del
 * porcentaje de descuento por referido (polimorfismo).
 */
public abstract class Membresia implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String ESTADO_ACTIVA = "Activa";
    public static final String ESTADO_VENCIDA = "Vencida";
    public static final String ESTADO_SUSPENDIDA = "Suspendida";

    protected String tipo;
    protected String duracion;
    protected double precioBase;
    protected String fechaInicio;
    protected String fechaVencimiento;
    protected String estado;
    protected double porcentajeDescuentoAplicable;

    public Membresia() {
    }

    public Membresia(String tipo, String duracion, double precioBase,
                     String fechaInicio, String fechaVencimiento, String estado,
                     double porcentajeDescuentoAplicable) {
        this.tipo = tipo;
        this.duracion = duracion;
        this.precioBase = precioBase;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
        this.porcentajeDescuentoAplicable = porcentajeDescuentoAplicable;
    }

    public String getTipo() { return tipo; }
    public String getDuracion() { return duracion; }
    public double getPrecioBase() { return precioBase; }
    public String getFechaInicio() { return fechaInicio; }
    public String getFechaVencimiento() { return fechaVencimiento; }
    public String getEstado() { return estado; }
    public double getPorcentajeDescuentoAplicable() { return porcentajeDescuentoAplicable; }

    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setPorcentajeDescuentoAplicable(double porcentajeDescuentoAplicable) {
        this.porcentajeDescuentoAplicable = porcentajeDescuentoAplicable;
    }

    /** Calcula el valor mensual. Cada subclase implementa su propia regla. */
    public abstract double calcularCostoMensual();

    /** Porcentaje de descuento por referido segun el tipo concreto de membresia. */
    public abstract double getPorcentajeDescuentoReferido();

    /**
     * Acumula un porcentaje de descuento adicional (por ejemplo, beneficios por
     * referidos). Se mantiene entre 0 y 100 para evitar descuentos invalidos.
     */
    public void acumularDescuento(double pct) {
        if (pct <= 0) {
            return;
        }
        double nuevo = porcentajeDescuentoAplicable + pct;
        porcentajeDescuentoAplicable = Math.min(nuevo, 100.0);
    }

    /**
     * Monto a pagar en una renovacion: costo mensual del tipo concreto menos
     * el descuento acumulado. El resultado nunca es negativo.
     */
    public double calcularMontoRenovacion() {
        double costo = calcularCostoMensual();
        double descuento = costo * porcentajeDescuentoAplicable / 100.0;
        return Math.max(0.0, round(costo - descuento));
    }

    /** Renueva la membresia extendiendo la fecha de vencimiento un mes. */
    public void renovar() {
        LocalDate fin;
        try {
            fin = fechaVencimiento == null || fechaVencimiento.isBlank()
                    ? LocalDate.now() : LocalDate.parse(fechaVencimiento);
        } catch (DateTimeParseException ex) {
            fin = LocalDate.now();
        }
        LocalDate hoy = LocalDate.now();
        LocalDate base = fin.isBefore(hoy) ? hoy : fin;
        fechaInicio = hoy.toString();
        fechaVencimiento = base.plusMonths(1).toString();
        estado = ESTADO_ACTIVA;
    }

    /** Suspende temporalmente la membresia. */
    public void suspender() {
        this.estado = ESTADO_SUSPENDIDA;
    }

    /** Activa la membresia (por ejemplo, despues de una suspension). */
    public void activar() {
        this.estado = ESTADO_ACTIVA;
    }

    /** Indica si la membresia permite el ingreso en este momento. */
    public boolean estaActiva() {
        if (!ESTADO_ACTIVA.equalsIgnoreCase(estado)) {
            return false;
        }
        if (fechaVencimiento == null || fechaVencimiento.isBlank()) {
            return true;
        }
        try {
            return !LocalDate.parse(fechaVencimiento).isBefore(LocalDate.now());
        } catch (DateTimeParseException ex) {
            return true;
        }
    }

    /** Redondeo a dos decimales para valores monetarios. */
    protected static double round(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return tipo + " - " + estado + " - vence " + fechaVencimiento;
    }
}
