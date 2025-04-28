/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.agrow.domain.machinery;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author andre
 */
public class Machinery {
    private int id;
    private String nombre;
    private String condicion;
    private Boolean disponibilidad;
    private LocalDate diaAdquisicion;
    private double costoAlquiler;
    private String ubicacion;
    private String capacidadTrabajo;

    public Machinery() {
    }

    public Machinery(int id, String nombre, String condicion, Boolean disponibilidad, LocalDate diaAdquisicion, double costoAlquiler, String ubicacion, String capacidadTrabajo) {
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
