package cr.ac.una.agrow.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_transport")
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transport")
    private int idTransport;

    @Column(name = "transport_date", nullable = false)
    private LocalDate transportDate;

    @Column(name = "transport_type", nullable = false, length = 50)
    private String transportType;

    @Column(name = "transport_company", nullable = false, length = 100)
    private String transportCompany;

    @Column(name = "driver_name", nullable = false, length = 100)
    private String driverName;

    @Column(name = "driver_phone", length = 15)
    private String driverPhone;

    @Column(name = "delivered", nullable = false)
    private boolean delivered;

    @Column(name = "vehicle_plate", nullable = false, length = 20, unique = true)
    private String vehiclePlate;

    @Column(name = "transport_cost", precision = 10, scale = 2)
    private BigDecimal transportCost;

    // Relación opcional con Sale (nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sale", nullable = true)
    private Sale sale;

    public Transport() {
    }

    public Transport(LocalDate transportDate, String transportType, String transportCompany,
            String driverName, String driverPhone, boolean delivered,
            String vehiclePlate, BigDecimal transportCost) {
        this.transportDate = transportDate;
        this.transportType = transportType;
        this.transportCompany = transportCompany;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.delivered = delivered;
        this.vehiclePlate = vehiclePlate;
        this.transportCost = transportCost;
    }

    // Getters y Setters
    public int getIdTransport() {
        return idTransport;
    }

    public void setIdTransport(int idTransport) {
        this.idTransport = idTransport;
    }

    public LocalDate getTransportDate() {
        return transportDate;
    }

    public void setTransportDate(LocalDate transportDate) {
        this.transportDate = transportDate;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getTransportCompany() {
        return transportCompany;
    }

    public void setTransportCompany(String transportCompany) {
        this.transportCompany = transportCompany;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public BigDecimal getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(BigDecimal transportCost) {
        this.transportCost = transportCost;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    @Override
    public String toString() {
        return "Transport{"
                + "idTransport=" + idTransport
                + ", transportDate=" + transportDate
                + ", transportType='" + transportType + '\''
                + ", transportCompany='" + transportCompany + '\''
                + ", driverName='" + driverName + '\''
                + ", driverPhone='" + driverPhone + '\''
                + ", delivered=" + delivered
                + ", vehiclePlate='" + vehiclePlate + '\''
                + ", transportCost=" + transportCost
                + '}';
    }
}
