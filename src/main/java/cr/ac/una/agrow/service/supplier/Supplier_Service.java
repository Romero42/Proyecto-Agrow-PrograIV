package cr.ac.una.agrow.service.supplier;

import cr.ac.una.agrow.domain.supplier.Supplier;
import cr.ac.una.agrow.jpa.SupplierRepository;
import cr.ac.una.agrow.service.CRUD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class Supplier_Service implements CRUD<Supplier> {
    @Autowired
    private SupplierRepository repoSupplier;
    private static final DateTimeFormatter DATE_FORMAT_INPUT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public boolean save(Supplier supplier) {
        try{
            repoSupplier.save(supplier);
            return true;
        }catch(DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void delete(Supplier supplier) {
        repoSupplier.deleteById(supplier.getSupplierIdentification());
    }

    @Override
    public List<Supplier> getAll() {
        return repoSupplier.findAll();
    }

    @Override
    public Supplier getById(int i) {
        return repoSupplier.getReferenceById(i);
    }
    public List<Supplier> search(String searchTerm){
        if(searchTerm == null || searchTerm.isEmpty()){
            return getAll();
        }
        // Usando el nuevo metodo de repositorio para filtrar por término de búsqueda
        return repoSupplier.findBySupplierNameOrCompanyNameContainingIgnoreCase(searchTerm);
    }

    // Métodos para paginación
    public Page<Supplier> getAllPaginated(int page, int size) {
        return repoSupplier.findAll(PageRequest.of(page, size));
    }

    public Page<Supplier> searchPagination(String searchTerm, int page, int size){
        if(searchTerm == null || searchTerm.isEmpty()){
            return getAllPaginated(page, size);
        }
        return repoSupplier.findByNameOrTypeContainingIgnoreCase(searchTerm, PageRequest.of(page, size));
    }

    public static String validateSupplierData(String supplierIdStr, String supplierName, String companyName, String phoneNumberStr, String email, String registrationDateStr, String creditLimitStr) {
        if (supplierIdStr == null || supplierIdStr.trim().isEmpty()) return "La identificación es obligatoria.";
        try {
            int id = Integer.parseInt(supplierIdStr);
            if (id <= 0) return "La identificación debe ser un número positivo.";
            if (supplierIdStr.length() < 5 || supplierIdStr.length() > 15) return "La identificación debe tener entre 5 y 15 dígitos.";
        } catch (NumberFormatException e) {
            return "La identificación debe ser un número válido.";
        }

        if (supplierName == null || supplierName.trim().isEmpty()) return "El nombre del contacto es obligatorio.";
        if (!supplierName.matches("[A-Za-záéíóúÁÉÍÓÚñÑ.\\s]+")) return "El nombre del contacto solo debe contener letras, puntos y espacios.";

        if (companyName == null || companyName.trim().isEmpty()) return "El nombre de la empresa es obligatorio.";

        if (phoneNumberStr == null || phoneNumberStr.trim().isEmpty()) return "El número de teléfono es obligatorio.";
        if (!phoneNumberStr.matches("[246789]\\d{7}")) return "El número de teléfono debe ser válido en Costa Rica.";

        if (email == null || email.trim().isEmpty()) return "El correo electrónico es obligatorio.";
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) return "El formato del correo electrónico no es válido.";

        if (registrationDateStr == null || registrationDateStr.trim().isEmpty()) return "La fecha de registro es obligatoria.";
        try {
            LocalDate regDate = LocalDate.parse(registrationDateStr, DATE_FORMAT_INPUT);
            if (regDate.isAfter(LocalDate.now())) return "La fecha de registro no puede ser futura.";
        } catch (DateTimeParseException e) {
            return "Formato de fecha de registro inválido.";
        }

        if (creditLimitStr == null || creditLimitStr.trim().isEmpty()) return "El límite de crédito es obligatorio.";
        try {
            double creditLimit = Double.parseDouble(creditLimitStr);
            if (creditLimit < 0) return "El límite de crédito no puede ser negativo.";
        } catch (NumberFormatException e) {
            return "El límite de crédito debe ser un número válido.";
        }

        return null;
    }

}
