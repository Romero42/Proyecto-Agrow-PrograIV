/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.agrow.domain.harvest;

import cr.ac.una.agrow.domain.producer.Producer;
import java.time.LocalDate;
import jakarta.persistence.*;
/**
 *
 * @author miste
 */

@Entity
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
        name = "Harvest.getByQuality",
        procedureName = "sp_getHarvestByQuality",
        resultClasses = Harvest.class,
        parameters = {
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "harvestQuality", type = String.class)
        }
    ),
    @NamedStoredProcedureQuery(
        name = "Harvest.getByDestiny",
        procedureName = "sp_getHarvestByDestiny",
        resultClasses = Harvest.class,
        parameters = {
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "destiny", type = String.class)
        }
    )
})
@Table(name = "tb_harvest")
public class Harvest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int idHarvest;

    private String typeHarvest;
    private LocalDate dateHarvested;
    private int quantityHarvested;
    private String quality;
    private String destiny;
    private boolean registeredHarvest;
    private String description;
    private int id_producer;

    public Harvest() {
    }

    public Harvest(int idHarvest, String typeHarvest, LocalDate dateHarvested, int quantityHarvested, String quality, String destiny, boolean registeredHarvest, String description, int id_producer) {
        this.idHarvest = idHarvest;
        this.typeHarvest = typeHarvest;
        this.dateHarvested = dateHarvested;
        this.quantityHarvested = quantityHarvested;
        this.quality = quality;
        this.destiny = destiny;
        this.registeredHarvest = registeredHarvest;
        this.description = description;
        this.id_producer = id_producer;
    }

    public int getIdHarvest() {
        return idHarvest;
    }

    public void setIdHarvest(int idHarvest) {
        this.idHarvest = idHarvest;
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

    public boolean isRegisteredHarvest() {
        return registeredHarvest;
    }

    public void setRegisteredHarvest(boolean registeredHarvest) {
        this.registeredHarvest = registeredHarvest;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId_producer() {
        return id_producer;
    }

    public void setId_producer(int id_producer) {
        this.id_producer = id_producer;
    }
}
