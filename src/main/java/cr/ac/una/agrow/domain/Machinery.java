package cr.ac.una.agrow.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_maquina")
public class Machinery {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "condicion", nullable = false, length = 50)
    private String condicion;

    @Column(name = "disponibilidad", nullable = false)
    private Boolean disponibilidad;

    @Column(name = "dia_adquisicion", nullable = false)
    private LocalDate diaAdquisicion;

    @Column(name = "costo_alquiler", nullable = false)
    private double costoAlquiler;

    @Column(name = "ubicacion", nullable = false, length = 100)
    private String ubicacion;

    @Column(name = "capacidad_trabajo", nullable = false, length = 50)
    private String capacidadTrabajo;

    public Machinery() {
    }

    public Machinery(int id, String nombre, String condicion, Boolean disponibilidad,
                     LocalDate diaAdquisicion, double costoAlquiler, String ubicacion,
                     String capacidadTrabajo) {
        this.id = id;
        this.nombre = nombre;
        this.condicion = condicion;
        this.disponibilidad = disponibilidad;
        this.diaAdquisicion = diaAdquisicion;
        this.costoAlquiler = costoAlquiler;
        this.ubicacion = ubicacion;
        this.capacidadTrabajo = capacidadTrabajo;
    }

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

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public Boolean getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public LocalDate getDiaAdquisicion() {
        return diaAdquisicion;
    }

    public void setDiaAdquisicion(LocalDate diaAdquisicion) {
        this.diaAdquisicion = diaAdquisicion;
    }

    public double getCostoAlquiler() {
        return costoAlquiler;
    }

    public void setCostoAlquiler(double costoAlquiler) {
        this.costoAlquiler = costoAlquiler;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getCapacidadTrabajo() {
        return capacidadTrabajo;
    }

    public void setCapacidadTrabajo(String capacidadTrabajo) {
        this.capacidadTrabajo = capacidadTrabajo;
    }
}
