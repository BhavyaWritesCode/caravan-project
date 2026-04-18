package com.caravan.model;

public class Vehicle {
    private int id;
    private String name;
    private String type;
    private int passengerCapacity;
    private boolean animalCompatible;
    private String animalType;
    private String status;

    public Vehicle(int id, String name, String type, int passengerCapacity,
                   boolean animalCompatible, String animalType, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.passengerCapacity = passengerCapacity;
        this.animalCompatible = animalCompatible;
        this.animalType = animalType;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getPassengerCapacity() { return passengerCapacity; }
    public boolean isAnimalCompatible() { return animalCompatible; }
    public String getAnimalType() { return animalType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Vehicle{id=" + id + ", name='" + name + "', type='" + type +
               "', capacity=" + passengerCapacity + ", animalCompatible=" + animalCompatible +
               ", animalType='" + animalType + "', status='" + status + "'}";
    }
}