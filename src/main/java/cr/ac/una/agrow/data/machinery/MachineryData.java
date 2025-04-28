package cr.ac.una.agrow.data.machinery;

import cr.ac.una.agrow.config.DBConnection;
import cr.ac.una.agrow.domain.machinery.Machinery;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class MachineryData {

    public boolean insertar(Machinery machinery) {
        try (Connection cn = DBConnection.getConnection()) {
            if (cn == null) {
                System.err.println("No se pudo conectar a la base de datos.");
                return false;
            }

            String sqlVerificacion = "SELECT 1 FROM tb_maquina WHERE id = ?";
            try (PreparedStatement verificacion = cn.prepareStatement(sqlVerificacion)) {
                verificacion.setInt(1, machinery.getId());
                ResultSet resultado = verificacion.executeQuery();
                if (resultado.next()) {
                    System.err.println("La maquinaria con ID " + machinery.getId() + " ya existe.");
                    return false;
                }
            }

            String sqlInsert = "INSERT INTO tb_maquina (id, nombre, condicion, disponibilidad, dia_Adquisicion, costo_Alquiler, ubicacion, capacidad_Trabajo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement sentencia = cn.prepareStatement(sqlInsert)) {
                sentencia.setInt(1, machinery.getId());
                sentencia.setString(2, machinery.getNombre());
                sentencia.setString(3, machinery.getCondicion());
                sentencia.setBoolean(4, machinery.getDisponibilidad());
                sentencia.setDate(5, machinery.getDiaAdquisicion() != null ? Date.valueOf(machinery.getDiaAdquisicion()) : null);
                sentencia.setDouble(6, machinery.getCostoAlquiler());
                sentencia.setString(7, machinery.getUbicacion());
                sentencia.setString(8, machinery.getCapacidadTrabajo());
                sentencia.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
public LinkedList<Machinery> obtenerMachinery() {
    LinkedList<Machinery> machineryList = new LinkedList<>();
    try (Connection cn = DBConnection.getConnection()) {
            if (cn == null) {
                System.err.println("No se pudo conectar a la base de datos.");
            }
        String sql = "SELECT m.id, m.nombre, m.condicion, m.disponibilidad, m.dia_Adquisicion, " +
                     "m.costo_Alquiler, m.ubicacion, m.capacidad_Trabajo FROM tb_maquina m";

        PreparedStatement sentencia = cn.prepareStatement(sql);
        ResultSet resultado = sentencia.executeQuery();

        while (resultado.next()) {
            //System.out.println("Máquina encontrada: " + resultado.getString("nombre"));
            Machinery machinery = new Machinery(
                resultado.getInt("id"),
                resultado.getString("nombre"),
                resultado.getString("condicion"),
                resultado.getBoolean("disponibilidad"),
                resultado.getDate("dia_Adquisicion").toLocalDate(),
                resultado.getDouble("costo_Alquiler"),
                resultado.getString("ubicacion"),
                resultado.getString("capacidad_Trabajo")
            );

            machineryList.add(machinery);
        }
    } catch (SQLException e) {
        // System.err.println("Error al obtener la maquinaria: " + e.getMessage());
    }
    return machineryList;
}
    public LinkedList<Machinery> filtrarPorDisponibilidad(boolean disponible) {
    LinkedList<Machinery> machineries = new LinkedList<>();
    String sql = "SELECT * FROM tb_maquina WHERE disponibilidad = ?";
    
    try (Connection cn = DBConnection.getConnection();
         PreparedStatement stmt = cn.prepareStatement(sql)) {
        
        stmt.setBoolean(1, disponible);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            machineries.add(new Machinery(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("condicion"),
                rs.getBoolean("disponibilidad"),
                rs.getDate("dia_Adquisicion").toLocalDate(),
                rs.getDouble("costo_Alquiler"),
                rs.getString("ubicacion"),
                rs.getString("capacidad_Trabajo")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return machineries;
}

public LinkedList<Machinery> obtenerMachineryPorNombre(String nombre) {
    LinkedList<Machinery> machineries = new LinkedList<>();
    String sql = "SELECT * FROM tb_maquina WHERE nombre LIKE ?";
    
    try (Connection cn = DBConnection.getConnection();
         PreparedStatement stmt = cn.prepareStatement(sql)) {
        
        stmt.setString(1, "%" + nombre + "%");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            machineries.add(new Machinery(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("condicion"),
                rs.getBoolean("disponibilidad"),
                rs.getDate("dia_Adquisicion").toLocalDate(),
                rs.getDouble("costo_Alquiler"),
                rs.getString("ubicacion"),
                rs.getString("capacidad_Trabajo")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return machineries;
}
    public boolean eliminar(int id) {
        try (Connection cn = DBConnection.getConnection()) {
            if (cn == null) {
                return false;
            }

            String sql = "DELETE FROM tb_maquina WHERE id = ?";
            try (PreparedStatement stmt = cn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                int filasAfectadas = stmt.executeUpdate();
                return filasAfectadas > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Machinery machinery) {
        try (Connection cn = DBConnection.getConnection()) {
            if (cn == null) {
                return false;
            }

            String sql = "UPDATE tb_maquina SET nombre = ?, condicion = ?, disponibilidad = ?, dia_Adquisicion = ?, " +
                         "costo_Alquiler = ?, ubicacion = ?, capacidad_Trabajo = ? WHERE id = ?";
            try (PreparedStatement stmt = cn.prepareStatement(sql)) {
                stmt.setString(1, machinery.getNombre());
                stmt.setString(2, machinery.getCondicion());
                stmt.setBoolean(3, machinery.getDisponibilidad());
                stmt.setDate(4, machinery.getDiaAdquisicion() != null ? Date.valueOf(machinery.getDiaAdquisicion()) : null);
                stmt.setDouble(5, machinery.getCostoAlquiler());
                stmt.setString(6, machinery.getUbicacion());
                stmt.setString(7, machinery.getCapacidadTrabajo());
                stmt.setInt(8, machinery.getId());

                int filasAfectadas = stmt.executeUpdate();
                return filasAfectadas > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public Machinery obtenerMaquinaPorId(int id) {
    Machinery machinery = null;
    String sql = "SELECT m.id, m.nombre, m.condicion, m.disponibilidad, m.dia_Adquisicion, " +
                 "m.costo_Alquiler, m.ubicacion, m.capacidad_Trabajo FROM tb_maquina m WHERE m.id = ?";

    try (Connection cn = DBConnection.getConnection();
         PreparedStatement stmt = cn.prepareStatement(sql)) {
        
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            machinery = new Machinery(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("condicion"),
                rs.getBoolean("disponibilidad"),
                rs.getDate("dia_Adquisicion").toLocalDate(),
                rs.getDouble("costo_Alquiler"),
                rs.getString("ubicacion"),
                rs.getString("capacidad_Trabajo")
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return machinery;
}

}