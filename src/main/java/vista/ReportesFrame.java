package vista;

import controller.ReporteController;
import modelo.Estudiante;
import modelo.Rol;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class ReportesFrame extends JFrame {

    private final ReporteController controller = new ReporteController();
    private final Usuario usuario;

    public ReportesFrame(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Biblioteca Escolar - Reportes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);

        inicializar();
    }

    private void inicializar() {
        JTabbedPane tabs = new JTabbedPane();

        if (usuario.getRol() == Rol.BIBLIOTECARIO) {
            tabs.addTab("Libros más prestados", panelLibrosMasPrestados());
            tabs.addTab("Historial por estudiante", panelHistorialBibliotecario());
            tabs.addTab("Actualmente prestados", panelPrestamosActuales());
        } else {
            tabs.addTab("Mi historial", panelHistorialEstudiante());
        }

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBorder(new EmptyBorder(12, 12, 12, 12));
        principal.add(tabs, BorderLayout.CENTER);

        setContentPane(principal);
    }

    private JPanel panelLibrosMasPrestados() {
        DefaultTableModel modelo = tablaModelo(
                "Libro", "Autor", "Total préstamos"
        );
        JTable tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);

        JButton refrescar = new JButton("Refrescar");
        refrescar.addActionListener(e -> cargarMasPrestados(modelo));
        cargarMasPrestados(modelo);

        return panelConTabla(tabla, refrescar);
    }

    private JPanel panelHistorialBibliotecario() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JComboBox<Estudiante> combo = new JComboBox<>();
        DefaultTableModel modelo = tablaModelo(
                "ID", "Libro", "Fecha préstamo", "Vencimiento", "Estado"
        );
        JTable tabla = new JTable(modelo);

        try {
            for (Estudiante estudiante : controller.listarEstudiantes()) {
                combo.addItem(estudiante);
            }
        } catch (SQLException e) {
            UiUtils.error(this, "No se pudieron cargar estudiantes.\n" + e.getMessage());
        }

        JButton consultar = new JButton("Consultar");
        consultar.addActionListener(e -> {
            Estudiante estudiante = (Estudiante) combo.getSelectedItem();

            if (estudiante == null) {
                UiUtils.error(this, "Selecciona un estudiante.");
                return;
            }

            cargarHistorial(modelo, estudiante.getId());
        });

        JPanel superior = new JPanel(new BorderLayout(8, 8));
        superior.add(combo, BorderLayout.CENTER);
        superior.add(consultar, BorderLayout.EAST);

        panel.add(superior, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel panelHistorialEstudiante() {
        DefaultTableModel modelo = tablaModelo(
                "ID", "Libro", "Fecha préstamo", "Vencimiento", "Estado"
        );
        JTable tabla = new JTable(modelo);

        try {
            Estudiante estudiante =
                    controller.buscarEstudiantePorRut(usuario.getRut());

            if (estudiante == null) {
                UiUtils.error(
                        this,
                        "Tu usuario no está vinculado a un estudiante con el mismo RUT."
                );
            } else {
                cargarHistorial(modelo, estudiante.getId());
            }
        } catch (SQLException e) {
            UiUtils.error(this, "No se pudo cargar el historial.\n" + e.getMessage());
        }

        JButton refrescar = new JButton("Refrescar");
        refrescar.addActionListener(e -> {
            try {
                Estudiante estudiante =
                        controller.buscarEstudiantePorRut(usuario.getRut());
                if (estudiante != null) {
                    cargarHistorial(modelo, estudiante.getId());
                }
            } catch (SQLException ex) {
                UiUtils.error(this, ex.getMessage());
            }
        });

        return panelConTabla(tabla, refrescar);
    }

    private JPanel panelPrestamosActuales() {
        DefaultTableModel modelo = tablaModelo(
                "ID", "Libro", "Estudiante", "Fecha préstamo", "Vencimiento"
        );
        JTable tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);

        JButton refrescar = new JButton("Refrescar");
        refrescar.addActionListener(e -> cargarPrestamosActuales(modelo));
        cargarPrestamosActuales(modelo);

        return panelConTabla(tabla, refrescar);
    }

    private JPanel panelConTabla(JTable tabla, JButton boton) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(boton);
        panel.add(acciones, BorderLayout.SOUTH);

        return panel;
    }

    private DefaultTableModel tablaModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void cargarMasPrestados(DefaultTableModel modelo) {
        modelo.setRowCount(0);

        try {
            for (Object[] fila : controller.librosMasPrestados()) {
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            UiUtils.error(this, "No se pudo generar el reporte.\n" + e.getMessage());
        }
    }

    private void cargarHistorial(DefaultTableModel modelo, int idEstudiante) {
        modelo.setRowCount(0);

        try {
            for (Object[] fila : controller.historialPorEstudiante(idEstudiante)) {
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            UiUtils.error(this, "No se pudo generar el historial.\n" + e.getMessage());
        }
    }

    private void cargarPrestamosActuales(DefaultTableModel modelo) {
        modelo.setRowCount(0);

        try {
            for (Object[] fila : controller.librosActualmentePrestados()) {
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            UiUtils.error(this, "No se pudo generar el reporte.\n" + e.getMessage());
        }
    }
}
