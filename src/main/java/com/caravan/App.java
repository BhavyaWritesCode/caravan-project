package com.caravan;

import com.caravan.database.DBConnection;

public class App {
    public static void main(String[] args) {
        DBConnection.getConnection();
        DBConnection.closeConnection();
    }
}