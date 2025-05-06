package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.Transport;
import cr.ac.una.agrow.jpa.Transport_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class Transport_Service {

    @Autowired
    private Transport_Repository repo;

    // ============ OPERACIONES CRUD BÁSICAS ============ //
    
    public Transport save(Transport transport) {
        try {
            return repo.save(transport);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al guardar transporte", ex);
        }
    }

    public boolean deleteById(Integer id) {
        if (!repo.existsById(id)) return false;
        
        try {
            repo.deleteById(id);
            return true;
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al eliminar transporte ID: " + id, ex);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Transport> getById(Integer id) {
        return repo.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Transport> getAll() {
        return repo.findAll();
    }

    // ============ MÉTODOS DE PAGINACIÓN Y FILTROS ============ //
    
    @Transactional(readOnly = true)
    public Page<Transport> getAllPaginated(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transport> findByFilters(Boolean estado, String destino, Pageable pageable) {
        return repo.findByFilters(estado, destino, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transport> findByEstado(Boolean estado, Pageable pageable) {
        return repo.findByEstado(estado, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transport> findByDestination(String destino, Pageable pageable) {
        return repo.findByTransportDestinationContainingIgnoreCase(destino, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transport> findByEstadoAndDestination(Boolean estado, String destino, Pageable pageable) {
        return repo.findByEstadoAndTransportDestinationContainingIgnoreCase(estado, destino, pageable);
    }

    // ============ MÉTODOS ADICIONALES ÚTILES ============ //
    
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return repo.existsById(id);
    }

    @Transactional(readOnly = true)
    public long countEnCamino() {
        return repo.countEnCamino();
    }
}