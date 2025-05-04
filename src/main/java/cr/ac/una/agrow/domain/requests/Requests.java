package cr.ac.una.agrow.domain.requests;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name="requests")
public class Requests {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int idrequest;
    private String name;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;
    private String address;
    private String type;
    private double amount;
    private String phone_number;
    private String description;

    private static final DateTimeFormatter FORMAT_DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_YYYY_MM_DD = DateTimeFormatter.ISO_LOCAL_DATE;


    public Requests() {
    }

    public Requests(int idrequest, String name, LocalDate date, String address, String type,
                    double amount, String phone_number, String description) {
        this.idrequest = idrequest;
        this.name = name;
        this.date = date;
        this.address = address;
        this.type = type;
        this.amount = amount;
        this.phone_number = phone_number;
        this.description = description;
    }

    public int getIdrequest() {
        return idrequest;
    }

    public void setIdrequest(int idrequest) {
        this.idrequest = idrequest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getFormattedDate() {
        if (date == null) return "-";
        return date.format(FORMAT_DD_MM_YYYY);
    }

    public String getDateForInput() {
        if (date == null) return "";
        return date.format(FORMAT_YYYY_MM_DD);
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getFormattedPhoneNumber() {
        String phoneStr = String.valueOf(this.phone_number);
        if (phoneStr.length() == 8) {
            return phoneStr.substring(0, 4) + "-" + phoneStr.substring(4);
        }
        return phoneStr;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Requests{" +
                "idrequest=" + idrequest +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", address='" + address + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", phone_number='" + phone_number + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
