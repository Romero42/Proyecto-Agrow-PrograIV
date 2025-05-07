
package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.Transport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransportRepository extends JpaRepository<Transport, Integer> {

    List<Transport> findByTransportDate(LocalDate transportDate);
    
    List<Transport> findByTransportType(String transportType);
    
    List<Transport> findByDelivered(boolean delivered);
    
    Optional<Transport> getTransportByIdTransport(int idTransport);
    
      Optional<Transport> findByVehiclePlate(String vehiclePlate);

    
    
    List<Transport> findByTransportTypeAndDelivered(String transportType, boolean delivered);

    }