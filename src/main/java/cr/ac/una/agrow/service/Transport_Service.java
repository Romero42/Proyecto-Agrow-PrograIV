package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.Transport;
import cr.ac.una.agrow.jpa.Transport_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Transport_Service implements CRUD<Transport> {

    @Autowired
    private Transport_Repository repo;

    // Operaciones CRUD básicas
    @Override
    public boolean save(Transport transport) {
        try {
            repo.save(transport);
            return true;
        } catch (DataAccessException ex) {
            ex.printStackTrace(); // Log del error
            return false;
        }
    }

    @Override
    public void delete(Transport transport) {
        // Implementación vacía como en el original
    }

    public boolean deleteById(int id) {
        if (!repo.existsById(id)) {
            return false;
        }
        try {
            repo.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // Log del error
            return false;
        }
    }

    @Override
    public List<Transport> getAll() {
        return repo.findAll();
    }

    @Override
    public Transport getById(int id) {
        return repo.findById(id).orElse(null);
    }

    public boolean update(Transport transport) {
        try {
            if (repo.existsById(transport.getTransportId())) {
                repo.save(transport);
                return true;
            }
            return false;
        } catch (DataAccessException ex) {
            ex.printStackTrace(); // Log del error
            return false;
        }
    }

    // Métodos de paginación y filtros
    public Page<Transport> getAllPaginated(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Page<Transport> findByEstado(boolean estado, Pageable pageable) {
        return repo.findByEstado(estado, pageable);
    }

    public Page<Transport> findByDestination(String destino, Pageable pageable) {
        return repo.findByTransportDestinationContainingIgnoreCase(destino, pageable);
    }

    // Método combinado para filtros
    public Page<Transport> findWithFilters(Boolean estado, String destino, Pageable pageable) {
        if (estado != null && destino != null && !destino.isEmpty()) {
            return repo.findByEstadoAndTransportDestinationContainingIgnoreCase(estado, destino, pageable);
        } else if (estado != null) {
            return repo.findByEstado(estado, pageable);
        } else if (destino != null && !destino.isEmpty()) {
            return repo.findByTransportDestinationContainingIgnoreCase(destino, pageable);
        }
        return repo.findAll(pageable);
    }
}