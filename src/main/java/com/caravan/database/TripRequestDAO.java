package com.caravan.database;

import com.caravan.model.TripRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripRequestDAO {

    // Add a new trip request
    public int addTripRequest(TripRequest request) {
        String sql = "INSERT INTO trip_requests (passenger_count, animal_type, " +
                     "pickup_location, drop_location, priority, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, 
                                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, request.getPassengerCount());
            stmt.setString(2, request.getAnimalType());
            stmt.setString(3, request.getPickupLocation());
            stmt.setString(4, request.getDropLocation());
            stmt.setInt(5, request.getPriority());
            stmt.setString(6, request.getStatus());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                generatedId = keys.getInt(1);
            }
            System.out.println("Trip request added with ID: " + generatedId);

        } catch (SQLException e) {
            System.out.println("Error adding trip request: " + e.getMessage());
        }
        return generatedId;
    }

    // Get all pending trip requests
    public List<TripRequest> getPendingRequests() {
        List<TripRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM trip_requests WHERE status = 'PENDING' " +
                     "ORDER BY priority DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(new TripRequest(
                    rs.getInt("id"),
                    rs.getInt("passenger_count"),
                    rs.getString("animal_type"),
                    rs.getString("pickup_location"),
                    rs.getString("drop_location"),
                    rs.getInt("priority"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching requests: " + e.getMessage());
        }
        return requests;
    }

    // Get all trip requests
    public List<TripRequest> getAllRequests() {
        List<TripRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM trip_requests ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(new TripRequest(
                    rs.getInt("id"),
                    rs.getInt("passenger_count"),
                    rs.getString("animal_type"),
                    rs.getString("pickup_location"),
                    rs.getString("drop_location"),
                    rs.getInt("priority"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching requests: " + e.getMessage());
        }
        return requests;
    }

    // Update trip request status
    public void updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE trip_requests SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();
            System.out.println("Trip request status updated to: " + status);

        } catch (SQLException e) {
            System.out.println("Error updating request: " + e.getMessage());
        }
    }
}