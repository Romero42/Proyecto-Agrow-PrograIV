package cr.ac.una.agrow.domain.supply;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Supply {
    private int idSupply;
    private String name;
    private String category;
    private double stock;
    private double stockMinimo;
    private String unitType;
    private double pricePerUnit;
    private LocalDate expirationDate;
    private Integer supplierId;
    private String supplierName;
    private String estado;

    private static final DateTimeFormatter FORMAT_DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_YYYY_MM_DD = DateTimeFormatter.ISO_LOCAL_DATE;

    public Supply() { }

    // Constructor para inserción (supplierId puede ser null inicialmente)
    public Supply(String name, String category, double stock, double stockMinimo,
                  String unitType, double pricePerUnit, LocalDate expirationDate, Integer supplierId) {
        this.name = name;
        this.category = category;
        setStock(stock);
        setStockMinimo(stockMinimo);
        this.unitType = unitType;
        setPricePerUnit(pricePerUnit);
        this.expirationDate = expirationDate;
        this.supplierId = supplierId;
    }

    // Constructor para actualización (supplierId debe tener valor)
    public Supply(int idSupply, String name, String category, double stock, double stockMinimo,
                  String unitType, double pricePerUnit, LocalDate expirationDate, Integer supplierId) {
        this.idSupply = idSupply;
        this.name = name;
        this.category = category;
        setStock(stock);
        setStockMinimo(stockMinimo);
        this.unitType = unitType;
        setPricePerUnit(pricePerUnit);
        this.expirationDate = expirationDate;
        this.supplierId = supplierId;
    }

    // Constructor completo desde BD
    public Supply(int idSupply, String name, String category, double stock, double stockMinimo,
                  String unitType, double pricePerUnit, LocalDate expirationDate, Integer supplierId, String supplierName, String estado) {
        this.idSupply = idSupply;
        this.name = name;
        this.category = category;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.unitType = unitType;
        this.pricePerUnit = pricePerUnit;
        this.expirationDate = expirationDate;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.estado = estado;
    }

    // --- Getters y Setters ---

    public int getIdSupply() { return idSupply; }
    public void setIdSupply(int idSupply) { this.idSupply = idSupply; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getStock() { return stock; }
    public void setStock(double stock) { this.stock = Math.max(0, stock); }

    public double getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(double stockMinimo) { this.stockMinimo = Math.max(0, stockMinimo); }

    public String getUnitType() { return unitType; }
    public void setUnitType(String unitType) { this.unitType = unitType; }

    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = Math.max(0.01, pricePerUnit); }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public String getFormattedExpirationDate() {
        if (expirationDate == null) return "-";
        return expirationDate.format(FORMAT_DD_MM_YYYY);
    }

    public String getExpirationDateForInput() {
        if (expirationDate == null) return "";
        return expirationDate.format(FORMAT_YYYY_MM_DD);
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() { return supplierName != null ? supplierName : "N/A"; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getEstado() {
        if (this.estado == null || this.estado.trim().isEmpty()) {
            if (this.stock <= 0) return "Agotado";
            if (this.stock <= this.stockMinimo) return "Bajo";
            return "Óptimo";
        }
        return estado;
    }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Supply{" +
               "idSupply=" + idSupply +
               ", name='" + name + '\'' +
               ", category='" + category + '\'' +
               ", stock=" + stock +
               ", stockMinimo=" + stockMinimo +
               ", unitType='" + unitType + '\'' +
               ", pricePerUnit=" + pricePerUnit +
               ", expirationDate=" + expirationDate +
               ", supplierId=" + supplierId + 
               ", supplierName='" + supplierName + '\'' +
               ", estado='" + estado + '\'' +
               '}';
    }
}
