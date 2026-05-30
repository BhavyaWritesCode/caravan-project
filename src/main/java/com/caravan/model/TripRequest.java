package com.caravan.model;

import java.time.LocalDateTime;

public class TripRequest {

    private int id;
    private int paxCount;
    private String animalType;
    private String pickup;
    private String drop;
    private int priority;
    private String status;
    private LocalDateTime createdAt;

    public TripRequest(int id, int paxCount, String animalType,
        String pickup, String drop, int priority,
        String status, LocalDateTime createdAt) {
        this.id = id;
        this.paxCount = paxCount;
        this.animalType = (animalType == null) ? "none" : animalType;
        this.pickup = pickup;
        this.drop = drop;
        this.priority = (priority < 1) ? 1 : priority;
        this.status = (status != null) ? status : "pending";
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getPaxCount() { return paxCount; }
    public String getAnimalType() { return animalType; }

    public String getPickup() {
        return pickup;
    }

    public String getDrop() {
        return drop;
    }

    public int getPriority() { return priority; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStatus(String s) {
        if(s == null || s.isEmpty()) return;
        this.status = s;
    }

    public void setPriority(int p) {
        if(p < 1) return;
        this.priority = p;
    }

    public boolean hasAnimal() {
        return !animalType.equals("none");
    }

    @Override
    public String toString() {
        return id + " | pax=" + paxCount + " pickup=" + pickup
            + " -> " + drop + " priority=" + priority
            + " animal=" + animalType + " [" + status + "]";
    }
}