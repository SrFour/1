/*
 * Autor original: Cristian A
 * Completado como parte del Entregable 7 (Sistema de Gestion de Membresias
 * y Control de Acceso para el Gimnasio "IronCore Fitness").
 */
package aplicaciongimnasio.modelo;

import java.io.Serializable;

/**
 * Clase abstracta que representa los datos personales comunes a toda persona
 * que interactua con el gimnasio (afiliados e invitados).
 *
 * Se declara abstracta porque nunca se instancia directamente: solo modela
 * el concepto general "Persona" y define el comportamiento obligatorio que
 * deben implementar sus subclases (mostrarInformacion).
 */
public abstract class Persona implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String documento;
    protected String nombre;
    protected String telefono;
    protected String correo;
    protected String fechaNacimiento;
    protected String fotografia;

    public Persona() {
    }

    public Persona(String id, String documento, String nombre, String telefono,
                   String correo, String fechaNacimiento, String fotografia) {
        this.id = id;
        this.documento = documento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.fotografia = fotografia;
    }

    public String getId() { return id; }
    public String getDocumento() { return documento; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getFotografia() { return fotografia; }

    public void setId(String id) { this.id = id; }
    public void setDocumento(String documento) { this.documento = documento; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public void setFotografia(String fotografia) { this.fotografia = fotografia; }

    /** Metodo abstracto: cada subclase decide como presentar sus datos. */
    public abstract void mostrarInformacion();

    /** Representacion comun para listados y depuracion. */
    @Override
    public String toString() {
        return nombre + " (" + documento + ")";
    }
}
