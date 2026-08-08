package com.caravan.util;

import com.caravan.database.DBConnection;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportGenerator {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void printSummaryReport() {
        System.out.println("\n========== CARAVAN DISPATCH SUMMARY ==========");
        System.out.println("Generated : " + LocalDateTime.now().format(FMT));
        System.out.println("─".repeat(48));

        printTotalTrips();
        printTotalRevenue();
        printMostUsedVehicle();
        printTopDriver();
        printPendingRequests();

        System.out.println("=".repeat(48) + "\n");
    }

    private static void printTotalTrips() {
        String sql = "SELECT COUNT(*) AS total FROM dispatches";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next())
                System.out.println("Total Trips     : " + rs.getInt("total"));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printTotalRevenue() {
        String sql = "SELECT SUM(estimated_cost) AS revenue FROM dispatches";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next())
                System.out.printf("Total Revenue   : ₹%.2f%n",
                    rs.getDouble("revenue"));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printMostUsedVehicle() {
        String sql = "SELECT v.name, COUNT(*) AS trips " +
                     "FROM dispatches d JOIN vehicles v ON d.vehicle_id = v.id " +
                     "GROUP BY v.name ORDER BY trips DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next())
                System.out.println("Most Used Vehicle: " + rs.getString("name") +
                                   " (" + rs.getInt("trips") + " trips)");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printTopDriver() {
        String sql = "SELECT dr.name, COUNT(*) AS trips " +
                     "FROM dispatches d JOIN drivers dr ON d.driver_id = dr.id " +
                     "GROUP BY dr.name ORDER BY trips DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next())
                System.out.println("Top Driver      : " + rs.getString("name") +
                                   " (" + rs.getInt("trips") + " trips)");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printPendingRequests() {
        String sql = "SELECT COUNT(*) AS pending FROM trip_requests " +
                     "WHERE status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next())
                System.out.println("Pending Requests: " + rs.getInt("pending"));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void exportDispatchesToCSV() {
        String filename = "caravan_report_" +
            LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";

        String sql = "SELECT d.id, t.pickup_location, t.drop_location, " +
                     "v.name AS vehicle, dr.name AS driver, " +
                     "d.distance_km, d.estimated_cost, d.status, d.dispatch_time " +
                     "FROM dispatches d " +
                     "JOIN trip_requests t ON d.request_id  = t.id " +
                     "JOIN vehicles      v ON d.vehicle_id  = v.id " +
                     "JOIN drivers      dr ON d.driver_id   = dr.id " +
                     "ORDER BY d.dispatch_time DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement st   = conn.createStatement();
             ResultSet rs   = st.executeQuery(sql);
             FileWriter fw  = new FileWriter(filename)) {

            // Header
            fw.write("ID,Pickup,Drop,Vehicle,Driver," + "Distance(km),Cost(INR),Status,DispatchTime\n");

            // Rows
            while (rs.next()) {
                fw.write(
                    rs.getInt("id") + "," +
                    rs.getString("pickup_location") + "," +
                    rs.getString("drop_location") + "," +
                    rs.getString("vehicle") + "," +
                    rs.getString("driver") + "," +
                    rs.getDouble("distance_km") + "," +
                    rs.getDouble("estimated_cost") + "," +
                    rs.getString("status") + "," +
                    rs.getTimestamp("dispatch_time") + "\n"
                );
            }

            System.out.println("Report exported: " + filename);

        } catch (SQLException | IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }
}