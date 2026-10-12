package vista;

import config.DatabaseConnection;
import modelo.Persona;
import modelo.Rol;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private final Usuario usuario;

    public MainFrame(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Biblioteca Escolar - " + usuario.getNombre());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(610, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        inicializar();
    }

    private void inicializar() {
        JPanel principal = new JPanel(new BorderLayout(15, 15));
        principal.setBorder(new EmptyBorder(20, 30, 25, 30));

        JLabel titulo = new JLabel("Biblioteca Escolar", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel sesion = new JLabel(
                "Sesión: " + usuario.getNombre()
                        + " | " + describirPersona(usuario),
                SwingConstants.CENTER
        );

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 5));
        encabezado.add(titulo);
        encabezado.add(sesion);

        JPanel botones = new JPanel(new GridLayout(0, 1, 8, 8));

        if (usuario.getRol() == Rol.BIBLIOTECARIO) {
            JButton usuarios = new JButton("Gestión de usuarios");
            JButton estudiantes = new JButton("Gestión de estudiantes");
            JButton categorias = new JButton("Gestión de categorías");
            JButton libros = new JButton("Gestión de libros");
            JButton prestamos = new JButton("Préstamos y devoluciones");
            JButton reportes = new JButton("Reportes");

            usuarios.addActionListener(e -> new UsuarioFrame().setVisible(true));
            estudiantes.addActionListener(e -> new EstudianteFrame().setVisible(true));
            categorias.addActionListener(e -> new CategoriaFrame().setVisible(true));
            libros.addActionListener(e -> new LibroFrame(false).setVisible(true));
            prestamos.addActionListener(e -> new PrestamoFrame(usuario).setVisible(true));
            reportes.addActionListener(e -> new ReportesFrame(usuario).setVisible(true));

            botones.add(usuarios);
            botones.add(estudiantes);
            botones.add(categorias);
            botones.add(libros);
            botones.add(prestamos);
            botones.add(reportes);

        } else {
            JButton libros = new JButton("Consultar libros");
            JButton prestamos = new JButton("Mis préstamos y devoluciones");
            JButton historial = new JButton("Mi historial");

            libros.addActionListener(e -> new LibroFrame(true).setVisible(true));
            prestamos.addActionListener(e -> new PrestamoFrame(usuario).setVisible(true));
            historial.addActionListener(e -> new ReportesFrame(usuario).setVisible(true));

            botones.add(libros);
            botones.add(prestamos);
            botones.add(historial);
        }

        JButton cerrarSesion = new JButton("Cerrar sesión");
        cerrarSesion.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        botones.add(cerrarSesion);

        principal.add(encabezado, BorderLayout.NORTH);
        principal.add(botones, BorderLayout.CENTER);

        setContentPane(principal);
    }


    /**
     * Recibo la clase base Persona para usar el comportamiento sobrescrito
     * de Usuario o Estudiante según corresponda.
     */
    private String describirPersona(Persona persona) {
        return persona.getTipoPersona();
    }

    @Override
    public void dispose() {
        super.dispose();
        // La conexión queda disponible si solo se cierra una ventana secundaria.
    }
}
