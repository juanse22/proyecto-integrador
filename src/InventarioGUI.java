import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Interfaz gráfica para la gestión de inventario del Salón Aremi.
 * Permite registrar, editar, eliminar y monitorear productos.
 * Incluye alertas de stock bajo y productos próximos a vencer.
 *
 * @author Sistema Aremi
 * @version 2.0
 */
public class InventarioGUI extends JFrame {
    // Colores del tema
    private static final Color ROSA_OSCURO = new Color(219, 112, 147);
    private static final Color ROSA_CLARO = new Color(255, 182, 193);
    private static final Color MORADO_SUAVE = new Color(230, 190, 255);

    // Componentes de la interfaz
    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtCantidad, txtProveedor, txtCosto, txtPrecioVenta, txtStockMinimo;
    private JTextField txtFechaVencimiento;
    private JComboBox<String> cboCategoria;
    private JButton btnAgregar, btnEditar, btnEliminar, btnLimpiar, btnActualizar, btnAlertas;
    private JTextField txtBuscar;

    // Conexión a base de datos
    private Connection conexion;
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/salon_aremi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "missgarro234";

    private int itemSeleccionadoId = -1;

    /**
     * Constructor de la interfaz de inventario
     */
    public InventarioGUI() {
        initializeDatabase();
        initComponents();
        cargarInventario();
        verificarAlertas();
    }

