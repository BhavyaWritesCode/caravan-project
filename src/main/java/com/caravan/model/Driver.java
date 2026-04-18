package com.caravan.model;

public class Driver {
    private int id;
    private String name;
    private String licenseNumber;
    private String phone;
    private String status;

    public Driver(int id, String name, String licenseNumber, 
                  String phone, String status) {
        this.id = id;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.phone = phone;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Driver{id=" + id + ", name='" + name + "', license='" + 
               licenseNumber + "', phone='" + phone + "', status='" + status + "'}";
    }
}