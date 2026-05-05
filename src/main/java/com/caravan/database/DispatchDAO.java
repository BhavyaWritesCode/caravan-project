package com.caravan.database;

import java.sql.*;


public class DispatchDAO {

    // Save a new dispatch record
    public void addDispatch(int requestId, int vehicleId, int driverId,
                            double distanceKm, double estimatedCost) {
        String sql = "INSERT INTO dispatches (request_id, vehicle_id, driver_id, " +
                     "distance_km, estimated_cost, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);
            stmt.setInt(2, vehicleId);
            stmt.setInt(3, driverId);
            stmt.setDouble(4, distanceKm);
            stmt.setDouble(5, estimatedCost);
            stmt.setString(6, "IN_PROGRESS");
            stmt.executeUpdate();
            System.out.println("Dispatch recorded successfully.");

        } catch (SQLException e) {
            System.out.println("Error recording dispatch: " + e.getMessage());
        }
    }

    // Complete a dispatch
    public void completeDispatch(int requestId) {
        String sql = "UPDATE dispatches SET status = 'COMPLETED' " +
                     "WHERE request_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);
            stmt.executeUpdate();
            System.out.println("Dispatch completed.");

        } catch (SQLException e) {
            System.out.println("Error completing dispatch: " + e.getMessage());
        }
    }

    // Get all dispatches
    public void printAllDispatches() {
        String sql = "SELECT d.id, t.pickup_location, t.drop_location, " +
                     "v.name AS vehicle, dr.name AS driver, " +
                     "d.distance_km, d.estimated_cost, d.status " +
                     "FROM dispatches d " +
                     "JOIN trip_requests t ON d.request_id = t.id " +
                     "JOIN vehicles v ON d.vehicle_id = v.id " +
                     "JOIN drivers dr ON d.driver_id = dr.id " +
                     "ORDER BY d.dispatch_time DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n========== DISPATCH HISTORY ==========");
            System.out.printf("%-5s %-20s %-20s %-15s %-15s %-10s %-10s %-12s%n",
                    "ID", "Pickup", "Drop", "Vehicle", "Driver",
                    "Dist(km)", "Cost(₹)", "Status");
            System.out.println("-".repeat(100));

            while (rs.next()) {
                System.out.printf("%-5d %-20s %-20s %-15s %-15s %-10.2f %-10.2f %-12s%n",
                        rs.getInt("id"),
                        rs.getString("pickup_location"),
                        rs.getString("drop_location"),
                        rs.getString("vehicle"),
                        rs.getString("driver"),
                        rs.getDouble("distance_km"),
                        rs.getDouble("estimated_cost"),
                        rs.getString("status"));
            }
            System.out.println("=".repeat(100));

        } catch (SQLException e) {
            System.out.println("Error fetching dispatches: " + e.getMessage());
        }
    }
}