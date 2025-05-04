package cr.ac.una.agrow.domain.supplier;

import cr.ac.una.agrow.domain.supply.Supply;
import jakarta.persistence.*;
// No longer need BigDecimal
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Entity
@Table(name = "supplier")
public class Supplier {

    @Id
    @Column(name = "supplierIdentification")
    private int supplierIdentification;

    @Column(nullable = false, length = 100)
    private String supplierName;

    @Column(nullable = false, length = 100)
    private String companyName;

    @Column(nullable = false)
    private int phoneNumber;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private LocalDate registrationDate;

    @Column(nullable = false)
    private boolean isActive;

    // CHANGE: Keep double, use columnDefinition
    @Column(nullable = false, columnDefinition = "DECIMAL(12,2)")
    private double creditLimit;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Supply> supplies;

    private static final DateTimeFormatter FORMAT_DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_YYYY_MM_DD = DateTimeFormatter.ISO_LOCAL_DATE;

    public Supplier() {
        this.creditLimit = 0.0; // Default for double
    }

    // Constructor using double
    public Supplier(int supplierIdentification, String supplierName, String companyName, int phoneNumber,
                    String email, LocalDate registrationDate, boolean isActive, double creditLimit) {
        this.supplierIdentification = supplierIdentification;
        this.supplierName = supplierName;
        this.companyName = companyName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.registrationDate = registrationDate;
        this.isActive = isActive;
        setCreditLimit(creditLimit); // Use setter
    }

    // --- Getters and Setters ---
    public int getSupplierIdentification() { return supplierIdentification; }
    public void setSupplierIdentification(int supplierIdentification) { this.supplierIdentification = supplierIdentification; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public int getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(int phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }

    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }

    // CHANGE: Getter and Setter for double
    public double getCreditLimit() { return creditLimit; }
    public void setCreditLimit(double creditLimit) {
        this.creditLimit = Math.max(0.0, creditLimit); // Ensure non-negative
    }

    public Set<Supply> getSupplies() { return supplies; }
    public void setSupplies(Set<Supply> supplies) { this.supplies = supplies; }

    // --- Formatted Getters ---
    public String getFormattedPhoneNumber() {
        String phoneStr = String.valueOf(this.phoneNumber);
        if (phoneStr.length() == 8) {
            return phoneStr.substring(0, 4) + "-" + phoneStr.substring(4);
        }
        return phoneStr;
    }

    public String getFormattedRegistrationDate() {
        if (registrationDate == null) return "-";
        return registrationDate.format(FORMAT_DD_MM_YYYY);
    }

    public String getRegistrationDateForInput() {
        if (registrationDate == null) return "";
        return registrationDate.format(FORMAT_YYYY_MM_DD);
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "supplierIdentification=" + supplierIdentification +
                ", supplierName='" + supplierName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", email='" + email + '\'' +
                ", registrationDate=" + (registrationDate != null ? getFormattedRegistrationDate() : "null") +
                ", isActive=" + isActive +
                ", creditLimit=" + creditLimit + // double prints directly
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return supplierIdentification == supplier.supplierIdentification;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(supplierIdentification);
    }
}