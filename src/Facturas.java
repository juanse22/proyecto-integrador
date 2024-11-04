import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class Facturas {
    Connection conexion;
    PreparedStatement preparar;
    ResultSet resultado;

    public Facturas() {
        conectar();
    }

    public void conectar() {
        try {
            conexion = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/?user=root",
                    "root",
                    "missgarro234"
            );
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base datos: " + e.getMessage());
        }
    }

    public void generarFactura(String factura) {
        try {
            String usuario = obtenerNombreUsuario(factura);
            String valorVenta = obtenerValorVenta(factura);
            String nombreVendedor = obtenerNombreVendedor(factura);

            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream("Factura.pdf"));
            documento.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
            documento.add(new Paragraph("FACTURA DE COMPRA", fontTitle));
            documento.add(new Paragraph("Nombre del Comprador: " + usuario));
            documento.add(new Paragraph("Valor de la venta: " + valorVenta));
            documento.add(new Paragraph("Nombre del Vendedor: " + nombreVendedor));

            Image imagenLogo = Image.getInstance("simple.jpg");
            imagenLogo.scaleToFit(100, 100);
            imagenLogo.setAlignment(Image.ALIGN_RIGHT);
            documento.add(imagenLogo);

            documento.close();
            System.out.println("La factura se ha generado y guardado en Factura.pdf");
        } catch (DocumentException | IOException e) {
            System.out.println("Error al generar el certificado: " + e.getMessage());
        }
    }

    String obtenerNombreUsuario(String factura) {
        String usuario = null;
        try {
            String sql = "select usuario from facturas where id_factura=?";
            preparar = conexion.prepareStatement(sql);
            preparar.setString(1, factura);
            resultado = preparar.executeQuery();
            if (resultado.next()) {
                usuario = resultado.getString("usuario");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el nombre de usuario: " + e.getMessage());
        }
        return usuario;
    }

    String obtenerValorVenta(String factura) {
        String valorVenta = null;
        try {
            String sql = "select valor_venta from facturas where id_factura=?";
            preparar = conexion.prepareStatement(sql);
            preparar.setString(1, factura);
            resultado = preparar.executeQuery();
            if (resultado.next()) {
                valorVenta = resultado.getString("valor_venta");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el valor de la venta: " + e.getMessage());
        }
        return valorVenta;
    }

    String obtenerNombreVendedor(String factura) {
        String nombreVendedor = null;
        try {
            String sql = "select nombre_vendedor from facturas where id_factura=?";
            preparar = conexion.prepareStatement(sql);
            preparar.setString(1, factura);
            resultado = preparar.executeQuery();
            if (resultado.next()) {
                nombreVendedor = resultado.getString("nombre_vendedor");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el nombre del vendedor: " + e.getMessage());
        }
        return nombreVendedor;
    }

    public static void main(String[] args) {
        Facturas factura1 = new Facturas();
        factura1.generarFactura("1002");
    }
}