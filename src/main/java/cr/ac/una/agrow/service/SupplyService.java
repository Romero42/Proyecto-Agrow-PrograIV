
package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.User;
import cr.ac.una.agrow.domain.supplier.Supplier;
import cr.ac.una.agrow.domain.Supply;
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
    public Page<Supply> getFilteredSuppliesPaginated(String searchTerm, String category, Integer supplierId, User loggedInUser, Pageable pageable) {
        try {
            Integer currentUserId = (loggedInUser != null) ? loggedInUser.getId_User() : null;
            Page<Supply> supplyPage = supplyRepository.findFilteredSuppliesWithOwnerPriority(searchTerm, category, supplierId, currentUserId, pageable);

            if (loggedInUser != null) {
                supplyPage.getContent().forEach(supply -> {
                    if (supply.getCreatedByUser() != null && supply.getCreatedByUser().getId_User() == loggedInUser.getId_User()) {
                        supply.setOwner(true);
                    }
                });
            }
            return supplyPage;
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error accessing data during filtered supply pagination", e);
            return Page.empty(pageable);
        }
    }

    @Transactional
    public boolean saveSupply(Supply supply, User loggedInUser) {
        try {
            if (supply.getIdSupply() == 0 && loggedInUser != null) {
                supply.setCreatedByUser(loggedInUser);
            }

            supplyRepository.save(supply);
            return true;
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error saving supply: " + (supply != null ? supply.getName() : "null"), e);
            return false;
        }
    }

    @Transactional
    public boolean updateSupply(Supply supply, User loggedInUser) {
        Optional<Supply> existingSupplyOpt = supplyRepository.findById(supply.getIdSupply());
        if (existingSupplyOpt.isEmpty()) {
            LOG.warning("Attempt to update non-existent supply ID: " + supply.getIdSupply());
            return false; // O lanzar excepción
        }
        Supply existingSupply = existingSupplyOpt.get();

        // Verificar propiedad si no es admin
        if (!"Administrador".equals(loggedInUser.getType())) {
            if (existingSupply.getCreatedByUser() == null || existingSupply.getCreatedByUser().getId_User() != loggedInUser.getId_User()) {
                LOG.warning("User " + loggedInUser.getUser() + " attempted to update supply " + supply.getIdSupply() + " without ownership.");

                return false;
            }
        }
        // Conservar el creador original
        supply.setCreatedByUser(existingSupply.getCreatedByUser());
        return saveSupply(supply, loggedInUser);
    }


    @Transactional
    public boolean deleteSupplyById(int idSupply, User loggedInUser) {
        Optional<Supply> supplyOpt = supplyRepository.findById(idSupply);
        if (supplyOpt.isEmpty()) {
            LOG.log(Level.WARNING, "Attempted to delete non-existent supply ID: " + idSupply);
            return false;
        }
        Supply supplyToDelete = supplyOpt.get();

        // Verificar propiedad si no es admin
        if (!"Administrador".equals(loggedInUser.getType())) {
            if (supplyToDelete.getCreatedByUser() == null || supplyToDelete.getCreatedByUser().getId_User() != loggedInUser.getId_User()) {
                LOG.warning("User " + loggedInUser.getUser() + " attempted to delete supply " + idSupply + " without ownership.");

                return false;
            }
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
        return supplierRepository.findAllByOrderByCompanyNameAsc();
    }

    @Transactional(readOnly = true)
    public Optional<Supplier> findSupplierById(Integer supplierId) {
        if (supplierId == null) return Optional.empty();
        return supplierRepository.findById(supplierId);
    }
}
