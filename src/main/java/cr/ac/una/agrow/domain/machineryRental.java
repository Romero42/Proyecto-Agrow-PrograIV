package cr.ac.una.agrow.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
@Entity
@Table(name="machinaryrental")
public class machineryRental {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id_machinaryrental;

    private String renterName;
    private String address;
    private String contactNumber;
    private LocalDate rentStartDay;
    private LocalDate rentFinalDay;
    private int id_maquina;

    public machineryRental() {}

    public machineryRental(int id_machinaryrental, String renterName, String address, String contactNumber, LocalDate rentStartDay, LocalDate rentFinalDay, int id_maquina) {
        this.id_machinaryrental = id_machinaryrental;
        this.renterName = renterName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.rentStartDay = rentStartDay;
        this.rentFinalDay = rentFinalDay;
        this.id_maquina = id_maquina;
    }

    public machineryRental(String renterName, String address, String contactNumber, LocalDate rentStartDay, LocalDate rentFinalDay, int id_maquina) {
        this.renterName = renterName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.rentStartDay = rentStartDay;
        this.rentFinalDay = rentFinalDay;
        this.id_maquina = id_maquina;
    }

    public int getId_machinaryrental() {
        return id_machinaryrental;
    }

    public void setId_machinaryrental(int id_machinaryrental) {
        this.id_machinaryrental = id_machinaryrental;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDate getRentStartDay() {
        return rentStartDay;
    }

    public void setRentStartDay(LocalDate rentStartDay) {
        this.rentStartDay = rentStartDay;
    }

    public LocalDate getRentFinalDay() {
        return rentFinalDay;
    }

    public void setRentFinalDay(LocalDate rentFinalDay) {
        this.rentFinalDay = rentFinalDay;
    }

    public int getId_maquina() {
        return id_maquina;
    }

    public void setId_maquina(int id_maquina) {
        this.id_maquina = id_maquina;
    }

    @Override
    public String toString() {
        return "machineryRental{" +
                "id_machinaryrental=" + id_machinaryrental +
                ", renterName='" + renterName + '\'' +
                ", address='" + address + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", rentStartDay=" + rentStartDay +
                ", rentFinalDay=" + rentFinalDay +
                ", id_maquina=" + id_maquina +
                '}';
    }
}
