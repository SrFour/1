/*
 * Autor original: Cristian A
 * Completado como parte del Entregable 7.
 */
package aplicaciongimnasio.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Registro de un ingreso diario al gimnasio. Puede corresponder a un afiliado
 * o a un invitado. Guarda fecha, hora, si la visita fue gratuita y el costo
 * cobrado (cero cuando es gratuita).
 */
public class RegistroVisita implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String fecha;
    private String hora;
    private boolean esGratis;
    private double costoCobrado;

    private String codigoPersona;
    private String tipoPersona;
    private String resultadoAcceso;

    public RegistroVisita() {
    }

    public RegistroVisita(String id, String fecha, String hora, boolean esGratis, double costoCobrado) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.esGratis = esGratis;
        this.costoCobrado = costoCobrado;
    }

    public String getId() { return id; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public boolean isEsGratis() { return esGratis; }
    public double getCostoCobrado() { return costoCobrado; }

    public void setId(String id) { this.id = id; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setHora(String hora) { this.hora = hora; }
    public void setEsGratis(boolean esGratis) { this.esGratis = esGratis; }
    public void setCostoCobrado(double costoCobrado) { this.costoCobrado = costoCobrado; }

    public String getCodigoPersona() { return codigoPersona; }
    public void setCodigoPersona(String codigoPersona) { this.codigoPersona = codigoPersona; }
    public String getTipoPersona() { return tipoPersona; }
    public void setTipoPersona(String tipoPersona) { this.tipoPersona = tipoPersona; }
    public String getResultadoAcceso() { return resultadoAcceso; }
    public void setResultadoAcceso(String resultadoAcceso) { this.resultadoAcceso = resultadoAcceso; }

    /**
     * Metodo original completado: si el invitado aun tiene cupo gratis, la
     * visita no genera cobro; de lo contrario se cobra la tarifa del pase.
     */
    public double calcularCosto(Invitado i, double tarifaBase) {
        if (i != null && i.tieneCupoGratis()) {
            esGratis = true;
            costoCobrado = 0.0;
        } else {
            esGratis = false;
            costoCobrado = Math.max(0.0, tarifaBase);
        }
        return costoCobrado;
    }

    /** Factory que crea el registro con fecha y hora actuales. */
    public static RegistroVisita ahora(String id, String codigoPersona, String tipoPersona) {
        RegistroVisita r = new RegistroVisita(id, LocalDate.now().toString(),
                LocalTime.now().withNano(0).toString(), false, 0.0);
        r.setCodigoPersona(codigoPersona);
        r.setTipoPersona(tipoPersona);
        return r;
    }

    @Override
    public String toString() {
        return (esGratis ? "Gratis" : "Cobrado $" + costoCobrado) + " | "
                + tipoPersona + " " + codigoPersona + " | " + fecha + " " + hora
                + " | " + resultadoAcceso;
    }
}
