package controller;

import dao.CategoriaDAO;
import modelo.Categoria;

import java.sql.SQLException;
import java.util.List;

public class CategoriaController {

    private final CategoriaDAO dao = new CategoriaDAO();

    public List<Categoria> listar() throws SQLException {
        return dao.listarTodos();
    }

    public void crear(Categoria categoria) throws SQLException {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        dao.crear(categoria);
    }

    public void actualizar(Categoria categoria) throws SQLException {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        dao.actualizar(categoria);
    }

    public void eliminar(int id) throws SQLException {
        dao.eliminar(id);
    }
}
