package cr.ac.una.agrow.service.supply;

import cr.ac.una.agrow.config.DBConnection;
import cr.ac.una.agrow.domain.supply.Supply;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SupplyService {

    private static final Logger LOG = Logger.getLogger(SupplyService.class.getName());

    public Supply getSupplyById(int idSupply) {
        String storedProc = "{CALL sp_GetSupplyById(?)}";
        Supply supply = null;
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = (conn != null) ? conn.prepareCall(storedProc) : null) {
            if (stmt == null) { LOG.log(Level.SEVERE, "No se pudo conectar/preparar en getSupplyById."); return null; }
            stmt.setInt(1, idSupply);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    supply = mapResultSetToSupply(rs);
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL en getSupplyById ID: " + idSupply, e);
        }
        return supply;
    }

    public List<Supply> getAllSupplies() {
        return getFilteredSupplies(null, null, null);
    }

    public List<Supply> getFilteredSupplies(String searchTerm, String category, Integer supplierId) {
        String storedProc = "{CALL sp_GetFilteredSupplies(?, ?, ?)}";
        List<Supply> supplies = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = (conn != null) ? conn.prepareCall(storedProc) : null) {
            if (stmt == null) { LOG.log(Level.SEVERE, "No se pudo conectar/preparar en getFilteredSupplies."); return Collections.emptyList(); }

            if (searchTerm != null && !searchTerm.trim().isEmpty()) { stmt.setString(1, "%" + searchTerm.trim() + "%"); }
            else { stmt.setNull(1, Types.VARCHAR); }

            if (category != null && !category.trim().isEmpty()) { stmt.setString(2, category.trim()); }
            else { stmt.setNull(2, Types.VARCHAR); }

            if (supplierId != null && supplierId > 0) { stmt.setInt(3, supplierId); }
            else { stmt.setNull(3, Types.INTEGER); }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    supplies.add(mapResultSetToSupply(rs));
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL en getFilteredSupplies", e);
            return Collections.emptyList();
        }
        return supplies;
    }

    public String saveSupplyInternal(Supply supply) {
        String storedProc = "{CALL sp_InsertSupply(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = (conn != null) ? conn.prepareCall(storedProc) : null) {
            if (stmt == null) { return "2,Error interno: No se pudo conectar a la BD."; }
            setSupplyStatementParams(stmt, supply);
            boolean hasResultSet = stmt.execute();
            int generatedId = -1;
            if (hasResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs != null && rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }
            if (generatedId > 0) {
                supply.setIdSupply(generatedId);
                return "1,Suministro '" + supply.getName() + "' guardado exitosamente.";
            } else {
                int updateCount = -1;
                try { updateCount = stmt.getUpdateCount(); } catch (SQLException ignored) {}
                if (updateCount > 0) {
                    LOG.log(Level.WARNING, "SP sp_InsertSupply afectó {0} filas pero no devolvió ID.", updateCount);
                    return "1,Suministro '" + supply.getName() + "' guardado (ID no confirmado).";
                } else {
                    LOG.log(Level.WARNING, "SP sp_InsertSupply no devolvió ID ni afectó filas.");
                    return "2,Error al guardar el suministro: No se confirmó la inserción.";
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL en saveSupplyInternal: " + supply.getName(), e);
            if (e.getSQLState().startsWith("23")) {
                return "2,Error de base de datos: Verifique que el proveedor exista y los datos sean válidos.";
            }
            return "2,Error de base de datos al guardar: " + e.getMessage();
        }
    }

    public String updateSupplyInternal(Supply supply) {
        String storedProc = "{CALL sp_UpdateSupply(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = (conn != null) ? conn.prepareCall(storedProc) : null) {
            if (stmt == null) { return "2,Error interno: No se pudo conectar a la BD."; }
            setSupplyStatementParamsForUpdate(stmt, supply);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return "1,Suministro '" + supply.getName() + "' actualizado exitosamente.";
            } else {
                if (getSupplyById(supply.getIdSupply()) == null) {
                    return "2,Error al actualizar: Suministro con ID " + supply.getIdSupply() + " no encontrado.";
                } else {
                    return "2,Error al actualizar: No se detectaron cambios o el suministro no existe.";
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL en updateSupplyInternal ID: " + supply.getIdSupply(), e);
            if (e.getSQLState().startsWith("23")) {
                return "2,Error de base de datos: Verifique que el proveedor exista.";
            }
            return "2,Error de base de datos al actualizar: " + e.getMessage();
        }
    }

    public String deleteSupplyInternal(int idSupply) {
        Supply supply = getSupplyById(idSupply);
        if (supply == null) {
            return "2,Error al eliminar: Suministro con ID " + idSupply + " no encontrado.";
        }
        String storedProc = "{CALL sp_DeleteSupply(?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = (conn != null) ? conn.prepareCall(storedProc) : null) {
            if (stmt == null) { return "2,Error interno: No se pudo conectar a la BD."; }
            stmt.setInt(1, idSupply);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return "1,Suministro '" + supply.getName() + "' (ID: " + idSupply + ") eliminado exitosamente.";
            } else {
                return "2,Error al eliminar. Suministro ID " + idSupply + " no pudo ser eliminado.";
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL en deleteSupplyInternal ID: " + idSupply, e);
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                return "2,Error al eliminar: El suministro '" + supply.getName() + "' está en uso.";
            }
            return "2,Error de base de datos al eliminar: " + e.getMessage();
        }
    }

    private Supply mapResultSetToSupply(ResultSet rs) throws SQLException {
        return new Supply(
                rs.getInt("idSupply"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("stock"),
                rs.getDouble("stockMinimo"),
                rs.getString("unitType"),
                rs.getDouble("pricePerUnit"),
                rs.getDate("expirationDate") != null ? rs.getDate("expirationDate").toLocalDate() : null,
                rs.getObject("supplierId", Integer.class),
                rs.getString("supplierName"),
                rs.getString("estado")
        );
    }

    private void setSupplyStatementParams(CallableStatement stmt, Supply s) throws SQLException {
        stmt.setString(1, s.getName());
        stmt.setString(2, s.getCategory());
        stmt.setDouble(3, s.getStock());
        stmt.setDouble(4, s.getStockMinimo());
        stmt.setString(5, s.getUnitType());
        stmt.setDouble(6, s.getPricePerUnit());
        stmt.setDate(7, s.getExpirationDate() != null ? Date.valueOf(s.getExpirationDate()) : null);
        if (s.getSupplierId() != null && s.getSupplierId() > 0) {
            stmt.setInt(8, s.getSupplierId());
        } else {
            stmt.setNull(8, Types.INTEGER);
        }
    }

    private void setSupplyStatementParamsForUpdate(CallableStatement stmt, Supply s) throws SQLException {
        stmt.setInt(1, s.getIdSupply());
        stmt.setString(2, s.getName());
        stmt.setString(3, s.getCategory());
        stmt.setDouble(4, s.getStock());
        stmt.setDouble(5, s.getStockMinimo());
        stmt.setString(6, s.getUnitType());
        stmt.setDouble(7, s.getPricePerUnit());
        stmt.setDate(8, s.getExpirationDate() != null ? Date.valueOf(s.getExpirationDate()) : null);
        if (s.getSupplierId() != null && s.getSupplierId() > 0) {
            stmt.setInt(9, s.getSupplierId());
        } else {
            stmt.setNull(9, Types.INTEGER);
        }
    }
}
