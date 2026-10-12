package service;

import config.DatabaseConnection;
import dao.LibroDAO;
import dao.PrestamoDAO;
import modelo.Prestamo;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Lógica crítica de préstamos.
 *
 * La instancia es única y los métodos que modifican stock son synchronized,
 * así evito que dos operaciones descuenten o devuelvan el mismo stock a la vez.
 */
public final class PrestamoService {

    private static final PrestamoService INSTANCE = new PrestamoService();
    private static final int DIAS_PRESTAMO = 7;

    private final LibroDAO libroDAO = new LibroDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    private PrestamoService() {
    }

    public static PrestamoService getInstance() {
        return INSTANCE;
    }

    public synchronized Prestamo registrarPrestamo(int idEstudiante, int idLibro)
            throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        boolean autoCommitAnterior = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            int stock = libroDAO.obtenerStockParaActualizacion(connection, idLibro);
            if (stock <= 0) {
                throw new SQLException("El libro seleccionado no tiene stock disponible.");
            }

            Prestamo prestamo = new Prestamo(
                    idEstudiante,
                    idLibro,
                    LocalDate.now(),
                    LocalDate.now().plusDays(DIAS_PRESTAMO)
            );

            // El préstamo queda registrado como historial del estudiante.
            prestamoDAO.crearEnTransaccion(connection, prestamo);
            libroDAO.actualizarStock(connection, idLibro, stock - 1);

            connection.commit();
            return prestamo;

        } catch (SQLException e) {
            connection.rollback();
            throw e;

        } finally {
            connection.setAutoCommit(autoCommitAnterior);
        }
    }

    public synchronized boolean devolverPrestamo(int idPrestamo)
            throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        boolean autoCommitAnterior = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            Prestamo prestamo =
                    prestamoDAO.buscarParaActualizacion(connection, idPrestamo);

            if (prestamo.isDevuelto()) {
                throw new SQLException("Este préstamo ya fue devuelto.");
            }

            int stockActual =
                    libroDAO.obtenerStockParaActualizacion(
                            connection,
                            prestamo.getIdLibro()
                    );

            boolean atrasado = prestamo.estaAtrasado();

            prestamoDAO.marcarDevuelto(connection, idPrestamo);
            libroDAO.actualizarStock(
                    connection,
                    prestamo.getIdLibro(),
                    stockActual + 1
            );

            connection.commit();
            return atrasado;

        } catch (SQLException e) {
            connection.rollback();
            throw e;

        } finally {
            connection.setAutoCommit(autoCommitAnterior);
        }
    }
}
