import java.time.LocalDate;

/**
 * Clase de prueba simple para validar ItemInventario.
 * No requiere JUnit - se ejecuta directamente con main().
 *
 * @author Sistema Aremi
 * @version 2.0
 */
public class TestInventario {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  PRUEBAS DE VALIDACIÓN - MÓDULO INVENTARIO            ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        int testsPasados = 0;
        int testsTotal = 0;

        // Test 1: Stock bajo
        testsTotal++;
        System.out.println("[Test 1] Validar detección de stock bajo...");
        ItemInventario item1 = new ItemInventario(
            "Tinte Rubio", 5, "Proveedor ABC", LocalDate.now().plusDays(45),
            50000, 80000, 10, "Tintes"
        );
        if (item1.tieneStockBajo()) {
            System.out.println("  ✓ PASS: Stock bajo detectado correctamente (5 <= 10)");
            testsPasados++;
        } else {
            System.out.println("  ✗ FAIL: No se detectó stock bajo");
        }

        // Test 2: Producto próximo a vencer
        testsTotal++;
        System.out.println("\n[Test 2] Validar detección de producto próximo a vencer...");
        ItemInventario item2 = new ItemInventario(
            "Esmalte Rojo", 15, "Proveedor XYZ", LocalDate.now().plusDays(20),
            10000, 15000, 5, "Esmaltes"
        );
        if (item2.estaProximoAVencer()) {
            System.out.println("  ✓ PASS: Producto próximo a vencer detectado (20 días)");
            testsPasados++;
        } else {
            System.out.println("  ✗ FAIL: No se detectó proximidad a vencimiento");
        }

        // Test 3: Producto vencido
        testsTotal++;
        System.out.println("\n[Test 3] Validar detección de producto vencido...");
        ItemInventario item3 = new ItemInventario(
            "Crema Caducada", 10, "Proveedor ABC", LocalDate.now().minusDays(5),
            20000, 30000, 5, "Cremas"
        );
        if (item3.estaVencido()) {
            System.out.println("  ✓ PASS: Producto vencido detectado correctamente");
            testsPasados++;
        } else {
            System.out.println("  ✗ FAIL: No se detectó producto vencido");
        }

        // Test 4: Cálculo de margen de ganancia
        testsTotal++;
        System.out.println("\n[Test 4] Validar cálculo de margen de ganancia...");
        ItemInventario item4 = new ItemInventario(
            "Producto Test", 10, "Proveedor", LocalDate.now().plusDays(100),
            50000, 80000, 5, "Test"
        );
        double margen = item4.calcularMargenGanancia();
        if (Math.abs(margen - 60.0) < 0.01) {
            System.out.println("  ✓ PASS: Margen calculado correctamente (60%)");
            testsPasados++;
        } else {
            System.out.println("  ✗ FAIL: Margen incorrecto. Esperado: 60%, Obtenido: " + margen);
        }

        // Test 5: Cálculo de valor de stock
        testsTotal++;
        System.out.println("\n[Test 5] Validar cálculo de valor total de stock...");
        ItemInventario item5 = new ItemInventario(
            "Producto Stock", 15, "Proveedor", LocalDate.now().plusDays(100),
            50000, 80000, 5, "Test"
        );
        double valorStock = item5.calcularValorStock();
        if (Math.abs(valorStock - 750000) < 0.01) {
            System.out.println("  ✓ PASS: Valor de stock calculado correctamente ($750,000)");
            testsPasados++;
        } else {
            System.out.println("  ✗ FAIL: Valor incorrecto. Esperado: 750000, Obtenido: " + valorStock);
        }

        // Test 6: Estados del producto
        testsTotal++;
        System.out.println("\n[Test 6] Validar estados del producto...");
        ItemInventario itemNormal = new ItemInventario(
            "Producto Normal", 20, "Proveedor", LocalDate.now().plusDays(100),
            10000, 15000, 5, "Test"
        );
        if (itemNormal.getEstado().equals("NORMAL")) {
            System.out.println("  ✓ PASS: Estado NORMAL detectado correctamente");
            testsPasados++;
        } else {
            System.out.println("  ✗ FAIL: Estado incorrecto. Obtenido: " + itemNormal.getEstado());
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