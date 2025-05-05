package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.Transport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface Transport_Repository extends JpaRepository<Transport, Integer> {

    // Métodos básicos de paginación
    Page<Transport> findAll(Pageable pageable);

    // Filtros esenciales
    Page<Transport> findByEstado(boolean estado, Pageable pageable);

    Page<Transport> findByTransportDestinationContainingIgnoreCase(String destino, Pageable pageable);

    Page<Transport> findByEstadoAndTransportDestinationContainingIgnoreCase(
            boolean estado, String destino, Pageable pageable);

    // Consultas específicas
    @Query("SELECT t FROM Transport t WHERE t.clientId = :clientId")
    Page<Transport> findByClientId(@Param("clientId") int clientId, Pageable pageable);

    @Query("SELECT t FROM Transport t WHERE t.compraId = :compraId")
    Page<Transport> findByCompraId(@Param("compraId") int compraId, Pageable pageable);

    @Query("SELECT t FROM Transport t WHERE t.clientId = :clientId AND t.estado = :estado")
    Page<Transport> findByClientIdAndEstado(
            @Param("clientId") int clientId,
            @Param("estado") boolean estado,
            Pageable pageable);

    @Query("SELECT t FROM Transport t WHERE t.estado = :estado")
    Page<Transport> findByEstado(@Param("estado") Boolean estado, Pageable pageable);
}
