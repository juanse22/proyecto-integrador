import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase temporal para verificar y asegurar que fernanda tenga permisos de administrador
 */
public class VerificarYActualizarUsuario {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/salon_aremi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "missgarro234";

    public static void main(String[] args) {
        System.out.println("=== VERIFICANDO Y ACTUALIZANDO USUARIO FERNANDA ===\n");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("✓ Conexión exitosa a la base de datos\n");

            // 1. Ver todos los usuarios actuales
            System.out.println("--- USUARIOS ACTUALES EN LA BASE DE DATOS ---");
            mostrarUsuarios(conn);

            // 2. Verificar si fernanda existe
            boolean fernandaExiste = verificarUsuarioExiste(conn, "fernanda");

            if (fernandaExiste) {
                System.out.println("\n--- ACTUALIZANDO USUARIO FERNANDA ---");
                // Actualizar fernanda para asegurar que sea administrador
                actualizarUsuarioFernanda(conn);
            } else {
                System.out.println("\n--- CREANDO USUARIO FERNANDA ---");
                // Crear fernanda como administrador
                crearUsuarioFernanda(conn);
            }

            // 3. Mostrar usuarios después del cambio
            System.out.println("\n--- USUARIOS DESPUÉS DE LA ACTUALIZACIÓN ---");
            mostrarUsuarios(conn);

            System.out.println("\n✓ Proceso completado exitosamente!");
            System.out.println("\nAhora puedes iniciar sesión con:");
            System.out.println("  Usuario: fernanda");
            System.out.println("  Contraseña: fernanda123");
            System.out.println("  Rol: administrador");

        } catch (SQLException e) {
            System.err.println("✗ Error de base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void mostrarUsuarios(Connection conn) throws SQLException {
        String query = "SELECT username, nombre_completo, rol FROM usuarios ORDER BY username";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.printf("%-20s %-30s %-15s%n", "USERNAME", "NOMBRE COMPLETO", "ROL");
            System.out.println("----------------------------------------------------------------");

            while (rs.next()) {
                String username = rs.getString("username");
                String nombreCompleto = rs.getString("nombre_completo");
                String rol = rs.getString("rol");
                System.out.printf("%-20s %-30s %-15s%n", username, nombreCompleto, rol);
            }
        }
    }

    private static boolean verificarUsuarioExiste(Connection conn, String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private static void actualizarUsuarioFernanda(Connection conn) throws SQLException {
        String passwordHash = hashPassword("fernanda123");
        String query = "UPDATE usuarios SET password = ?, nombre_completo = ?, rol = ? WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, passwordHash);
            stmt.setString(2, "Fernanda Rodriguez");
            stmt.setString(3, "administrador");
            stmt.setString(4, "fernanda");

            int filasActualizadas = stmt.executeUpdate();
            System.out.println("✓ Usuario 'fernanda' actualizado: " + filasActualizadas + " fila(s) afectada(s)");
            System.out.println("  - Rol establecido: administrador");
            System.out.println("  - Contraseña actualizada: fernanda123");
        }
    }

    private static void crearUsuarioFernanda(Connection conn) throws SQLException {
        String passwordHash = hashPassword("fernanda123");
        String query = "INSERT INTO usuarios (username, password, nombre_completo, rol) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "fernanda");
            stmt.setString(2, passwordHash);
            stmt.setString(3, "Fernanda Rodriguez");
            stmt.setString(4, "administrador");

            int filasInsertadas = stmt.executeUpdate();
            System.out.println("✓ Usuario 'fernanda' creado: " + filasInsertadas + " fila(s) insertada(s)");
            System.out.println("  - Rol: administrador");
            System.out.println("  - Contraseña: fernanda123");
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}