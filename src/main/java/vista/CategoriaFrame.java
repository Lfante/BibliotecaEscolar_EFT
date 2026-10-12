package vista;

import controller.CategoriaController;
import modelo.Categoria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaFrame extends JFrame {

    private final CategoriaController controller = new CategoriaController();

    private JTextField nombre;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private List<Categoria> datos = new ArrayList<>();
    private int idSeleccionado = -1;

    public CategoriaFrame() {
        setTitle("Biblioteca Escolar - Categorías");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 430);
        setLocationRelativeTo(null);

        inicializar();
        refrescar();
    }

    private void inicializar() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formulario = new JPanel(new BorderLayout(8, 8));
        formulario.add(new JLabel("Nombre:"), BorderLayout.WEST);
        nombre = new JTextField();
        formulario.add(nombre, BorderLayout.CENTER);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre"},
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

    private void crear() {
        try {
            controller.crear(new Categoria(nombre.getText().trim()));
            UiUtils.info(this, "Categoría creada.");
            limpiar();
            refrescar();
        } catch (IllegalArgumentException | SQLException e) {
            UiUtils.error(this, e.getMessage());
        }
    }

    private void actualizar() {
        if (idSeleccionado < 0) {
            UiUtils.error(this, "Selecciona una categoría.");
            return;
        }

        try {
            controller.actualizar(
                    new Categoria(idSeleccionado, nombre.getText().trim())
            );
            UiUtils.info(this, "Categoría actualizada.");
            limpiar();
            refrescar();
        } catch (IllegalArgumentException | SQLException e) {
            UiUtils.error(this, e.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado < 0) {
            UiUtils.error(this, "Selecciona una categoría.");
            return;
        }

        if (!UiUtils.confirmar(this, "¿Eliminar la categoría seleccionada?")) {
            return;
        }

        try {
            controller.eliminar(idSeleccionado);
            UiUtils.info(this, "Categoría eliminada.");
            limpiar();
            refrescar();
        } catch (SQLException e) {
            UiUtils.error(
                    this,
                    "No se pudo eliminar. Revisa si tiene libros asociados.\n"
                            + e.getMessage()
            );
        }
    }

    private void refrescar() {
        modeloTabla.setRowCount(0);

        try {
            datos = controller.listar();
            for (Categoria categoria : datos) {
                modeloTabla.addRow(new Object[]{
                        categoria.getId(),
                        categoria.getNombre()
                });
            }
        } catch (SQLException e) {
            UiUtils.error(this, "No se pudieron cargar categorías.\n" + e.getMessage());
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= datos.size()) {
            return;
        }

        Categoria categoria = datos.get(fila);
        idSeleccionado = categoria.getId();
        nombre.setText(categoria.getNombre());
    }

    private void limpiar() {
        idSeleccionado = -1;
        nombre.setText("");
        tabla.clearSelection();
    }
}
