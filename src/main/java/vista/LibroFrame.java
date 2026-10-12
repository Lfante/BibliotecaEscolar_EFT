package vista;

import controller.LibroController;
import modelo.Categoria;
import modelo.Libro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroFrame extends JFrame {

    private final LibroController controller = new LibroController();
    private final boolean soloLectura;

    private JTextField titulo;
    private JTextField autor;
    private JTextField isbn;
    private JTextField editorial;
    private JSpinner stock;
    private JComboBox<Categoria> categoria;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private List<Libro> datos = new ArrayList<>();

    private int idSeleccionado = -1;

    /*
     * Lo uso para evitar que el listener de selección se ejecute
     * mientras estoy reconstruyendo la tabla.
     */
    private boolean actualizandoTabla = false;

    public LibroFrame(boolean soloLectura) {
        this.soloLectura = soloLectura;

        setTitle(
                soloLectura
                        ? "Biblioteca Escolar - Consulta de libros"
                        : "Biblioteca Escolar - Gestión de libros"
        );

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 590);
        setLocationRelativeTo(null);

        inicializar();
        cargarCategorias();
        refrescar();
    }

    private void inicializar() {

        JPanel principal = new JPanel(
                new BorderLayout(10, 10)
        );

        principal.setBorder(
                new EmptyBorder(15, 15, 15, 15)
        );

        JPanel formulario = new JPanel(
                new GridLayout(3, 4, 8, 8)
        );

        formulario.add(new JLabel("Título:"));

        titulo = new JTextField();

        formulario.add(titulo);


        formulario.add(new JLabel("Autor:"));

        autor = new JTextField();

        formulario.add(autor);


        formulario.add(new JLabel("ISBN:"));

        isbn = new JTextField();

        formulario.add(isbn);


        formulario.add(new JLabel("Editorial:"));

        editorial = new JTextField();

        formulario.add(editorial);


        formulario.add(new JLabel("Stock:"));

        stock = new JSpinner(
                new SpinnerNumberModel(
                        0,
                        0,
                        9999,
                        1
                )
        );

        formulario.add(stock);


        formulario.add(new JLabel("Categoría:"));

        categoria = new JComboBox<>();

        formulario.add(categoria);


        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Título",
                        "Autor",
                        "ISBN",
                        "Editorial",
                        "Stock",
                        "Categoría"
                },
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };


        tabla = new JTable(modeloTabla);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        /*
         * Creo el sorter directamente para poder reemplazarlo
         * de forma segura cuando se refresca la tabla.
         */
        tabla.setRowSorter(
                new TableRowSorter<>(modeloTabla)
        );


        if (!soloLectura) {

            tabla.getSelectionModel()
                    .addListSelectionListener(e -> {

                        if (!e.getValueIsAdjusting()
                                && !actualizandoTabla) {

                            cargarSeleccion();
                        }
                    });
        }


        JButton refrescar =
                new JButton("Refrescar");

        refrescar.addActionListener(
                e -> refrescar()
        );


        JPanel acciones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER
                        )
                );


        if (!soloLectura) {

            JButton crear =
                    new JButton("Crear");

            JButton actualizar =
                    new JButton("Actualizar");

            JButton eliminar =
                    new JButton("Eliminar");

            JButton limpiar =
                    new JButton("Limpiar");


            crear.addActionListener(
                    e -> crear()
            );

            actualizar.addActionListener(
                    e -> actualizar()
            );

            eliminar.addActionListener(
                    e -> eliminar()
            );

            limpiar.addActionListener(
                    e -> limpiar()
            );


            acciones.add(crear);
            acciones.add(actualizar);
            acciones.add(eliminar);
            acciones.add(limpiar);
        }


        acciones.add(refrescar);


        /*
         * El estudiante puede consultar libros,
         * pero no modificar sus datos.
         */
        if (soloLectura) {

            titulo.setEnabled(false);
            autor.setEnabled(false);
            isbn.setEnabled(false);
            editorial.setEnabled(false);
            stock.setEnabled(false);
            categoria.setEnabled(false);
        }


        principal.add(
                formulario,
                BorderLayout.NORTH
        );

        principal.add(
                new JScrollPane(tabla),
                BorderLayout.CENTER
        );

        principal.add(
                acciones,
                BorderLayout.SOUTH
        );


        setContentPane(principal);
    }


    private void cargarCategorias() {

        categoria.removeAllItems();

        try {

            for (Categoria item
                    : controller.listarCategorias()) {

                categoria.addItem(item);
            }

        } catch (SQLException e) {

            UiUtils.error(
                    this,
                    "No se pudieron cargar las categorías.\n"
                            + e.getMessage()
            );
        }
    }


    private Libro formulario(int id) {

        Categoria seleccionada =
                (Categoria) categoria.getSelectedItem();


        return new Libro(
                id,
                titulo.getText().trim(),
                autor.getText().trim(),
                isbn.getText().trim(),
                editorial.getText().trim(),
                (Integer) stock.getValue(),

                seleccionada == null
                        ? 0
                        : seleccionada.getId(),

                seleccionada == null
                        ? ""
                        : seleccionada.getNombre()
        );
    }


    private void crear() {

        try {

            controller.crear(
                    formulario(0)
            );


            UiUtils.info(
                    this,
                    "Libro registrado correctamente."
            );


            limpiar();
            refrescar();


        } catch (
                IllegalArgumentException
                | SQLException e
        ) {

            UiUtils.error(
                    this,
                    e.getMessage()
            );
        }
    }


    private void actualizar() {

        if (idSeleccionado < 0) {

            UiUtils.error(
                    this,
                    "Selecciona un libro."
            );

            return;
        }


        try {

            controller.actualizar(
                    formulario(
                            idSeleccionado
                    )
            );


            UiUtils.info(
                    this,
                    "Libro actualizado correctamente."
            );


            limpiar();
            refrescar();


        } catch (
                IllegalArgumentException
                | SQLException e
        ) {

            UiUtils.error(
                    this,
                    e.getMessage()
            );
        }
    }


    private void eliminar() {

        if (idSeleccionado < 0) {

            UiUtils.error(
                    this,
                    "Selecciona un libro."
            );

            return;
        }


        if (!UiUtils.confirmar(
                this,
                "¿Eliminar el libro seleccionado?"
        )) {

            return;
        }


        try {

            /*
             * Guardo el ID antes de modificar la tabla.
             */
            int idEliminar =
                    idSeleccionado;


            /*
             * Limpio primero la selección visual.
             */
            actualizandoTabla = true;

            tabla.clearSelection();

            idSeleccionado = -1;

            actualizandoTabla = false;


            controller.eliminar(
                    idEliminar
            );


            UiUtils.info(
                    this,
                    "Libro eliminado."
            );


            limpiarFormulario();
            refrescar();


        } catch (SQLException e) {

            actualizandoTabla = false;

            UiUtils.error(
                    this,
                    "No se pudo eliminar. "
                            + "Revisa si tiene préstamos asociados.\n"
                            + e.getMessage()
            );
        }
    }


    private void refrescar() {

        /*
         * Mientras se reconstruye el JTable no quiero
         * procesar eventos de selección.
         */
        actualizandoTabla = true;


        try {

            tabla.clearSelection();

            idSeleccionado = -1;


            /*
             * Desconecto temporalmente el sorter.
             *
             * Así no intenta convertir índices mientras
             * el número de filas está cambiando.
             */
            tabla.setRowSorter(null);


            modeloTabla.setRowCount(0);


            datos =
                    controller.listar();


            for (Libro libro : datos) {

                modeloTabla.addRow(
                        new Object[]{
                                libro.getId(),
                                libro.getTitulo(),
                                libro.getAutor(),
                                libro.getIsbn(),
                                libro.getEditorial(),
                                libro.getStock(),
                                libro.getNombreCategoria()
                        }
                );
            }


            /*
             * Una vez terminada la carga creo un sorter
             * nuevo sobre el modelo ya actualizado.
             */
            tabla.setRowSorter(
                    new TableRowSorter<>(
                            modeloTabla
                    )
            );


        } catch (SQLException e) {

            UiUtils.error(
                    this,
                    "No se pudieron cargar los libros.\n"
                            + e.getMessage()
            );


        } finally {

            actualizandoTabla = false;
        }
    }


    private void cargarSeleccion() {

        if (actualizandoTabla) {
            return;
        }


        int filaVista =
                tabla.getSelectedRow();


        if (filaVista < 0) {
            return;
        }


        int filaModelo;


        try {

            filaModelo =
                    tabla.convertRowIndexToModel(
                            filaVista
                    );


        } catch (
                IndexOutOfBoundsException
                | IllegalArgumentException e
        ) {

            return;
        }


        if (filaModelo < 0
                || filaModelo >= datos.size()) {

            return;
        }


        Libro libro =
                datos.get(filaModelo);


        idSeleccionado =
                libro.getId();


        titulo.setText(
                libro.getTitulo()
        );

        autor.setText(
                libro.getAutor()
        );

        isbn.setText(
                libro.getIsbn()
        );

        editorial.setText(
                libro.getEditorial()
        );

        stock.setValue(
                libro.getStock()
        );


        seleccionarCategoria(
                libro.getIdCategoria()
        );
    }


    private void seleccionarCategoria(
            int idCategoria
    ) {

        for (
                int i = 0;
                i < categoria.getItemCount();
                i++
        ) {

            Categoria item =
                    categoria.getItemAt(i);


            if (item.getId()
                    == idCategoria) {

                categoria.setSelectedIndex(i);

                return;
            }
        }
    }


    private void limpiar() {

        actualizandoTabla = true;

        tabla.clearSelection();

        actualizandoTabla = false;


        idSeleccionado = -1;

        limpiarFormulario();
    }


    private void limpiarFormulario() {

        titulo.setText("");
        autor.setText("");
        isbn.setText("");
        editorial.setText("");

        stock.setValue(0);


        if (categoria.getItemCount() > 0) {

            categoria.setSelectedIndex(0);
        }
    }
}