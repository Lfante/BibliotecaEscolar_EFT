package controller;

import dao.UsuarioDAO;
import modelo.Usuario;

import java.sql.SQLException;
import java.util.List;

public class UsuarioController {

    private final UsuarioDAO dao = new UsuarioDAO();

    public List<Usuario> listar() throws SQLException {
        return dao.listarTodos();
    }

    public void crear(Usuario usuario) throws SQLException {
        validar(usuario);
        dao.crear(usuario);
    }

    public void actualizar(Usuario usuario) throws SQLException {
        validar(usuario);
        dao.actualizar(usuario);
    }

    public void eliminar(int id) throws SQLException {
        dao.eliminar(id);
    }

    private void validar(Usuario usuario) {
        if (usuario.getNombre().isBlank()
                || usuario.getRut().isBlank()
                || usuario.getCorreo().isBlank()
                || usuario.getContrasena().isBlank()) {
            throw new IllegalArgumentException(
                    "Nombre, RUT, correo y contraseña son obligatorios."
            );
        }

        if (!usuario.getCorreo().contains("@")) {
            throw new IllegalArgumentException("El correo no tiene un formato válido.");
        }
    }
}
