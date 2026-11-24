/*
 * Autor original: Cristian A
 * Completado como parte del Entregable 7.
 */
package aplicaciongimnasio.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Afiliado del gimnasio. Extiende Persona (herencia) y mantiene su membresia,
 * el codigo de referido, el contador de referidos exitosos y el historial de
 * visitas. La lista de invitados se guarda como composicion: el Afiliado es
 * el owner de sus invitados.
 */
public class Afiliado extends Persona {
    private static final long serialVersionUID = 1L;

    private String codigoAfiliado;
    private int conteoReferidosExitosos;

    // Relaciones con otras clases del dominio.
    private Membresia membresia;
    private String codigoReferidor;
    private final List<Invitado> invitados = new ArrayList<>();
    private final List<RegistroVisita> historialVisitas = new ArrayList<>();

    /** Umbral a partir del cual el afiliado obtiene un mes gratis. */
    public static final int REFERIDOS_PARA_BENEFICIO = 3;

    public Afiliado() {
        super();
    }

    public Afiliado(String codigoAfiliado, int conteoReferidosExitosos) {
        this.codigoAfiliado = codigoAfiliado;
        this.conteoReferidosExitosos = conteoReferidosExitosos;
    }

    public Afiliado(String codigoAfiliado, int conteoReferidosExitosos, String id,
                    String documento, String nombre, String telefono, String correo,
                    String fechaNacimiento, String fotografia) {
        super(id, documento, nombre, telefono, correo, fechaNacimiento, fotografia);
        this.codigoAfiliado = codigoAfiliado;
        this.conteoReferidosExitosos = conteoReferidosExitosos;
    }

    public String getCodigoAfiliado() { return codigoAfiliado; }
    public int getConteoReferidosExitosos() { return conteoReferidosExitosos; }

    public void setCodigoAfiliado(String codigoAfiliado) { this.codigoAfiliado = codigoAfiliado; }

    public void setConteoReferidosExitosos(int conteoReferidosExitosos) {
        this.conteoReferidosExitosos = Math.max(0, conteoReferidosExitosos);
    }

    public Membresia getMembresia() { return membresia; }

    public void setMembresia(Membresia membresia) { this.membresia = membresia; }

    public String getCodigoReferidor() { return codigoReferidor; }

    public void setCodigoReferidor(String codigoReferidor) { this.codigoReferidor = codigoReferidor; }

    /** Vista de solo lectura de los invitados asociados. */
    public List<Invitado> getInvitados() {
        return Collections.unmodifiableList(invitados);
    }

    /** Vista de solo lectura del historial de visitas. */
    public List<RegistroVisita> getHistorialVisitas() {
        return Collections.unmodifiableList(historialVisitas);
    }

    public void agregarInvitado(Invitado invitado) {
        if (invitado != null && !invitados.contains(invitado)) {
            invitados.add(invitado);
        }
    }

    public void agregarVisita(RegistroVisita visita) {
        if (visita != null) {
            historialVisitas.add(visita);
        }
    }

    /** Implementacion del metodo abstracto heredado de Persona. */
    @Override
    public void mostrarInformacion() {
        StringBuilder sb = new StringBuilder();
        sb.append("Afiliado: ").append(getNombre()).append('\n')
          .append("Documento: ").append(getDocumento()).append('\n')
          .append("Codigo: ").append(codigoAfiliado).append('\n')
          .append("Correo: ").append(getCorreo()).append('\n')
          .append("Membresia: ").append(membresia != null ? membresia.toString() : "Sin membresia")
          .append('\n')
          .append("Referidos exitosos: ").append(conteoReferidosExitosos).append('\n')
          .append("Invitados registrados: ").append(invitados.size());
        System.out.println(sb);
    }

    /** Incrementa en uno el contador de referidos exitosos. */
    public void incrementarReferidos() {
        conteoReferidosExitosos++;
    }

    /** Indica si ya alcanzo el beneficio por acumulacion de referidos. */
    public boolean tieneBeneficioPorReferidos() {
        return conteoReferidosExitosos >= REFERIDOS_PARA_BENEFICIO;
    }

    /** Motivo por el cual se autoriza o niega el ingreso diario. */
    public String validarAcceso() {
        if (membresia == null) {
            return "Acceso DENEGADO: el afiliado no tiene membresia asignada.";
        }
        if (!membresia.estaActiva()) {
            return "Acceso DENEGADO: la membresia esta " + membresia.getEstado().toLowerCase() + ".";
        }
        return "Acceso AUTORIZADO: membresia " + membresia.getTipo() + " activa.";
    }

    public boolean puedeIngresar() {
        return membresia != null && membresia.estaActiva();
    }
}
