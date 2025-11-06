import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Clase modelo para representar un item del inventario del salón de belleza.
 * Gestiona productos como tintes, esmaltes, cremas, etc.
 *
 * @author Sistema Aremi
 * @version 2.0
 */
public class ItemInventario {
    private int id;
    private String nombre;
    private int cantidad;
    private String proveedor;
    private LocalDate fechaVencimiento;
    private double costo;
    private double precioVenta;
    private int stockMinimo;
    private String categoria; // ej: "Tintes", "Esmaltes", "Cremas", "Herramientas"

    // Constantes para alertas
    public static final int DIAS_ALERTA_VENCIMIENTO = 30;

    /**
     * Constructor completo para un item de inventario
     */
    public ItemInventario(int id, String nombre, int cantidad, String proveedor,
                         LocalDate fechaVencimiento, double costo, double precioVenta,
                         int stockMinimo, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.proveedor = proveedor;
        this.fechaVencimiento = fechaVencimiento;
        this.costo = costo;
        this.precioVenta = precioVenta;
        this.stockMinimo = stockMinimo;
        this.categoria = categoria;
    }

    /**
     * Constructor sin ID (para nuevos items antes de insertarlos en BD)
     */
    public ItemInventario(String nombre, int cantidad, String proveedor,
                         LocalDate fechaVencimiento, double costo, double precioVenta,
                         int stockMinimo, String categoria) {
        this(0, nombre, cantidad, proveedor, fechaVencimiento, costo, precioVenta,
             stockMinimo, categoria);
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    /**
     * Verifica si el stock está por debajo del mínimo
     * @return true si el stock está bajo
     */
    public boolean tieneStockBajo() {
        return cantidad <= stockMinimo;
    }

    /**
     * Verifica si el producto está próximo a vencer
     * @return true si está próximo a vencer (dentro de 30 días)
     */
    public boolean estaProximoAVencer() {
        if (fechaVencimiento == null) return false;
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
        return diasRestantes >= 0 && diasRestantes <= DIAS_ALERTA_VENCIMIENTO;
    }

    /**
     * Verifica si el producto ya venció
     * @return true si ya venció
     */
    public boolean estaVencido() {
        if (fechaVencimiento == null) return false;
        return fechaVencimiento.isBefore(LocalDate.now());
    }

    /**
     * Calcula el margen de ganancia del producto
     * @return margen de ganancia en porcentaje
     */
    public double calcularMargenGanancia() {
        if (costo == 0) return 0;
        return ((precioVenta - costo) / costo) * 100;
    }

    /**
     * Calcula el valor total del stock
     * @return valor total basado en el costo
     */
    public double calcularValorStock() {
        return cantidad * costo;
    }

    /**
     * Retorna el estado del item para alertas
     * @return String con el estado (Normal, Stock Bajo, Próximo a Vencer, Vencido)
     */
    public String getEstado() {
        if (estaVencido()) return "VENCIDO";
        if (estaProximoAVencer()) return "PRÓXIMO A VENCER";
        if (tieneStockBajo()) return "STOCK BAJO";
        return "NORMAL";
    }

    @Override
    public String toString() {
        return String.format("%s - Cantidad: %d - Estado: %s",
                           nombre, cantidad, getEstado());
    }
}