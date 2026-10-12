package vista;

import controller.UsuarioController;
import modelo.Rol;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioFrame extends JFrame {

    private final UsuarioController controller = new UsuarioController();

    private JTextField nombre;
    private JTextField rut;
    private JTextField correo;
    private JPasswordField contrasena;
    private JComboBox<Rol> rol;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private List<Usuario> datos = new ArrayList<>();
    private int idSeleccionado = -1;

    public UsuarioFrame() {
        setTitle("Biblioteca Escolar - Usuarios");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(870, 570);
        setLocationRelativeTo(null);

        inicializar();
        refrescar();
    }

    private void inicializar() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formulario = new JPanel(new GridLayout(5, 2, 8, 8));

        formulario.add(new JLabel("Nombre:"));
        nombre = new JTextField();
        formulario.add(nombre);

        formulario.add(new JLabel("RUT:"));
        rut = new JTextField();
        formulario.add(rut);

        formulario.add(new JLabel("Correo:"));
        correo = new JTextField();
        formulario.add(correo);

        formulario.add(new JLabel("Contraseña:"));
        contrasena = new JPasswordField();
        formulario.add(contrasena);

        formulario.add(new JLabel("Rol:"));
        rol = new JComboBox<>(Rol.values());
        formulario.add(rol);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "RUT", "Correo", "Rol"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });

        JButton crear = new JButton("Crear");
        JButton actualizar = new JButton("Actualizar");
        JButton eliminar = new JButton("Eliminar");
        JButton limpiar = new JButton("Limpiar");
        JButton refrescar = new JButton("Refrescar");

        crear.addActionListener(e -> crear());
        actualizar.addActionListener(e -> actualizar());
        eliminar.addActionListener(e -> eliminar());
        limpiar.addActionListener(e -> limpiar());
        refrescar.addActionListener(e -> refrescar());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        acciones.add(crear);
        acciones.add(actualizar);
        acciones.add(eliminar);
        acciones.add(limpiar);
        acciones.add(refrescar);

        principal.add(formulario, BorderLayout.NORTH);
        principal.add(new JScrollPane(tabla), BorderLayout.CENTER);
        principal.add(acciones, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private Usuario formulario(int id) {
        return new Usuario(
                id,
                nombre.getText().trim(),
                rut.getText().trim(),
                correo.getText().trim(),
                new String(contrasena.getPassword()),
                (Rol) rol.getSelectedItem()
        );
    }

    private void crear() {
        try {
            controller.crear(formulario(0));
            UiUtils.info(this, "Usuario registrado correctamente.");
            limpiar();
            refrescar();
        } catch (IllegalArgumentException | SQLException e) {
            UiUtils.error(this, e.getMessage());
        }
    }

    private void actualizar() {
        if (idSeleccionado < 0) {
            UiUtils.error(this, "Selecciona un usuario.");
            return;
        }

        try {
            controller.actualizar(formulario(idSeleccionado));
            UiUtils.info(this, "Usuario actualizado correctamente.");
            limpiar();
            refrescar();
        } catch (IllegalArgumentException | SQLException e) {
            UiUtils.error(this, e.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado < 0) {
            UiUtils.error(this, "Selecciona un usuario.");
            return;
        }

        if (!UiUtils.confirmar(this, "¿Eliminar el usuario seleccionado?")) {
            return;
        }

        try {
            controller.eliminar(idSeleccionado);
            UiUtils.info(this, "Usuario eliminado.");
            limpiar();
            refrescar();
        } catch (SQLException e) {
            UiUtils.error(this, "No se pudo eliminar.\n" + e.getMessage());
        }
    }

    private void refrescar() {
        modeloTabla.setRowCount(0);

        try {
            datos = controller.listar();

            for (Usuario usuario : datos) {
                modeloTabla.addRow(new Object[]{
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getRut(),
                        usuario.getCorreo(),
                        usuario.getRol()
                });
            }
        } catch (SQLException e) {
            UiUtils.error(this, "No se pudieron cargar los usuarios.\n" + e.getMessage());
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= datos.size()) {
            return;
        }

        Usuario usuario = datos.get(fila);
        idSeleccionado = usuario.getId();
        nombre.setText(usuario.getNombre());
        rut.setText(usuario.getRut());
        correo.setText(usuario.getCorreo());
        contrasena.setText(usuario.getContrasena());
        rol.setSelectedItem(usuario.getRol());
    }

    private void limpiar() {
        idSeleccionado = -1;
        nombre.setText("");
        rut.setText("");
        correo.setText("");
        contrasena.setText("");
        rol.setSelectedIndex(0);
        tabla.clearSelection();
    }
}
