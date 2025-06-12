package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.Transport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransportRepository extends JpaRepository<Transport, Integer> {
    
    // Métodos básicos de consulta
    List<Transport> findByTransportDate(LocalDate transportDate);
    
    List<Transport> findByTransportType(String transportType);
    
    List<Transport> findByDelivered(boolean delivered);
    
    Optional<Transport> getTransportByIdTransport(int idTransport);
    
    Optional<Transport> findByVehiclePlate(String vehiclePlate);
    
    List<Transport> findByTransportTypeAndDelivered(String transportType, boolean delivered);
    
    // Métodos de paginación con ordenamiento por fecha descendente
    @Query("SELECT t FROM Transport t WHERE t.transportType = :transportType ORDER BY t.transportDate DESC, t.idTransport DESC")
    Page<Transport> findByTransportType(@Param("transportType") String transportType, Pageable pageable);
    
    @Query("SELECT t FROM Transport t WHERE t.delivered = :delivered ORDER BY t.transportDate DESC, t.idTransport DESC")
    Page<Transport> findByDelivered(@Param("delivered") boolean delivered, Pageable pageable);
    
    @Query("SELECT t FROM Transport t WHERE t.transportType = :transportType AND t.delivered = :delivered ORDER BY t.transportDate DESC, t.idTransport DESC")
    Page<Transport> findByTransportTypeAndDelivered(@Param("transportType") String transportType, @Param("delivered") boolean delivered, Pageable pageable);
    
    // Sobrescribir findAll para que también tenga ordenamiento
    @Query("SELECT t FROM Transport t ORDER BY t.transportDate DESC, t.idTransport DESC")
    Page<Transport> findAll(Pageable pageable);
}