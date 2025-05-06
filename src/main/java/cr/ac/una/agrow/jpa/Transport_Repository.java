package cr.ac.una.agrow.jpa;

import cr.ac.una.agrow.domain.Transport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Transport_Repository extends JpaRepository<Transport, Integer> {

    // Consulta básica por estado
    Page<Transport> findByEstado(Boolean estado, Pageable pageable);
    
    // Consulta básica por destino (case insensitive)
    Page<Transport> findByTransportDestinationContainingIgnoreCase(String destino, Pageable pageable);
    
    // Consulta combinada estado + destino
    Page<Transport> findByEstadoAndTransportDestinationContainingIgnoreCase(
            Boolean estado, String destino, Pageable pageable);

   @Query("SELECT t FROM Transport t WHERE " +
       "(:estado IS NULL OR t.estado = :estado) AND " +
       "(:destino IS NULL OR LOWER(t.transportDestination) LIKE LOWER(CONCAT('%', :destino, '%')))")
Page<Transport> findByFilters(
        @Param("estado") Boolean estado,
        @Param("destino") String destino,
        Pageable pageable);

    // Consultas adicionales
    @Query("SELECT t FROM Transport t WHERE t.clientId = :clientId")
    Page<Transport> findByClientId(@Param("clientId") int clientId, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Transport t WHERE t.estado = true")
    long countEnCamino();
}