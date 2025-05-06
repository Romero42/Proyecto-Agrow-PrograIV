package cr.ac.una.agrow.domain;

import cr.ac.una.agrow.domain.harvest.Harvest;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sale")
    private int idSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_harvest", nullable = false)
    private Harvest harvest;

    @Column(name = "quantity_sold", nullable = false)
    private int quantitySold;

    @Column(name = "sale_date", nullable = false)
    private LocalDate saleDate;

    @Column(name = "buyer_name", nullable = false, length = 100)
    private String buyerName;

    @Column(name = "buyer_phone", length = 15)
    private String buyerPhone;

    @Column(name = "buyer_address", length = 255)
    private String buyerAddress;

    @Column(name = "transport_option", length = 100)
    private String transportOption;

    @Column(name = "price_per_unit_sold", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double pricePerUnitSold; // Precio al momento de la venta

    @Column(name = "total_sale_amount", nullable = false, columnDefinition = "DECIMAL(12,2)")
    private double totalSaleAmount; // Calculado: quantitySold * pricePerUnitSold

    public Sale() {
        this.saleDate = LocalDate.now();
    }

    // Constructor completo
    public Sale(Harvest harvest, int quantitySold, LocalDate saleDate, String buyerName, String buyerPhone, String buyerAddress, String transportOption, double pricePerUnitSold, double totalSaleAmount) {
        this.harvest = harvest;
        this.quantitySold = quantitySold;
        this.saleDate = saleDate;
        this.buyerName = buyerName;
        this.buyerPhone = buyerPhone;
        this.buyerAddress = buyerAddress;
        this.transportOption = transportOption;
        this.pricePerUnitSold = pricePerUnitSold;
        this.totalSaleAmount = totalSaleAmount;
    }

    // Getters y Setters

    public int getIdSale() {
        return idSale;
    }

    public void setIdSale(int idSale) {
        this.idSale = idSale;
    }

    public Harvest getHarvest() {
        return harvest;
    }

    public void setHarvest(Harvest harvest) {
        this.harvest = harvest;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getTransportOption() {
        return transportOption;
    }

    public void setTransportOption(String transportOption) {
        this.transportOption = transportOption;
    }

    public double getPricePerUnitSold() {
        return pricePerUnitSold;
    }

    public void setPricePerUnitSold(double pricePerUnitSold) {
        this.pricePerUnitSold = pricePerUnitSold;
    }

    public double getTotalSaleAmount() {
        return totalSaleAmount;
    }

    public void setTotalSaleAmount(double totalSaleAmount) {
        this.totalSaleAmount = totalSaleAmount;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "idSale=" + idSale +
                ", harvestId=" + (harvest != null ? harvest.getIdHarvest() : "null") +
                ", quantitySold=" + quantitySold +
                ", saleDate=" + saleDate +
                ", buyerName='" + buyerName + '\'' +
                ", totalSaleAmount=" + totalSaleAmount +
                '}';
    }
}