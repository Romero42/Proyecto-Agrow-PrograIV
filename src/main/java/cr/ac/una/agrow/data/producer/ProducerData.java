package cr.ac.una.agrow.data.producer;

import cr.ac.una.agrow.config.DBConnection; // Importar la conexión UNIFICADA
import cr.ac.una.agrow.domain.producer.Producer;
import cr.ac.una.agrow.service.producer.ProducerService; // Importar Service para generarCodigo
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
// import java.time.LocalDate; // No necesario aquí si se maneja en el Service/Controller
import java.util.LinkedList;

public class ProducerData {

    // Ya no necesita la lista estática aquí si el Service la maneja
    // public static LinkedList<Producer> list = new LinkedList<>();

    // No necesita obtener la conexión estáticamente aquí si cada método la pide
    // public static Connection cn = DBConnection.getConnection();

    // Llena una lista con los datos de la BD dependiendo del filtro solicitado
    public static LinkedList<Producer> getListProducer(String typeList, String type) {
        LinkedList<Producer> producers = new LinkedList<>(); // Crear lista local
        Connection cn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            cn = DBConnection.getConnection(); // Obtener conexión
             if (cn == null) {
                 System.err.println("Error: No se pudo obtener conexión en getListProducer.");
                 return producers; // Devuelve lista vacía si no hay conexión
             }

            if ("allProducer".equals(typeList)) {
                stmt = cn.prepareCall("{call spListProducer()}");
            } else if ("city".equals(typeList)) {
                stmt = cn.prepareCall("{call spListCity(?)}");
                stmt.setString(1, type);
            } else {
                 System.err.println("Error: Tipo de listado no reconocido: " + typeList);
                 return producers; // Devuelve lista vacía
            }


            if(stmt != null) { // Solo ejecutar si el statement se preparó
                 rs = stmt.executeQuery();
                 while (rs.next()) {
                     producers.add(new Producer(
                         rs.getInt("id_producer"),
                         rs.getString("producerName"),
                         rs.getString("contacNumber"),
                         rs.getDate("registrationDate").toLocalDate(),
                         rs.getString("producerType"),
                         rs.getString("email"),
                         rs.getString("city"),
                         rs.getString("address"),
                         rs.getBoolean("isActive")
                     ));
                 }
            }

        } catch (SQLException e) {
            System.err.println("Error SQL en ProducerData.getListProducer: " + e.getMessage());
             e.printStackTrace();
        } finally {
             // Cerrar recursos en orden inverso
             try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
             try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
             // La conexión se cierra automáticamente por el ShutdownHook o manualmente si es necesario
             // DBConnection.closeConnection(); // No cerrar aquí, podría ser usada por otros
        }

        return producers;
    }

    // Se busca un Producer en especifico mediante el id_producer asociado
    public static Producer getProducer(int id_producer) {
        Producer p = null;
        Connection cn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            cn = DBConnection.getConnection(); // Obtener conexión
             if (cn == null) {
                 System.err.println("Error: No se pudo obtener conexión en getProducer.");
                 return null;
             }
            stmt = cn.prepareCall("{call spGetProducer(?)}");
            stmt.setInt(1, id_producer);
            rs = stmt.executeQuery();

            if (rs.next()) {
                p = new Producer(
                    rs.getInt("id_producer"),
                    rs.getString("producerName"),
                    rs.getString("contacNumber"),
                    rs.getDate("registrationDate").toLocalDate(),
                    rs.getString("producerType"),
                    rs.getString("email"),
                    rs.getString("city"),
                    rs.getString("address"),
                    rs.getBoolean("isActive")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error SQL en ProducerData.getProducer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            // DBConnection.closeConnection();
        }
        return p;
    }

    // Guarda un Producer en la BD
    public static boolean saveProducer(Producer p) {
        boolean save = false;
        Connection cn = null;
        CallableStatement stmt = null;

        try {
            cn = DBConnection.getConnection(); // Obtener conexión
             if (cn == null) {
                 System.err.println("Error: No se pudo obtener conexión en saveProducer.");
                 return false;
             }
            stmt = cn.prepareCall("{call spAddProducer(?, ?, ?, ?, ?, ?, ?, ?, ?)}");

            stmt.setInt(1, p.getId_producer()); // El ID ahora lo genera el Service
            stmt.setString(2, p.getProducerName());
            stmt.setString(3, p.getContactNumber());
            stmt.setDate(4, Date.valueOf(p.getRegistrationDate()));
            stmt.setString(5, p.getProducerType());
            stmt.setString(6, p.getEmail());
            stmt.setString(7, p.getCity());
            stmt.setString(8, p.getAddress());
            stmt.setBoolean(9, p.isIsActive()); // Usar el estado del objeto

            stmt.executeUpdate();
            save = true;

        } catch (SQLException e) {
            // Si el id está repetido, el Service debería haber generado uno nuevo.
            // Si aún así falla, es un error diferente o concurrencia.
             System.err.println("Error SQL en ProducerData.saveProducer: " + e.getMessage());
             System.err.println("SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode());
             e.printStackTrace();
            // No reintentar aquí, la lógica de ID debe estar en el Service
            // if (e.getErrorCode() == 1062) { ... }
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            // DBConnection.closeConnection();
        }

        return save;
    }

    // Actualiza un Producer en la base de datos
    public static boolean updateProducer(Producer p) {
        boolean update = false;
        Connection cn = null;
        CallableStatement stmt = null;

        try {
            cn = DBConnection.getConnection(); // Obtener conexión
             if (cn == null) {
                 System.err.println("Error: No se pudo obtener conexión en updateProducer.");
                 return false;
             }
            stmt = cn.prepareCall("{call spUpdateProducer(?, ?, ?, ?, ?, ?, ?, ?, ?)}");

            stmt.setInt(1, p.getId_producer());
            stmt.setString(2, p.getProducerName());
            stmt.setString(3, p.getContactNumber());
            stmt.setDate(4, Date.valueOf(p.getRegistrationDate()));
            stmt.setString(5, p.getProducerType());
            stmt.setString(6, p.getEmail());
            stmt.setString(7, p.getCity());
            stmt.setString(8, p.getAddress());
            stmt.setBoolean(9, p.isIsActive());

            stmt.executeUpdate();
            update = true;
        } catch (SQLException e) {
             System.err.println("Error SQL en ProducerData.updateProducer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            // DBConnection.closeConnection();
        }

        return update;
    }

    // Elimina un Producer en la base de datos mediante el id
    public static boolean deleteProducer(int id_producer) {
        boolean delete = false;
        Connection cn = null;
        CallableStatement stmt = null;

        try {
            cn = DBConnection.getConnection(); // Obtener conexión
             if (cn == null) {
                 System.err.println("Error: No se pudo obtener conexión en deleteProducer.");
                 return false;
             }
            stmt = cn.prepareCall("{call spDeleteProducer(?)}");
            stmt.setInt(1, id_producer);

            stmt.executeUpdate();
            delete = true;
        } catch (SQLException e) {
            System.err.println("Error SQL en ProducerData.deleteProducer: " + e.getMessage());
            // Podría fallar por restricciones de FK si el productor está en uso
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            // DBConnection.closeConnection();
        }

        return delete;
    }
}
