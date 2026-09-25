import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import com.orm.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // Crear Usuario de carga
            Usuario usuarioCarga = new Usuario();
            usuarioCarga.setUsuario("admin");
            usuarioCarga.setClave("123456");
            usuarioCarga.setNombre("Juan");
            usuarioCarga.setApellido("Pérez");
            em.persist(usuarioCarga);

            // Crear TipoMoneda
            TipoMoneda tipoMoneda = new TipoMoneda();
            tipoMoneda.setCodigoAfip("PES");
            tipoMoneda.setDenominacion("Peso Argentino");
            tipoMoneda.setSimbolo("$");
            tipoMoneda.setFechaAlta(LocalDateTime.now());
            tipoMoneda.setFechaModificacion(LocalDateTime.now());
            tipoMoneda.setUsuarioCarga(usuarioCarga);
            tipoMoneda.setUsuarioModificacion(usuarioCarga);
            em.persist(tipoMoneda);

            // Crear CondicionIva
            CondicionIva condicionIva = new CondicionIva();
            condicionIva.setCodigoAfip(1);
            condicionIva.setDenominacion("IVA Responsable Inscripto");
            condicionIva.setFechaAlta(LocalDateTime.now());
            condicionIva.setFechaModificacion(LocalDateTime.now());
            condicionIva.setUsuarioCarga(usuarioCarga);
            condicionIva.setUsuarioModificacion(usuarioCarga);
            em.persist(condicionIva);

            // Crear PuntoVenta
            PuntoVenta puntoVenta = new PuntoVenta();
            puntoVenta.setNumero(1);
            puntoVenta.setDescripcion("Punto de Venta Principal");
            puntoVenta.setFechaAlta(LocalDateTime.now());
            puntoVenta.setFechaModificacion(LocalDateTime.now());
            puntoVenta.setUsuarioCarga(usuarioCarga);
            puntoVenta.setUsuarioModificacion(usuarioCarga);
            em.persist(puntoVenta);

            // Crear Rubro
            Rubro rubro = new Rubro();
            rubro.setCodigo(1);
            rubro.setDenominacion("Electrónica");
            rubro.setFechaAlta(LocalDateTime.now());
            rubro.setFechaModificacion(LocalDateTime.now());
            rubro.setUsuarioCarga(usuarioCarga);
            rubro.setUsuarioModificacion(usuarioCarga);
            em.persist(rubro);

            // Crear Marca
            Marca marca = new Marca();
            marca.setCodigo(1);
            marca.setDenominacion("Samsung");
            marca.setFechaAlta(LocalDateTime.now());
            marca.setFechaModificacion(LocalDateTime.now());
            marca.setUsuarioCarga(usuarioCarga);
            marca.setUsuarioModificacion(usuarioCarga);
            em.persist(marca);

            // Crear ListaPrecio
            ListaPrecio listaPrecio = new ListaPrecio();
            listaPrecio.setCodigo("LP001");
            listaPrecio.setDenominacion("Lista Precio 2024");
            listaPrecio.setFechaAlta(LocalDateTime.now());
            listaPrecio.setFechaModificacion(LocalDateTime.now());
            listaPrecio.setUsuarioCarga(usuarioCarga);
            listaPrecio.setUsuarioModificacion(usuarioCarga);
            em.persist(listaPrecio);

            // Crear Articulo
            Articulo articulo = new Articulo();
            articulo.setCodigo("ART001");
            articulo.setDenominacion("Monitor 24 pulgadas");
            articulo.setRubro(rubro);
            articulo.setMarca(marca);
            articulo.setFechaAlta(LocalDateTime.now());
            articulo.setFechaModificacion(LocalDateTime.now());
            articulo.setUsuarioCarga(usuarioCarga);
            articulo.setUsuarioModificacion(usuarioCarga);
            em.persist(articulo);

            // Crear ListaPrecioArticulo
            ListaPrecioArticulo listaPrecioArticulo = new ListaPrecioArticulo();
            listaPrecioArticulo.setListaPrecio(listaPrecio);
            listaPrecioArticulo.setArticulo(articulo);
            listaPrecioArticulo.setPrecioVenta(150.00);
            listaPrecioArticulo.setFechaAlta(LocalDateTime.now());
            listaPrecioArticulo.setFechaModificacion(LocalDateTime.now());
            listaPrecioArticulo.setUsuarioCarga(usuarioCarga);
            listaPrecioArticulo.setUsuarioModificacion(usuarioCarga);
            em.persist(listaPrecioArticulo);

            // Crear Contacto
            Contacto contacto = new Contacto();
            contacto.setEmail("cliente@ejemplo.com");
            contacto.setTelefono("2612345678");
            contacto.setCelular("2619876543");
            em.persist(contacto);

            // Crear Domicilio
            Domicilio domicilio = new Domicilio();
            domicilio.setNombreCalle("Avenida San Martín");
            domicilio.setNumeroCalle("1234");
            em.persist(domicilio);

            // Crear Cliente
            Cliente cliente = new Cliente();
            cliente.setCuitCuil("20-12345678-9");
            cliente.setDenominacion("Empresa Cliente SA");
            cliente.setContacto(contacto);
            cliente.setDomicilio(domicilio);
            cliente.setFechaAlta(LocalDateTime.now());
            cliente.setFechaModificacion(LocalDateTime.now());
            cliente.setUsuarioCarga(usuarioCarga);
            cliente.setUsuarioModificacion(usuarioCarga);
            em.persist(cliente);

            // Crear FacturaVenta
            FacturaVenta facturaVenta = new FacturaVenta();
            facturaVenta.setNumero(1L);
            facturaVenta.setFechaEmision(LocalDateTime.now());
            facturaVenta.setCliente(cliente);
            facturaVenta.setCondicionIva(condicionIva);
            facturaVenta.setTipoMoneda(tipoMoneda);
            facturaVenta.setPuntoVenta(puntoVenta);
            facturaVenta.setEstado("EMITIDA");
            facturaVenta.setFechaAlta(LocalDateTime.now());
            facturaVenta.setFechaModificacion(LocalDateTime.now());
            facturaVenta.setUsuarioCarga(usuarioCarga);
            facturaVenta.setUsuarioModificacion(usuarioCarga);

            // Crear FacturaVentaDetalle
            FacturaVentaDetalle detalle1 = new FacturaVentaDetalle();
            detalle1.setFactura(facturaVenta);
            detalle1.setListaPrecioArticulo(listaPrecioArticulo);
            detalle1.setDescripcion("Monitor 24 pulgadas Samsung");
            detalle1.setCantidad(2.0);
            detalle1.setPrecioUnitario(150.00);
            detalle1.setImporteSubtotal(300.00);

            // Crear lista de detalles
            List<FacturaVentaDetalle> detalles = new ArrayList<>();
            detalles.add(detalle1);
            facturaVenta.setDetalles(detalles);
            facturaVenta.setImporteTotal(300.00);

            // Persistir solo la FacturaVenta (cascada persiste automáticamente los detalles)
            em.persist(facturaVenta);

            em.getTransaction().commit();

            System.out.println("Factura persistida exitosamente con cascada.");

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
