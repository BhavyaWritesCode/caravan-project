package com.caravan.database;

import com.caravan.model.Driver;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {

    public void addDriver(Driver d) {
        String sql = "INSERT INTO drivers (name, license_number, phone, status) " +
                     "VALUES (?, ?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getName());
            ps.setString(2, d.getLicNo());
            ps.setString(3, d.getPhone());
            ps.setString(4, d.getStatus());
            ps.executeUpdate();
            System.out.println("driver added: " + d.getName());

        } catch(SQLException e) {
            System.out.println("addDriver failed: " + e.getMessage());
        }
    }

    public List<Driver> getAvailableDrivers() {
        List<Driver> list = new ArrayList<>();
        String sql = "SELECT * FROM drivers WHERE status = 'AVAILABLE'";

        try(Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while(rs.next())
                list.add(mapRow(rs));

        } catch(SQLException e) {
            System.out.println("getAvailableDrivers failed: " + e.getMessage());
        }
        return list;
    }

    public List<Driver> getAllDrivers() {
        List<Driver> list = new ArrayList<>();
        String sql = "SELECT * FROM drivers";

        try(Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while(rs.next())
                list.add(mapRow(rs));

        } catch(SQLException e) {
            System.out.println("getAllDrivers failed: " + e.getMessage());
        }
        return list;
    }

    public void updateStatus(int did, String status) {
        String sql = "UPDATE drivers SET status = ? WHERE id = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, did);
            ps.executeUpdate();
            System.out.println("driver " + did + " status -> " + status);

        } catch(SQLException e) {
            System.out.println("updateStatus failed: " + e.getMessage());
        }
    }

    public void deleteDriver(int did) {
        String sql = "DELETE FROM drivers WHERE id = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, did);
            ps.executeUpdate();
            System.out.println("driver " + did + " deleted");

        } catch(SQLException e) {
            System.out.println("deleteDriver failed: " + e.getMessage());
        }
    }

    private Driver mapRow(ResultSet rs) throws SQLException {
        return new Driver(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("license_number"),
            rs.getString("phone"),
            rs.getString("status")
        );
    }
}