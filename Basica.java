/*
 * Autor original: Cristian A
 * Completado como parte del Entregable 7.
 */
package aplicaciongimnasio.modelo;

/**
 * Membresia basica: tarifa plana, acceso a maquinas y pesas.
 * No agrega costos adicionales ni descuento por referido.
 */
public class Basica extends Membresia {
    private static final long serialVersionUID = 1L;

    public Basica() {
        super();
        setTipo("Basica");
    }

    public Basica(String tipo, String duracion, double precioBase, String fechaInicio,
                  String fechaVencimiento, String estado, double porcentajeDescuentoAplicable) {
        super(tipo, duracion, precioBase, fechaInicio, fechaVencimiento, estado,
              porcentajeDescuentoAplicable);
    }

    /** Tarifa plana: se cobra exactamente el precio base. */
    @Override
    public double calcularCostoMensual() {
        return getPrecioBase();
    }

    @Override
    public double getPorcentajeDescuentoReferido() {
        return 0.0;
    }
}
