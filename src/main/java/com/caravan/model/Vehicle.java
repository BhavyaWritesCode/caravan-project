package com.caravan.model;

public class Vehicle {

    private int id;
    private String name;
    private String type;
    private int capacity;
    private boolean animalOk;
    private String animalType;
    private String status;

    public Vehicle(int id, String name, String type, int capacity,
        boolean animalOk, String animalType, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.animalOk = animalOk;
        this.animalType = animalOk ? animalType : null;
        this.status = (status != null) ? status : "available";
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }

    public int getCapacity() {
        return capacity;
    }

    public boolean isAnimalOk() { return animalOk; }
    public String getAnimalType() { return animalType; }
    public String getStatus() { return status; }

    public void setStatus(String s) {
        if(s == null || s.isEmpty()) return;
        this.status = s;
    }

    public void setAnimalOk(boolean animalOk) {
        this.animalOk = animalOk;
        if(!animalOk) this.animalType = null;
    }

    @Override
    public String toString() {
        String ani = animalOk ? animalType : "none";
        return id + " | " + name + " (" + type + ") capacity=" + capacity
            + " animal=" + ani + " status=" + status;
    }
}