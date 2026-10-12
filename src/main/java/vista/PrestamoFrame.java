package vista;

import controller.PrestamoController;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Rol;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrestamoFrame extends JFrame {

    private final PrestamoController controller = new PrestamoController();
    private final Usuario usuario;

    private JComboBox<Estudiante> estudiantes;
    private JComboBox<Libro> libros;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton botonPrestar;
    private JButton botonDevolver;

    private List<Prestamo> datos = new ArrayList<>();
    private Estudiante estudianteSesion;

    public PrestamoFrame(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Biblioteca Escolar - Préstamos y devoluciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 560);
        setLocationRelativeTo(null);

        inicializar();
        cargarCombos();
        refrescar();
    }

    private void inicializar() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formulario = new JPanel(new GridLayout(2, 2, 8, 8));

        formulario.add(new JLabel("Estudiante:"));
        estudiantes = new JComboBox<>();
        formulario.add(estudiantes);

        formulario.add(new JLabel("Libro:"));
        libros = new JComboBox<>();
        formulario.add(libros);

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID", "Estudiante", "Libro",
                        "Fecha préstamo", "Vencimiento", "Estado"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);

        botonPrestar = new JButton("Registrar préstamo");
        botonDevolver = new JButton("Registrar devolución");
        JButton refrescar = new JButton("Refrescar");

        botonPrestar.addActionListener(e -> registrarPrestamo());
        botonDevolver.addActionListener(e -> devolver());
        refrescar.addActionListener(e -> {
            cargarCombos();
            refrescar();
        });

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        acciones.add(botonPrestar);
        acciones.add(botonDevolver);
        acciones.add(refrescar);

        principal.add(formulario, BorderLayout.NORTH);
        principal.add(new JScrollPane(tabla), BorderLayout.CENTER);
        principal.add(acciones, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private void cargarCombos() {
        estudiantes.removeAllItems();
        libros.removeAllItems();

        try {
            if (usuario.getRol() == Rol.ESTUDIANTE) {
                estudianteSesion = controller.buscarEstudiantePorRut(usuario.getRut());

                if (estudianteSesion == null) {
                    UiUtils.error(
                            this,
                            "Tu usuario no está vinculado a un estudiante con el mismo RUT."
                    );
                } else {
                    estudiantes.addItem(estudianteSesion);
                    estudiantes.setEnabled(false);
                }

            } else {
                for (Estudiante estudiante : controller.listarEstudiantes()) {
                    estudiantes.addItem(estudiante);
                }
            }

            for (Libro libro : controller.listarLibros()) {
                libros.addItem(libro);
            }

        } catch (SQLException e) {
            UiUtils.error(this, "No se pudieron cargar los datos.\n" + e.getMessage());
        }
    }

    private void registrarPrestamo() {
        Estudiante estudiante = (Estudiante) estudiantes.getSelectedItem();
        Libro libro = (Libro) libros.getSelectedItem();

        if (estudiante == null || libro == null) {
            UiUtils.error(this, "Selecciona estudiante y libro.");
            return;
        }

        setProcesando(true);

        controller.registrarPrestamoAsync(
                estudiante.getId(),
                libro.getId(),
                prestamo -> {
                    UiUtils.info(
                            this,
                            "Préstamo #" + prestamo.getId()
                                    + " registrado.\nVence el "
                                    + prestamo.getFechaDevolucion() + "."
                    );
                    cargarCombos();
                    refrescar();
                },
                mensaje -> UiUtils.error(this, mensaje),
                () -> setProcesando(false)
        );
    }

    private void devolver() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            UiUtils.error(this, "Selecciona un préstamo de la tabla.");
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        Prestamo prestamo = datos.get(filaModelo);

        if (prestamo.isDevuelto()) {
            UiUtils.error(this, "El préstamo seleccionado ya fue devuelto.");
            return;
        }

        setProcesando(true);

        controller.devolverPrestamoAsync(
                prestamo.getId(),
                atrasado -> {
                    String mensaje = atrasado
                            ? "Devolución registrada. El préstamo estaba atrasado."
                            : "Devolución registrada dentro del plazo.";

                    UiUtils.info(this, mensaje);
                    cargarCombos();
                    refrescar();
                },
                mensaje -> UiUtils.error(this, mensaje),
                () -> setProcesando(false)
        );
    }

    private void refrescar() {
        modeloTabla.setRowCount(0);

        try {
            if (usuario.getRol() == Rol.ESTUDIANTE) {
                if (estudianteSesion == null) {
                    datos = new ArrayList<>();
                } else {
                    datos = controller.listarPorEstudiante(estudianteSesion.getId());
                }
            } else {
                datos = controller.listar();
            }

            for (Prestamo prestamo : datos) {
                String estado;

                if (prestamo.isDevuelto()) {
                    estado = "DEVUELTO";
                } else if (prestamo.estaAtrasado()) {
                    estado = "ATRASADO";
                } else {
                    estado = "EN PRÉSTAMO";
                }

                modeloTabla.addRow(new Object[]{
                        prestamo.getId(),
                        prestamo.getEstudiante(),
                        prestamo.getLibro(),
                        prestamo.getFechaPrestamo(),
                        prestamo.getFechaDevolucion(),
                        estado
                });
            }

        } catch (SQLException e) {
            UiUtils.error(this, "No se pudieron cargar los préstamos.\n" + e.getMessage());
        }
    }

    private void setProcesando(boolean procesando) {
        botonPrestar.setEnabled(!procesando);
        botonDevolver.setEnabled(!procesando);
        setCursor(
                procesando
                        ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                        : Cursor.getDefaultCursor()
        );
    }
}
