package cr.ac.una.agrow.domain.harvest;

import cr.ac.una.agrow.domain.producer.Producer; // Assuming this import might be needed if you had a Producer object association, but not strictly for id_producer
import java.time.LocalDate;
import jakarta.persistence.*;

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
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "destiny", type = String.class) // Corrected name from "destiny" to match actual usage, or "harvestDestiny" if that was intended in SP
                }
        )
})
@Table(name = "tb_harvest")
public class Harvest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_harvest")
    private int idHarvest;

    @Column(name = "type_harvest")
    private String typeHarvest;

    @Column(name = "date_harvested")
    private LocalDate dateHarvested;

    @Column(name = "quantity_harvested")
    private int quantityHarvested;

    @Column(name = "available_quantity")
    private int availableQuantity;

    @Column(name = "quality")
    private String quality;

    @Column(name = "destiny")
    private String destiny;

    @Column(name = "registered_harvest")
    private boolean registeredHarvest;

    @Column(name = "description")
    private String description;

    @Column(name = "id_producer")
    private Integer id_producer; // MODIFIED: Changed from int to Integer

    public Harvest() {
        // id_producer will be null by default for Integer
    }

    public Harvest(int idHarvest, String typeHarvest, LocalDate dateHarvested, int quantityHarvested, int availableQuantity, String quality, String destiny, boolean registeredHarvest, String description, Integer id_producer) {
        this.idHarvest = idHarvest;
        this.typeHarvest = typeHarvest;
        this.dateHarvested = dateHarvested;
        this.quantityHarvested = quantityHarvested;
        this.availableQuantity = availableQuantity;
        this.quality = quality;
        this.destiny = destiny;
        this.registeredHarvest = registeredHarvest;
        this.description = description;
        this.id_producer = id_producer;
    }

    // Getters y Setters

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

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
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

    public Integer getId_producer() {
        return id_producer;
    }

    public void setId_producer(Integer id_producer) {
        this.id_producer = id_producer;
    }

}