/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.agrow.data.harvest;

import cr.ac.una.agrow.domain.harvest.Harvest;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;

/**
 *
 * @author miste
 */
public class HarvestData {

    private String url = "jdbc:mysql://localhost:3306/db_agrow";
    private String usuario = "root";
    private String contrasena = "";

    public LinkedList<Harvest> getHarvested() {

        LinkedList<Harvest> list = new LinkedList<>();
        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena); CallableStatement stmt = conn.prepareCall("{CALL sp_getHarvest()}")) {

            ResultSet rs = stmt.executeQuery();
            // Procesa el conjunto de resultados y llena la lista observable
            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                LocalDate date = rs.getDate("date").toLocalDate();
                int quantity = rs.getInt("quantity");
                String quality = rs.getString("quality");
                String destiny = rs.getString("destiny");
                boolean register = rs.getBoolean("registered");
                String description = rs.getString("description");
                list.add(new Harvest(id, type, date, quantity, quality, destiny, register, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public LinkedList<Harvest> getHarvestedByQuality(String harvestQuality) {
        LinkedList<Harvest> list = new LinkedList<>();

        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena); CallableStatement stmt = conn.prepareCall("{CALL sp_getHarvestByQuality(?)}")) {

            stmt.setString(1, harvestQuality);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Harvest harvest = new Harvest(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("quantity"),
                        rs.getString("quality"),
                        rs.getString("destiny"),
                        rs.getBoolean("registered"),
                        rs.getString("description")
                );
                list.add(harvest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public LinkedList<Harvest> getHarvestedByDestiny(String destiny) {
        LinkedList<Harvest> list = new LinkedList<>();

        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena); CallableStatement stmt = conn.prepareCall("{CALL sp_getHarvestByDestiny(?)}")) {

            stmt.setString(1, destiny);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Harvest harvest = new Harvest(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("quantity"),
                        rs.getString("quality"),
                        rs.getString("destiny"),
                        rs.getBoolean("registered"),
                        rs.getString("description")
                );
                list.add(harvest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void saveHarvestInDb(String type, LocalDate date, int quantity, String quality, String destiny, boolean register, String description) {
        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena); CallableStatement stmt = conn.prepareCall("CALL sp_saveHarvest(?, ?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, type);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            stmt.setInt(3, quantity);
            stmt.setString(4, quality);
            stmt.setString(5, destiny);
            stmt.setBoolean(6, register);
            stmt.setString(7, description);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteHarvestById(int id) {
        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena); CallableStatement stmt = conn.prepareCall("{CALL sp_deleteHarvest(?)}")) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0; // Retorna true si eliminó algo

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateHarvest(int id, String type, LocalDate date, int quantity, String quality, String destiny, boolean register, String description) {

        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena); CallableStatement stmt = conn.prepareCall("{CALL sp_updateHarvest(?, ?, ?, ?, ?, ?, ?, ?)}")) {

            stmt.setInt(1, id);
            stmt.setString(2, type);
            stmt.setDate(3, java.sql.Date.valueOf(date));
            stmt.setInt(4, quantity);
            stmt.setString(5, quality);
            stmt.setString(6, destiny);
            stmt.setBoolean(7, register);
            stmt.setString(8, description);

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
