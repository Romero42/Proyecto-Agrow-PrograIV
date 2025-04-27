package cr.ac.una.agrow.data.supplier;

import cr.ac.una.agrow.domain.supplier.Supplier;
import cr.ac.una.agrow.service.supplier.SupplierService;
import java.util.List;

public class DataSupplier {

    private static final SupplierService supplierService = new SupplierService();

    public static List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    public static Supplier getSupplierById(int id) {
        return supplierService.getSupplierById(id);
    }

    public static String saveSupplier(Supplier supplier) {
        return supplierService.saveSupplierInternal(supplier);
    }

    public static String updateSupplier(Supplier supplier) {
        return supplierService.updateSupplierInternal(supplier);
    }

    public static String deleteSupplier(int id) {
        return supplierService.deleteSupplierInternal(id);
    }

    public static List<Supplier> findSuppliers(String searchTerm) {
        return supplierService.findSuppliers(searchTerm);
    }
}
