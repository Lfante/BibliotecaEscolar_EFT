package controller;

import dao.EstudianteDAO;
import modelo.Estudiante;

import java.sql.SQLException;
import java.util.List;

public class EstudianteController {

    private final EstudianteDAO dao = new EstudianteDAO();

    public List<Estudiante> listar() throws SQLException {
        return dao.listarTodos();
    }

    public Estudiante buscarPorRut(String rut) throws SQLException {
        return dao.buscarPorRut(rut);
    }

    public void crear(Estudiante estudiante) throws SQLException {
        validar(estudiante);
        dao.crear(estudiante);
    }

    public void actualizar(Estudiante estudiante) throws SQLException {
        validar(estudiante);
        dao.actualizar(estudiante);
    }

    public void eliminar(int id) throws SQLException {
        dao.eliminar(id);
    }

    private void validar(Estudiante estudiante) {
        if (estudiante.getNombre().isBlank()
                || estudiante.getRut().isBlank()
                || estudiante.getCurso().isBlank()
                || estudiante.getCorreo().isBlank()) {
            throw new IllegalArgumentException(
                    "Nombre, RUT, curso y correo son obligatorios."
            );
        }

        if (!estudiante.getCorreo().contains("@")) {
            throw new IllegalArgumentException("El correo no tiene un formato válido.");
        }
    }
}
