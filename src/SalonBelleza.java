import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.sql.*; // Nuevo import para SQL

public class SalonBelleza extends JFrame {
    // Configuración de la base de datos
    private Connection conexion;
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/?user=root";
    private static final String USER = "root";
    private static final String PASSWORD = "missgarro234";

    private ArrayList<Servicio> servicios;
    private ArrayList<Gasto> gastos;
    private SimpleDateFormat dateFormat;

    private JTextField txtTipoServicio;
    private JTextField txtPrecio;
    private JTextField txtEmpleada;
    private JTextField txtConceptoGasto;
    private JTextField txtMontoGasto;
    private JTextArea txtResumen;

    public SalonBelleza() {
        // Inicializar la conexión a la base de datos
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error al conectar a la base de datos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        servicios = new ArrayList<>();
        gastos = new ArrayList<>();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        setTitle("Salón de Belleza Aremi");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Registrar Servicio", crearPanelServicio());
        tabbedPane.addTab("Registrar Gasto", crearPanelGasto());
        tabbedPane.addTab("Resumen", crearPanelResumen());

        add(tabbedPane);

        // Agregar cierre de conexión
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (conexion != null) {
                        conexion.close();
                        System.out.println("Conexión cerrada");
                    }
                } catch (SQLException ex) {
                    System.out.println("Error al cerrar la conexión: " + ex.getMessage());
                }
            }
        });
    }

    private JPanel crearPanelServicio() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtTipoServicio = new JTextField();
        txtPrecio = new JTextField();
        txtEmpleada = new JTextField();

        panel.add(new JLabel("Tipo de Servicio:"));
        panel.add(txtTipoServicio);
        panel.add(new JLabel("Precio:"));
        panel.add(txtPrecio);
        panel.add(new JLabel("Empleada:"));
        panel.add(txtEmpleada);

        JButton btnRegistrar = new JButton("Registrar Servicio");
        btnRegistrar.addActionListener(e -> registrarServicio());

        panel.add(new JLabel(""));
        panel.add(btnRegistrar);

        return panel;
    }

    private JPanel crearPanelGasto() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtConceptoGasto = new JTextField();
        txtMontoGasto = new JTextField();

        panel.add(new JLabel("Concepto:"));
        panel.add(txtConceptoGasto);
        panel.add(new JLabel("Monto:"));
        panel.add(txtMontoGasto);

        JButton btnRegistrar = new JButton("Registrar Gasto");
        btnRegistrar.addActionListener(e -> registrarGasto());

        panel.add(new JLabel(""));
        panel.add(btnRegistrar);

        return panel;
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtResumen = new JTextArea();
        txtResumen.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtResumen);

        JButton btnActualizar = new JButton("Actualizar Resumen");
        btnActualizar.addActionListener(e -> actualizarResumen());

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnActualizar, BorderLayout.SOUTH);

        return panel;
    }

    private void registrarServicio() {
        try {
            String tipo = txtTipoServicio.getText();
            double precio = Double.parseDouble(txtPrecio.getText());
            String empleada = txtEmpleada.getText();
            String fecha = dateFormat.format(new Date());

            if (tipo.isEmpty() || empleada.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor complete todos los campos",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Guardar en la base de datos
            String sql = "INSERT INTO servicios (tipo, precio, empleada, fecha) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
                stmt.setString(1, tipo);
                stmt.setDouble(2, precio);
                stmt.setString(3, empleada);
                stmt.setString(4, fecha);
                stmt.executeUpdate();
            }

            servicios.add(new Servicio(tipo, precio, empleada, fecha));

            txtTipoServicio.setText("");
            txtPrecio.setText("");
            txtEmpleada.setText("");

            JOptionPane.showMessageDialog(this,
                    "Servicio registrado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar en la base de datos: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "El precio debe ser un número válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarGasto() {
        try {
            String concepto = txtConceptoGasto.getText();
            double monto = Double.parseDouble(txtMontoGasto.getText());
            String fecha = dateFormat.format(new Date());

            if (concepto.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor complete todos los campos",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Guardar en la base de datos
            String sql = "INSERT INTO gastos (concepto, monto, fecha) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
                stmt.setString(1, concepto);
                stmt.setDouble(2, monto);
                stmt.setString(3, fecha);
                stmt.executeUpdate();
            }

            gastos.add(new Gasto(concepto, monto, fecha));

            txtConceptoGasto.setText("");
            txtMontoGasto.setText("");

            JOptionPane.showMessageDialog(this,
                    "Gasto registrado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar en la base de datos: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "El monto debe ser un número válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarResumen() {
        try {
            StringBuilder resumen = new StringBuilder();
            double ingresoTotal = 0;
            double gastoTotal = 0;

            // Cargar servicios desde la base de datos
            resumen.append("=== SERVICIOS REALIZADOS ===\n\n");
            String sqlServicios = "SELECT * FROM servicios";
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlServicios)) {
                while (rs.next()) {
                    String fecha = rs.getString("fecha");
                    String tipo = rs.getString("tipo");
                    double precio = rs.getDouble("precio");
                    String empleada = rs.getString("empleada");

                    resumen.append(String.format("%s - %s - $%.2f - %s\n",
                            fecha, tipo, precio, empleada));
                    ingresoTotal += precio;
                }
            }

            // Cargar gastos desde la base de datos
            resumen.append("\n=== GASTOS REALIZADOS ===\n\n");
            String sqlGastos = "SELECT * FROM gastos";
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlGastos)) {
                while (rs.next()) {
                    String fecha = rs.getString("fecha");
                    String concepto = rs.getString("concepto");
                    double monto = rs.getDouble("monto");

                    resumen.append(String.format("%s - %s - $%.2f\n",
                            fecha, concepto, monto));
                    gastoTotal += monto;
                }
            }

            resumen.append("\n=== RESUMEN FINANCIERO ===\n");
            resumen.append(String.format("Ingreso Total: $%.2f\n", ingresoTotal));
            resumen.append(String.format("Gasto Total: $%.2f\n", gastoTotal));
            resumen.append(String.format("Beneficio Neto: $%.2f\n", ingresoTotal - gastoTotal));

            resumen.append("\n=== NÓMINAS (20% de comisión) ===\n");
            String sqlNominas = "SELECT empleada, SUM(precio * 0.20) as comision FROM servicios GROUP BY empleada";
            try (Statement stmt = conexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlNominas)) {
                while (rs.next()) {
                    String empleada = rs.getString("empleada");
                    double comision = rs.getDouble("comision");
                    resumen.append(String.format("Empleada %s: $%.2f\n",
                            empleada, comision));
                }
            }

            txtResumen.setText(resumen.toString());

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar el resumen: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}