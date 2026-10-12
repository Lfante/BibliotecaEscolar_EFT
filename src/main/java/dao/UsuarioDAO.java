package dao;

import config.DatabaseConnection;
import modelo.Rol;
import modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements CrudDAO<Usuario> {

    private Connection conexion() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void crear(Usuario usuario) throws SQLException {
        String sql = """
                INSERT INTO usuarios (nombre, rut, correo, `contraseña`, rol)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getRol().name().toLowerCase());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = """
                SELECT id, nombre, rut, correo, `contraseña`, rol
                FROM usuarios
                ORDER BY id
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
        }
        return usuarios;
    }

    @Override
    public void actualizar(Usuario usuario) throws SQLException {
        String sql = """
                UPDATE usuarios
                SET nombre = ?, rut = ?, correo = ?, `contraseña` = ?, rol = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getRol().name().toLowerCase());
            ps.setInt(6, usuario.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Usuario autenticar(String correo, String contrasena) throws SQLException {
        String sql = """
                SELECT id, nombre, rut, correo, `contraseña`, rol
                FROM usuarios
                WHERE correo = ? AND `contraseña` = ?
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("correo"),
                rs.getString("contraseña"),
                Rol.valueOf(rs.getString("rol").toUpperCase())
        );
    }
}
