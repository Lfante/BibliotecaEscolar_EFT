package modelo;

import java.time.LocalDate;

public class Prestamo implements Identificable {

    private int id;
    private int idEstudiante;
    private int idLibro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto;
    private String estudiante;
    private String libro;

    public Prestamo() {
    }

    public Prestamo(int id, int idEstudiante, int idLibro,
                    LocalDate fechaPrestamo, LocalDate fechaDevolucion,
                    boolean devuelto, String estudiante, String libro) {
        this.id = id;
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
        this.estudiante = estudiante;
        this.libro = libro;
    }

    public Prestamo(int idEstudiante, int idLibro,
                    LocalDate fechaPrestamo, LocalDate fechaDevolucion) {
        this(0, idEstudiante, idLibro, fechaPrestamo, fechaDevolucion,
                false, "", "");
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public String getLibro() {
        return libro;
    }

    public void setLibro(String libro) {
        this.libro = libro;
    }

    public boolean estaAtrasado() {
        return !devuelto
                && fechaDevolucion != null
                && LocalDate.now().isAfter(fechaDevolucion);
    }
}
