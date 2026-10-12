package dao;

import config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    private Connection conexion() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Object[]> librosMasPrestados() throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        String sql = """
                SELECT l.titulo, l.autor, COUNT(p.id) AS total_prestamos
                FROM libros l
                LEFT JOIN prestamos p ON p.id_libro = l.id
                GROUP BY l.id, l.titulo, l.autor
                ORDER BY total_prestamos DESC, l.titulo
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getInt("total_prestamos")
                });
            }
        }
        return filas;
    }

    public List<Object[]> historialPorEstudiante(int idEstudiante)
            throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        String sql = """
                SELECT p.id, l.titulo, p.fecha_prestamo,
                       p.fecha_devolucion, p.devuelto
                FROM prestamos p
                INNER JOIN libros l ON l.id = p.id_libro
                WHERE p.id_estudiante = ?
                ORDER BY p.fecha_prestamo DESC, p.id DESC
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getDate("fecha_prestamo"),
                            rs.getDate("fecha_devolucion"),
                            rs.getBoolean("devuelto") ? "DEVUELTO" : "PENDIENTE"
                    });
                }
            }
        }
        return filas;
    }

    public List<Object[]> librosActualmentePrestados() throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        String sql = """
                SELECT p.id, l.titulo, e.nombre AS estudiante,
                       p.fecha_prestamo, p.fecha_devolucion
                FROM prestamos p
                INNER JOIN libros l ON l.id = p.id_libro
                INNER JOIN estudiantes e ON e.id = p.id_estudiante
                WHERE p.devuelto = FALSE
                ORDER BY p.fecha_devolucion, p.id
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("estudiante"),
                        rs.getDate("fecha_prestamo"),
                        rs.getDate("fecha_devolucion")
                });
            }
        }
        return filas;
    }
}
