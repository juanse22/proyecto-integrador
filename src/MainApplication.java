import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Mostrar el diálogo de inicio de sesión primero
            LoginSystemAremi loginDialog = new LoginSystemAremi();
            loginDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            loginDialog.setVisible(true);

            // Si el inicio de sesión es exitoso, se mostrará la ventana principal
            if (loginDialog.isLoginSuccessful()) {
                JFrame frame = new JFrame("Sistema de Gestión de Spa");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Crear el panel principal que contendrá todo
                JPanel mainPanel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Image img = new ImageIcon("C:\\Users\\Juan Sebastian\\IdeaProjects\\Proyecto-Integrador\\src\\6911600.jpg").getImage();
                        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                    }
                };

                // Crear una barra de botones en la parte superior
                JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                topPanel.setOpaque(false);

                JButton btnAgendarCita = new JButton("Agendar Cita");
                btnAgendarCita.addActionListener(e -> {
                    new AgendarCitaGUI().setVisible(true);
                });

                JButton btnGestionClientes = new JButton("Gestión de Clientes");
                btnGestionClientes.addActionListener(e -> {
                    new InterfazClientes().setVisible(true);
                });

                topPanel.add(btnAgendarCita);
                topPanel.add(btnGestionClientes);

                // Crear una barra de botones en la parte inferior
                JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                bottomPanel.setOpaque(false);

                JButton btnPago = new JButton("Pago");
                btnPago.addActionListener(e -> {
                    new Pago();
                });

                JButton btnGenerarFactura = new JButton("Generar Factura");
                btnGenerarFactura.addActionListener(e -> {
                    mostrarDialogoFactura(frame);
                });

                bottomPanel.add(btnPago);
                bottomPanel.add(btnGenerarFactura);

                // Agregar los paneles al mainPanel
                mainPanel.add(topPanel, BorderLayout.NORTH);
                mainPanel.add(bottomPanel, BorderLayout.SOUTH);

                // Configurar la ventana principal
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);
                frame.add(mainPanel);
                frame.setVisible(true);
            }
        });
    }

    private static void mostrarDialogoFactura(JFrame parent) {
        // Crear un diálogo personalizado
        JDialog dialog = new JDialog(parent, "Buscar Factura", true);
        dialog.setLayout(new BorderLayout(10, 10));

        // Panel para la entrada de búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Ingrese el nombre o teléfono del cliente:");
        JTextField searchField = new JTextField(20);

        searchPanel.add(label, BorderLayout.NORTH);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Panel para los resultados
        JPanel resultPanel = new JPanel(new BorderLayout(5, 5));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> resultList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(resultList);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton searchButton = new JButton("Buscar");
        JButton generateButton = new JButton("Generar Factura");
        JButton cancelButton = new JButton("Cancelar");

        buttonPanel.add(searchButton);
        buttonPanel.add(generateButton);
        buttonPanel.add(cancelButton);

        // Agregar todos los paneles al diálogo
        dialog.add(searchPanel, BorderLayout.NORTH);
        dialog.add(resultPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Configurar acciones de los botones
        Facturas facturas = new Facturas();

        searchButton.addActionListener(e -> {
            String criterio = searchField.getText().trim();
            if (!criterio.isEmpty()) {
                listModel.clear();
                List<String> facturasEncontradas = facturas.buscarFacturasPorNombreOTelefono(criterio);
                if (facturasEncontradas.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "No se encontraron facturas para el criterio proporcionado.",
                            "Sin resultados",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    for (String facturaId : facturasEncontradas) {
                        listModel.addElement(facturaId);
                    }
                }
            }
        });

        generateButton.addActionListener(e -> {
            String selectedFactura = resultList.getSelectedValue();
            if (selectedFactura != null) {
                facturas.generarFactura(selectedFactura);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Por favor, seleccione una factura de la lista.",
                        "Selección requerida",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        // Configurar el diálogo
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
}
