package com.caravan.database;

import com.caravan.model.TripRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripRequestDAO {

    public int addTripRequest(TripRequest req) {
        String sql = "INSERT INTO trip_requests (passenger_count, animal_type, " +
                     "pickup_location, drop_location, priority, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        int newId = -1;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql,
                                   Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, req.getPaxCount());
            ps.setString(2, req.getAnimalType());
            ps.setString(3, req.getPickup());
            ps.setString(4, req.getDrop());
            ps.setInt(5, req.getPriority());
            ps.setString(6, req.getStatus());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if(keys.next())
                newId = keys.getInt(1);

            System.out.println("trip request added, id=" + newId);

        } catch(SQLException e) {
            System.out.println("addTripRequest failed: " + e.getMessage());
        }
        return newId;
    }

    public List<TripRequest> getPendingRequests() {
        List<TripRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM trip_requests WHERE status = 'PENDING' " +
                     "ORDER BY priority DESC";

        try(Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while(rs.next())
                list.add(mapRow(rs));

        } catch(SQLException e) {
            System.out.println("getPendingRequests failed: " + e.getMessage());
        }
        return list;
    }

    public List<TripRequest> getAllRequests() {
        List<TripRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM trip_requests ORDER BY created_at DESC";

        try(Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while(rs.next())
                list.add(mapRow(rs));

        } catch(SQLException e) {
            System.out.println("getAllRequests failed: " + e.getMessage());
        }
        return list;
    }

    public void updateStatus(int reqId, String status) {
        String sql = "UPDATE trip_requests SET status = ? WHERE id = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, reqId);
            ps.executeUpdate();
            System.out.println("trip " + reqId + " status -> " + status);

        } catch(SQLException e) {
            System.out.println("updateStatus failed: " + e.getMessage());
        }
    }

    private TripRequest mapRow(ResultSet rs) throws SQLException {
        return new TripRequest(
            rs.getInt("id"),
            rs.getInt("passenger_count"),
            rs.getString("animal_type"),
            rs.getString("pickup_location"),
            rs.getString("drop_location"),
            rs.getInt("priority"),
            rs.getString("status"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}