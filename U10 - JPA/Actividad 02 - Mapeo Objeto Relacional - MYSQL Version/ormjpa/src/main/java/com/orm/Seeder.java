package com.orm;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Carga datos de prueba pensados para validar las 22 consultas JPQL del TP.
 *
 * Idea general: los datos NO son al azar. Cada valor esta puesto para que alguna
 * consulta tenga un caso "que entra", un caso "que no entra" y un caso borde.
 *
 * Regla de oro del orden: primero se guarda lo que es apuntado y despues lo que
 * apunta (Usuario -> tablas sueltas -> Contacto/Domicilio -> Cliente -> Articulo
 * -> ListaPrecioArticulo -> FacturaVenta).
 */
public class Seeder {

    // ------------------------------------------------------------------
    // Punto de entrada
    // ------------------------------------------------------------------
    public static void cargarDatos(EntityManager em) {

        // Guarda de seguridad: si ya hay usuarios, la base ya fue cargada.
        // Evita duplicar datos si hbm2ddl.auto esta en "update" en vez de "create".
        Long usuariosExistentes = em.createQuery("SELECT COUNT(u) FROM Usuario u", Long.class)
                .getSingleResult();
        if (usuariosExistentes > 0) {
            System.out.println("[Seeder] La base ya tiene datos, no se carga nada.");
            return;
        }

        em.getTransaction().begin();
        try {
            // --------------------------------------------------------------
            // 1) USUARIOS (lo primero, porque todo lo demas los necesita)
            //    Consultas 10 y 17: admin 7 facturas, maria 5, carlos 2.
            // --------------------------------------------------------------
            Usuario admin = crearUsuario(em, "admin", "Juan", "Pérez");
            Usuario maria = crearUsuario(em, "maria", "María", "Gómez");
            Usuario carlos = crearUsuario(em, "carlos", "Carlos", "López");

            // --------------------------------------------------------------
            // 2) TABLAS SUELTAS (solo dependen del usuario)
            // --------------------------------------------------------------
            Rubro electronica = crearRubro(em, admin, 1, "Electrónica");
            Rubro informatica = crearRubro(em, admin, 2, "Informática");

            // Philips no tiene ningun articulo facturado (consulta 20).
            Marca samsung = crearMarca(em, admin, 1, "Samsung");
            Marca logitech = crearMarca(em, admin, 2, "Logitech");
            Marca philips = crearMarca(em, admin, 3, "Philips");

            TipoMoneda peso = crearTipoMoneda(em, admin, "PES", "Peso Argentino", "$");

            CondicionIva respInscripto = crearCondicionIva(em, admin, 1, "IVA Responsable Inscripto");
            CondicionIva monotributo = crearCondicionIva(em, admin, 6, "Responsable Monotributo");
            CondicionIva consFinal = crearCondicionIva(em, admin, 5, "Consumidor Final");

            // Numeros 1, 2 y 5 entran en la consulta 9 (IN); el 3 no.
            // El 3 (Deposito) no tiene facturas: no aparece en la consulta 16.
            PuntoVenta pv1 = crearPuntoVenta(em, admin, 1, "Casa Central");
            PuntoVenta pv2 = crearPuntoVenta(em, admin, 2, "Sucursal Norte");
            crearPuntoVenta(em, admin, 3, "Deposito");
            PuntoVenta pv5 = crearPuntoVenta(em, admin, 5, "Sucursal Sur");

            ListaPrecio lista = crearListaPrecio(em, admin, "LP2026", "Lista General 2026");

            // --------------------------------------------------------------
            // 3) CLIENTES (cada uno necesita SU PROPIO Contacto y Domicilio,
            //    porque la relacion es OneToOne y no se pueden compartir)
            //
            //    Consulta 6 (texto "andina" o CUIT que empiece con "20-"):
            //      c1 y c3 entran por CUIT, c2 y c4 por nombre (mayusc./minusc.),
            //      c5 no entra por ningun lado.
            // --------------------------------------------------------------
            Cliente c1 = crearCliente(em, admin, "20-12345678-9", "Empresa Cliente SA", "Avenida San Martín", "1234");
            Cliente c2 = crearCliente(em, admin, "30-71234567-8", "Distribuidora ANDINA SRL", "Calle Las Heras", "250");
            Cliente c3 = crearCliente(em, admin, "20-30111222-5", "Kiosco La Esquina", "Calle Mitre", "88");
            Cliente c4 = crearCliente(em, admin, "23-20555666-4", "Ferretería andina Hnos.", "Calle Godoy Cruz", "910");
            Cliente c5 = crearCliente(em, admin, "30-55555555-5", "Cooperativa del Sur", "Ruta 40", "5500");

            // --------------------------------------------------------------
            // 4) ARTICULOS y sus precios
            //    ART004 nunca se factura (consulta 21).
            //    ART005 no tiene marca (consulta 12 la trae con LEFT JOIN,
            //    las consultas con JOIN comun la dejan afuera: 13 y 18).
            // --------------------------------------------------------------
            Articulo monitor = crearArticulo(em, admin, "ART001", "Monitor 24 pulgadas", electronica, samsung);
            Articulo teclado = crearArticulo(em, admin, "ART002", "Teclado mecánico", informatica, logitech);
            Articulo mouse = crearArticulo(em, admin, "ART003", "Mouse inalámbrico", informatica, logitech);
            Articulo auriculares = crearArticulo(em, admin, "ART004", "Auriculares", electronica, philips);
            Articulo hdmi = crearArticulo(em, admin, "ART005", "Cable HDMI", electronica, null);

            // Precios redondos a proposito: asi los totales de las facturas
            // dan exactamente los importes de borde que necesitamos.
            ListaPrecioArticulo pMonitor = crearPrecio(em, admin, lista, monitor, 10000.0);
            ListaPrecioArticulo pTeclado = crearPrecio(em, admin, lista, teclado, 2500.0);
            ListaPrecioArticulo pMouse = crearPrecio(em, admin, lista, mouse, 1000.0);
            crearPrecio(em, admin, lista, auriculares, 5000.0); // existe pero jamas se factura
            ListaPrecioArticulo pHdmi = crearPrecio(em, admin, lista, hdmi, 500.0);

            // --------------------------------------------------------------
            // 5) FACTURAS (14 en total, suman $322.000, promedio $23.000)
            //
            // Parametros: numero, fecha, usuario, cliente, puntoVenta, condIva,
            //             estado, fechaAnulacion (null si no esta anulada), lineas...
            // --------------------------------------------------------------
            LocalDateTime sinAnular = null;

            // F1: $5.000 - BAJO
            crearFactura(em, 1, LocalDateTime.of(2026, 1, 15, 10, 0), admin, c1, pv1, respInscripto, peso,
                    "EMITIDA", sinAnular,
                    new Linea(pTeclado, 2));

            // F2: $9.500 - BAJO (justo debajo de 10.000)
            crearFactura(em, 2, LocalDateTime.of(2026, 2, 10, 11, 30), admin, c2, pv1, monotributo, peso,
                    "PAGADA", sinAnular,
                    new Linea(pMouse, 9), new Linea(pHdmi, 1));

            // F3: $10.000 - borde exacto. Consulta 5 (> 10000) NO la trae.
            //     Fecha = inicio exacto del rango de la consulta 4 (1 de marzo, 00:00).
            crearFactura(em, 3, LocalDateTime.of(2026, 3, 1, 0, 0), admin, c1, pv1, respInscripto, peso,
                    "EMITIDA", sinAnular,
                    new Linea(pMonitor, 1));

            // F4: $10.500 - justo arriba de 10.000. Consulta 5 SI la trae.
            crearFactura(em, 4, LocalDateTime.of(2026, 3, 5, 9, 15), admin, c3, pv1, respInscripto, peso,
                    "EMITIDA", sinAnular,
                    new Linea(pMonitor, 1), new Linea(pHdmi, 1));

            // F5: $25.000
            crearFactura(em, 5, LocalDateTime.of(2026, 3, 15, 14, 0), admin, c2, pv2, respInscripto, peso,
                    "EMITIDA", sinAnular,
                    new Linea(pMonitor, 2), new Linea(pTeclado, 2));

            // F6: $50.000 - borde exacto. En la consulta 22 es MEDIO VALOR.
            crearFactura(em, 6, LocalDateTime.of(2026, 3, 20, 16, 45), admin, c1, pv2, respInscripto, peso,
                    "PAGADA", sinAnular,
                    new Linea(pMonitor, 5));

            // F7: $50.500 - ALTO VALOR.
            //     Fecha = fin exacto del rango de la consulta 4 (31 de marzo, 23:59:59).
            crearFactura(em, 7, LocalDateTime.of(2026, 3, 31, 23, 59, 59), admin, c4, pv2, monotributo, peso,
                    "EMITIDA", sinAnular,
                    new Linea(pMonitor, 5), new Linea(pHdmi, 1));

            // F8: $75.000 - ALTO VALOR. Fecha 1 de abril: justo FUERA del rango de marzo.
            crearFactura(em, 8, LocalDateTime.of(2026, 4, 1, 8, 0), maria, c5, pv5, respInscripto, peso,
                    "EMITIDA", sinAnular,
                    new Linea(pMonitor, 7), new Linea(pTeclado, 2));

            // F9: $12.000 - sin cliente (cliente = null, la consigna lo permite).
            crearFactura(em, 9, LocalDateTime.of(2026, 4, 10, 12, 0), maria, null, pv5, consFinal, peso,
                    "BORRADOR", sinAnular,
                    new Linea(pMonitor, 1), new Linea(pMouse, 2));

            // F10: $30.000 - anulada.
            crearFactura(em, 10, LocalDateTime.of(2026, 4, 20, 15, 30), maria, c1, pv1, respInscripto, peso,
                    "ANULADA", LocalDateTime.of(2026, 4, 21, 9, 0),
                    new Linea(pMonitor, 3));

            // F11: $1.500 - anulada.
            crearFactura(em, 11, LocalDateTime.of(2026, 5, 5, 10, 10), maria, c3, pv2, consFinal, peso,
                    "ANULADA", LocalDateTime.of(2026, 5, 6, 9, 0),
                    new Linea(pMouse, 1), new Linea(pHdmi, 1));

            // F12: $20.000 - caso borde a proposito: estado EMITIDA pero con
            //      fechaAnulacion cargada. Sirve para probar que el IS NULL de la
            //      consulta 5 filtra por la fecha y no solo por el estado.
            crearFactura(em, 12, LocalDateTime.of(2026, 5, 18, 17, 0), maria, c2, pv1, monotributo, peso,
                    "EMITIDA", LocalDateTime.of(2026, 5, 19, 9, 0),
                    new Linea(pMonitor, 2));

            // F13: $15.000 - tiene DOS articulos Logitech (teclado y mouse), por eso
            //      en la consulta 13 aparece duplicada si falta el DISTINCT.
            crearFactura(em, 13, LocalDateTime.of(2026, 6, 2, 9, 0), carlos, c4, pv5, respInscripto, peso,
                    "PAGADA", sinAnular,
                    new Linea(pMonitor, 1), new Linea(pTeclado, 1), new Linea(pMouse, 2), new Linea(pHdmi, 1));

            // F14: $8.000 - sin cliente.
            crearFactura(em, 14, LocalDateTime.of(2026, 6, 25, 13, 0), carlos, null, pv1, consFinal, peso,
                    "EMITIDA", sinAnular,
                    new Linea(pTeclado, 3), new Linea(pHdmi, 1));

            em.getTransaction().commit();
            System.out.println("[Seeder] Datos de prueba cargados correctamente.");

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    // ------------------------------------------------------------------
    // Linea de factura: "este articulo (con su precio), esta cantidad"
    // ------------------------------------------------------------------
    private static class Linea {
        final ListaPrecioArticulo precio;
        final double cantidad;

        Linea(ListaPrecioArticulo precio, double cantidad) {
            this.precio = precio;
            this.cantidad = cantidad;
        }
    }

    // ------------------------------------------------------------------
    // Auditoria: completa los campos obligatorios de AuditoriaApp.
    // fechaBaja y usuarioBaja NO se tocan: quedan en null (= registro activo).
    // ------------------------------------------------------------------
    private static <T extends AuditoriaApp> T auditar(T entidad, Usuario usuario) {
        LocalDateTime ahora = LocalDateTime.now();
        entidad.setFechaAlta(ahora);
        entidad.setFechaModificacion(ahora);
        entidad.setUsuarioCarga(usuario);
        entidad.setUsuarioModificacion(usuario);
        return entidad;
    }

    // ------------------------------------------------------------------
    // Metodos "crear": arman la entidad, la guardan y la devuelven.
    // ------------------------------------------------------------------
    private static Usuario crearUsuario(EntityManager em, String usuario, String nombre, String apellido) {
        Usuario u = new Usuario();
        u.setUsuario(usuario);
        u.setClave("123456");
        u.setNombre(nombre);
        u.setApellido(apellido);
        em.persist(u);
        return u;
    }

    private static Rubro crearRubro(EntityManager em, Usuario carga, int codigo, String denominacion) {
        Rubro r = auditar(new Rubro(), carga);
        r.setCodigo(codigo);
        r.setDenominacion(denominacion);
        em.persist(r);
        return r;
    }

    private static Marca crearMarca(EntityManager em, Usuario carga, int codigo, String denominacion) {
        Marca m = auditar(new Marca(), carga);
        m.setCodigo(codigo);
        m.setDenominacion(denominacion);
        em.persist(m);
        return m;
    }

    private static TipoMoneda crearTipoMoneda(EntityManager em, Usuario carga, String codigoAfip,
            String denominacion, String simbolo) {
        TipoMoneda t = auditar(new TipoMoneda(), carga);
        t.setCodigoAfip(codigoAfip);
        t.setDenominacion(denominacion);
        t.setSimbolo(simbolo);
        em.persist(t);
        return t;
    }

    private static CondicionIva crearCondicionIva(EntityManager em, Usuario carga, int codigoAfip,
            String denominacion) {
        CondicionIva c = auditar(new CondicionIva(), carga);
        c.setCodigoAfip(codigoAfip);
        c.setDenominacion(denominacion);
        em.persist(c);
        return c;
    }

    private static PuntoVenta crearPuntoVenta(EntityManager em, Usuario carga, int numero, String descripcion) {
        PuntoVenta p = auditar(new PuntoVenta(), carga);
        p.setNumero(numero);
        p.setDescripcion(descripcion);
        em.persist(p);
        return p;
    }

    private static ListaPrecio crearListaPrecio(EntityManager em, Usuario carga, String codigo,
            String denominacion) {
        ListaPrecio l = auditar(new ListaPrecio(), carga);
        l.setCodigo(codigo);
        l.setDenominacion(denominacion);
        em.persist(l);
        return l;
    }

    private static Cliente crearCliente(EntityManager em, Usuario carga, String cuitCuil, String denominacion,
            String calle, String numeroCalle) {
        // Primero lo que el cliente necesita (Contacto y Domicilio propios)...
        Contacto contacto = new Contacto();
        contacto.setEmail(denominacion.toLowerCase().replaceAll("[^a-z]", "") + "@ejemplo.com");
        contacto.setTelefono("2614000000");
        contacto.setCelular("2615000000");
        em.persist(contacto);

        Domicilio domicilio = new Domicilio();
        domicilio.setNombreCalle(calle);
        domicilio.setNumeroCalle(numeroCalle);
        em.persist(domicilio);

        // ...y despues el cliente que los usa.
        Cliente c = auditar(new Cliente(), carga);
        c.setCuitCuil(cuitCuil);
        c.setDenominacion(denominacion);
        c.setContacto(contacto);
        c.setDomicilio(domicilio);
        em.persist(c);
        return c;
    }

    private static Articulo crearArticulo(EntityManager em, Usuario carga, String codigo, String denominacion,
            Rubro rubro, Marca marca) {
        Articulo a = auditar(new Articulo(), carga);
        a.setCodigo(codigo);
        a.setDenominacion(denominacion);
        a.setRubro(rubro);
        a.setMarca(marca); // puede ser null: articulo sin marca
        em.persist(a);
        return a;
    }

    private static ListaPrecioArticulo crearPrecio(EntityManager em, Usuario carga, ListaPrecio lista,
            Articulo articulo, double precioVenta) {
        ListaPrecioArticulo lpa = auditar(new ListaPrecioArticulo(), carga);
        lpa.setListaPrecio(lista);
        lpa.setArticulo(articulo);
        lpa.setPrecioVenta(precioVenta);
        em.persist(lpa);
        return lpa;
    }

    // ------------------------------------------------------------------
    // Factura: arma los detalles, calcula el total sumando los subtotales
    // y guarda SOLO la factura (el cascade guarda los detalles).
    // ------------------------------------------------------------------
    private static FacturaVenta crearFactura(EntityManager em, long numero, LocalDateTime fechaEmision,
            Usuario carga, Cliente cliente, PuntoVenta puntoVenta, CondicionIva condicionIva,
            TipoMoneda moneda, String estado, LocalDateTime fechaAnulacion, Linea... lineas) {

        FacturaVenta f = auditar(new FacturaVenta(), carga);
        f.setNumero(numero);
        f.setFechaEmision(fechaEmision);
        f.setCliente(cliente); // puede ser null
        f.setPuntoVenta(puntoVenta);
        f.setCondicionIva(condicionIva);
        f.setTipoMoneda(moneda);
        f.setEstado(estado);
        f.setFechaAnulacion(fechaAnulacion);

        ArrayList<FacturaVentaDetalle> detalles = new ArrayList<>();
        double total = 0;
        for (Linea linea : lineas) {
            double precioUnitario = linea.precio.getPrecioVenta();
            double subtotal = linea.cantidad * precioUnitario;

            FacturaVentaDetalle d = new FacturaVentaDetalle();
            d.setFactura(f);
            d.setListaPrecioArticulo(linea.precio);
            d.setDescripcion(linea.precio.getArticulo().getDenominacion());
            d.setCantidad(linea.cantidad);
            d.setPrecioUnitario(precioUnitario);
            d.setPorcentajeBonificacion(0.0);
            d.setImporteNeto(subtotal);
            d.setImporteIva(0.0);
            d.setImporteSubtotal(subtotal);
            detalles.add(d);

            total += subtotal;
        }
        f.setDetalles(detalles);
        f.setImporteTotal(total);

        // Cobrado / saldo segun el estado
        double cobrado = "PAGADA".equals(estado) ? total : 0.0;
        f.setImporteCobrado(cobrado);
        f.setImporteSaldo(total - cobrado);

        em.persist(f); // el cascade = ALL guarda los detalles solo
        return f;
    }
}
