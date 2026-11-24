/*
 * Autor original: Cristian A
 * Completado como parte del Entregable 7.
 */
package aplicaciongimnasio.servicio;

import aplicaciongimnasio.modelo.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Clase agregado "Gimnasio": concentra la logica de negocio y mantiene las
 * colecciones de afiliados, invitados, visitas, referidos, facturas y los
 * parametros configurables. Es el punto de entrada de la logica para la GUI.
 */
public class Gimnasio implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String direccion;
    private double tarifaPaseDiarioInvitado;

    private final Map<String, Afiliado> afiliados = new LinkedHashMap<>();
    private final Map<String, Invitado> invitados = new LinkedHashMap<>();
    private final List<RegistroVisita> visitas = new ArrayList<>();
    private final List<RegistroReferido> referidos = new ArrayList<>();
    private final List<Factura> facturas = new ArrayList<>();
    private final Map<String, ParametrosMembresia> parametros = new LinkedHashMap<>();
    private int consecutivo = 1;

    public Gimnasio(String nombre, String direccion, double tarifaPaseDiarioInvitado) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.tarifaPaseDiarioInvitado = tarifaPaseDiarioInvitado;
        configurarValoresPorDefecto();
    }

    /** Configuracion inicial de tipos de membresia y descuentos. */
    private void configurarValoresPorDefecto() {
        parametros.put("Basica", new ParametrosMembresia("Basica", 80000, 0, 0, 0));
        parametros.put("Premium", new ParametrosMembresia("Premium", 120000, 25000, 0, 5));
        parametros.put("VIP", new ParametrosMembresia("VIP", 180000, 25000, 35000, 10));
        // La membresia VIP incluye ademas las clases grupales del plan Premium.
    }

    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public double getTarifaPaseDiarioInvitado() { return tarifaPaseDiarioInvitado; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setTarifaPaseDiarioInvitado(double tarifaPaseDiarioInvitado) {
        this.tarifaPaseDiarioInvitado = Math.max(0, tarifaPaseDiarioInvitado);
    }

    public Collection<Afiliado> getAfiliados() {
        return Collections.unmodifiableCollection(afiliados.values());
    }

    public Collection<Invitado> getInvitados() {
        return Collections.unmodifiableCollection(invitados.values());
    }

    public List<RegistroVisita> getVisitas() {
        return Collections.unmodifiableList(visitas);
    }

    public List<RegistroReferido> getReferidos() {
        return Collections.unmodifiableList(referidos);
    }

    public List<Factura> getFacturas() {
        return Collections.unmodifiableList(facturas);
    }

    public Collection<ParametrosMembresia> getParametros() {
        return Collections.unmodifiableCollection(parametros.values());
    }

    public ParametrosMembresia getParametro(String tipo) {
        return parametros.get(tipo);
    }

    public Afiliado buscarAfiliado(String codigo) {
        return codigo == null ? null : afiliados.get(codigo.trim());
    }

    public Afiliado buscarAfiliadoPorDocumento(String documento) {
        if (documento == null) return null;
        for (Afiliado a : afiliados.values()) {
            if (documento.equals(a.getDocumento())) return a;
        }
        return null;
    }

    public Invitado buscarInvitado(String codigo) {
        return codigo == null ? null : invitados.get(codigo.trim());
    }

    /** Metodo original completado (se conserva el nombre del esqueleto). */
    public void reigstrarPersona(Persona p) {
        if (p instanceof Afiliado) {
            afiliados.put(((Afiliado) p).getCodigoAfiliado(), (Afiliado) p);
        } else if (p instanceof Invitado) {
            invitados.put(((Invitado) p).getCodigoInvitado(), (Invitado) p);
        }
    }

    public void registrarAfiliado(Afiliado a) {
        reigstrarPersona(a);
    }

    public void registrarInvitado(Invitado i) {
        reigstrarPersona(i);
    }

    public void registrarSuscripcion(Membresia m) {
        if (m != null && !parametros.containsKey(m.getTipo())) {
            parametros.put(m.getTipo(), new ParametrosMembresia(m.getTipo(),
                    m.getPrecioBase(), 0, 0, m.getPorcentajeDescuentoReferido()));
        }
    }

    public void registrarIngreso(RegistroVisita r) {
        if (r != null) {
            visitas.add(r);
        }
    }

    public void renovarSuscripcion(Membresia m) {
        if (m != null) {
            m.renovar();
        }
    }

    public void suspenderSuscripcion(Membresia m) {
        if (m != null) {
            m.suspender();
        }
    }

    public String siguienteCodigo(String prefijo) {
        return prefijo + String.format("%04d", consecutivo++);
    }

    public void ajustarConsecutivo(int valor) {
        consecutivo = Math.max(consecutivo, valor);
    }

    // ------------------------------------------------------------------
    // Registro de afiliados con referido y descuentos
    // ------------------------------------------------------------------

    /**
     * Registra un nuevo afiliado. Si el codigo de referido corresponde a un
     * afiliado existente, aplica descuento tanto al referidor como al nuevo
     * afiliado, incrementa el contador de referidos y guarda el registro.
     *
     * @return factura generada por la afiliacion
     */
    public Factura registrarAfiliadoConReferido(Afiliado nuevo, String tipoMembresia,
                                                 String codigoReferidor) {
        ParametrosMembresia p = parametros.get(tipoMembresia);
        if (p == null) {
            throw new IllegalArgumentException("Tipo de membresia desconocido: " + tipoMembresia);
        }

        Membresia membresia = p.crearMembresia();
        nuevo.setMembresia(membresia);
        registrarAfiliado(nuevo);
        registrarSuscripcion(membresia);

        double descuentoNuevo = 0.0;
        double descuentoReferidor = 0.0;

        Afiliado referidor = buscarAfiliado(codigoReferidor);
        if (referidor != null && referidor != nuevo) {
            double porcentaje = referidor.getMembresia() != null
                    ? referidor.getMembresia().getPorcentajeDescuentoReferido()
                    : p.getPorcentajeDescuentoReferido();

            // Descuento para el nuevo afiliado en su primera mensualidad.
            descuentoNuevo = round(membresia.calcularCostoMensual() * porcentaje / 100.0);
            membresia.acumularDescuento(porcentaje);

            // Descuento para el afiliado que refirio en su proxima mensualidad.
            if (referidor.getMembresia() != null) {
                descuentoReferidor = round(referidor.getMembresia().calcularCostoMensual()
                        * porcentaje / 100.0);
                referidor.getMembresia().acumularDescuento(porcentaje);
            }

            referidor.incrementarReferidos();
            nuevo.setCodigoReferidor(referidor.getCodigoAfiliado());

            RegistroReferido rr = new RegistroReferido(LocalDate.now().toString(),
                    referidor.getCodigoAfiliado(), nuevo.getCodigoAfiliado(), porcentaje,
                    descuentoReferidor, descuentoNuevo);
            referidos.add(rr);

            if (referidor.tieneBeneficioPorReferidos()) {
                RegistroReferido beneficio = new RegistroReferido(LocalDate.now().toString(),
                        referidor.getCodigoAfiliado(), nuevo.getCodigoAfiliado(),
                        100.0 / Afiliado.REFERIDOS_PARA_BENEFICIO, 0, 0);
                beneficio.setExitoso(true);
                referidos.add(beneficio);
                Membresia mReferidor = referidor.getMembresia();
                if (mReferidor != null) {
                    double costo = mReferidor.calcularCostoMensual();
                    facturas.add(new Factura(siguienteCodigo("F-"), nombre, direccion,
                            referidor.getNombre(), referidor.getDocumento(),
                            Factura.TipoPago.RENOVACION,
                            "Beneficio por " + referidor.getConteoReferidosExitosos()
                                    + " referidos: mes gratis",
                            costo, costo));
                    mReferidor.acumularDescuento(100.0);
                }
            }
        }

        Factura factura = new Factura(siguienteCodigo("F-"), nombre, direccion,
                nuevo.getNombre(), nuevo.getDocumento(), Factura.TipoPago.AFILIACION,
                "Afiliacion " + tipoMembresia, membresia.calcularCostoMensual(), descuentoNuevo);
        if (referidor != null) {
            factura.agregarNota("Referido por " + referidor.getCodigoAfiliado()
                    + " | Descuento referidor: $" + descuentoReferidor);
        }
        if (nuevo.getMembresia() != null && nuevo.getMembresia().estaActiva()) {
            factura.agregarNota("Membresia activa desde " + membresia.getFechaInicio()
                    + " hasta " + membresia.getFechaVencimiento());
        }
        facturas.add(factura);
        return factura;
    }

    // ------------------------------------------------------------------
    // Visitantes y control de acceso
    // ------------------------------------------------------------------

    /** Registra un invitado asociado al afiliado que lo invita. */
    public Invitado registrarInvitado(String codigoAfiliado, String nombre, String documento,
                                      String fotografia, int cupoGratis) {
        Afiliado afiliado = buscarAfiliado(codigoAfiliado);
        if (afiliado == null) {
            throw new IllegalArgumentException("No existe el afiliado invitador: " + codigoAfiliado);
        }
        Invitado invitado = new Invitado(
                siguienteCodigo("INV-"), cupoGratis, 0,
                siguienteCodigo("P-"), documento, nombre, "", "", "", fotografia);
        invitado.setCodigoAfiliadoInvitador(afiliado.getCodigoAfiliado());
        afiliado.agregarInvitado(invitado);
        invitados.put(invitado.getCodigoInvitado(), invitado);
        return invitado;
    }

    /**
     * Registra el ingreso diario de un afiliado o invitado. Valida las reglas
     * de acceso correspondientes y devuelve el registro creado. Si el invitado
     * supera su cupo gratuito, genera una factura por el pase diario.
     *
     * @return el registro de visita con el resultado de la validacion
     */
    public RegistroVisita registrarIngreso(String codigo, boolean esAfiliado) {
        RegistroVisita visita;
        if (esAfiliado) {
            Afiliado afiliado = buscarAfiliado(codigo);
            if (afiliado == null) {
                throw new IllegalArgumentException("Afiliado no encontrado: " + codigo);
            }
            visita = RegistroVisita.ahora(siguienteCodigo("V-"), codigo, "Afiliado");
            visita.setResultadoAcceso(afiliado.validarAcceso());
            visita.setEsGratis(true);
            if (afiliado.puedeIngresar()) {
                afiliado.agregarVisita(visita);
            }
        } else {
            Invitado invitado = buscarInvitado(codigo);
            if (invitado == null) {
                throw new IllegalArgumentException("Invitado no encontrado: " + codigo);
            }
            visita = RegistroVisita.ahora(siguienteCodigo("V-"), codigo, "Invitado");
            visita.setResultadoAcceso(invitado.validarAcceso());
            visita.calcularCosto(invitado, tarifaPaseDiarioInvitado);
            if (visita.isEsGratis()) {
                invitado.consumirVisitaGratis();
            } else {
                Factura f = new Factura(siguienteCodigo("F-"), nombre, direccion,
                        invitado.getNombre(), invitado.getDocumento(),
                        Factura.TipoPago.VISITA_INVITADO,
                        "Pase diario - visita " + (invitado.getVisitasConsumidasMes() + 1),
                        tarifaPaseDiarioInvitado, 0);
                f.agregarNota("Supero el cupo de " + invitado.getCupoGratisMensual()
                        + " visitas gratuitas del mes.");
                facturas.add(f);
                visita.setResultadoAcceso(visita.getResultadoAcceso()
                        + " | Factura " + f.getNumero());
            }
            invitado.agregarVisita(visita);
        }
        visitas.add(visita);
        return visita;
    }

    /** Renueva la membresia de un afiliado y genera la factura del pago. */
    public Factura renovarMembresia(String codigoAfiliado) {
        Afiliado afiliado = buscarAfiliado(codigoAfiliado);
        if (afiliado == null || afiliado.getMembresia() == null) {
            throw new IllegalArgumentException("Afiliado sin membresia: " + codigoAfiliado);
        }
        Membresia m = afiliado.getMembresia();
        double subtotal = m.calcularCostoMensual();
        double descuento = round(subtotal * m.getPorcentajeDescuentoAplicable() / 100.0);
        m.renovar();
        Factura f = new Factura(siguienteCodigo("F-"), nombre, direccion,
                afiliado.getNombre(), afiliado.getDocumento(), Factura.TipoPago.RENOVACION,
                "Renovacion " + m.getTipo(), subtotal, descuento);
        f.agregarNota("Nuevo vencimiento: " + m.getFechaVencimiento());
        facturas.add(f);
        return f;
    }

    public void suspenderMembresia(String codigoAfiliado) {
        Afiliado a = buscarAfiliado(codigoAfiliado);
        if (a == null || a.getMembresia() == null) {
            throw new IllegalArgumentException("Afiliado sin membresia: " + codigoAfiliado);
        }
        a.getMembresia().suspender();
    }

    /**
     * Convierte un invitado en afiliado conservando su historial de visitas y
     * el registro de quien lo invito originalmente.
     */
    public Afiliado convertirInvitadoEnAfiliado(String codigoInvitado, String tipoMembresia) {
        Invitado invitado = buscarInvitado(codigoInvitado);
        if (invitado == null) {
            throw new IllegalArgumentException("Invitado no encontrado: " + codigoInvitado);
        }
        ParametrosMembresia p = parametros.get(tipoMembresia);
        if (p == null) {
            throw new IllegalArgumentException("Tipo de membresia desconocido: " + tipoMembresia);
        }

        String codigoAfiliado = siguienteCodigo("AF-");
        Afiliado afiliado = new Afiliado(codigoAfiliado, 0, invitado.getId(),
                invitado.getDocumento(), invitado.getNombre(), invitado.getTelefono(),
                invitado.getCorreo(), invitado.getFechaNacimiento(), invitado.getFotografia());
        afiliado.setMembresia(p.crearMembresia());
        afiliado.setCodigoReferidor(invitado.getCodigoAfiliadoInvitador());

        // Se conserva el historial previo como invitado.
        for (RegistroVisita rv : invitado.getHistorialVisitas()) {
            afiliado.agregarVisita(rv);
        }
        afiliados.put(codigoAfiliado, afiliado);
        invitado.setConvertidoAAfiliado(true);

        Factura f = new Factura(siguienteCodigo("F-"), nombre, direccion,
                afiliado.getNombre(), afiliado.getDocumento(), Factura.TipoPago.AFILIACION,
                "Afiliacion por conversion de invitado (" + tipoMembresia + ")",
                afiliado.getMembresia().calcularCostoMensual(), 0);
        f.agregarNota("Ingreso original como invitado de "
                + invitado.getCodigoAfiliadoInvitador()
                + " | Visitas previas conservadas: " + invitado.getHistorialVisitas().size());
        facturas.add(f);
        return afiliado;
    }

    /** Ranking de afiliados con mas referidos exitosos. */
    public List<Afiliado> rankingReferidos() {
        List<Afiliado> lista = new ArrayList<>(afiliados.values());
        lista.sort(Comparator.comparingInt(Afiliado::getConteoReferidosExitosos).reversed());
        return lista;
    }

    /** Actualiza los parametros administrativos de un tipo de membresia. */
    public void actualizarParametros(String tipo, double precioBase, double costoClases,
                                     double costoEntrenador, double descuentoReferido) {
        ParametrosMembresia p = parametros.get(tipo);
        if (p == null) {
            throw new IllegalArgumentException("Tipo desconocido: " + tipo);
        }
        p.setPrecioBase(precioBase);
        p.setCostoClases(costoClases);
        p.setCostoEntrenadorSesion(costoEntrenador);
        p.setPorcentajeDescuentoReferido(descuentoReferido);
    }

    /** Restaura el estado interno luego de cargar desde archivo. */
    void restore(String n, String d, double tarifa, int consec,
                 Collection<Afiliado> afs, Collection<Invitado> invs,
                 List<RegistroVisita> viss, List<RegistroReferido> refs,
                 List<Factura> fats, Collection<ParametrosMembresia> pars) {
        nombre = n;
        direccion = d;
        tarifaPaseDiarioInvitado = tarifa;
        consecutivo = consec;
        afiliados.clear();
        for (Afiliado a : afs) afiliados.put(a.getCodigoAfiliado(), a);
        invitados.clear();
        for (Invitado i : invs) invitados.put(i.getCodigoInvitado(), i);
        visitas.clear(); visitas.addAll(viss);
        referidos.clear(); referidos.addAll(refs);
        facturas.clear(); facturas.addAll(fats);
        parametros.clear();
        for (ParametrosMembresia p : pars) parametros.put(p.getTipo(), p);
        if (parametros.isEmpty()) configurarValoresPorDefecto();
    }

    int getConsecutivo() { return consecutivo; }

    private static double round(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
