package dao;

import config.DatabaseConnection;
import modelo.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO implements CrudDAO<Libro> {

    private Connection conexion() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void crear(Libro libro) throws SQLException {
        String sql = """
                INSERT INTO libros (titulo, autor, isbn, editorial, stock, id_categoria)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    libro.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Libro> listarTodos() throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String sql = """
                SELECT l.id, l.titulo, l.autor, l.isbn, l.editorial,
                       l.stock, l.id_categoria, c.nombre AS categoria
                FROM libros l
                INNER JOIN categorias c ON c.id = l.id_categoria
                ORDER BY l.id
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                libros.add(mapear(rs));
            }
        }
        return libros;
    }

    @Override
    public void actualizar(Libro libro) throws SQLException {
        String sql = """
                UPDATE libros
                SET titulo = ?, autor = ?, isbn = ?, editorial = ?,
                    stock = ?, id_categoria = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());
            ps.setInt(7, libro.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM libros WHERE id = ?";

        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int obtenerStockParaActualizacion(Connection connection, int idLibro)
            throws SQLException {
        String sql = "SELECT stock FROM libros WHERE id = ? FOR UPDATE";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idLibro);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("El libro seleccionado no existe.");
                }
                return rs.getInt("stock");
            }
        }
    }

    public void actualizarStock(Connection connection, int idLibro, int nuevoStock)
            throws SQLException {
        if (nuevoStock < 0) {
            throw new SQLException("El stock no puede quedar negativo.");
        }

        String sql = "UPDATE libros SET stock = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idLibro);
            ps.executeUpdate();
        }
    }

    private Libro mapear(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("isbn"),
                rs.getString("editorial"),
                rs.getInt("stock"),
                rs.getInt("id_categoria"),
                rs.getString("categoria")
        );
    }
}
