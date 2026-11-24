/*
 * Creado para el Entregable 7: persistencia mediante archivos.
 */
package aplicaciongimnasio.servicio;

import aplicaciongimnasio.modelo.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Estrategia de persistencia basada en archivos. Guarda el estado completo
 * del gimnasio mediante serializacion Java (.dat) y, ademas, exporta un
 * resumen legible en CSV (.csv) para facilitar la revision del contenido.
 */
public final class Almacenamiento {
    private static final String ARCHIVO_DAT = "datos/gimnasio.dat";
    private static final String ARCHIVO_CSV = "datos/resumen.csv";

    private Almacenamiento() { }

    /** Guarda el estado actual en disco. */
    public static void guardar(Gimnasio g) throws IOException {
        new File("datos").mkdirs();
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(ARCHIVO_DAT)))) {
            out.writeObject(g.getNombre());
            out.writeObject(g.getDireccion());
            out.writeDouble(g.getTarifaPaseDiarioInvitado());
            out.writeInt(g.getConsecutivo());
            out.writeObject(new ArrayList<>(g.getAfiliados()));
            out.writeObject(new ArrayList<>(g.getInvitados()));
            out.writeObject(new ArrayList<>(g.getVisitas()));
            out.writeObject(new ArrayList<>(g.getReferidos()));
            out.writeObject(new ArrayList<>(g.getFacturas()));
            out.writeObject(new ArrayList<>(g.getParametros()));
        }
        exportarCsv(g);
    }

    /** Carga el estado desde disco. Si no existe, devuelve null. */
    @SuppressWarnings("unchecked")
    public static Gimnasio cargar() throws IOException, ClassNotFoundException {
        File f = new File(ARCHIVO_DAT);
        if (!f.exists()) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(f)))) {
            String nombre = (String) in.readObject();
            String direccion = (String) in.readObject();
            double tarifa = in.readDouble();
            int consec = in.readInt();
            List<Afiliado> afs = (List<Afiliado>) in.readObject();
            List<Invitado> invs = (List<Invitado>) in.readObject();
            List<RegistroVisita> viss = (List<RegistroVisita>) in.readObject();
            List<RegistroReferido> refs = (List<RegistroReferido>) in.readObject();
            List<Factura> fats = (List<Factura>) in.readObject();
            List<ParametrosMembresia> pars = (List<ParametrosMembresia>) in.readObject();
            Gimnasio g = new Gimnasio(nombre, direccion, tarifa);
            g.restore(nombre, direccion, tarifa, consec, afs, invs, viss, refs, fats, pars);
            return g;
        }
    }

    /** Exporta un resumen legible con afiliados, visitas y facturas. */
    private static void exportarCsv(Gimnasio g) throws IOException {
        try (PrintWriter w = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(ARCHIVO_CSV), StandardCharsets.UTF_8))) {
            w.println("SECCION;CODIGO;NOMBRE;DOCUMENTO;DETALLE;VALOR");
            for (Afiliado a : g.getAfiliados()) {
                w.println("AFILIADO;" + csv(a.getCodigoAfiliado()) + ";" + csv(a.getNombre()) + ";"
                        + csv(a.getDocumento()) + ";"
                        + csv(a.getMembresia() == null ? "Sin membresia" : a.getMembresia().toString())
                        + ";" + csv(String.valueOf(a.getConteoReferidosExitosos())));
            }
            for (RegistroVisita v : g.getVisitas()) {
                w.println("VISITA;" + csv(v.getId()) + ";" + csv(v.getCodigoPersona()) + ";"
                        + csv(v.getFecha()) + ";" + csv(v.getResultadoAcceso()) + ";"
                        + v.getCostoCobrado());
            }
            for (Factura f : g.getFacturas()) {
                w.println("FACTURA;" + csv(f.getNumero()) + ";" + csv(f.getCliente()) + ";"
                        + csv(f.getDocumentoCliente()) + ";" + csv(f.getDetalle()) + ";"
                        + f.getTotal());
            }
        }
    }

    private static String csv(String s) {
        if (s == null) return "";
        String v = s.replace(";", ",").replace("\n", " ").replace("\r", " ");
        return "\"" + v.replace("\"", "'") + "\"";
    }
}
