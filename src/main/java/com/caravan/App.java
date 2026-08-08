package com.caravan;

import com.caravan.api.WeatherService;
import com.caravan.api.RouteService;
import com.caravan.database.*;
import com.caravan.dispatch.DispatchEngine;
import com.caravan.model.*;
import com.caravan.util.ReportGenerator;
import java.time.LocalDateTime;
import java.util.Scanner;

public class App {

    private static final Scanner sc     = new Scanner(System.in);
    private static final DispatchEngine engine = new DispatchEngine();
    private static final VehicleDAO    vehicleDAO = new VehicleDAO();
    private static final DriverDAO     driverDAO  = new DriverDAO();

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║       THE CARAVAN PROJECT        ║");
        System.out.println("║   Logistics & Dispatch System    ║");
        System.out.println("╚══════════════════════════════════╝");

        engine.initialize();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1  -> addVehicle();
                case 2  -> addDriver();
                case 3  -> addTripRequest();
                case 4  -> dispatchNext();
                case 5  -> engine.viewQueue();
                case 6  -> engine.viewBST();
                case 7  -> engine.viewHistory();
                case 8  -> markUrgent();
                case 9  -> completeTrip();
                case 10 -> checkWeather();
                case 11 -> ReportGenerator.printSummaryReport();
                case 12 -> ReportGenerator.exportDispatchesToCSV();
                case 0  -> running = false;
                default -> System.out.println("Invalid choice.");
            }
        }
        System.out.println("Goodbye!");
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n─────────────────────────────────");
        System.out.println(" 1. Add Vehicle");
        System.out.println(" 2. Add Driver");
        System.out.println(" 3. Add Trip Request");
        System.out.println(" 4. Dispatch Next Request");
        System.out.println(" 5. View Dispatch Queue");
        System.out.println(" 6. View Vehicle BST");
        System.out.println(" 7. View Dispatch History");
        System.out.println(" 8. Mark Request as Urgent");
        System.out.println(" 9. Complete a Trip");
        System.out.println("10. Check Weather");
        System.out.println("11. Summary Report");
        System.out.println("12. Export Report to CSV");
        System.out.println(" 0. Exit");
        System.out.println("─────────────────────────────────");
    }

    private static void addVehicle() {
        System.out.println("\n── Add Vehicle ──");
        String name   = readString("Name: ");
        String type   = readString("Type (Car/Van/Bus/Truck): ");
        int    cap    = readInt("Passenger Capacity: ");
        String anOk   = readString("Animal Compatible? (yes/no): ");
        boolean animalOk = anOk.equalsIgnoreCase("yes");
        String anType = animalOk ? readString("Animal Type: ") : null;

        Vehicle v = new Vehicle(0, name, type, cap, animalOk, anType, "AVAILABLE");
        vehicleDAO.addVehicle(v);
        engine.addVehicleToBST(v);
    }

    private static void addDriver() {
        System.out.println("\n── Add Driver ──");
        String name  = readString("Name: ");
        String licNo = readString("License Number: ");
        String phone = readString("Phone: ");

        Driver d = new Driver(0, name, licNo, phone, "AVAILABLE");
        driverDAO.addDriver(d);
    }

    private static void addTripRequest() {
        System.out.println("\n── Add Trip Request ──");
        int    pax     = readInt("Passenger Count: ");
        String animal  = readString("Animal Type (or 'none'): ");
        String pickup  = readString("Pickup Location: ");
        String drop    = readString("Drop Location: ");
        int    priority = readInt("Priority (1=Low, 2=Normal, 3=High): ");

        TripRequest req = new TripRequest(
            0, pax, animal, pickup, drop,
            priority, "PENDING", LocalDateTime.now()
        );
        engine.addRequest(req);
    }

    private static void dispatchNext() {
        System.out.println("\n── Dispatch Next Request ──");
        String pickup = readString("Pickup Location: ");
        String drop   = readString("Drop Location: ");

        System.out.println("Checking weather...");
        WeatherService.isSafeToDispatchFromAddress(pickup);

        System.out.println("Calculating distance...");
        double distance = RouteService.getDistanceFromAddresses(pickup, drop);

        if (distance <= 0) {
            System.out.println("Could not calculate distance. Using default 10km.");
            distance = 10.0;
        }

        engine.dispatchNext(distance);
    }

    private static void markUrgent() {
        int id = readInt("Enter Request ID to mark urgent: ");
        engine.markUrgent(id);
    }

    private static void completeTrip() {
        int reqId     = readInt("Request ID: ");
        int vehicleId = readInt("Vehicle ID: ");
        int driverId  = readInt("Driver ID: ");
        engine.completeTrip(reqId, vehicleId, driverId);
    }

    private static void checkWeather() {
        String address = readString("Enter location: ");
        WeatherService.printWeatherReport(address);
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        try {
            int val = Integer.parseInt(sc.nextLine().trim());
            return val;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Defaulting to 0.");
            return 0;
        }
    }
}