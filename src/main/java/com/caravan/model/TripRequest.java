package com.caravan.model;

import java.time.LocalDateTime;

public class TripRequest {
    private int id;
    private int passengerCount;
    private String animalType;
    private String pickupLocation;
    private String dropLocation;
    private int priority;
    private String status;
    private LocalDateTime createdAt;

    public TripRequest(int id, int passengerCount, String animalType,
                       String pickupLocation, String dropLocation, 
                       int priority, String status, LocalDateTime createdAt) {
        this.id = id;
        this.passengerCount = passengerCount;
        this.animalType = animalType;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getPassengerCount() { return passengerCount; }
    public String getAnimalType() { return animalType; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDropLocation() { return dropLocation; }
    public int getPriority() { return priority; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setStatus(String status) { this.status = status; }
    public void setPriority(int priority) { this.priority = priority; }

    @Override
    public String toString() {
        return "TripRequest{id=" + id + ", passengers=" + passengerCount +
               ", animal='" + animalType + "', pickup='" + pickupLocation +
               "', drop='" + dropLocation + "', priority=" + priority +
               ", status='" + status + "'}";
    }
}