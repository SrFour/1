/*
 * Creado para el Entregable 7: interfaz grafica de usuario (Swing).
 */
package aplicaciongimnasio.gui;

import aplicaciongimnasio.modelo.*;
import aplicaciongimnasio.servicio.Almacenamiento;
import aplicaciongimnasio.servicio.Gimnasio;
import aplicaciongimnasio.util.GeneradorPDF;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana principal de la aplicacion. Expone las operaciones del gimnasio
 * mediante una interfaz grafica organizada en cinco paneles: Afiliados,
 * Invitados, Ingreso diario, Membresias y facturas, y Administracion.
 */
public class VentanaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;

    private final Gimnasio gimnasio;
    private final JTable tablaAfiliados = new JTable();
    private final JTable tablaInvitados = new JTable();
    private final JTable tablaVisitas = new JTable();
    private final JTable tablaFacturas = new JTable();
    private final JTable tablaRanking = new JTable();
    private final JLabel estado = new JLabel("Listo.");

    public VentanaPrincipal(Gimnasio gimnasio) {
        this.gimnasio = gimnasio;
        setTitle("IronCore Fitness - Gestion de gimnasio");
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Afiliados", panelAfiliados());
        tabs.addTab("Invitados", panelInvitados());
        tabs.addTab("Ingreso diario", panelIngreso());
        tabs.addTab("Membresias y facturas", panelMembresias());
        tabs.addTab("Administracion", panelAdministracion());
        add(tabs, BorderLayout.CENTER);

        estado.setBorder(new EmptyBorder(6, 10, 6, 10));
        add(estado, BorderLayout.SOUTH);

        JMenuBar barra = new JMenuBar();
        JMenu archivo = new JMenu("Archivo");
        JMenuItem guardar = new JMenuItem("Guardar datos");
        guardar.addActionListener(e -> guardarDatos());
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> {
            guardarDatos();
            dispose();
        });
        archivo.add(guardar);
        archivo.add(salir);
        barra.add(archivo);
        setJMenuBar(barra);

        refrescarTodo();
    }

    // ---------------- Panel Afiliados ----------------

    private JPanel panelAfiliados() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = gc();
        JTextField nombre = new JTextField(14);
        JTextField documento = new JTextField(12);
        JTextField correo = new JTextField(14);
        JTextField foto = new JTextField(16);
        JComboBox<String> tipo = new JComboBox<>(new String[]{"Basica", "Premium", "VIP"});
        JTextField referidor = new JTextField(10);

        addFila(form, c, 0, "Nombre:", nombre);
        addFila(form, c, 1, "Documento:", documento);
        addFila(form, c, 2, "Correo:", correo);
        addFila(form, c, 3, "Fotografia (ruta):", foto);
        addFila(form, c, 4, "Tipo de membresia:", tipo);
        addFila(form, c, 5, "Codigo de referido:", referidor);

        JButton registrar = new JButton("Registrar afiliado");
        registrar.addActionListener((ActionEvent e) -> {
            try {
                if (nombre.getText().isBlank() || documento.getText().isBlank()) {
                    throw new IllegalArgumentException("Nombre y documento son obligatorios.");
                }
                Afiliado a = new Afiliado(
                        gimnasio.siguienteCodigo("AF-"), 0,
                        gimnasio.siguienteCodigo("P-"), documento.getText().trim(),
                        nombre.getText().trim(), "", correo.getText().trim(),
                        "", foto.getText().trim());
                Factura f = gimnasio.registrarAfiliadoConReferido(a,
                        (String) tipo.getSelectedItem(), referidor.getText().trim());
                String ruta = GeneradorPDF.generarFactura(f);
                estado.setText("Afiliado " + a.getCodigoAfiliado() + " registrado. Factura PDF: " + ruta);
                nombre.setText(""); documento.setText(""); correo.setText("");
                foto.setText(""); referidor.setText("");
                refrescarTodo();
            } catch (Exception ex) {
                error(ex);
            }
        });
        form.add(registrar, gc(0, 6, 2));

        JButton convertir = new JButton("Convertir invitado en afiliado");
        convertir.addActionListener(e -> {
            String codigo = JOptionPane.showInputDialog(this, "Codigo del invitado:");
            if (codigo == null || codigo.isBlank()) return;
            String t = (String) JOptionPane.showInputDialog(this,
                    "Tipo de membresia:", "Conversion", JOptionPane.QUESTION_MESSAGE,
                    null, new String[]{"Basica", "Premium", "VIP"}, "Basica");
            if (t == null) return;
            try {
                Afiliado a = gimnasio.convertirInvitadoEnAfiliado(codigo.trim(), t);
                estado.setText("Invitado convertido en afiliado " + a.getCodigoAfiliado()
                        + " (historial conservado).");
                refrescarTodo();
            } catch (Exception ex) {
                error(ex);
            }
        });
        form.add(convertir, gc(0, 7, 2));

        p.add(form, BorderLayout.NORTH);
        p.add(tabla(tablaAfiliados, new String[]{"Codigo", "Nombre", "Documento", "Membresia",
                "Referidos", "Acceso"}), BorderLayout.CENTER);
        return p;
    }

    // ---------------- Panel Invitados ----------------

    private JPanel panelInvitados() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = gc();
        JTextField afiliado = new JTextField(12);
        JTextField nombre = new JTextField(14);
        JTextField documento = new JTextField(12);
        JTextField foto = new JTextField(16);
        JSpinner cupo = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));

        addFila(form, c, 0, "Codigo del afiliado invitador:", afiliado);
        addFila(form, c, 1, "Nombre del invitado:", nombre);
        addFila(form, c, 2, "Documento:", documento);
        addFila(form, c, 3, "Fotografia (ruta):", foto);
        addFila(form, c, 4, "Visitas gratis al mes:", cupo);

        JButton registrar = new JButton("Registrar invitado");
        registrar.addActionListener(e -> {
            try {
                Invitado i = gimnasio.registrarInvitado(afiliado.getText().trim(),
                        nombre.getText().trim(), documento.getText().trim(),
                        foto.getText().trim(), (Integer) cupo.getValue());
                estado.setText("Invitado " + i.getCodigoInvitado() + " registrado.");
                afiliado.setText(""); nombre.setText(""); documento.setText(""); foto.setText("");
                refrescarTodo();
            } catch (Exception ex) {
                error(ex);
            }
        });
        form.add(registrar, gc(0, 5, 2));
        p.add(form, BorderLayout.NORTH);
        p.add(tabla(tablaInvitados, new String[]{"Codigo", "Nombre", "Documento",
                "Invitador", "Cupo usado", "Cupo total"}), BorderLayout.CENTER);
        return p;
    }

    // ---------------- Panel Ingreso diario ----------------

    private JPanel panelIngreso() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = gc();
        JTextField codigo = new JTextField(16);
        JComboBox<String> tipoPersona = new JComboBox<>(new String[]{"Afiliado", "Invitado"});
        addFila(form, c, 0, "Codigo o documento:", codigo);
        addFila(form, c, 1, "Tipo de persona:", tipoPersona);

        JButton registrar = new JButton("Registrar ingreso");
        registrar.addActionListener(e -> {
            String valor = codigo.getText().trim();
            if (valor.isEmpty()) {
                estado.setText("Ingrese un codigo o documento.");
                return;
            }
            boolean esAfiliado = "Afiliado".equals(tipoPersona.getSelectedItem());
            try {
                Afiliado a = esAfiliado ? gimnasio.buscarAfiliado(valor) : null;
                if (a == null) a = gimnasio.buscarAfiliadoPorDocumento(valor);
                if (esAfiliado && a == null) {
                    throw new IllegalArgumentException("Afiliado no encontrado: " + valor);
                }
                String codigoReal = a != null ? a.getCodigoAfiliado() : valor;
                RegistroVisita rv = gimnasio.registrarIngreso(codigoReal, esAfiliado);
                estado.setText(rv.getResultadoAcceso());
                codigo.setText("");
                refrescarTodo();
            } catch (Exception ex) {
                estado.setText(ex.getMessage());
            }
        });
        form.add(registrar, gc(0, 2, 2));
        p.add(form, BorderLayout.NORTH);
        p.add(tabla(tablaVisitas, new String[]{"ID", "Fecha", "Hora", "Tipo", "Persona",
                "Gratis", "Costo", "Resultado"}), BorderLayout.CENTER);
        return p;
    }

    // ---------------- Panel Membresias y facturas ----------------

    private JPanel panelMembresias() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = gc();
        JTextField codigo = new JTextField(14);
        addFila(form, c, 0, "Codigo del afiliado:", codigo);

        JButton renovar = new JButton("Renovar membresia y generar factura PDF");
        renovar.addActionListener(e -> {
            try {
                Factura f = gimnasio.renovarMembresia(codigo.getText().trim());
                String ruta = GeneradorPDF.generarFactura(f);
                estado.setText("Membresia renovada. Factura PDF: " + ruta);
                refrescarTodo();
            } catch (Exception ex) {
                error(ex);
            }
        });
        form.add(renovar, gc(0, 1, 2));

        JButton suspender = new JButton("Suspender membresia");
        suspender.addActionListener(e -> {
            try {
                gimnasio.suspenderMembresia(codigo.getText().trim());
                estado.setText("Membresia suspendida.");
                refrescarTodo();
            } catch (Exception ex) {
                error(ex);
            }
        });
        form.add(suspender, gc(0, 2, 2));

        JButton imprimir = new JButton("Generar PDF de factura seleccionada");
        imprimir.addActionListener(e -> {
            int fila = tablaFacturas.getSelectedRow();
            if (fila < 0) {
                estado.setText("Seleccione una factura en la tabla.");
                return;
            }
            try {
                String numero = (String) tablaFacturas.getValueAt(fila, 0);
                Factura f = gimnasio.getFacturas().stream()
                        .filter(x -> x.getNumero().equals(numero)).findFirst().orElse(null);
                if (f != null) {
                    estado.setText("PDF generado: " + GeneradorPDF.generarFactura(f));
                }
            } catch (Exception ex) {
                error(ex);
            }
        });
        form.add(imprimir, gc(0, 3, 2));
        p.add(form, BorderLayout.NORTH);

        JTabbedPane inferior = new JTabbedPane();
        inferior.add("Facturas", tabla(tablaFacturas, new String[]{"Numero", "Fecha", "Tipo",
                "Cliente", "Subtotal", "Descuento", "Total"}));
        inferior.add("Ranking de referidos", tabla(tablaRanking, new String[]{"Codigo",
                "Afiliado", "Referidos exitosos", "Beneficio"}));
        p.add(inferior, BorderLayout.CENTER);
        return p;
    }

    // ---------------- Panel Administracion ----------------

    private JPanel panelAdministracion() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints c = gc();

        JTextField tarifa = new JTextField(String.valueOf(gimnasio.getTarifaPaseDiarioInvitado()), 10);
        addFila(p, c, 0, "Tarifa pase diario invitado:", tarifa);

        int fila = 1;
        for (String tipo : new String[]{"Basica", "Premium", "VIP"}) {
            ParametrosMembresia pm = gimnasio.getParametro(tipo);
            JTextField precio = new JTextField(String.valueOf(pm.getPrecioBase()), 9);
            JTextField clases = new JTextField(String.valueOf(pm.getCostoClases()), 9);
            JTextField entrenador = new JTextField(String.valueOf(pm.getCostoEntrenadorSesion()), 9);
            JTextField descuento = new JTextField(String.valueOf(pm.getPorcentajeDescuentoReferido()), 9);
            JPanel filaPanel = new JPanel();
            filaPanel.add(new JLabel("Precio:")); filaPanel.add(precio);
            filaPanel.add(new JLabel("Clases:")); filaPanel.add(clases);
            filaPanel.add(new JLabel("Entrenador:")); filaPanel.add(entrenador);
            filaPanel.add(new JLabel("Descuento %:")); filaPanel.add(descuento);
            p.add(new JLabel(tipo + ":"), gc(0, fila, 1));
            p.add(filaPanel, gc(1, fila, 1));
            if ("Basica".equals(tipo)) {
                precio.setName("precioBasica"); clases.setName("clasesBasica");
                entrenador.setName("entrenadorBasica"); descuento.setName("descBasica");
            } else if ("Premium".equals(tipo)) {
                precio.setName("precioPremium"); clases.setName("clasesPremium");
                entrenador.setName("entrenadorPremium"); descuento.setName("descPremium");
            } else {
                precio.setName("precioVIP"); clases.setName("clasesVIP");
                entrenador.setName("entrenadorVIP"); descuento.setName("descVIP");
            }
            fila++;
        }

        JButton aplicar = new JButton("Aplicar configuracion y guardar");
        aplicar.addActionListener(e -> {
            try {
                gimnasio.setTarifaPaseDiarioInvitado(Double.parseDouble(tarifa.getText().trim()));
                for (String tipo : new String[]{"Basica", "Premium", "VIP"}) {
                    double precio = Double.parseDouble(buscarCampo(p, "precio" + tipo).getText());
                    double clases = Double.parseDouble(buscarCampo(p, "clases" + tipo).getText());
                    double entrenador = Double.parseDouble(buscarCampo(p, "entrenador" + tipo).getText());
                    double desc = Double.parseDouble(buscarCampo(p, "desc" + tipo).getText());
                    gimnasio.actualizarParametros(tipo, precio, clases, entrenador, desc);
                }
                guardarDatos();
                estado.setText("Configuracion aplicada y datos guardados.");
            } catch (Exception ex) {
                error(ex);
            }
        });
        p.add(aplicar, gc(0, fila, 2));

        JButton verRanking = new JButton("Consultar ranking de referidos");
        verRanking.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("Ranking de afiliados por referidos exitosos:\n\n");
            List<Afiliado> ranking = gimnasio.rankingReferidos();
            if (ranking.isEmpty()) sb.append("Aun no hay afiliados registrados.");
            for (int i = 0; i < ranking.size(); i++) {
                Afiliado a = ranking.get(i);
                sb.append(i + 1).append(". ").append(a.getNombre())
                  .append(" (").append(a.getCodigoAfiliado()).append(") - ")
                  .append(a.getConteoReferidosExitosos()).append(" referidos\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Ranking", JOptionPane.INFORMATION_MESSAGE);
        });
        p.add(verRanking, gc(0, fila + 1, 2));
        return p;
    }

    /** Busca un componente por nombre dentro del panel de administracion. */
    private JTextField buscarCampo(java.awt.Container cont, String nombre) {
        for (java.awt.Component c : cont.getComponents()) {
            if (c instanceof JTextField && nombre.equals(c.getName())) {
                return (JTextField) c;
            }
            if (c instanceof java.awt.Container) {
                JTextField r = buscarCampo((java.awt.Container) c, nombre);
                if (r != null) return r;
            }
        }
        return null;
    }

    // ---------------- Utilidades de la GUI ----------------

    private void refrescarTodo() {
        DefaultTableModel mA = new DefaultTableModel(new String[]{"Codigo", "Nombre", "Documento",
                "Membresia", "Referidos", "Acceso"}, 0);
        for (Afiliado a : gimnasio.getAfiliados()) {
            mA.addRow(new Object[]{a.getCodigoAfiliado(), a.getNombre(), a.getDocumento(),
                    a.getMembresia() == null ? "-" : a.getMembresia().getTipo(),
                    a.getConteoReferidosExitosos(),
                    a.puedeIngresar() ? "Activo" : "Sin acceso"});
        }
        tablaAfiliados.setModel(mA);

        DefaultTableModel mI = new DefaultTableModel(new String[]{"Codigo", "Nombre",
                "Documento", "Invitador", "Cupo usado", "Cupo total"}, 0);
        for (Invitado i : gimnasio.getInvitados()) {
            mI.addRow(new Object[]{i.getCodigoInvitado(), i.getNombre(), i.getDocumento(),
                    i.getCodigoAfiliadoInvitador(), i.getVisitasConsumidasMes(),
                    i.getCupoGratisMensual()});
        }
        tablaInvitados.setModel(mI);

        DefaultTableModel mV = new DefaultTableModel(new String[]{"ID", "Fecha", "Hora", "Tipo",
                "Persona", "Gratis", "Costo", "Resultado"}, 0);
        for (RegistroVisita v : gimnasio.getVisitas()) {
            mV.addRow(new Object[]{v.getId(), v.getFecha(), v.getHora(), v.getTipoPersona(),
                    v.getCodigoPersona(), v.isEsGratis() ? "Si" : "No", v.getCostoCobrado(),
                    v.getResultadoAcceso()});
        }
        tablaVisitas.setModel(mV);

        DefaultTableModel mF = new DefaultTableModel(new String[]{"Numero", "Fecha", "Tipo",
                "Cliente", "Subtotal", "Descuento", "Total"}, 0);
        for (Factura f : gimnasio.getFacturas()) {
            mF.addRow(new Object[]{f.getNumero(), f.getFecha().toString(), f.getTitulo(),
                    f.getCliente(), f.getSubtotal(), f.getDescuento(), f.getTotal()});
        }
        tablaFacturas.setModel(mF);

        DefaultTableModel mR = new DefaultTableModel(new String[]{"Codigo", "Afiliado",
                "Referidos exitosos", "Beneficio"}, 0);
        for (Afiliado a : gimnasio.rankingReferidos()) {
            mR.addRow(new Object[]{a.getCodigoAfiliado(), a.getNombre(),
                    a.getConteoReferidosExitosos(),
                    a.tieneBeneficioPorReferidos() ? "Mes gratis" : "-"});
        }
        tablaRanking.setModel(mR);
    }

    private void guardarDatos() {
        try {
            Almacenamiento.guardar(gimnasio);
            estado.setText("Datos guardados correctamente en la carpeta 'datos'.");
        } catch (IOException ex) {
            error(ex);
        }
    }

    private void error(Exception ex) {
        ex.printStackTrace();
        estado.setText("Error: " + ex.getMessage());
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JScrollPane tabla(JTable tabla, String[] columnas) {
        tabla.setModel(new DefaultTableModel(columnas, 0));
        tabla.setAutoCreateRowSorter(true);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(24);
        JScrollPane sp = new JScrollPane(tabla);
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    private void addFila(JPanel panel, GridBagConstraints c, int fila, String etiqueta,
                         JComponent campo) {
        panel.add(new JLabel(etiqueta), gc(0, fila, 1));
        panel.add(campo, gc(1, fila, 1));
    }

    private GridBagConstraints gc() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        return c;
    }

    private GridBagConstraints gc(int x, int y, int ancho) {
        GridBagConstraints c = gc();
        c.gridx = x; c.gridy = y; c.gridwidth = ancho;
        return c;
    }
}
