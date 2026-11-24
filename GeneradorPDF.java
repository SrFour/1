/*
 * Creado para el Entregable 7: generacion de documentos PDF para facturas.
 */
package aplicaciongimnasio.util;

import aplicaciongimnasio.modelo.Factura;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Generador de PDF sin librerias externas. Construye un archivo PDF 1.4
 * basico con pagina carta, fuente Helvetica y texto en Latin-1 (WinAnsi),
 * suficiente para tildes y enye del idioma espanol.
 */
public final class GeneradorPDF {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private GeneradorPDF() { }

    /** Genera el PDF de una factura y devuelve la ruta del archivo creado. */
    public static String generarFactura(Factura f) throws IOException {
        new java.io.File("facturas").mkdirs();
        String nombreArchivo = "facturas/" + f.getNumero().replaceAll("[^A-Za-z0-9_-]", "_")
                + ".pdf";

        List<String> lineas = new ArrayList<>();
        lineas.add(f.getTitulo());
        lineas.add("");
        lineas.add(f.getNombreGimnasio() + "  |  NIT " + f.getNit());
        lineas.add(f.getDireccionGimnasio());
        lineas.add("");
        lineas.add("Numero de factura: " + f.getNumero());
        lineas.add("Fecha: " + f.getFecha().format(FECHA));
        lineas.add("");
        lineas.add("CLIENTE");
        lineas.add("  Nombre: " + f.getCliente());
        lineas.add("  Documento: " + f.getDocumentoCliente());
        lineas.add("");
        lineas.add("DETALLE");
        lineas.add("  Concepto: " + f.getDetalle());
        lineas.add("  Subtotal: $" + fmt(f.getSubtotal()));
        lineas.add("  Descuento: -$" + fmt(f.getDescuento()));
        lineas.add("  TOTAL A PAGAR: $" + fmt(f.getTotal()));
        lineas.add("");
        if (!f.getNotas().isEmpty()) {
            lineas.add("OBSERVACIONES");
            for (String nota : f.getNotas()) {
                lineas.add("  - " + nota);
            }
            lineas.add("");
        }
        lineas.add("Gracias por confiar en " + f.getNombreGimnasio() + ".");

        byte[] pdf = construirPdf(lineas);
        try (FileOutputStream out = new FileOutputStream(nombreArchivo)) {
            out.write(pdf);
        }
        return nombreArchivo;
    }

    private static String fmt(double v) {
        return String.format(java.util.Locale.US, "%,.2f", v);
    }

    private static byte[] construirPdf(List<String> lineas) throws IOException {
        StringBuilder contenido = new StringBuilder("BT\n/F1 11 Tf\n14 TL\n50 742 Td\n");
        for (String linea : lineas) {
            String segura = linea.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
            contenido.append('(').append(segura).append(") Tj\nT*\n");
        }
        contenido.append("ET\n");

        byte[] stream = contenido.toString().getBytes(StandardCharsets.ISO_8859_1);
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();

        String header = "%PDF-1.4\n%\u00E2\u00E3\u00CF\u00D3\n";
        pdf.write(header.getBytes(StandardCharsets.ISO_8859_1));

        int[] offsets = new int[6];
        offsets[1] = pdf.size();
        write(pdf, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        offsets[2] = pdf.size();
        write(pdf, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

        offsets[3] = pdf.size();
        write(pdf, "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] "
                + "/Resources << /Font << /F1 5 0 R >> >> /Contents 4 0 R >>\nendobj\n");

        offsets[4] = pdf.size();
        write(pdf, "4 0 obj\n<< /Length " + stream.length + " >>\nstream\n");
        pdf.write(stream);
        write(pdf, "\nendstream\nendobj\n");

        offsets[5] = pdf.size();
        write(pdf, "5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica "
                + "/Encoding /WinAnsiEncoding >>\nendobj\n");

        int xref = pdf.size();
        write(pdf, "xref\n0 6\n0000000000 65535 f \n");
        for (int i = 1; i <= 5; i++) {
            write(pdf, String.format(java.util.Locale.US, "%010d 00000 n \n", offsets[i]));
        }
        write(pdf, "trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n");
        return pdf.toByteArray();
    }

    private static void write(ByteArrayOutputStream out, String s) throws IOException {
        out.write(s.getBytes(StandardCharsets.ISO_8859_1));
    }
}
