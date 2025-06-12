package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.Transport;
import cr.ac.una.agrow.jpa.TransportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TransportService {


    @Autowired
    private TransportRepository transportRepository;

    @Transactional
    public Transport saveTransport(Transport transport) {
        try {
            return transportRepository.save(transport);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al guardar el transporte", e);
        }
    }

    @Transactional
    public Transport updateTransport(Transport transport) {
        try {
            if (!transportRepository.existsById(transport.getIdTransport())) {
                throw new RuntimeException("Transporte no encontrado con ID: " + transport.getIdTransport());
            }
            return transportRepository.save(transport);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al actualizar el transporte", e);
        }
    }

    @Transactional
    public boolean deleteTransport(int idTransport) {
        try {
            if (!transportRepository.existsById(idTransport)) {
                return false;
            }
            transportRepository.deleteById(idTransport);
            return true;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al eliminar el transporte", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Transport> getAllTransports() {
        try {
            return transportRepository.findAll();
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Transport> getTransportById(int idTransport) {
        return transportRepository.getTransportByIdTransport(idTransport);
    }

    @Transactional(readOnly = true)
    public Optional<Transport> findByVehiclePlate(String vehiclePlate) {
        try {
            return transportRepository.findByVehiclePlate(vehiclePlate);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public List<Transport> getByDate(LocalDate date) {
        try {
            return transportRepository.findByTransportDate(date);
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public List<Transport> getByType(String transportType) {
        try {
            return transportRepository.findByTransportType(transportType);
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public List<Transport> getByDelivered(boolean delivered) {
        try {
            return transportRepository.findByDelivered(delivered);
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public List<Transport> getByTypeAndDelivered(String transportType, boolean delivered) {
        try {
            return transportRepository.findByTransportTypeAndDelivered(transportType, delivered);
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    
    @Transactional(readOnly = true)
    public Page<Transport> getAllPaged(Pageable pageable) {
        try {
            return transportRepository.findAll(pageable);
        } catch (DataAccessException ex) {
            return Page.empty(pageable);
        }
    }

    @Transactional(readOnly = true)
    public Page<Transport> getFilteredTransportsPaged(String transportType, Boolean delivered, Pageable pageable) {
        try {
            if (transportType != null && delivered != null) {
                return transportRepository.findByTransportTypeAndDelivered(transportType, delivered, pageable);
            } else if (transportType != null) {
                return transportRepository.findByTransportType(transportType, pageable);
            } else if (delivered != null) {
                return transportRepository.findByDelivered(delivered, pageable);
            } else {
                return transportRepository.findAll(pageable);
            }
        } catch (DataAccessException ex) {
            return Page.empty(pageable);
        }
    }
}