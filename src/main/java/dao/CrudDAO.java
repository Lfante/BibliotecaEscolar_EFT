package dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones comunes que implementan los DAO principales.
 */
public interface CrudDAO<T> {
    void crear(T entidad) throws SQLException;
    List<T> listarTodos() throws SQLException;
    void actualizar(T entidad) throws SQLException;
    void eliminar(int id) throws SQLException;
}
