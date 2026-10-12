package vista;

import controller.EstudianteController;
import modelo.Estudiante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstudianteFrame extends JFrame {

    private final EstudianteController controller = new EstudianteController();

    private JTextField nombre;
    private JTextField rut;
    private JTextField curso;
    private JTextField correo;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private List<Estudiante> datos = new ArrayList<>();
    private int idSeleccionado = -1;

    public EstudianteFrame() {
        setTitle("Biblioteca Escolar - Estudiantes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(830, 540);
        setLocationRelativeTo(null);

        inicializar();
        refrescar();
    }

    private void inicializar() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 8));

        formulario.add(new JLabel("Nombre:"));
        nombre = new JTextField();
        formulario.add(nombre);

        formulario.add(new JLabel("RUT:"));
        rut = new JTextField();
        formulario.add(rut);

        formulario.add(new JLabel("Curso:"));
        curso = new JTextField();
        formulario.add(curso);

        formulario.add(new JLabel("Correo:"));
        correo = new JTextField();
        formulario.add(correo);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "RUT", "Curso", "Correo"},
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

    private Estudiante formulario(int id) {
        return new Estudiante(
                id,
                nombre.getText().trim(),
                rut.getText().trim(),
                curso.getText().trim(),
                correo.getText().trim()
        );
    }

    private void crear() {
        try {
            controller.crear(formulario(0));
            UiUtils.info(this, "Estudiante registrado correctamente.");
            limpiar();
            refrescar();
        } catch (IllegalArgumentException | SQLException e) {
            UiUtils.error(this, e.getMessage());
        }
    }

    private void actualizar() {
        if (idSeleccionado < 0) {
            UiUtils.error(this, "Selecciona un estudiante.");
            return;
        }

        try {
            controller.actualizar(formulario(idSeleccionado));
            UiUtils.info(this, "Estudiante actualizado correctamente.");
            limpiar();
            refrescar();
        } catch (IllegalArgumentException | SQLException e) {
            UiUtils.error(this, e.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado < 0) {
            UiUtils.error(this, "Selecciona un estudiante.");
            return;
        }

        if (!UiUtils.confirmar(this, "¿Eliminar el estudiante seleccionado?")) {
            return;
        }

        try {
            controller.eliminar(idSeleccionado);
            UiUtils.info(this, "Estudiante eliminado.");
            limpiar();
            refrescar();
        } catch (SQLException e) {
            UiUtils.error(
                    this,
                    "No se pudo eliminar. Revisa si tiene préstamos asociados.\n"
                            + e.getMessage()
            );
        }
    }

    private void refrescar() {
        modeloTabla.setRowCount(0);

        try {
            datos = controller.listar();

            for (Estudiante estudiante : datos) {
                modeloTabla.addRow(new Object[]{
                        estudiante.getId(),
                        estudiante.getNombre(),
                        estudiante.getRut(),
                        estudiante.getCurso(),
                        estudiante.getCorreo()
                });
            }
        } catch (SQLException e) {
            UiUtils.error(this, "No se pudieron cargar estudiantes.\n" + e.getMessage());
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= datos.size()) {
            return;
        }

        Estudiante estudiante = datos.get(fila);
        idSeleccionado = estudiante.getId();
        nombre.setText(estudiante.getNombre());
        rut.setText(estudiante.getRut());
        curso.setText(estudiante.getCurso());
        correo.setText(estudiante.getCorreo());
    }

    private void limpiar() {
        idSeleccionado = -1;
        nombre.setText("");
        rut.setText("");
        curso.setText("");
        correo.setText("");
        tabla.clearSelection();
    }
}
