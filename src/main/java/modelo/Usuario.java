package modelo;

/**
 * Usuario que puede autenticarse en el sistema.
 */
public class Usuario extends Persona {

    private String contrasena;
    private Rol rol;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String rut, String correo,
                   String contrasena, Rol rol) {
        super(id, nombre, rut, correo);
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public Usuario(String nombre, String rut, String correo,
                   String contrasena, Rol rol) {
        this(0, nombre, rut, correo, contrasena, rol);
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public String getTipoPersona() {
        return "Usuario - " + rol;
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (" + rol + ")";
    }
}
