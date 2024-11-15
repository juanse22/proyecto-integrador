// DatabaseConnection.java
import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/salon_belleza";
    private static final String USER = "root";
    private static final String PASSWORD = "missgarro234";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver no encontrado", e);
        }
    }

    public static void guardarCita(String nombre, String telefono, String servicio,
                                   Date fecha, String hora) throws SQLException {
        String sql = "INSERT INTO citas (nombre, telefono, servicio, fecha, hora) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, telefono);
            pstmt.setString(3, servicio);
            pstmt.setDate(4, fecha);
            pstmt.setString(5, hora);

            pstmt.executeUpdate();
        }
    }

    // Método para probar la conexión
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}