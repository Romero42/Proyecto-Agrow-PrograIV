package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.harvest.Harvest;
import cr.ac.una.agrow.domain.Sale;
import cr.ac.una.agrow.jpa.HarvestRepository;
import cr.ac.una.agrow.jpa.SaleRepository;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para el manejo de operaciones CRUD sobre Cosechas.
 */
@Service
public class HarvestService implements CRUD<Harvest> {

    private static final Logger LOG = Logger.getLogger(HarvestService.class.getName());

    @Autowired
    private HarvestRepository harvestRepository;

    @Autowired(required = false)
    private SaleRepository saleRepository;

    @Override
    @Transactional
    public boolean save(Harvest harvest) {
        try {
            if (harvest.getIdHarvest() <= 0) {
                // Nueva cosecha: disponibilidad = cantidad cosechada
                harvest.setAvailableQuantity(harvest.getQuantityHarvested());
            } else {
                // Edición: no permitir disponible > total ni < 0
                if (harvest.getAvailableQuantity() > harvest.getQuantityHarvested()) {
                    Harvest existing = getById(harvest.getIdHarvest());
                    if (existing != null) {
                        int sold = existing.getQuantityHarvested() - existing.getAvailableQuantity();
                        harvest.setAvailableQuantity(
                                Math.max(sold, harvest.getQuantityHarvested() - sold)
                        );
                    } else {
                        harvest.setAvailableQuantity(harvest.getQuantityHarvested());
                    }
                }
                if (harvest.getAvailableQuantity() < 0) {
                    harvest.setAvailableQuantity(0);
                }
            }
            harvestRepository.save(harvest);
            return true;
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error al guardar cosecha", ex);
            return false;
        }
    }

    @Override
    @Transactional
    public void delete(Harvest harvest) {
        if (harvest == null || harvest.getIdHarvest() <= 0) {
            LOG.warning("Intento de eliminar cosecha inválida.");
            throw new IllegalArgumentException("Cosecha inválida para eliminación.");
        }

        if (saleRepository == null) {
            LOG.warning("SaleRepository no disponible. Omitiendo verificación de ventas.");
        }

        try {
            if (saleRepository != null) {
                List<Sale> ventas = saleRepository.findByHarvestIdHarvest(harvest.getIdHarvest());
                if (!ventas.isEmpty()) {
                    LOG.info("Eliminando " + ventas.size() +
                            " ventas asociadas a la cosecha ID: " + harvest.getIdHarvest());
                    saleRepository.deleteAll(ventas);
                    saleRepository.flush();
                }
            }
            harvestRepository.delete(harvest);
            LOG.info("Cosecha ID: " + harvest.getIdHarvest() + " eliminada exitosamente.");
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE,
                    "Error al eliminar cosecha o sus ventas asociadas", ex);
            throw new RuntimeException(
                    "Error de base de datos al eliminar la cosecha o sus ventas.", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Harvest> getAll() {
        try {
            return harvestRepository.findAll();
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error al obtener todas las cosechas", ex);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Harvest getById(int id) {
        try {
            return harvestRepository.findById(id).orElse(null);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error al obtener cosecha por ID: " + id, ex);
            return null;
        }
    }

    /**
     * Obtiene cosechas filtradas de forma paginada.
     */
    @Transactional(readOnly = true)
    public Page<Harvest> getFilteredHarvestsPaged(String quality, String destiny, Pageable pageable) {
        try {
            String q = (quality != null && quality.isEmpty()) ? null : quality;
            String d = (destiny  != null && destiny.isEmpty())  ? null : destiny;
            return harvestRepository.findFilteredHarvests(q, d, pageable);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error al obtener cosechas filtradas", ex);
            return Page.empty(pageable);
        }
    }

    /**
     * Obtiene todas las cosechas paginadas (sin filtros).
     */
    @Transactional(readOnly = true)
    public Page<Harvest> getAllPaged(Pageable pageable) {
        return getFilteredHarvestsPaged(null, null, pageable);
    }

    /**
     * Cosechas disponibles de un productor específico.
     */
    @Transactional(readOnly = true)
    public List<Harvest> getAvailableHarvestsByProducer(int producerId) {
        try {
            return harvestRepository.findAvailableByProducerId(producerId);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE,
                    "Error al obtener cosechas disponibles del productor ID: " + producerId, ex);
            return Collections.emptyList();
        }
    }

    /**
     * Todas las cosechas de un productor específico.
     */
    @Transactional(readOnly = true)
    public List<Harvest> getAllHarvestsByProducer(int producerId) {
        try {
            return harvestRepository.findAllByProducerId(producerId);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE,
                    "Error al obtener todas las cosechas del productor ID: " + producerId, ex);
            return Collections.emptyList();
        }
    }
}
