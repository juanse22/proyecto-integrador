import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PagoDetailsDialog extends JDialog {
    private JTextField nombreTextField;
    private JTextField montoTextField;
    private JTextField motivoTextField;
    private JComboBox<String> servicioComboBox;
    private JButton guardarButton;

    public PagoDetailsDialog(JFrame parent) {
        super(parent, "Detalles del Pago", true);
        setLayout(new BorderLayout());
        setBackground(new Color(255, 224, 224)); // Rosa suave

        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 224, 224)); // Rosa suave

        JLabel titleLabel = new JLabel("Ingrese los detalles del pago", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(219, 112, 147)); // Rosa oscuro
        add(titleLabel, BorderLayout.NORTH);

        mainPanel.add(new JLabel("Nombre:"));
        nombreTextField = new JTextField();
        mainPanel.add(nombreTextField);

        mainPanel.add(new JLabel("Monto:"));
        montoTextField = new JTextField();
        mainPanel.add(montoTextField);

        mainPanel.add(new JLabel("Motivo:"));
        motivoTextField = new JTextField();
        mainPanel.add(motivoTextField);

        mainPanel.add(new JLabel("Servicio:"));
        servicioComboBox = new JComboBox<>(new String[]{"Uñas", "Depilación de Cera", "Bikini", "Diseño de Cejas", "Manicure", "Pedicure"});
        mainPanel.add(servicioComboBox);

        guardarButton = new JButton("Guardar");
        guardarButton.setBackground(new Color(219, 112, 147)); // Rosa oscuro
        guardarButton.setForeground(Color.WHITE);
        guardarButton.setFocusPainted(false);
        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarDetalles();
            }
        });
        mainPanel.add(guardarButton);

        add(mainPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(parent);
    }

    private void guardarDetalles() {
        String nombre = nombreTextField.getText();
        String monto = montoTextField.getText();
        String motivo = motivoTextField.getText();
        String servicio = (String) servicioComboBox.getSelectedItem();

        // Aquí puedes agregar la lógica para guardar los detalles en la base de datos
        System.out.println("Nombre: " + nombre);
        System.out.println("Monto: " + monto);
        System.out.println("Motivo: " + motivo);
        System.out.println("Servicio: " + servicio);

        dispose();
    }
}
