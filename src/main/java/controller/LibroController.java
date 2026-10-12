package controller;

import dao.CategoriaDAO;
import dao.LibroDAO;
import modelo.Categoria;
import modelo.Libro;

import java.sql.SQLException;
import java.util.List;

public class LibroController {

    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public List<Libro> listar() throws SQLException {
        return libroDAO.listarTodos();
    }

    public List<Categoria> listarCategorias() throws SQLException {
        return categoriaDAO.listarTodos();
    }

    public void crear(Libro libro) throws SQLException {
        validar(libro);
        libroDAO.crear(libro);
    }

    public void actualizar(Libro libro) throws SQLException {
        validar(libro);
        libroDAO.actualizar(libro);
    }

    public void eliminar(int id) throws SQLException {
        libroDAO.eliminar(id);
    }

    private void validar(Libro libro) {
        if (libro.getTitulo().isBlank()
                || libro.getAutor().isBlank()
                || libro.getIsbn().isBlank()
                || libro.getEditorial().isBlank()) {
            throw new IllegalArgumentException(
                    "Título, autor, ISBN y editorial son obligatorios."
            );
        }

        if (libro.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }

        if (libro.getIdCategoria() <= 0) {
            throw new IllegalArgumentException("Debes seleccionar una categoría.");
        }
    }
}
