/*
 * Creado para el Entregable 7: modelo de datos de una factura.
 */
package aplicaciongimnasio.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una factura emitida por un pago: afiliacion nueva, renovacion
 * mensual o cobro de visita de invitado. Refleja descuentos por referidos
 * cuando aplican y es la fuente de datos para generar el PDF.
 */
public class Factura implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TipoPago { AFILIACION, RENOVACION, VISITA_INVITADO }

    private String numero;
    private LocalDate fecha;
    private String nombreGimnasio;
    private String direccionGimnasio;
    private String nit;
    private String cliente;
    private String documentoCliente;
    private TipoPago tipoPago;
    private String detalle;
    private double subtotal;
    private double descuento;
    private double total;
    private final List<String> notas = new ArrayList<>();

    public Factura(String numero, String nombreGimnasio, String direccionGimnasio,
                   String cliente, String documentoCliente, TipoPago tipoPago,
                   String detalle, double subtotal, double descuento) {
        this.numero = numero;
        this.fecha = LocalDate.now();
        this.nombreGimnasio = nombreGimnasio;
        this.direccionGimnasio = direccionGimnasio;
        this.nit = "900.123.456-7";
        this.cliente = cliente;
        this.documentoCliente = documentoCliente;
        this.tipoPago = tipoPago;
        this.detalle = detalle;
        this.subtotal = Math.max(0.0, subtotal);
        this.descuento = Math.max(0.0, Math.min(descuento, subtotal));
        this.total = Math.round((subtotal - this.descuento) * 100.0) / 100.0;
    }

    public String getNumero() { return numero; }
    public LocalDate getFecha() { return fecha; }
    public String getNombreGimnasio() { return nombreGimnasio; }
    public String getDireccionGimnasio() { return direccionGimnasio; }
    public String getNit() { return nit; }
    public String getCliente() { return cliente; }
    public String getDocumentoCliente() { return documentoCliente; }
    public TipoPago getTipoPago() { return tipoPago; }
    public String getDetalle() { return detalle; }
    public double getSubtotal() { return subtotal; }
    public double getDescuento() { return descuento; }
    public double getTotal() { return total; }
    public List<String> getNotas() { return Collections.unmodifiableList(notas); }

    public void agregarNota(String nota) {
        if (nota != null && !nota.isBlank()) {
            notas.add(nota);
        }
    }

    public String getTitulo() {
        switch (tipoPago) {
            case AFILIACION: return "FACTURA DE AFILIACION";
            case RENOVACION: return "FACTURA DE RENOVACION MENSUAL";
            case VISITA_INVITADO: return "FACTURA DE PASE DE INVITADO";
            default: return "FACTURA";
        }
    }

    @Override
    public String toString() {
        return numero + " | " + getTitulo() + " | " + cliente + " | Total $" + total;
    }
}
