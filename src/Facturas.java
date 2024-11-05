import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class Facturas {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/salon_aremi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "missgarro234";

    public Facturas() {
        conectar();
    }

    public void conectar() {
        try {
            Connection conexion = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (conexion != null) {
                System.out.println("Conexión exitosa a la base de datos.");
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    public void generarFactura(String factura) {
        try (Connection conexion = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String usuario = obtenerNombreUsuario(conexion, factura);
            String valorVenta = obtenerValorVenta(conexion, factura);
            String nombreVendedor = obtenerNombreVendedor(conexion, factura);

            if (usuario == null || valorVenta == null || nombreVendedor == null) {
                System.out.println("No se pudo obtener la información completa de la factura.");
                return;
            }

            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream("Factura.pdf"));
            documento.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
            documento.add(new Paragraph("FACTURA DE COMPRA", fontTitle));
            documento.add(new Paragraph("Nombre del Comprador: " + usuario));
            documento.add(new Paragraph("Valor de la venta: " + valorVenta));
            documento.add(new Paragraph("Nombre del Vendedor: " + nombreVendedor));

            String rutaImagen = "C:\\Users\\Juan Sebastian\\IdeaProjects\\Proyecto-Integrador\\src\\AREMI.png";
            Image imagenLogo = Image.getInstance(rutaImagen);
            imagenLogo.scaleToFit(100, 100);
            imagenLogo.setAlignment(Image.ALIGN_RIGHT);
            documento.add(imagenLogo);

            documento.close();
            System.out.println("La factura se ha generado y guardado en Factura.pdf");
        } catch (DocumentException | IOException | SQLException e) {
            System.out.println("Error al generar el certificado: " + e.getMessage());
        }
    }

    private String obtenerNombreUsuario(Connection conexion, String factura) {
        String usuario = null;
        try (PreparedStatement preparar = conexion.prepareStatement("SELECT usuario FROM facturas WHERE id_factura=?")) {
            preparar.setString(1, factura);
            try (ResultSet resultado = preparar.executeQuery()) {
                if (resultado.next()) {
                    usuario = resultado.getString("usuario");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el nombre de usuario: " + e.getMessage());
        }
        return usuario;
    }

    private String obtenerValorVenta(Connection conexion, String factura) {
        String valorVenta = null;
        try (PreparedStatement preparar = conexion.prepareStatement("SELECT valor_venta FROM facturas WHERE id_factura=?")) {
            preparar.setString(1, factura);
            try (ResultSet resultado = preparar.executeQuery()) {
                if (resultado.next()) {
                    valorVenta = resultado.getString("valor_venta");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el valor de la venta: " + e.getMessage());
        }
        return valorVenta;
    }

    private String obtenerNombreVendedor(Connection conexion, String factura) {
        String nombreVendedor = null;
        try (PreparedStatement preparar = conexion.prepareStatement("SELECT nombre_vendedor FROM facturas WHERE id_factura=?")) {
            preparar.setString(1, factura);
            try (ResultSet resultado = preparar.executeQuery()) {
                if (resultado.next()) {
                    nombreVendedor = resultado.getString("nombre_vendedor");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el nombre del vendedor: " + e.getMessage());
        }
        return nombreVendedor;
    }

    public static void main(String[] args) {
        Facturas factura1 = new Facturas();
        factura1.generarFactura("1"); // Asegúrate de que el ID exista en la base de datos
    }
}
