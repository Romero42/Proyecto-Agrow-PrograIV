package cr.ac.una.agrow.domain;

import cr.ac.una.agrow.domain.supplier.Supplier;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "supply")
public class Supply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSupply;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double stock;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double stockMinimo;

    @Column(nullable = false, length = 30)
    private String unitType;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double pricePerUnit;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplierId", nullable = false)
    private Supplier supplier;

    @Transient
    private String supplierName;

    @Transient
    private String estado;

    private static final DateTimeFormatter FORMAT_DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_YYYY_MM_DD = DateTimeFormatter.ISO_LOCAL_DATE;

    public Supply() {
        this.stock = 0.0;
        this.stockMinimo = 0.0;
        this.pricePerUnit = 0.0;
    }

    public Supply(String name, String category, double stock, double stockMinimo, String unitType, double pricePerUnit, LocalDate expirationDate, Supplier supplier) {
        this.name = name;
        this.category = category;
        setStock(stock);
        setStockMinimo(stockMinimo);
        this.unitType = unitType;
        setPricePerUnit(pricePerUnit);
        this.expirationDate = expirationDate;
        this.supplier = supplier;
    }

    public int getIdSupply() { return idSupply; }
    public void setIdSupply(int idSupply) { this.idSupply = idSupply; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getStock() { return stock; }
    public void setStock(double stock) {
        this.stock = Math.max(0.0, stock);
    }

    public double getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(double stockMinimo) {
        this.stockMinimo = Math.max(0.0, stockMinimo);
    }

    public String getUnitType() { return unitType; }
    public void setUnitType(String unitType) { this.unitType = unitType; }

    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = Math.max(0.01, pricePerUnit);
    }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public String getFormattedExpirationDate() {
        if (expirationDate == null) return "-";
        return expirationDate.format(FORMAT_DD_MM_YYYY);
    }

    public String getExpirationDateForInput() {
        if (expirationDate == null) return "";
        return expirationDate.format(FORMAT_YYYY_MM_DD);
    }

    public String getSupplierName() {
        return (this.supplier != null) ? this.supplier.getCompanyName() : "N/A";
    }

    public String getEstado() {
        // Calculation remains the same logic, using double comparison
        if (this.stock <= 0) return "Agotado";
        if (this.stock <= this.stockMinimo) return "Bajo";
        return "Óptimo";
    }

    @Transient
    public Integer getSupplierId() {
        return (this.supplier != null) ? this.supplier.getSupplierIdentification() : null;
    }

    @Override
    public String toString() {
        return "Supply{" +
                "idSupply=" + idSupply +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", stock=" + stock + // double prints directly
                ", stockMinimo=" + stockMinimo +
                ", unitType='" + unitType + '\'' +
                ", pricePerUnit=" + pricePerUnit +
                ", expirationDate=" + expirationDate +
                ", supplierId=" + getSupplierId() +
                ", supplierName='" + getSupplierName() + '\'' +
                ", estado='" + getEstado() + '\'' +
                '}';
    }
}