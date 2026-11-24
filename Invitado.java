/*
 * Autor original: Cristian A
 * Completado como parte del Entregable 7.
 */
package aplicaciongimnasio.modelo;

import java.io.Serializable;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Invitado: persona que no es miembro pero puede visitar el gimnasio invitada
 * por un afiliado. Comparte datos personales con Persona por herencia, pero
 * tiene reglas de acceso propias (cupo mensual de visitas gratis).
 *
 * Si el invitado se afilia, se conservan el codigo de quien lo invito y su
 * historial de visitas como invitado (requisito del enunciado).
 */
public class Invitado extends Persona implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codigoInvitado;
    private int cupoGratisMensual;
    private int visitasConsumidasMes;

    private String codigoAfiliadoInvitador;
    private String mesControl;
    private final List<RegistroVisita> historialVisitas = new ArrayList<>();
    private boolean convertidoAAfiliado;

    public Invitado() {
        super();
    }

    public Invitado(String codigoInvitado, int cupoGratisMensual, int visitasConsumidasMes,
                    String id, String documento, String nombre, String telefono, String correo,
                    String fechaNacimiento, String fotografia) {
        super(id, documento, nombre, telefono, correo, fechaNacimiento, fotografia);
        this.codigoInvitado = codigoInvitado;
        this.cupoGratisMensual = cupoGratisMensual;
        this.visitasConsumidasMes = visitasConsumidasMes;
        this.mesControl = YearMonth.now().toString();
    }

    public String getCodigoInvitado() { return codigoInvitado; }
    public int getCupoGratisMensual() { return cupoGratisMensual; }
    public int getVisitasConsumidasMes() { return visitasConsumidasMes; }

    public void setCodigoInvitado(String codigoInvitado) { this.codigoInvitado = codigoInvitado; }
    public void setCupoGratisMensual(int cupoGratisMensual) {
        this.cupoGratisMensual = Math.max(0, cupoGratisMensual);
    }
    public void setVisitasConsumidasMes(int visitasConsumidasMes) {
        this.visitasConsumidasMes = Math.max(0, visitasConsumidasMes);
    }

    public String getCodigoAfiliadoInvitador() { return codigoAfiliadoInvitador; }
    public void setCodigoAfiliadoInvitador(String codigoAfiliadoInvitador) {
        this.codigoAfiliadoInvitador = codigoAfiliadoInvitador;
    }

    public String getMesControl() { return mesControl; }
    public void setMesControl(String mesControl) { this.mesControl = mesControl; }

    /** Historial de visitas previas (y posteriores) como invitado. */
    public List<RegistroVisita> getHistorialVisitas() {
        return Collections.unmodifiableList(historialVisitas);
    }

    public void agregarVisita(RegistroVisita visita) {
        if (visita != null) {
            historialVisitas.add(visita);
        }
    }

    public boolean isConvertidoAAfiliado() { return convertidoAAfiliado; }

    public void setConvertidoAAfiliado(boolean convertidoAAfiliado) {
        this.convertidoAAfiliado = convertidoAAfiliado;
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("Invitado: " + getNombre() + " (" + getDocumento() + ") | "
                + "Codigo: " + codigoInvitado + " | Visitado por: " + codigoAfiliadoInvitador
                + " | Cupo: " + visitasConsumidasMes + "/" + cupoGratisMensual);
    }

    /**
     * Reinicia el contador cuando cambia el mes. Asi el cupo de visitas
     * gratuitas se renueva cada mes sin perder el historial.
     */
    public void verificarCambioDeMes() {
        String mesActual = YearMonth.now().toString();
        if (!mesActual.equals(mesControl)) {
            mesControl = mesActual;
            visitasConsumidasMes = 0;
        }
    }

    /** Metodo original completado: quedan visitas gratis este mes. */
    public boolean tieneCupoGratis() {
        verificarCambioDeMes();
        return visitasConsumidasMes < cupoGratisMensual;
    }

    /** Consume una visita gratuita si hay cupo disponible. */
    public boolean consumirVisitaGratis() {
        if (!tieneCupoGratis()) {
            return false;
        }
        visitasConsumidasMes++;
        return true;
    }

    /** Motivo de autorizacion o rechazo del ingreso diario. */
    public String validarAcceso() {
        if (tieneCupoGratis()) {
            return "Acceso AUTORIZADO: visita gratuita " + (visitasConsumidasMes + 1)
                    + " de " + cupoGratisMensual + ".";
        }
        return "Acceso AUTORIZADO CON COBRO: supero las " + cupoGratisMensual
                + " visitas gratuitas del mes; se genera cobro de pase diario.";
    }
}
