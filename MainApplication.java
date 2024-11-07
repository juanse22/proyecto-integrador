import javax.swing.*;
import java.awt.*;

public class MainApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema de Gestión de Spa");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Crear el panel principal que contendrá todo
            JPanel mainPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Image img = new ImageIcon("C:\\Users\\Juan Sebastian\\IdeaProjects\\proyectoIntegrador\\src\\6911600.jpg").getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                }
            };

            // Crear una barra de botones en la parte superior
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.setOpaque(false);

            JButton btnLogin = new JButton("Login");
            btnLogin.addActionListener(e -> {
                new LoginSystemAremi().setVisible(true);
            });

            JButton btnAgendarCita = new JButton("Agendar Cita");
            btnAgendarCita.addActionListener(e -> {
                new AgendarCitaGUI().setVisible(true);
            });

            JButton btnGestionClientes = new JButton("Gestión de Clientes");
            btnGestionClientes.addActionListener(e -> {
                new InterfazClientes().setVisible(true);
            });

            topPanel.add(btnLogin);
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
                String facturaId = JOptionPane.showInputDialog(frame, "Ingrese el ID de la factura:");
                if (facturaId != null && !facturaId.isEmpty()) {
                    Facturas facturas = new Facturas();
                    facturas.generarFactura(facturaId);
                }
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
        });
    }
}
