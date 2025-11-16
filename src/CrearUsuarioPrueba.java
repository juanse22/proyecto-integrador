import java.sql.*;
import java.security.MessageDigest;

/**
 * Script para crear un usuario de prueba en el sistema
 * Ejecuta este archivo para crear/resetear usuarios
 */
public class CrearUsuarioPrueba {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/salon_aremi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "missgarro234";

    public static void main(String[] args) {
        System.out.println("=== CREADOR DE USUARIOS DE PRUEBA ===\n");

        // Crear usuarios de prueba
        crearUsuario("admin", "admin123", "Administrador", "Administrador del Sistema");
        crearUsuario("empleado", "empleado123", "Empleado", "Empleado de Prueba");

        System.out.println("\n=== USUARIOS CREADOS EXITOSAMENTE ===");
        System.out.println("\nCredenciales de acceso:");
        System.out.println("+--------------+---------------+----------------+");
        System.out.println("| Usuario      | Contrasena    | Rol            |");
        System.out.println("+--------------+---------------+----------------+");
        System.out.println("| admin        | admin123      | Administrador  |");
        System.out.println("| empleado     | empleado123   | Empleado       |");
        System.out.println("+--------------+---------------+----------------+");

        // Mostrar todos los usuarios existentes
        mostrarUsuarios();
    }

    private static void crearUsuario(String username, String password, String rol, String nombreCompleto) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Generar hash de la contraseña
            String hashedPassword = hashPassword(password);

            // Verificar si el usuario ya existe
            String checkSql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();

                if (rs.getInt(1) > 0) {
                    // Usuario existe, actualizar contraseña
                    String updateSql = "UPDATE usuarios SET password = ?, rol = ?, nombre_completo = ? WHERE username = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, hashedPassword);
                        updateStmt.setString(2, rol);
                        updateStmt.setString(3, nombreCompleto);
                        updateStmt.setString(4, username);
                        updateStmt.executeUpdate();
                        System.out.println("✓ Usuario '" + username + "' actualizado");
                    }
                } else {
                    // Usuario no existe, crear nuevo
                    String insertSql = "INSERT INTO usuarios (username, password, rol, nombre_completo) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, username);
                        insertStmt.setString(2, hashedPassword);
                        insertStmt.setString(3, rol);
                        insertStmt.setString(4, nombreCompleto);
                        insertStmt.executeUpdate();
                        System.out.println("✓ Usuario '" + username + "' creado");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al crear usuario '" + username + "': " + e.getMessage());
        }
    }

    private static void mostrarUsuarios() {
        System.out.println("\n=== TODOS LOS USUARIOS EN LA BASE DE DATOS ===");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT username, rol, nombre_completo FROM usuarios";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                System.out.println("\n+---------------------+----------------+---------------------------+");
                System.out.println("| Username            | Rol            | Nombre Completo           |");
                System.out.println("+---------------------+----------------+---------------------------+");

                while (rs.next()) {
                    String username = rs.getString("username");
                    String rol = rs.getString("rol");
                    String nombreCompleto = rs.getString("nombre_completo");

                    System.out.printf("| %-19s | %-14s | %-25s |%n",
                        username, rol, nombreCompleto);
                }

                System.out.println("+---------------------+----------------+---------------------------+");
            }
        } catch (SQLException e) {
            System.err.println("Error al mostrar usuarios: " + e.getMessage());
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}