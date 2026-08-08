package com.caravan.util;

public class CostCalculator {

    private static final double CAR_RATE   = 12.0;
    private static final double VAN_RATE   = 18.0;
    private static final double BUS_RATE   = 25.0;
    private static final double TRUCK_RATE = 30.0;

    private static final double ANIMAL_SURCHARGE  = 1.25; 
    private static final double URGENT_SURCHARGE  = 1.50; 
    private static final double NIGHT_SURCHARGE   = 1.20; 

    private static final double MINIMUM_FARE = 50.0;

    public static double calculateBaseCost(double distanceKm, String vehicleType) {
        double rate = switch (vehicleType.toLowerCase()) {
            case "car"   -> CAR_RATE;
            case "van"   -> VAN_RATE;
            case "bus"   -> BUS_RATE;
            case "truck" -> TRUCK_RATE;
            default      -> CAR_RATE;
        };
        return Math.max(distanceKm * rate, MINIMUM_FARE);
    }

    public static double calculateTotalCost(double distanceKm, String vehicleType,boolean hasAnimal, int priority,boolean isNightTrip) {
        double cost = calculateBaseCost(distanceKm, vehicleType);

        if (hasAnimal)        cost *= ANIMAL_SURCHARGE;
        if (priority == 99)   cost *= URGENT_SURCHARGE;
        if (isNightTrip)      cost *= NIGHT_SURCHARGE;

        return Math.round(cost * 100.0) / 100.0; 
    }

    public static void printCostBreakdown(double distanceKm, String vehicleType,
                                          boolean hasAnimal, int priority,
                                          boolean isNightTrip) {
        double base  = calculateBaseCost(distanceKm, vehicleType);
        double total = calculateTotalCost(distanceKm, vehicleType,
                                          hasAnimal, priority, isNightTrip);

        System.out.println("\n========== COST BREAKDOWN ==========");
        System.out.printf("Distance        : %.2f km%n", distanceKm);
        System.out.printf("Vehicle Type    : %s%n", vehicleType);
        System.out.printf("Base Cost       : ₹%.2f%n", base);
        if (hasAnimal)      System.out.println("Animal Surcharge: +25%");
        if (priority == 99) System.out.println("Urgent Surcharge: +50%");
        if (isNightTrip)    System.out.println("Night Surcharge : +20%");
        System.out.println("─".repeat(36));
        System.out.printf("Total Cost      : ₹%.2f%n", total);
        System.out.println("=".repeat(36) + "\n");
    }
}