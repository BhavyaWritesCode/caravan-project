package com.caravan.dispatch;

import com.caravan.database.*;
import com.caravan.datastructures.*;
import com.caravan.model.*;
import com.caravan.util.CostCalculator;
import java.time.LocalTime;
import java.util.List;

public class DispatchEngine {

    private final VehicleDAO     vehicleDAO   = new VehicleDAO();
    private final DriverDAO      driverDAO    = new DriverDAO();
    private final TripRequestDAO requestDAO   = new TripRequestDAO();
    private final DispatchDAO    dispatchDAO  = new DispatchDAO();
    private final DispatchHeap   dispatchHeap = new DispatchHeap();
    private final VehicleBST     vehicleBST   = new VehicleBST();

    public void initialize() {
        System.out.println("\nInitializing Caravan Dispatch Engine...");
        dispatchHeap.loadFromDB();
        List<Vehicle> vehicles = vehicleDAO.getAvailableVehicles();
        for (Vehicle v : vehicles) vehicleBST.insert(v);
        System.out.println("Engine initialized — " +
                dispatchHeap.size() + " requests, " +
                vehicleBST.countVehicles() + " vehicles.");
    }

    public void addRequest(TripRequest request) {
        int id = requestDAO.addTripRequest(request);
        request = new TripRequest(
            id,
            request.getPaxCount(),
            request.getAnimalType(),
            request.getPickup(),
            request.getDrop(),
            request.getPriority(),
            request.getStatus(),
            request.getCreatedAt()
        );
        dispatchHeap.addRequest(request);
    }

    public void dispatchNext(double distanceKm) {
        if (dispatchHeap.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }

        TripRequest request = dispatchHeap.pollNext();
        if (request == null) return;

        Vehicle vehicle;
        if (request.hasAnimal()) {
            vehicle = vehicleBST.findAnimalCompatible(
                request.getPaxCount(), request.getAnimalType());
        } else {
            vehicle = vehicleBST.findClosestFit(request.getPaxCount());
        }

        if (vehicle == null) {
            System.out.println("No suitable vehicle — returning to queue.");
            dispatchHeap.addRequest(request);
            return;
        }

        List<Driver> drivers = driverDAO.getAvailableDrivers();
        if (drivers.isEmpty()) {
            System.out.println("No drivers available — returning to queue.");
            dispatchHeap.addRequest(request);
            return;
        }
        Driver driver = drivers.get(0);

        boolean isNight  = isNightTime();
        double totalCost = CostCalculator.calculateTotalCost(
                distanceKm, vehicle.getType(),
                request.hasAnimal(), request.getPriority(), isNight);

        dispatchDAO.addDispatch(request.getId(), vehicle.getId(),
                driver.getId(), distanceKm, totalCost);

        vehicleDAO.updateStatus(vehicle.getId(), "ON_TRIP");
        driverDAO.updateStatus(driver.getId(), "ON_TRIP");
        requestDAO.updateStatus(request.getId(), "DISPATCHED");

        vehicleBST.delete(vehicle.getCapacity());

        CostCalculator.printCostBreakdown(distanceKm, vehicle.getType(),
                request.hasAnimal(), request.getPriority(), isNight);

        System.out.println("Dispatched!");
        System.out.println("   Request  : ID=" + request.getId());
        System.out.println("   Vehicle  : " + vehicle.getName());
        System.out.println("   Driver   : " + driver.getName());
        System.out.printf ("   Distance : %.2f km%n", distanceKm);
        System.out.printf ("   Cost     : ₹%.2f%n", totalCost);
    }

    public void dispatchAll(double defaultDistance) {
        System.out.println("\nDispatching all pending requests...");
        while (!dispatchHeap.isEmpty()) dispatchNext(defaultDistance);
        System.out.println("All requests processed.");
    }

    public void markUrgent(int requestId) {
        dispatchHeap.markUrgent(requestId);
        requestDAO.updateStatus(requestId, "URGENT");
    }

    public void completeTrip(int requestId, int vehicleId, int driverId) {
        dispatchDAO.completeDispatch(requestId);
        vehicleDAO.updateStatus(vehicleId, "AVAILABLE");
        driverDAO.updateStatus(driverId, "AVAILABLE");
        System.out.println("Trip ID=" + requestId + " completed.");
    }

    public void viewQueue()   { dispatchHeap.printQueue(); }
    public void viewBST()     { vehicleBST.printTree(); }
    public void viewHistory() { dispatchDAO.printAllDispatches(); }

    public void addVehicleToBST(Vehicle vehicle) {
        vehicleBST.insert(vehicle);
    }
    private boolean isNightTime() {
        int hour = LocalTime.now().getHour();
        return hour >= 21 || hour < 6;
    }
}