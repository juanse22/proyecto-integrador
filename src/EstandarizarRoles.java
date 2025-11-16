import java.sql.*;

/**
 * Programa para estandarizar todos los roles a minúsculas
 */
public class EstandarizarRoles {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/salon_aremi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "missgarro234";

    public static void main(String[] args) {
        System.out.println("=== ESTANDARIZANDO ROLES ===\n");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("✓ Conexión exitosa\n");

            // Estandarizar todos los roles a minúsculas
            String query = "UPDATE usuarios SET rol = LOWER(rol)";
            try (Statement stmt = conn.createStatement()) {
                int filasActualizadas = stmt.executeUpdate(query);
                System.out.println("✓ " + filasActualizadas + " usuario(s) actualizado(s)\n");
            }

            // Mostrar resultado
            System.out.println("--- USUARIOS DESPUÉS DE ESTANDARIZAR ---");
            String querySelect = "SELECT username, nombre_completo, rol FROM usuarios ORDER BY username";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(querySelect)) {

                System.out.printf("%-20s %-30s %-15s%n", "USERNAME", "NOMBRE COMPLETO", "ROL");
                System.out.println("----------------------------------------------------------------");

                while (rs.next()) {
                    String username = rs.getString("username");
                    String nombreCompleto = rs.getString("nombre_completo");
                    String rol = rs.getString("rol");
                    System.out.printf("%-20s %-30s %-15s%n", username, nombreCompleto, rol);
                }
            }

            System.out.println("\n✓ Todos los roles ahora están en minúsculas ('administrador' y 'empleado')");

        } catch (SQLException e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}