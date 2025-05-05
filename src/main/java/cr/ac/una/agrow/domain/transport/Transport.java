package cr.ac.una.agrow.domain.transport;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "transport")
public class Transport {
    
    
    @Id   
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transportId;   
    private int clientId;
    private int compraId;
    private boolean estado;
    private String transportOrigin;
    private String transportDestination;
    private LocalDate transportDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private double transportCost;

    public Transport() {
    }

    public Transport(int transportId, int clientId, int compraId, boolean estado, 
                   String transportOrigin, String transportDestination, 
                   LocalDate transportDate, LocalTime departureTime, 
                   LocalTime arrivalTime, double transportCost) {
        this.transportId = transportId;
        this.clientId = clientId;
        this.compraId = compraId;
        this.estado = estado;
        this.transportOrigin = transportOrigin;
        this.transportDestination = transportDestination;
        this.transportDate = transportDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.transportCost = transportCost;
    }

    // Getters y Setters (igual que antes)
    public int getTransportId() {
        return transportId;
    }

    public void setTransportId(int transportId) {
        this.transportId = transportId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getCompraId() {
        return compraId;
    }

    public void setCompraId(int compraId) {
        this.compraId = compraId;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getTransportOrigin() {
        return transportOrigin;
    }

    public void setTransportOrigin(String transportOrigin) {
        this.transportOrigin = transportOrigin;
    }

    public String getTransportDestination() {
        return transportDestination;
    }

    public void setTransportDestination(String transportDestination) {
        this.transportDestination = transportDestination;
    }

    public LocalDate getTransportDate() {
        return transportDate;
    }

    public void setTransportDate(LocalDate transportDate) {
        this.transportDate = transportDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(double transportCost) {
        this.transportCost = transportCost;
    }

    @Override
    public String toString() {
        return "Transport{" + 
               "transportId=" + transportId + 
               ", clientId=" + clientId + 
               ", compraId=" + compraId + 
               ", estado=" + estado + 
               ", transportOrigin=" + transportOrigin + 
               ", transportDestination=" + transportDestination + 
               ", transportDate=" + transportDate + 
               ", departureTime=" + departureTime + 
               ", arrivalTime=" + arrivalTime + 
               ", transportCost=" + transportCost + '}';
    }
}