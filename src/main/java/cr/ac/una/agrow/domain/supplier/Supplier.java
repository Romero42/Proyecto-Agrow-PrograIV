package cr.ac.una.agrow.domain.supplier;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Supplier {
    private int supplierIdentification;
    private String supplierName;
    private String companyName;
    private int phoneNumber;
    private String email;
    private LocalDate registrationDate;
    private boolean isActive;
    private double creditLimit;

    private static final DateTimeFormatter FORMAT_DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_YYYY_MM_DD = DateTimeFormatter.ISO_LOCAL_DATE;

    public Supplier() {
    }

    public Supplier(int supplierIdentification, String supplierName, String companyName, int phoneNumber,
                    String email, LocalDate registrationDate, boolean isActive, double creditLimit) {
        this.supplierIdentification = supplierIdentification;
        this.supplierName = supplierName;
        this.companyName = companyName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.registrationDate = registrationDate;
        this.isActive = isActive;
        this.creditLimit = creditLimit;
    }

    public int getSupplierIdentification() {
        return supplierIdentification;
    }

    public void setSupplierIdentification(int supplierIdentification) {
        this.supplierIdentification = supplierIdentification;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getFormattedPhoneNumber() {
        String phoneStr = String.valueOf(this.phoneNumber);
        if (phoneStr.length() == 8) {
            return phoneStr.substring(0, 4) + "-" + phoneStr.substring(4);
        }
        // Devuelve como está si no tiene 8 dígitos (aunque la validación debería prevenir esto)
        return phoneStr;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

     public String getFormattedRegistrationDate() {
          if (registrationDate == null) return "-";
          return registrationDate.format(FORMAT_DD_MM_YYYY);
     }

     public String getRegistrationDateForInput() {
        if (registrationDate == null) return "";
        return registrationDate.format(FORMAT_YYYY_MM_DD);
     }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    // Thymeleaf usa isIsActive()
    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
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
               ", creditLimit=" + creditLimit +
               '}';
    }
}
