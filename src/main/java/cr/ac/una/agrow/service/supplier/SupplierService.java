package cr.ac.una.agrow.service.supplier;

import cr.ac.una.agrow.config.DBConnection;
import cr.ac.una.agrow.domain.supplier.Supplier;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SupplierService {

    private static final Logger LOG = Logger.getLogger(SupplierService.class.getName());
    private static final DateTimeFormatter DATE_FORMAT_INPUT = DateTimeFormatter.ISO_LOCAL_DATE;

    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new LinkedList<>();
        String sqlCall = "{call sp_GetAllSuppliers()}";
        try (Connection cn = DBConnection.getConnection();
             CallableStatement stmt = (cn != null) ? cn.prepareCall(sqlCall) : null;
             ResultSet rs = (stmt != null) ? stmt.executeQuery() : null) {

            if (rs == null && cn == null) { LOG.log(Level.SEVERE, "No se pudo obtener conexión a la BD."); return suppliers; }
            if (rs == null) { LOG.log(Level.WARNING, "SP {0} no devolvió resultados.", sqlCall); return suppliers; }

            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL en getAllSuppliers", e);
        }
        return suppliers;
    }

    public Supplier getSupplierById(int id) {
        Supplier supplier = null;
        String sqlCall = "{call sp_GetSupplierById(?)}";
        try (Connection cn = DBConnection.getConnection();
             CallableStatement stmt = (cn != null) ? cn.prepareCall(sqlCall) : null) {

            if (stmt == null) { return null; }
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    supplier = mapResultSetToSupplier(rs);
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL en getSupplierById ID: " + id, e);
        }
        return supplier;
    }

    public List<Supplier> findSuppliers(String searchTerm) {
        List<Supplier> suppliers = new LinkedList<>();
        String sqlCall = "{call sp_FindSuppliers(?)}";
        try (Connection cn = DBConnection.getConnection();
             CallableStatement stmt = (cn != null) ? cn.prepareCall(sqlCall) : null) {

            if (stmt == null) { return suppliers; }
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                stmt.setString(1, "%" + searchTerm.trim() + "%");
            } else {
                stmt.setNull(1, Types.VARCHAR);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(mapResultSetToSupplier(rs));
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL en findSuppliers término: " + searchTerm, e);
        }
        return suppliers;
    }

    public String saveSupplierInternal(Supplier supplier) {
        if (getSupplierById(supplier.getSupplierIdentification()) != null) {
            return "2,Error: Ya existe un proveedor con la identificación: " + supplier.getSupplierIdentification();
        }
        String sqlCall = "{call sp_SaveSupplier(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection cn = DBConnection.getConnection();
             CallableStatement stmt = (cn != null) ? cn.prepareCall(sqlCall) : null) {

            if (stmt == null) { return "2,Error interno: No se pudo conectar a la BD."; }
            setSupplierStatementParams(stmt, supplier);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return "1,Proveedor '" + supplier.getCompanyName() + "' guardado exitosamente.";
            } else {
                return "2,Error al guardar el proveedor, no se afectaron filas.";
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL saveSupplierInternal ID: " + supplier.getSupplierIdentification(), e);
            if (e.getSQLState().equals("23000")) {
                return "2,Error: Identificación duplicada o violación de restricción única.";
            }
            return "2,Error de base de datos al guardar: " + e.getMessage();
        }
    }

    public String updateSupplierInternal(Supplier supplier) {
        String sqlCall = "{call sp_UpdateSupplier(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection cn = DBConnection.getConnection();
             CallableStatement stmt = (cn != null) ? cn.prepareCall(sqlCall) : null) {

            if (stmt == null) { return "2,Error interno: No se pudo conectar a la BD."; }
            setSupplierStatementParams(stmt, supplier);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return "1,Proveedor '" + supplier.getCompanyName() + "' actualizado exitosamente.";
            } else {
                if (getSupplierById(supplier.getSupplierIdentification()) == null) {
                    return "2,Error al actualizar: Proveedor con ID " + supplier.getSupplierIdentification() + " no encontrado.";
                } else {
                    return "2,Error al actualizar. No se detectaron cambios o el proveedor no existe.";
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL updateSupplierInternal ID: " + supplier.getSupplierIdentification(), e);
            return "2,Error de base de datos al actualizar: " + e.getMessage();
        }
    }

    public String deleteSupplierInternal(int id) {
        Supplier supplier = getSupplierById(id);
        if (supplier == null) {
            return "2,Error al eliminar: Proveedor con ID " + id + " no encontrado.";
        }
        String sqlCall = "{call sp_DeleteSupplier(?)}";
        try (Connection cn = DBConnection.getConnection();
             CallableStatement stmt = (cn != null) ? cn.prepareCall(sqlCall) : null) {

            if (stmt == null) { return "2,Error interno: No se pudo conectar a la BD."; }
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return "1,Proveedor '" + supplier.getCompanyName() + "' (ID: " + id + ") eliminado exitosamente.";
            } else {
                return "2,Error al eliminar. Proveedor con ID " + id + " no pudo ser eliminado.";
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL deleteSupplierInternal ID: " + id, e);
            if (e.getSQLState().startsWith("23")) {
                return "2,Error al eliminar: El proveedor tiene registros asociados.";
            }
            return "2,Error de base de datos al eliminar: " + e.getMessage();
        }
    }

    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Date registrationDateSql = rs.getDate("registrationDate");
        LocalDate registrationDate = (registrationDateSql != null) ? registrationDateSql.toLocalDate() : null;
        return new Supplier(
                rs.getInt("supplierIdentification"),
                rs.getString("supplierName"),
                rs.getString("companyName"),
                rs.getInt("phoneNumber"),
                rs.getString("email"),
                registrationDate,
                rs.getBoolean("isActive"),
                rs.getDouble("creditLimit")
        );
    }

    private void setSupplierStatementParams(CallableStatement stmt, Supplier s) throws SQLException {
        stmt.setInt(1, s.getSupplierIdentification());
        stmt.setString(2, s.getSupplierName());
        stmt.setString(3, s.getCompanyName());
        stmt.setInt(4, s.getPhoneNumber());
        stmt.setString(5, s.getEmail());
        stmt.setDate(6, Date.valueOf(s.getRegistrationDate()));
        stmt.setBoolean(7, s.isIsActive());
        stmt.setDouble(8, s.getCreditLimit());
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
