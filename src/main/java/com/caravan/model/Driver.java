package com.caravan.model;

public class Driver {

    private int id;
    private String name;
    private String licNo;
    private String phone;
    private String status;

    public Driver(int id, String name, String licNo, String phone, String status) {
        this.id = id;
        this.name = name;
        this.licNo = licNo;
        this.phone = (phone != null) ? phone : "N/A";
        this.status = (status != null) ? status : "available";
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public String getLicNo() {
        return licNo;
    }

    public String getPhone() { return phone; }
    public String getStatus() { return status; }

    public void setStatus(String s) {
        if(s == null || s.isEmpty()) return;
        this.status = s;
    }

    public boolean isAvailable() {
        return status.equals("available");
    }

    @Override
    public String toString() {
        return id + " | " + name + " lic=" + licNo
            + " ph=" + phone + " [" + status + "]";
    }
}