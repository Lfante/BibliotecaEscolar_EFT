package dao;

import config.DatabaseConnection;
import modelo.Prestamo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO implements CrudDAO<Prestamo> {

    private Connection conexion() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void crear(Prestamo prestamo) throws SQLException {
        crearEnTransaccion(conexion(), prestamo);
    }

    public void crearEnTransaccion(Connection connection, Prestamo prestamo)
            throws SQLException {
        String sql = """
                INSERT INTO prestamos
                    (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, prestamo.getIdEstudiante());
            ps.setInt(2, prestamo.getIdLibro());
            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            ps.setBoolean(5, prestamo.isDevuelto());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    prestamo.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Prestamo> listarTodos() throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = """
                SELECT p.id, p.id_estudiante, p.id_libro,
                       p.fecha_prestamo, p.fecha_devolucion, p.devuelto,
                       e.nombre AS estudiante, l.titulo AS libro
                FROM prestamos p
                INNER JOIN estudiantes e ON e.id = p.id_estudiante
                INNER JOIN libros l ON l.id = p.id_libro
                ORDER BY p.id DESC
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                prestamos.add(mapear(rs));
            }
        }
        return prestamos;
    }

    public List<Prestamo> listarPorEstudiante(int idEstudiante) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = """
                SELECT p.id, p.id_estudiante, p.id_libro,
                       p.fecha_prestamo, p.fecha_devolucion, p.devuelto,
                       e.nombre AS estudiante, l.titulo AS libro
                FROM prestamos p
                INNER JOIN estudiantes e ON e.id = p.id_estudiante
                INNER JOIN libros l ON l.id = p.id_libro
                WHERE p.id_estudiante = ?
                ORDER BY p.id DESC
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(mapear(rs));
                }
            }
        }
        return prestamos;
    }

    @Override
    public void actualizar(Prestamo prestamo) throws SQLException {
        String sql = """
                UPDATE prestamos
                SET id_estudiante = ?, id_libro = ?,
                    fecha_prestamo = ?, fecha_devolucion = ?, devuelto = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, prestamo.getIdEstudiante());
            ps.setInt(2, prestamo.getIdLibro());
            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            ps.setBoolean(5, prestamo.isDevuelto());
            ps.setInt(6, prestamo.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM prestamos WHERE id = ?";

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Prestamo buscarParaActualizacion(Connection connection, int idPrestamo)
            throws SQLException {
        String sql = """
                SELECT p.id, p.id_estudiante, p.id_libro,
                       p.fecha_prestamo, p.fecha_devolucion, p.devuelto,
                       e.nombre AS estudiante, l.titulo AS libro
                FROM prestamos p
                INNER JOIN estudiantes e ON e.id = p.id_estudiante
                INNER JOIN libros l ON l.id = p.id_libro
                WHERE p.id = ?
                FOR UPDATE
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idPrestamo);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("El préstamo seleccionado no existe.");
                }
                return mapear(rs);
            }
        }
    }

    public void marcarDevuelto(Connection connection, int idPrestamo)
            throws SQLException {
        String sql = "UPDATE prestamos SET devuelto = TRUE WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idPrestamo);
            ps.executeUpdate();
        }
    }

    private Prestamo mapear(ResultSet rs) throws SQLException {
        return new Prestamo(
                rs.getInt("id"),
                rs.getInt("id_estudiante"),
                rs.getInt("id_libro"),
                rs.getDate("fecha_prestamo").toLocalDate(),
                rs.getDate("fecha_devolucion").toLocalDate(),
                rs.getBoolean("devuelto"),
                rs.getString("estudiante"),
                rs.getString("libro")
        );
    }
}
