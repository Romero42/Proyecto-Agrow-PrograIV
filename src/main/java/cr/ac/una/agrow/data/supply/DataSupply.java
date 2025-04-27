package cr.ac.una.agrow.data.supply;

import cr.ac.una.agrow.service.supply.SupplyService;
import cr.ac.una.agrow.domain.supply.Supply;
import java.util.List;

public class DataSupply {

    private static final SupplyService supplyService = new SupplyService();

    public static List<Supply> getSupplies() {
        return supplyService.getAllSupplies();
    }

    public static Supply getSupply(int idSupply) {
        return supplyService.getSupplyById(idSupply);
    }

    public static String saveSupply(Supply supply) {
        return supplyService.saveSupplyInternal(supply);
    }

    public static String updateSupply(Supply supply) {
        return supplyService.updateSupplyInternal(supply);
    }

    public static String deleteSupply(int idSupply) {
        return supplyService.deleteSupplyInternal(idSupply);
    }

    public static List<Supply> getFilteredSupplies(String searchTerm, String category, Integer supplierId) {
        return supplyService.getFilteredSupplies(searchTerm, category, supplierId);
    }
}
