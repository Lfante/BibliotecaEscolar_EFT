package vista;

import controller.AuthController;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private final AuthController controller = new AuthController();

    private JTextField campoCorreo;
    private JPasswordField campoContrasena;
    private JButton botonIngresar;

    public LoginFrame() {
        setTitle("Biblioteca Escolar - Ingreso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 285);
        setLocationRelativeTo(null);
        setResizable(false);

        inicializar();
    }

    private void inicializar() {
        JPanel principal = new JPanel(new BorderLayout(12, 12));
        principal.setBorder(new EmptyBorder(25, 35, 25, 35));

        JLabel titulo = new JLabel("Sistema de Biblioteca Escolar", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 21));

        JPanel formulario = new JPanel(new GridLayout(2, 2, 8, 12));

        formulario.add(new JLabel("Correo:"));
        campoCorreo = new JTextField();
        formulario.add(campoCorreo);

        formulario.add(new JLabel("Contraseña:"));
        campoContrasena = new JPasswordField();
        formulario.add(campoContrasena);

        botonIngresar = new JButton("Ingresar");
        botonIngresar.addActionListener(e -> ingresar());

        getRootPane().setDefaultButton(botonIngresar);

        principal.add(titulo, BorderLayout.NORTH);
        principal.add(formulario, BorderLayout.CENTER);
        principal.add(botonIngresar, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private void ingresar() {
        botonIngresar.setEnabled(false);

        try {
            Usuario usuario = controller.autenticar(
                    campoCorreo.getText(),
                    new String(campoContrasena.getPassword())
            );

            dispose();
            new MainFrame(usuario).setVisible(true);

        } catch (IllegalArgumentException e) {
            UiUtils.error(this, e.getMessage());
            botonIngresar.setEnabled(true);

        } catch (SQLException e) {
            UiUtils.error(
                    this,
                    "No fue posible consultar la base de datos.\n" + e.getMessage()
            );
            botonIngresar.setEnabled(true);
        }
    }
}
