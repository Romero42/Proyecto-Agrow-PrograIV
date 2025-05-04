package cr.ac.una.agrow.service.supply;

import cr.ac.una.agrow.domain.supplier.Supplier;
import cr.ac.una.agrow.domain.supply.Supply;
import cr.ac.una.agrow.jpa.SupplierRepository;
import cr.ac.una.agrow.jpa.SupplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SupplyService {

    private static final Logger LOG = Logger.getLogger(SupplyService.class.getName());

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public Optional<Supply> getSupplyById(int idSupply) {
        try {

            return supplyRepository.findById(idSupply);
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error accessing data for supply ID: " + idSupply, e);
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public Page<Supply> getAllSuppliesPaginated(Pageable pageable) {
        try {
            return supplyRepository.findAll(pageable);
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error accessing data for all supplies pagination", e);
            return Page.empty(pageable);
        }
    }

    @Transactional(readOnly = true)
    public Page<Supply> getFilteredSuppliesPaginated(String searchTerm, String category, Integer supplierId, Pageable pageable) {
        try {

            return supplyRepository.findFilteredSupplies(searchTerm, category, supplierId, pageable);
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error accessing data during filtered supply pagination", e);
            return Page.empty(pageable);
        }
    }

    @Transactional
    public boolean saveSupply(Supply supply) {
        try {

            supplyRepository.save(supply);
            return true;
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error saving supply: " + (supply != null ? supply.getName() : "null"), e);

            return false;
        }
    }

    @Transactional
    public boolean deleteSupplyById(int idSupply) {
        if (!supplyRepository.existsById(idSupply)) {
            LOG.log(Level.WARNING, "Attempted to delete non-existent supply ID: " + idSupply);
            return false;
        }
        try {
            supplyRepository.deleteById(idSupply);
            return true;
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error deleting supply ID: " + idSupply, e);

            return false;
        }
    }


    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        try {
            return supplierRepository.findAllByOrderByCompanyNameAsc();
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error accessing supplier list", e);
            return List.of();
        }
    }


    @Transactional(readOnly = true)
    public Optional<Supplier> findSupplierById(Integer supplierId) {
        if (supplierId == null) return Optional.empty();
        try {
            return supplierRepository.findById(supplierId);
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error accessing supplier ID: " + supplierId, e);
            return Optional.empty();
        }
    }


}