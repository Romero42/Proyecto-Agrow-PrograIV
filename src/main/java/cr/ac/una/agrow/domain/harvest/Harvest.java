/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.agrow.domain.harvest;

import java.time.LocalDate;

/**
 *
 * @author miste
 */
public class Harvest {

    private int idHarvest;
    private String typeHarvest;
    private LocalDate dateHarvested;
    private int quantityHarvested;
    private String quality;
    private String destiny;
    private boolean registeredHarvest;
    private String description;

    public Harvest() {
    }

    public Harvest(int idHarvest, String typeHarvest, LocalDate dateHarvested, int quantityHarvested, String quality, String destiny, Boolean registeredHarvest, String description) {
        this.idHarvest = idHarvest;
        this.typeHarvest = typeHarvest;
        this.dateHarvested = dateHarvested;
        this.quantityHarvested = quantityHarvested;
        this.quality = quality;
        this.destiny = destiny;
        this.registeredHarvest = registeredHarvest;
        this.description = description;
    }

    public int getIdHarvest() {
        return idHarvest;
    }

    public void setIdHarvest(int idHarvest) {
        this.idHarvest = idHarvest;
    }

    public boolean isRegisteredHarvest() {
        return registeredHarvest;
    }

    public void setRegisteredHarvest(boolean registeredHarvest) {
        this.registeredHarvest = registeredHarvest;
    }

    public String getTypeHarvest() {
        return typeHarvest;
    }

    public void setTypeHarvest(String typeHarvest) {
        this.typeHarvest = typeHarvest;
    }

    public LocalDate getDateHarvested() {
        return dateHarvested;
    }

    public void setDateHarvested(LocalDate dateHarvested) {
        this.dateHarvested = dateHarvested;
    }

    public int getQuantityHarvested() {
        return quantityHarvested;
    }

    public void setQuantityHarvested(int quantityHarvested) {
        this.quantityHarvested = quantityHarvested;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getDestiny() {
        return destiny;
    }

    public void setDestiny(String destiny) {
        this.destiny = destiny;
    }

    public Boolean getRegisteredHarvest() {
        return registeredHarvest;
    }

    public void setRegisteredHarvest(Boolean registeredHarvest) {
        this.registeredHarvest = registeredHarvest;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
