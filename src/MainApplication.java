import javax.swing.*;
import java.awt.BorderLayout;  // Agregamos este import

public class MainApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema de Gestión de Spa");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Crear el panel principal que contendrá todo
            JPanel mainPanel = new JPanel(new BorderLayout());

            // Crear una barra de menú
            JMenuBar menuBar = new JMenuBar();
            JMenu menuOpciones = new JMenu("Opciones");

            // Crear items del menú
            JMenuItem itemAgendarCita = new JMenuItem("Agendar Cita");
            itemAgendarCita.addActionListener(e -> {
                new AgendarCitaGUI().setVisible(true);
            });

            // Agregar items al menú
            menuOpciones.add(itemAgendarCita);
            menuBar.add(menuOpciones);

            // Configurar la ventana principal
            frame.setJMenuBar(menuBar);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }
}