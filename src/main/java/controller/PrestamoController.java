package controller;

import dao.EstudianteDAO;
import dao.LibroDAO;
import dao.PrestamoDAO;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;
import service.PrestamoService;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class PrestamoController {

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final PrestamoService service = PrestamoService.getInstance();

    public List<Prestamo> listar() throws SQLException {
        return prestamoDAO.listarTodos();
    }

    public List<Prestamo> listarPorEstudiante(int idEstudiante)
            throws SQLException {
        return prestamoDAO.listarPorEstudiante(idEstudiante);
    }

    public List<Estudiante> listarEstudiantes() throws SQLException {
        return estudianteDAO.listarTodos();
    }

    public List<Libro> listarLibros() throws SQLException {
        return libroDAO.listarTodos();
    }

    public Estudiante buscarEstudiantePorRut(String rut) throws SQLException {
        return estudianteDAO.buscarPorRut(rut);
    }

    /**
     * SwingWorker ejecuta la operación en segundo plano para no congelar la UI.
     */
    public void registrarPrestamoAsync(
            int idEstudiante,
            int idLibro,
            Consumer<Prestamo> alTerminar,
            Consumer<String> alFallar,
            Runnable alFinalizar) {

        new SwingWorker<Prestamo, Void>() {
            @Override
            protected Prestamo doInBackground() throws Exception {
                return service.registrarPrestamo(idEstudiante, idLibro);
            }

            @Override
            protected void done() {
                try {
                    alTerminar.accept(get());
                } catch (Exception e) {
                    alFallar.accept(extraerMensaje(e));
                } finally {
                    alFinalizar.run();
                }
            }
        }.execute();
    }

    public void devolverPrestamoAsync(
            int idPrestamo,
            Consumer<Boolean> alTerminar,
            Consumer<String> alFallar,
            Runnable alFinalizar) {

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return service.devolverPrestamo(idPrestamo);
            }

            @Override
            protected void done() {
                try {
                    alTerminar.accept(get());
                } catch (Exception e) {
                    alFallar.accept(extraerMensaje(e));
                } finally {
                    alFinalizar.run();
                }
            }
        }.execute();
    }

    private String extraerMensaje(Exception e) {
        Throwable causa = e;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return causa.getMessage() == null
                ? "Ocurrió un error durante la operación."
                : causa.getMessage();
    }
}
