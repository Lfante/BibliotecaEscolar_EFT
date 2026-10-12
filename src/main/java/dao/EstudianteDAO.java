package dao;

import config.DatabaseConnection;
import modelo.Estudiante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO implements CrudDAO<Estudiante> {

    private Connection conexion() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void crear(Estudiante estudiante) throws SQLException {
        String sql = """
                INSERT INTO estudiantes (nombre, rut, curso, correo)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    estudiante.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Estudiante> listarTodos() throws SQLException {
        List<Estudiante> estudiantes = new ArrayList<>();
        String sql = """
                SELECT id, nombre, rut, curso, correo
                FROM estudiantes
                ORDER BY id
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                estudiantes.add(mapear(rs));
            }
        }
        return estudiantes;
    }

    @Override
    public void actualizar(Estudiante estudiante) throws SQLException {
        String sql = """
                UPDATE estudiantes
                SET nombre = ?, rut = ?, curso = ?, correo = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());
            ps.setInt(5, estudiante.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM estudiantes WHERE id = ?";

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Estudiante buscarPorRut(String rut) throws SQLException {
        String sql = """
                SELECT id, nombre, rut, curso, correo
                FROM estudiantes
                WHERE rut = ?
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, rut);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    private Estudiante mapear(ResultSet rs) throws SQLException {
        return new Estudiante(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("curso"),
                rs.getString("correo")
        );
    }
}
