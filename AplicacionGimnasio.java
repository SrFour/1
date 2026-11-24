/*
 * Autor original: Cristian A
 * Completado como parte del Entregable 7.
 */
package aplicaciongimnasio;

import aplicaciongimnasio.gui.VentanaPrincipal;
import aplicaciongimnasio.servicio.Almacenamiento;
import aplicaciongimnasio.servicio.Gimnasio;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada de la aplicacion. Carga el estado guardado (si existe),
 * crea el agregado Gimnasio y muestra la interfaz grafica.
 */
public class AplicacionGimnasio {

    public static void main(String[] args) {
        Gimnasio gimnasio = crearGimnasio();
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(gimnasio).setVisible(true));
    }

    /** Carga los datos persistidos o crea una instancia nueva. */
    private static Gimnasio crearGimnasio() {
        try {
            Gimnasio guardado = Almacenamiento.cargar();
            if (guardado != null) {
                return guardado;
            }
        } catch (Exception ex) {
            System.err.println("No se pudo cargar el estado guardado: " + ex.getMessage());
        }
        return new Gimnasio("IronCore Fitness", "Calle Principal 123", 15000.0);
    }
}
