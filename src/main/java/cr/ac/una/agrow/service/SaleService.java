package cr.ac.una.agrow.service;

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
    public boolean processSale(Sale sale, int quantityToSell) throws IllegalArgumentException {
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
            harvestRepository.save(harvest); // Guardar la cosecha actualizada

            sale.setQuantitySold(quantityToSell);
            sale.setSaleDate(LocalDate.now()); // Asegurar fecha actual

            sale.setTotalSaleAmount(sale.getPricePerUnitSold() * quantityToSell);
            saleRepository.save(sale);

            LOG.info("Venta procesada exitosamente para cosecha ID: " + harvest.getIdHarvest() + ", Cantidad: " + quantityToSell);
            return true;

        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error de acceso a datos al procesar la venta para cosecha ID: " + sale.getHarvest().getIdHarvest(), e);
            // La transacción hará rollback automáticamente aquí si es una RuntimeException (como DataAccessException)
            throw new RuntimeException("Error de base de datos al procesar la venta.", e); // Relanzar para asegurar rollback
        } catch (IllegalArgumentException e) {
            LOG.log(Level.WARNING, "Error de validación al procesar venta: " + e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Page<Sale> listAllSales(Pageable pageable) {
        try {
            return saleRepository.findAll(pageable);
        } catch (DataAccessException e) {
            LOG.log(Level.SEVERE, "Error al listar ventas paginadas", e);
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