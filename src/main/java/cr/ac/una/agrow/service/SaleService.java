
package cr.ac.una.agrow.service;

import cr.ac.una.agrow.domain.User;
import cr.ac.una.agrow.domain.harvest.Harvest;
import cr.ac.una.agrow.domain.Sale;
import cr.ac.una.agrow.jpa.HarvestRepository;
import cr.ac.una.agrow.jpa.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SaleService {

    private static final Logger LOG = Logger.getLogger(SaleService.class.getName());

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private HarvestRepository harvestRepository;

    @Transactional
    public boolean processSale(Sale sale, int quantityToSell, User loggedInUser) throws IllegalArgumentException { // Added loggedInUser
        if (sale == null || sale.getHarvest() == null || sale.getHarvest().getIdHarvest() <= 0) {
            LOG.warning("Intento de procesar venta con datos inválidos (sale o harvest nulo/inválido).");
            throw new IllegalArgumentException("Datos de venta inválidos.");
        }
        if (quantityToSell <= 0) {
            throw new IllegalArgumentException("La cantidad a vender debe ser mayor que cero.");
        }

        try {
            Optional<Harvest> harvestOpt = harvestRepository.findById(sale.getHarvest().getIdHarvest());
            if (harvestOpt.isEmpty()) {
                throw new IllegalArgumentException("La cosecha seleccionada (ID: " + sale.getHarvest().getIdHarvest() + ") no existe.");
            }
            Harvest harvest = harvestOpt.get();

            if (harvest.getAvailableQuantity() < quantityToSell) {
                throw new IllegalArgumentException("No hay suficiente stock disponible. Disponible: " + harvest.getAvailableQuantity() + ", Solicitado: " + quantityToSell);
            }

            harvest.setAvailableQuantity(harvest.getAvailableQuantity() - quantityToSell);
            harvestRepository.save(harvest);

            sale.setHarvest(harvest);
            sale.setQuantitySold(quantityToSell);
            sale.setTotalSaleAmount(sale.getPricePerUnitSold() * quantityToSell);
            if (loggedInUser != null) {
                sale.setCreatedByUser(loggedInUser);
            }
            saleRepository.save(sale);

            LOG.info("Venta procesada exitosamente para cosecha ID: " + harvest.getIdHarvest() + ", Cantidad: " + quantityToSell);
            return true;

        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error de acceso a datos al procesar la venta para cosecha ID: " + (sale.getHarvest() != null ? sale.getHarvest().getIdHarvest() : "N/A"), e);
            throw new RuntimeException("Error de base de datos al procesar la venta.", e);
        } catch (IllegalArgumentException e) {
            LOG.log(Level.WARNING, "Error de validación al procesar venta: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public boolean updateSale(Sale updatedSaleData, int newQuantitySold, User loggedInUser) throws IllegalArgumentException, RuntimeException {
        if (updatedSaleData == null || updatedSaleData.getIdSale() <= 0) {
            throw new IllegalArgumentException("ID de venta inválido para actualizar.");
        }

        Optional<Sale> originalSaleOpt = saleRepository.findById(updatedSaleData.getIdSale());
        if (originalSaleOpt.isEmpty()) {
            throw new IllegalArgumentException("Venta con ID " + updatedSaleData.getIdSale() + " no encontrada para actualizar.");
        }
        Sale originalSale = originalSaleOpt.get();

        // Verificar propiedad si no es admin
        if (!"Administrador".equals(loggedInUser.getType())) {
            if (originalSale.getCreatedByUser() == null || originalSale.getCreatedByUser().getId_User() != loggedInUser.getId_User()) {
                LOG.warning("User " + loggedInUser.getUser() + " attempted to update sale " + originalSale.getIdSale() + " without ownership.");
                throw new SecurityException("No tiene permisos para actualizar esta venta.");
            }
        }

        if (newQuantitySold <= 0) {
            throw new IllegalArgumentException("La nueva cantidad vendida debe ser mayor que cero.");
        }
        if (updatedSaleData.getPricePerUnitSold() <= 0) {
            throw new IllegalArgumentException("El precio por unidad debe ser mayor a cero.");
        }
        if (updatedSaleData.getBuyerName() == null || updatedSaleData.getBuyerName().trim().isEmpty() ||
                updatedSaleData.getBuyerPhone() == null || updatedSaleData.getBuyerPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre y teléfono del comprador son obligatorios.");
        }

        try {
            Harvest harvest = harvestRepository.findById(originalSale.getHarvest().getIdHarvest())
                    .orElseThrow(() -> new RuntimeException("Cosecha asociada (ID: " + originalSale.getHarvest().getIdHarvest() + ") no encontrada."));

            int quantityDifference = newQuantitySold - originalSale.getQuantitySold();

            if (quantityDifference > 0 && harvest.getAvailableQuantity() < quantityDifference) {
                throw new IllegalArgumentException("Stock insuficiente para la nueva cantidad. Adicional requerido: " + quantityDifference + ", Disponible: " + harvest.getAvailableQuantity());
            } else if (quantityDifference < 0 && (-quantityDifference > originalSale.getQuantitySold())) {

            }


            harvest.setAvailableQuantity(harvest.getAvailableQuantity() - quantityDifference);
            harvestRepository.save(harvest);

            originalSale.setBuyerName(updatedSaleData.getBuyerName().trim());
            originalSale.setBuyerPhone(updatedSaleData.getBuyerPhone().trim());
            originalSale.setBuyerAddress(updatedSaleData.getBuyerAddress() != null ? updatedSaleData.getBuyerAddress().trim() : null);
            originalSale.setTransportOption(updatedSaleData.getTransportOption() != null ? updatedSaleData.getTransportOption().trim() : null);
            originalSale.setPricePerUnitSold(updatedSaleData.getPricePerUnitSold());
            originalSale.setQuantitySold(newQuantitySold);
            originalSale.setSaleDate(updatedSaleData.getSaleDate());
            originalSale.setTotalSaleAmount(newQuantitySold * updatedSaleData.getPricePerUnitSold());
            // El createdByUser no se modifica en una actualización

            saleRepository.save(originalSale);

            LOG.info("Venta ID: " + originalSale.getIdSale() + " actualizada exitosamente. Diferencia Cantidad: " + quantityDifference);
            return true;

        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error de acceso a datos al actualizar venta ID: " + updatedSaleData.getIdSale(), e);
            throw new RuntimeException("Error de base de datos al actualizar la venta.", e);
        }
    }

    @Transactional
    public boolean deleteSale(int idSale, User loggedInUser) throws RuntimeException {
        Optional<Sale> saleOpt = saleRepository.findById(idSale);
        if (saleOpt.isEmpty()) {
            LOG.warning("Intento de eliminar venta no existente ID: " + idSale);
            return false;
        }
        Sale saleToDelete = saleOpt.get();

        // Verificar propiedad si no es admin
        if (!"Administrador".equals(loggedInUser.getType())) {
            if (saleToDelete.getCreatedByUser() == null || saleToDelete.getCreatedByUser().getId_User() != loggedInUser.getId_User()) {
                LOG.warning("User " + loggedInUser.getUser() + " attempted to delete sale " + idSale + " without ownership.");
                throw new SecurityException("No tiene permisos para eliminar esta venta.");
            }
        }

        try {
            Harvest harvest = harvestRepository.findById(saleToDelete.getHarvest().getIdHarvest())
                    .orElseThrow(() -> new RuntimeException("Cosecha asociada (ID: " + saleToDelete.getHarvest().getIdHarvest() + ") no encontrada al eliminar venta."));

            harvest.setAvailableQuantity(harvest.getAvailableQuantity() + saleToDelete.getQuantitySold());
            harvestRepository.save(harvest);

            saleRepository.deleteById(idSale);

            LOG.info("Venta ID: " + idSale + " eliminada exitosamente. Stock restaurado: " + saleToDelete.getQuantitySold());
            return true;

        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error de acceso a datos al eliminar venta ID: " + idSale, e);
            throw new RuntimeException("Error de base de datos al eliminar la venta.", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<Sale> listFilteredSales(LocalDate saleDate, String buyerName, User loggedInUser, Pageable pageable) {
        try {
            String effectiveBuyerName = (buyerName != null && buyerName.trim().isEmpty()) ? null : buyerName;
            Integer currentUserId = (loggedInUser != null) ? loggedInUser.getId_User() : null;
            Page<Sale> salesPage = saleRepository.findFilteredSalesWithOwnerPriority(saleDate, effectiveBuyerName, currentUserId, pageable);

            if (loggedInUser != null) {
                salesPage.getContent().forEach(sale -> {
                    if (sale.getCreatedByUser() != null && sale.getCreatedByUser().getId_User() == loggedInUser.getId_User()) {
                        sale.setOwner(true);
                    }
                });
            }
            return salesPage;
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error al listar ventas filtradas", e);
            return Page.empty(pageable);
        }
    }


    @Transactional(readOnly = true)
    public Optional<Sale> getSaleById(int idSale) {
        try {
            return saleRepository.findById(idSale);
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error al obtener venta por ID: " + idSale, e);
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public List<Sale> getSalesByHarvestId(int harvestId) {
        try {
            return saleRepository.findByHarvestIdHarvest(harvestId);
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error al obtener ventas por ID de cosecha: " + harvestId, e);
            return Collections.emptyList();
        }
    }
}