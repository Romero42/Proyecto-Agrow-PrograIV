package cr.ac.una.agrow.service.producer;

import cr.ac.una.agrow.config.DBConnection;
import cr.ac.una.agrow.domain.producer.Producer;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProducerService {

    private static final Logger LOG = Logger.getLogger(ProducerService.class.getName());

    public List<Producer> getAllProducers() {
        return executeListProcedure("{call spListProducer()}", null);
    }

    public List<Producer> getProducersByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return new LinkedList<>();
        }
        return executeListProcedure("{call spListCity(?)}", city.trim());
    }

    public Producer getProducerById(int id_producer) {
        Producer p = null;
        String sqlCall = "{call spGetProducer(?)}";
        try (Connection cn = DBConnection.getConnection();
             CallableStatement stmt = (cn != null) ? cn.prepareCall(sqlCall) : null) {
            if (stmt == null) {
                LOG.log(Level.SEVERE, "No se pudo obtener conexión o preparar statement en getProducerById.");
                return null;
            }
            stmt.setInt(1, id_producer);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    p = mapResultSetToProducer(rs);
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL en getProducerById ID: " + id_producer, e);
        }
        return p;
    }

    public String saveProducerInternal(Producer p) {
        int uniqueId = generarCodigoUnico();
        if (uniqueId == -1) {
            return "2,Error interno: No se pudo generar un ID único.";
        }
        p.setId_producer(uniqueId);

        String sqlCall = "{call spAddProducer(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection cn = DBConnection.getConnection();
             CallableStatement stmt = (cn != null) ? cn.prepareCall(sqlCall) : null) {
            if (stmt == null) {
                return "2,Error interno: No se pudo conectar a la BD.";
            }
            setProducerStatementParams(stmt, p);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return "1,El productor '" + p.getProducerName() + "' se ha guardado exitosamente.";
            } else {
                return "2,Error al guardar el productor. No se afectaron filas.";
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL saveProducerInternal ID: " + p.getId_producer(), e);
            if (e.getSQLState().equals("23000")) {
                return "2,Error: Ya existe un productor con la identificación " + p.getId_producer() + " o datos únicos duplicados.";
            }
            return "2,Error de base de datos al guardar: " + e.getMessage();
        }
    }

    public String updateProducerInternal(Producer p) {
        String sqlCall = "{call spUpdateProducer(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection cn = DBConnection.getConnection();
             CallableStatement stmt = (cn != null) ? cn.prepareCall(sqlCall) : null) {
            if (stmt == null) {
                return "2,Error interno: No se pudo conectar a la BD.";
            }
            setProducerStatementParams(stmt, p);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return "1,El productor '" + p.getProducerName() + "' se ha actualizado exitosamente.";
            } else {
                if (getProducerById(p.getId_producer()) == null) {
                    return "2,Error al actualizar: Productor con ID " + p.getId_producer() + " no encontrado.";
                } else {
                    return "2,Error al actualizar. No se detectaron cambios o el productor no existe.";
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL updateProducerInternal ID: " + p.getId_producer(), e);
            return "2,Error de base de datos al actualizar: " + e.getMessage();
        }
    }

    public String deleteProducerInternal(int id_producer) {
        Producer producer = getProducerById(id_producer);
        if (producer == null) {
            return "2,Error al eliminar: Productor con ID " + id_producer + " no encontrado.";
        }
        String sqlCall = "{call spDeleteProducer(?)}";
        try (Connection cn = DBConnection.getConnection();
             CallableStatement stmt = (cn != null) ? cn.prepareCall(sqlCall) : null) {
            if (stmt == null) {
                return "2,Error interno: No se pudo conectar a la BD.";
            }
            stmt.setInt(1, id_producer);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return "1,El productor '" + producer.getProducerName() + "' (ID: " + id_producer + ") se ha eliminado exitosamente.";
            } else {
                return "2,Error al eliminar: El productor con ID " + id_producer + " no pudo ser eliminado.";
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL deleteProducerInternal ID: " + id_producer, e);
            if (e.getSQLState().startsWith("23")) {
                return "2,Error al eliminar: El productor tiene registros asociados.";
            }
            return "2,Error de base de datos al eliminar: " + e.getMessage();
        }
    }

    public List<Producer> executeListProcedure(String sqlCall, String parameter) {
        List<Producer> producers = new LinkedList<>();
        try (Connection cn = DBConnection.getConnection();
             CallableStatement stmt = (cn != null) ? cn.prepareCall(sqlCall) : null) {
            if (stmt == null) {
                LOG.log(Level.SEVERE, "No se pudo obtener conexión o preparar statement en executeListProcedure.");
                return producers;
            }
            if (parameter != null) {
                stmt.setString(1, parameter);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    producers.add(mapResultSetToProducer(rs));
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error SQL ejecutando SP: " + sqlCall, e);
        }
        return producers;
    }

    public Producer mapResultSetToProducer(ResultSet rs) throws SQLException {
        Date registrationDateSql = rs.getDate("registrationDate");
        LocalDate registrationDate = (registrationDateSql != null) ? registrationDateSql.toLocalDate() : null;
        return new Producer(
                rs.getInt("id_producer"),
                rs.getString("producerName"),
                rs.getString("contactNumber"),
                registrationDate,
                rs.getString("producerType"),
                rs.getString("email"),
                rs.getString("city"),
                rs.getString("address"),
                rs.getBoolean("isActive")
        );
    }

    public void setProducerStatementParams(CallableStatement stmt, Producer p) throws SQLException {
        stmt.setInt(1, p.getId_producer());
        stmt.setString(2, p.getProducerName());
        stmt.setString(3, p.getContactNumber());
        if (p.getRegistrationDate() != null) {
            stmt.setDate(4, Date.valueOf(p.getRegistrationDate()));
        } else {
            stmt.setNull(4, Types.DATE);
        }
        stmt.setString(5, p.getProducerType());
        stmt.setString(6, p.getEmail());
        stmt.setString(7, p.getCity());
        stmt.setString(8, p.getAddress());
        stmt.setBoolean(9, p.isActive());
    }

    public static int generarCodigo() {
        return ThreadLocalRandom.current().nextInt(10000, 100000);
    }

    public int generarCodigoUnico() {
        int attempts = 0;
        final int MAX_ATTEMPTS = 10;
        while (attempts < MAX_ATTEMPTS) {
            int codigo = generarCodigo();
            if (getProducerById(codigo) == null) {
                return codigo;
            }
            attempts++;
        }
        LOG.log(Level.SEVERE, "Fallo al generar ID único de productor tras " + MAX_ATTEMPTS + " intentos.");
        return -1;
    }
}
