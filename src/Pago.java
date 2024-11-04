import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Pago {
    private JPanel panel1;
    private JRadioButton efectivoRadioButton;
    private JRadioButton tarjetaDeCreditoDebitoRadioButton;
    private JRadioButton tranferenciaRadioButton;
    private JButton regresarButton;
    private JButton pagarButton;

    public Pago() {
        JFrame frame = new JFrame("Pago");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Agrupar los radio buttons
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(efectivoRadioButton);
        grupo.add(tarjetaDeCreditoDebitoRadioButton);
        grupo.add(tranferenciaRadioButton);

        regresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Pedido(); // Asegúrate de tener esta clase
            }
        });

        pagarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validarSeleccionPago()) {
                    procesarPago(frame);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Por favor seleccione un método de pago",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private boolean validarSeleccionPago() {
        return efectivoRadioButton.isSelected() ||
                tarjetaDeCreditoDebitoRadioButton.isSelected() ||
                tranferenciaRadioButton.isSelected();
    }

    private void procesarPago(JFrame frame) {
        Facturas facturas = new Facturas();
        facturas.generarFactura("1002"); // Aquí deberías pasar el ID real
        JOptionPane.showMessageDialog(frame, "Pago procesado correctamente!");
        frame.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Pago();
            }
        });
    }
}