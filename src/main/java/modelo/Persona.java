package modelo;

/**
 * Base común para usuarios y estudiantes.
 * Me sirve para reutilizar los datos personales y aplicar herencia.
 */
public abstract class Persona implements Identificable {

    protected int id;
    protected String nombre;
    protected String rut;
    protected String correo;

    protected Persona() {
    }

    protected Persona(int id, String nombre, String rut, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Cada tipo de persona entrega su propia descripción.
     */
    public abstract String getTipoPersona();
}
