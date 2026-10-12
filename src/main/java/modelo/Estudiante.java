package modelo;

/**
 * Estudiante asociado a los préstamos de libros.
 */
public class Estudiante extends Persona {

    private String curso;

    public Estudiante() {
    }

    public Estudiante(int id, String nombre, String rut,
                      String curso, String correo) {
        super(id, nombre, rut, correo);
        this.curso = curso;
    }

    public Estudiante(String nombre, String rut, String curso, String correo) {
        this(0, nombre, rut, curso, correo);
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    @Override
    public String getTipoPersona() {
        return "Estudiante - " + curso;
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (" + curso + ")";
    }
}
