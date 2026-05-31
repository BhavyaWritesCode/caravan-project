package com.caravan.database;

import com.caravan.model.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    public void addVehicle(Vehicle v) {
        String sql = "INSERT INTO vehicles (name, type, passenger_capacity, " +
                     "animal_compatible, animal_type, status) VALUES (?, ?, ?, ?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, v.getName());
            ps.setString(2, v.getType());
            ps.setInt(3, v.getCapacity());
            ps.setBoolean(4, v.isAnimalOk());
            ps.setString(5, v.getAnimalType());
            ps.setString(6, v.getStatus());
            ps.executeUpdate();
            System.out.println("vehicle added: " + v.getName());

        } catch(SQLException e) {
            System.out.println("addVehicle failed: " + e.getMessage());
        }
    }

    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE status = 'AVAILABLE'";

        try(Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while(rs.next())
                list.add(mapRow(rs));

        } catch(SQLException e) {
            System.out.println("getAvailableVehicles failed: " + e.getMessage());
        }
        return list;
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";

        try(Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while(rs.next())
                list.add(mapRow(rs));

        } catch(SQLException e) {
            System.out.println("getAllVehicles failed: " + e.getMessage());
        }
        return list;
    }

    public void updateStatus(int vid, String status) {
        String sql = "UPDATE vehicles SET status = ? WHERE id = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, vid);
            ps.executeUpdate();
            System.out.println("vehicle " + vid + " status -> " + status);

        } catch(SQLException e) {
            System.out.println("updateStatus failed: " + e.getMessage());
        }
    }

    public void deleteVehicle(int vid) {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vid);
            ps.executeUpdate();
            System.out.println("vehicle " + vid + " deleted");

        } catch(SQLException e) {
            System.out.println("deleteVehicle failed: " + e.getMessage());
        }
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        return new Vehicle(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("type"),
            rs.getInt("passenger_capacity"),
            rs.getBoolean("animal_compatible"),
            rs.getString("animal_type"),
            rs.getString("status")
        );
    }
}