    /**
     * Inicializa la conexión a la base de datos y crea la tabla si no existe
     */
    private void initializeDatabase() {
        try {
            conexion = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            crearTablaInventario();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al conectar con la base de datos: " + e.getMessage(),
                "Error de Conexión",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Crea la tabla de inventario en la base de datos si no existe
     */
    private void crearTablaInventario() {
        String sql = "CREATE TABLE IF NOT EXISTS inventario (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "nombre VARCHAR(100) NOT NULL," +
                    "cantidad INT NOT NULL," +
                    "proveedor VARCHAR(100)," +
                    "fecha_vencimiento DATE," +
                    "costo DECIMAL(10,2) NOT NULL," +
                    "precio_venta DECIMAL(10,2) NOT NULL," +
                    "stock_minimo INT DEFAULT 5," +
                    "categoria VARCHAR(50)," +
                    "fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

        try (Statement stmt = conexion.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicializa todos los componentes de la interfaz gráfica
     */
    private void initComponents() {
        setTitle("📦 Gestión de Inventario - Salón Aremi");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel de formulario
        JPanel formPanel = createFormPanel();

        // Panel de tabla
        JPanel tablePanel = createTablePanel();

        // Panel de botones de acción
        JPanel actionPanel = createActionPanel();

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Listener para cerrar la conexión al cerrar la ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarConexion();
            }
        });
    }

    /**
     * Crea el panel del formulario de entrada de datos
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ROSA_OSCURO, 2),
            "Información del Producto",
            0, 0, new Font("Arial", Font.BOLD, 14), ROSA_OSCURO
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inicializar campos
        txtNombre = createStyledTextField();
        txtCantidad = createStyledTextField();
        txtProveedor = createStyledTextField();
        txtFechaVencimiento = createStyledTextField();
        txtFechaVencimiento.setToolTipText("Formato: YYYY-MM-DD");
        txtCosto = createStyledTextField();
        txtPrecioVenta = createStyledTextField();
        txtStockMinimo = createStyledTextField();
        txtStockMinimo.setText("5");

        String[] categorias = {"Tintes", "Esmaltes", "Cremas", "Herramientas",
                              "Accesorios", "Depilación", "Manicure/Pedicure", "Otros"};
        cboCategoria = createStyledComboBox(categorias);

        // Agregar campos al panel
        addFormField(panel, "Nombre:", txtNombre, gbc, 0, 0);
        addFormField(panel, "Cantidad:", txtCantidad, gbc, 1, 0);
        addFormField(panel, "Categoría:", cboCategoria, gbc, 2, 0);
        addFormField(panel, "Proveedor:", txtProveedor, gbc, 3, 0);

        addFormField(panel, "Fecha Vencimiento:", txtFechaVencimiento, gbc, 0, 1);
        addFormField(panel, "Costo ($):", txtCosto, gbc, 1, 1);
        addFormField(panel, "Precio Venta ($):", txtPrecioVenta, gbc, 2, 1);
        addFormField(panel, "Stock Mínimo:", txtStockMinimo, gbc, 3, 1);

        return panel;
    }

    /**
     * Crea el panel de la tabla de inventario
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("🔍 Buscar:"));
        txtBuscar = createStyledTextField();
        txtBuscar.setPreferredSize(new Dimension(250, 30));
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarTabla();
            }
        });
        searchPanel.add(txtBuscar);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Cantidad", "Categoría", "Proveedor",
                           "Vencimiento", "Costo", "P. Venta", "Stock Mín.", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaInventario = new JTable(modeloTabla);
        tablaInventario.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaInventario.setRowHeight(25);
        tablaInventario.getTableHeader().setBackground(ROSA_OSCURO);
        tablaInventario.getTableHeader().setForeground(Color.WHITE);
        tablaInventario.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Ocultar columna ID
        tablaInventario.getColumnModel().getColumn(0).setMinWidth(0);
        tablaInventario.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaInventario.getColumnModel().getColumn(0).setWidth(0);

        tablaInventario.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    cargarItemSeleccionado();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaInventario);
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea el panel de botones de acción
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);

        btnAgregar = createStyledButton("Agregar", ROSA_OSCURO);
        btnEditar = createStyledButton("Editar", new Color(100, 149, 237));
        btnEliminar = createStyledButton("Eliminar", new Color(220, 20, 60));
        btnLimpiar = createStyledButton("Limpiar", new Color(128, 128, 128));
        btnActualizar = createStyledButton("Actualizar", new Color(34, 139, 34));
        btnAlertas = createStyledButton("⚠ Ver Alertas", new Color(255, 140, 0));

        // Eventos
        btnAgregar.addActionListener(e -> agregarItem());
        btnEditar.addActionListener(e -> editarItem());
        btnEliminar.addActionListener(e -> eliminarItem());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnActualizar.addActionListener(e -> {
            cargarInventario();
            verificarAlertas();
        });
        btnAlertas.addActionListener(e -> mostrarAlertas());

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);
        panel.add(btnActualizar);
        panel.add(btnAlertas);

        return panel;
    }

    /**
     * Carga todos los items del inventario desde la base de datos
     */
    private void cargarInventario() {
        modeloTabla.setRowCount(0);

        String sql = "SELECT * FROM inventario ORDER BY nombre";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ItemInventario item = crearItemDesdeResultSet(rs);
                agregarFilaTabla(item);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar inventario: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Crea un objeto ItemInventario desde un ResultSet
     */
    private ItemInventario crearItemDesdeResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        int cantidad = rs.getInt("cantidad");
        String proveedor = rs.getString("proveedor");
        Date fechaVencSQL = rs.getDate("fecha_vencimiento");
        LocalDate fechaVenc = fechaVencSQL != null ? fechaVencSQL.toLocalDate() : null;
        double costo = rs.getDouble("costo");
        double precioVenta = rs.getDouble("precio_venta");
        int stockMinimo = rs.getInt("stock_minimo");
        String categoria = rs.getString("categoria");

        return new ItemInventario(id, nombre, cantidad, proveedor, fechaVenc,
                                 costo, precioVenta, stockMinimo, categoria);
    }

    /**
     * Agrega una fila a la tabla con los datos del item
     */
    private void agregarFilaTabla(ItemInventario item) {
        Object[] fila = {
            item.getId(),
            item.getNombre(),
            item.getCantidad(),
            item.getCategoria(),
            item.getProveedor(),
            item.getFechaVencimiento() != null ?
                item.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
            String.format("$%.2f", item.getCosto()),
            String.format("$%.2f", item.getPrecioVenta()),
            item.getStockMinimo(),
            item.getEstado()
        };
        modeloTabla.addRow(fila);
    }

    /**
     * Agrega un nuevo item al inventario
     */
    private void agregarItem() {
        if (!validarCampos()) return;

        String sql = "INSERT INTO inventario (nombre, cantidad, proveedor, fecha_vencimiento, " +
                    "costo, precio_venta, stock_minimo, categoria) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            setParametrosItem(pstmt);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Producto agregado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

            limpiarCampos();
            cargarInventario();
            verificarAlertas();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al agregar producto: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Edita el item seleccionado
     */
    private void editarItem() {
        if (itemSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un producto para editar",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validarCampos()) return;

        String sql = "UPDATE inventario SET nombre=?, cantidad=?, proveedor=?, " +
                    "fecha_vencimiento=?, costo=?, precio_venta=?, stock_minimo=?, " +
                    "categoria=? WHERE id=?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            setParametrosItem(pstmt);
            pstmt.setInt(9, itemSeleccionadoId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Producto actualizado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

            limpiarCampos();
            cargarInventario();
            verificarAlertas();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar producto: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina el item seleccionado
     */
    private void eliminarItem() {
        if (itemSeleccionadoId == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un producto para eliminar",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar este producto?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM inventario WHERE id=?";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setInt(1, itemSeleccionadoId);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                    "Producto eliminado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

                limpiarCampos();
                cargarInventario();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Establece los parámetros del PreparedStatement con los valores del formulario
     */
    private void setParametrosItem(PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, txtNombre.getText().trim());
        pstmt.setInt(2, Integer.parseInt(txtCantidad.getText().trim()));
        pstmt.setString(3, txtProveedor.getText().trim());

        String fechaVencStr = txtFechaVencimiento.getText().trim();
        if (!fechaVencStr.isEmpty()) {
            pstmt.setDate(4, Date.valueOf(fechaVencStr));
        } else {
            pstmt.setNull(4, Types.DATE);
        }

        pstmt.setDouble(5, Double.parseDouble(txtCosto.getText().trim()));
        pstmt.setDouble(6, Double.parseDouble(txtPrecioVenta.getText().trim()));
        pstmt.setInt(7, Integer.parseInt(txtStockMinimo.getText().trim()));
        pstmt.setString(8, cboCategoria.getSelectedItem().toString());
    }

    /**
     * Carga los datos del item seleccionado en los campos del formulario
     */
    private void cargarItemSeleccionado() {
        int selectedRow = tablaInventario.getSelectedRow();
        if (selectedRow == -1) return;

        itemSeleccionadoId = (int) modeloTabla.getValueAt(selectedRow, 0);
        txtNombre.setText(modeloTabla.getValueAt(selectedRow, 1).toString());
        txtCantidad.setText(modeloTabla.getValueAt(selectedRow, 2).toString());
        cboCategoria.setSelectedItem(modeloTabla.getValueAt(selectedRow, 3).toString());
        txtProveedor.setText(modeloTabla.getValueAt(selectedRow, 4).toString());

        String fechaVenc = modeloTabla.getValueAt(selectedRow, 5).toString();
        if (!fechaVenc.equals("N/A")) {
            LocalDate fecha = LocalDate.parse(fechaVenc, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            txtFechaVencimiento.setText(fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            txtFechaVencimiento.setText("");
        }

        String costoStr = modeloTabla.getValueAt(selectedRow, 6).toString().replace("$", "");
        txtCosto.setText(costoStr);
        String precioStr = modeloTabla.getValueAt(selectedRow, 7).toString().replace("$", "");
        txtPrecioVenta.setText(precioStr);
        txtStockMinimo.setText(modeloTabla.getValueAt(selectedRow, 8).toString());
    }

    /**
     * Valida los campos del formulario
     */
    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return false;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad < 0) {
                mostrarError("La cantidad no puede ser negativa");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número entero");
            return false;
        }

        try {
            double costo = Double.parseDouble(txtCosto.getText().trim());
            if (costo < 0) {
                mostrarError("El costo no puede ser negativo");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("El costo debe ser un número válido");
            return false;
        }

        try {
            double precio = Double.parseDouble(txtPrecioVenta.getText().trim());
            if (precio < 0) {
                mostrarError("El precio de venta no puede ser negativo");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("El precio de venta debe ser un número válido");
            return false;
        }

        try {
            int stockMin = Integer.parseInt(txtStockMinimo.getText().trim());
            if (stockMin < 0) {
                mostrarError("El stock mínimo no puede ser negativo");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("El stock mínimo debe ser un número entero");
            return false;
        }

        String fechaVenc = txtFechaVencimiento.getText().trim();
        if (!fechaVenc.isEmpty()) {
            try {
                LocalDate.parse(fechaVenc);
            } catch (Exception e) {
                mostrarError("Formato de fecha inválido. Use: YYYY-MM-DD");
                return false;
            }
        }

        return true;
    }

    /**
     * Verifica y notifica sobre productos con alertas
     */
    private void verificarAlertas() {
        ArrayList<String> alertas = new ArrayList<>();

        String sql = "SELECT * FROM inventario";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ItemInventario item = crearItemDesdeResultSet(rs);

                if (item.estaVencido()) {
                    alertas.add("❌ VENCIDO: " + item.getNombre());
                } else if (item.estaProximoAVencer()) {
                    long dias = ChronoUnit.DAYS.between(LocalDate.now(), item.getFechaVencimiento());
                    alertas.add("⚠ PRÓXIMO A VENCER (" + dias + " días): " + item.getNombre());
                }

                if (item.tieneStockBajo()) {
                    alertas.add("📉 STOCK BAJO (" + item.getCantidad() + " unidades): " + item.getNombre());
                }
            }

            if (!alertas.isEmpty()) {
                btnAlertas.setBackground(new Color(255, 69, 0));
                btnAlertas.setText("⚠ Alertas (" + alertas.size() + ")");
            } else {
                btnAlertas.setBackground(new Color(34, 139, 34));
                btnAlertas.setText("✓ Sin Alertas");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra un diálogo con todas las alertas activas
     */
    private void mostrarAlertas() {
        ArrayList<String> alertas = new ArrayList<>();

        String sql = "SELECT * FROM inventario";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ItemInventario item = crearItemDesdeResultSet(rs);

                if (item.estaVencido()) {
                    alertas.add("❌ VENCIDO: " + item.getNombre() +
                              " (Vencido el " + item.getFechaVencimiento() + ")");
                } else if (item.estaProximoAVencer()) {
                    long dias = ChronoUnit.DAYS.between(LocalDate.now(), item.getFechaVencimiento());
                    alertas.add("⚠ PRÓXIMO A VENCER: " + item.getNombre() +
                              " (Quedan " + dias + " días)");
                }

                if (item.tieneStockBajo()) {
                    alertas.add("📉 STOCK BAJO: " + item.getNombre() +
                              " (Cantidad: " + item.getCantidad() +
                              ", Mínimo: " + item.getStockMinimo() + ")");
                }
            }

            if (alertas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No hay alertas activas. ¡Todo está en orden! ✓",
                    "Sistema de Alertas",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder mensaje = new StringBuilder("ALERTAS ACTIVAS:\n\n");
                for (String alerta : alertas) {
                    mensaje.append(alerta).append("\n");
                }

                JTextArea textArea = new JTextArea(mensaje.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 300));

                JOptionPane.showMessageDialog(this,
                    scrollPane,
                    "Sistema de Alertas - " + alertas.size() + " alertas",
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Filtra la tabla según el texto de búsqueda
     */
    private void filtrarTabla() {
        String busqueda = txtBuscar.getText().toLowerCase();

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaInventario.setRowSorter(sorter);

        if (busqueda.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + busqueda));
        }
    }

    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarCampos() {
        txtNombre.setText("");
        txtCantidad.setText("");
        txtProveedor.setText("");
        txtFechaVencimiento.setText("");
        txtCosto.setText("");
        txtPrecioVenta.setText("");
        txtStockMinimo.setText("5");
        cboCategoria.setSelectedIndex(0);
        itemSeleccionadoId = -1;
        tablaInventario.clearSelection();
    }

    /**
     * Cierra la conexión a la base de datos
     */
    private void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Métodos auxiliares para crear componentes estilizados
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ROSA_CLARO),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)
        ));
        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
        return combo;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(130, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addFormField(JPanel panel, String labelText, JComponent field,
                             GridBagConstraints gbc, int col, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));

        gbc.gridx = col * 2;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(label, gbc);

        gbc.gridx = col * 2 + 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Validación",
                                     JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Método main para pruebas independientes
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InventarioGUI gui = new InventarioGUI();
            gui.setVisible(true);
        });
    }
}