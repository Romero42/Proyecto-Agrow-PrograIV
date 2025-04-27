package cr.ac.una.agrow.domain.producer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Producer {

    private int id_producer;
    private String producerName;
    private String contactNumber;
    private LocalDate registrationDate;
    private String producerType;
    private String email;
    private String city;
    private String address;
    private boolean isActive;

    public Producer() {
    }

    public Producer(int id_producer, String producerName, String contactNumber, LocalDate registrationDate, String producerType, String email, String city, String address, boolean isActive) {
        this.id_producer = id_producer;
        this.producerName = producerName;
        this.contactNumber = contactNumber;
        this.registrationDate = registrationDate;
        this.producerType = producerType;
        this.email = email;
        this.city = city;
        this.address = address;
        this.isActive = isActive;
    }



    public int getId_producer() {
        return id_producer;
    }

    public void setId_producer(int id_producer) {
        this.id_producer = id_producer;
    }

    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

     public String getFormattedRegistrationDate() {
          if (registrationDate == null) {
               return "-";
          }
          return registrationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
     }

     public String getRegistrationDateForInput() {
        if (registrationDate == null) {
            return "";
        }
        return registrationDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
     }


    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getProducerType() {
        return producerType;
    }

    public void setProducerType(String producerType) {
        this.producerType = producerType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isIsActive() {
        return isActive;
    }
    public void setIsActive(boolean active) {
        this.isActive = active;
    }


    @Override
    public String toString() {
        return "Producer{" +
               "id_producer=" + id_producer +
               ", producerName='" + producerName + '\'' +
               ", contactNumber='" + contactNumber + '\'' +
               ", registrationDate=" + (registrationDate != null ? getFormattedRegistrationDate() : "null") +
               ", producerType='" + producerType + '\'' +
               ", email='" + email + '\'' +
               ", city='" + city + '\'' +
               ", address='" + address + '\'' +
               ", isActive=" + isActive +
               '}';
    }
}
