package com.caravan.database;

import com.caravan.model.Driver;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {

    public void addDriver(Driver driver) {
        String sql = "INSERT INTO drivers (name, license_number, phone, status) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, driver.getName());
            stmt.setString(2, driver.getLicenseNumber());
            stmt.setString(3, driver.getPhone());
            stmt.setString(4, driver.getStatus());
            stmt.executeUpdate();
            System.out.println("Driver added: " + driver.getName());

        } catch (SQLException e) {
            System.out.println("Error adding driver: " + e.getMessage());
        }
    }

    // Get all available drivers
    public List<Driver> getAvailableDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers WHERE status = 'AVAILABLE'";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                drivers.add(new Driver(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("license_number"),
                    rs.getString("phone"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching drivers: " + e.getMessage());
        }
        return drivers;
    }

    // Get all drivers
    public List<Driver> getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                drivers.add(new Driver(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("license_number"),
                    rs.getString("phone"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching drivers: " + e.getMessage());
        }
        return drivers;
    }

    // Update driver status
    public void updateDriverStatus(int driverId, String status) {
        String sql = "UPDATE drivers SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, driverId);
            stmt.executeUpdate();
            System.out.println("Driver status updated to: " + status);

        } catch (SQLException e) {
            System.out.println("Error updating driver: " + e.getMessage());
        }
    }

    // Delete a driver
    public void deleteDriver(int driverId) {
        String sql = "DELETE FROM drivers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, driverId);
            stmt.executeUpdate();
            System.out.println("Driver deleted successfully.");

        } catch (SQLException e) {
            System.out.println("Error deleting driver: " + e.getMessage());
        }
    }
}