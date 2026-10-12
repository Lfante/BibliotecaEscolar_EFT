package controller;

import dao.UsuarioDAO;
import modelo.Usuario;

import java.sql.SQLException;

public class AuthController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario autenticar(String correo, String contrasena) throws SQLException {
        if (correo == null || correo.isBlank()
                || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException(
                    "Correo y contraseña son obligatorios."
            );
        }

        Usuario usuario = usuarioDAO.autenticar(
                correo.trim(),
                contrasena
        );

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Correo o contraseña incorrectos."
            );
        }

        return usuario;
    }
}
