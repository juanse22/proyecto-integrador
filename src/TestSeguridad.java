/**
 * Clase de prueba simple para validar SeguridadManager.
 * No requiere JUnit - se ejecuta directamente con main().
 *
 * @author Sistema Aremi
 * @version 2.0
 */
public class TestSeguridad {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  PRUEBAS DE VALIDACIÓN - MÓDULO SEGURIDAD             ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        int testsPasados = 0;
        int testsTotal = 0;

        // Test 1: Hash de contraseñas
        testsTotal++;
        System.out.println("[Test 1] Validar hash SHA-256...");
        String password = "12345";
        String hash1 = SeguridadManager.hashPassword(password);
        String hash2 = SeguridadManager.hashPassword(password);

        if (hash1 != null && hash1.length() == 64 && hash1.equals(hash2)) {
            System.out.println("  ✓ PASS: Hash generado correctamente (64 caracteres, consistente)");
            testsPasados++;
        } else {
            System.out.println("  ✗ FAIL: Hash incorrecto o inconsistente");
        }

        // Limpiar sesión antes de cada test
        SeguridadManager.cerrarSesion();

        // Test 2: Autenticación de administrador
        testsTotal++;
        System.out.println("\n[Test 2] Validar autenticación de administrador...");
        SeguridadManager.setUsuarioActual("admin", SeguridadManager.ROL_ADMINISTRADOR);

        if (SeguridadManager.hayUsuarioAutenticado() &&
            SeguridadManager.esAdministrador() &&
            !SeguridadManager.esEmpleado()) {
            System.out.println("  ✓ PASS: Administrador autenticado correctamente");
            testsPasados++;
        } else {
            System.out.println("  ✗ FAIL: Error en autenticación de administrador");
        }

        // Limpiar
        SeguridadManager.cerrarSesion();

        // Test 3: Autenticación de empleado
        testsTotal++;
        System.out.println("\n[Test 3] Validar autenticación de empleado...");
        SeguridadManager.setUsuarioActual("empleada1", SeguridadManager.ROL_EMPLEADO);

        if (SeguridadManager.hayUsuarioAutenticado() &&
            !SeguridadManager.esAdministrador() &&
            SeguridadManager.esEmpleado()) {
            System.out.println("  ✓ PASS: Empleado autenticado correctamente");
            testsPasados++;
        } else {
            System.out.println("  ✗ FAIL: Error en autenticación de empleado");
        }

        // Test 4: Permisos de administrador
        testsTotal++;
        System.out.println("\n[Test 4] Validar permisos de administrador...");
        SeguridadManager.cerrarSesion();
        SeguridadManager.setUsuarioActual("admin", SeguridadManager.ROL_ADMINISTRADOR);

        boolean todosPermisos = SeguridadManager.tienePermiso("ver_nomina") &&
                                SeguridadManager.tienePermiso("gestionar_usuarios") &&
                                SeguridadManager.tienePermiso("agendar_cita");

        if (todosPermisos) {
            System.out.println("  ✓ PASS: Administrador tiene todos los permisos");
            testsPasados++;
        } else {
            System.out.println("  ✗ FAIL: Administrador no tiene todos los permisos");
        }

        // Test 5: Permisos limitados de empleado
        testsTotal++;
        System.out.println("\n[Test 5] Validar permisos limitados de empleado...");
        SeguridadManager.cerrarSesion();
        SeguridadManager.setUsuarioActual("empleada1", SeguridadManager.ROL_EMPLEADO);

        boolean permisosCorrectos = SeguridadManager.tienePermiso("agendar_cita") &&
                                    SeguridadManager.tienePermiso("registrar_servicio") &&
                                    !SeguridadManager.tienePermiso("ver_nomina");

        if (permisosCorrectos) {
            System.out.println("  ✓ PASS: Empleado tiene permisos correctos (limitados)");
            testsPasados++;
        } else {
            System.out.println("  ✗ FAIL: Permisos de empleado incorrectos");
        }

        // Test 6: Cerrar sesión
        testsTotal++;
        System.out.println("\n[Test 6] Validar cierre de sesión...");
        SeguridadManager.cerrarSesion();

        if (!SeguridadManager.hayUsuarioAutenticado() &&
            SeguridadManager.getUsuarioActual() == null &&
            SeguridadManager.getRolActual() == null) {
            System.out.println("  ✓ PASS: Sesión cerrada correctamente");
            testsPasados++;
        } else {
            System.out.println("  ✗ FAIL: Error al cerrar sesión");
        }

        // Test 7: Sin autenticación no hay permisos
        testsTotal++;
        System.out.println("\n[Test 7] Validar sin autenticación no hay permisos...");

        if (!SeguridadManager.hayUsuarioAutenticado() &&
            !SeguridadManager.tienePermiso("agendar_cita")) {
            System.out.println("  ✓ PASS: Sin autenticación no se otorgan permisos");
            testsPasados++;
        } else {
            System.out.println("  ✗ FAIL: Se otorgan permisos sin autenticación");
        }

        // Resumen final
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║  RESUMEN DE PRUEBAS                                    ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.printf("║  Tests ejecutados: %-5d                               ║%n", testsTotal);
        System.out.printf("║  Tests exitosos:   %-5d                               ║%n", testsPasados);
        System.out.printf("║  Tests fallidos:   %-5d                               ║%n", testsTotal - testsPasados);
        System.out.printf("║  Porcentaje:       %.1f%%                              ║%n", (testsPasados * 100.0 / testsTotal));
        System.out.println("╚════════════════════════════════════════════════════════╝");

        if (testsPasados == testsTotal) {
            System.out.println("\n✅ TODAS LAS PRUEBAS PASARON EXITOSAMENTE");
        } else {
            System.out.println("\n⚠ ALGUNAS PRUEBAS FALLARON");
        }
    }
}