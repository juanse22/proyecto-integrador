import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase para gestionar la seguridad y control de acceso del sistema.
 * Maneja roles de usuario y permisos de forma centralizada.
 *
 * @author Sistema Aremi
 * @version 2.0
 */
public class SeguridadManager {

    // Roles del sistema
    public static final String ROL_ADMINISTRADOR = "administrador";
    public static final String ROL_EMPLEADO = "empleado";

    // Usuario actual en sesión (patrón Singleton simple)
    private static String usuarioActual = null;
    private static String rolActual = null;

    /**
     * Establece el usuario actual en sesión
     */
    public static void setUsuarioActual(String usuario, String rol) {
        usuarioActual = usuario;
        rolActual = rol;
    }

    /**
     * Obtiene el usuario actual
     */
    public static String getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Obtiene el rol del usuario actual
     */
    public static String getRolActual() {
        return rolActual;
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    public static boolean hayUsuarioAutenticado() {
        return usuarioActual != null && rolActual != null;
    }

    /**
     * Verifica si el usuario actual es administrador
     */
    public static boolean esAdministrador() {
        return ROL_ADMINISTRADOR.equals(rolActual);
    }

    /**
     * Verifica si el usuario actual es empleado
     */
    public static boolean esEmpleado() {
        return ROL_EMPLEADO.equals(rolActual);
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public static void cerrarSesion() {
        usuarioActual = null;
        rolActual = null;
    }

    /**
     * Hashea una contraseña usando SHA-256 (mismo método que LoginSystemAremi)
     * @param password Contraseña en texto plano
     * @return Hash SHA-256 de la contraseña
     */
    public static String hashPassword(String password) {
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

    /**
     * Verifica si el usuario tiene permisos para una acción específica
     * @param accion Acción a verificar (ej: "gestionar_inventario", "ver_nomina")
     * @return true si tiene permiso
     */
    public static boolean tienePermiso(String accion) {
        if (!hayUsuarioAutenticado()) return false;

        // Administrador tiene todos los permisos
        if (esAdministrador()) return true;

        // Permisos específicos para empleados
        switch (accion) {
            case "agendar_cita":
            case "registrar_servicio":
            case "registrar_pago":
                return true;
            case "gestionar_inventario":
            case "ver_nomina":
            case "gestionar_usuarios":
            case "ver_reportes_financieros":
                return false;
            default:
                return false;
        }
    }
}