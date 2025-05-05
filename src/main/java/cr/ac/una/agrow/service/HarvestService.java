package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.harvest.Harvest;
import cr.ac.una.agrow.jpa.HarvestRepository;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class HarvestService implements CRUD<Harvest> {

    private static final Logger LOG = Logger.getLogger(HarvestService.class.getName());


    @Autowired
    private HarvestRepository harvestRepository;

    @Override
    @Transactional
    public boolean save(Harvest harvest) {
        try {

            if (harvest.getIdHarvest() <= 0) {
                harvest.setAvailableQuantity(harvest.getQuantityHarvested());
            }

            harvestRepository.save(harvest);
            return true;
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error saving harvest", ex);
            return false;
        }
    }

    @Override
    @Transactional
    public void delete(Harvest harvest) {
        try {
            harvestRepository.delete(harvest);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error deleting harvest ID: " + harvest.getIdHarvest(), ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Harvest> getAll() {
        try {
            return harvestRepository.findAll();
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error getting all harvests", ex);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Harvest getById(int id) {
        try {
            return harvestRepository.findById(id).orElse(null);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error getting harvest by ID: " + id, ex);
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Harvest> getHarvestByQuality(String quality) {
        try {
            return harvestRepository.getByQuality(quality);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error getting harvests by quality: " + quality, ex);
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public List<Harvest> getHarvestByDestiny(String destiny) {
        try {
            return harvestRepository.getByDestiny(destiny);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error getting harvests by destiny: " + destiny, ex);
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public Page<Harvest> getAllPaged(Pageable pageable) {
        try {
            return harvestRepository.findAll(pageable);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error getting all harvests paged", ex);
            return Page.empty(pageable);
        }
    }

    @Transactional(readOnly = true)
    public Page<Harvest> getHarvestByQualityPaged(String quality, Pageable pageable) {
        try {
            return harvestRepository.getByQualityPaged(quality, pageable);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error getting harvests by quality paged: " + quality, ex);
            return Page.empty(pageable);
        }
    }

    @Transactional(readOnly = true)
    public Page<Harvest> getHarvestByDestinyPaged(String destiny, Pageable pageable) {
        try {
            return harvestRepository.getByDestinyPaged(destiny, pageable);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error getting harvests by destiny paged: " + destiny, ex);
            return Page.empty(pageable);
        }
    }

    // Método para obtener cosechas disponibles por productor
    @Transactional(readOnly = true)
    public List<Harvest> getAvailableHarvestsByProducer(int producerId) {
        try {
            return harvestRepository.findAvailableByProducerId(producerId);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error getting available harvests for producer ID: " + producerId, ex);
            return Collections.emptyList();
        }
    }

    // Método para obtener TODAS las cosechas por productor
    @Transactional(readOnly = true)
    public List<Harvest> getAllHarvestsByProducer(int producerId) {
        try {
            return harvestRepository.findAllByProducerId(producerId);
        } catch (DataAccessException ex) {
            LOG.log(Level.SEVERE, "Error getting all harvests for producer ID: " + producerId, ex);
            return Collections.emptyList();
        }
    }
}