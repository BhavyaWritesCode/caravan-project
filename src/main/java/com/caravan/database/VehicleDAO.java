package com.caravan.database;

import com.caravan.model.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    // Add a new vehicle to database
    public void addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (name, type, passenger_capacity, " +
                     "animal_compatible, animal_type, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicle.getName());
            stmt.setString(2, vehicle.getType());
            stmt.setInt(3, vehicle.getPassengerCapacity());
            stmt.setBoolean(4, vehicle.isAnimalCompatible());
            stmt.setString(5, vehicle.getAnimalType());
            stmt.setString(6, vehicle.getStatus());
            stmt.executeUpdate();
            System.out.println("✅ Vehicle added: " + vehicle.getName());

        } catch (SQLException e) {
            System.out.println("❌ Error adding vehicle: " + e.getMessage());
        }
    }

    // Get all available vehicles
    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE status = 'AVAILABLE'";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehicles.add(new Vehicle(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getInt("passenger_capacity"),
                    rs.getBoolean("animal_compatible"),
                    rs.getString("animal_type"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching vehicles: " + e.getMessage());
        }
        return vehicles;
    }

    // Get all vehicles
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehicles.add(new Vehicle(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getInt("passenger_capacity"),
                    rs.getBoolean("animal_compatible"),
                    rs.getString("animal_type"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching vehicles: " + e.getMessage());
        }
        return vehicles;
    }

    // Update vehicle status
    public void updateVehicleStatus(int vehicleId, String status) {
        String sql = "UPDATE vehicles SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, vehicleId);
            stmt.executeUpdate();
            System.out.println("✅ Vehicle status updated to: " + status);

        } catch (SQLException e) {
            System.out.println("❌ Error updating vehicle: " + e.getMessage());
        }
    }

    // Delete a vehicle
    public void deleteVehicle(int vehicleId) {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vehicleId);
            stmt.executeUpdate();
            System.out.println("✅ Vehicle deleted successfully.");

        } catch (SQLException e) {
            System.out.println("❌ Error deleting vehicle: " + e.getMessage());
        }
    }
}