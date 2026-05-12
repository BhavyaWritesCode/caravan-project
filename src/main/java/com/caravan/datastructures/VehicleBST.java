package com.caravan.datastructures;

import com.caravan.model.Vehicle;
import java.util.ArrayList;
import java.util.List;

public class VehicleBST {

    // ─── Inner Node Class ───────────────────────────────────────────
    private static class Node {
        Vehicle vehicle;
        Node left, right;

        Node(Vehicle vehicle) {
            this.vehicle = vehicle;
        }
    }

    private Node root;

    // ─── INSERT ─────────────────────────────────────────────────────
    public void insert(Vehicle vehicle) {
        root = insertRec(root, vehicle);
        System.out.println("Vehicle inserted into BST: " + vehicle.getName());
    }

    private Node insertRec(Node node, Vehicle vehicle) {
        if (node == null) return new Node(vehicle);
        if (vehicle.getPassengerCapacity() < node.vehicle.getPassengerCapacity())
            node.left = insertRec(node.left, vehicle);
        else if (vehicle.getPassengerCapacity() > node.vehicle.getPassengerCapacity())
            node.right = insertRec(node.right, vehicle);
        return node;
    }

    // ─── DELETE ─────────────────────────────────────────────────────
    public void delete(int capacity) {
        root = deleteRec(root, capacity);
        System.out.println("Vehicle with capacity " + capacity + " removed from BST.");
    }

    private Node deleteRec(Node node, int capacity) {
        if (node == null) return null;
        if (capacity < node.vehicle.getPassengerCapacity())
            node.left = deleteRec(node.left, capacity);
        else if (capacity > node.vehicle.getPassengerCapacity())
            node.right = deleteRec(node.right, capacity);
        else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            Node minNode = findMin(node.right);
            node.vehicle = minNode.vehicle;
            node.right = deleteRec(node.right, minNode.vehicle.getPassengerCapacity());
        }
        return node;
    }

    private Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    // ─── SEARCH BEST FIT ────────────────────────────────────────────
    public Vehicle findBestFit(int requiredCapacity) {
        Vehicle best = null;
        Node current = root;
        while (current != null) {
            if (current.vehicle.getPassengerCapacity() >= requiredCapacity
                    && current.vehicle.getStatus().equals("AVAILABLE")) {
                best = current.vehicle;
                current = current.left; // go smaller — find closest fit
            } else {
                current = current.right;
            }
        }
        return best;
    }

    // ─── CLOSEST CAPACITY FIT ───────────────────────────────────────
    public Vehicle findClosestFit(int requiredCapacity) {
        Vehicle closest = null;
        int minDiff = Integer.MAX_VALUE;
        List<Vehicle> all = getAllVehiclesInOrder();
        for (Vehicle v : all) {
            if (v.getStatus().equals("AVAILABLE") &&
                v.getPassengerCapacity() >= requiredCapacity) {
                int diff = v.getPassengerCapacity() - requiredCapacity;
                if (diff < minDiff) {
                    minDiff = diff;
                    closest = v;
                }
            }
        }
        return closest;
    }

    // ─── ANIMAL COMPATIBLE FILTER ───────────────────────────────────
    public Vehicle findAnimalCompatible(int requiredCapacity, String animalType) {
        List<Vehicle> all = getAllVehiclesInOrder();
        Vehicle best = null;
        int minDiff = Integer.MAX_VALUE;
        for (Vehicle v : all) {
            if (v.getStatus().equals("AVAILABLE") &&
                v.isAnimalCompatible() &&
                v.getPassengerCapacity() >= requiredCapacity &&
                (animalType == null || animalType.equalsIgnoreCase(v.getAnimalType()))) {
                int diff = v.getPassengerCapacity() - requiredCapacity;
                if (diff < minDiff) {
                    minDiff = diff;
                    best = v;
                }
            }
        }
        return best;
    }

    // ─── FIND BY TYPE ────────────────────────────────────────────────
    public List<Vehicle> findByType(String type) {
        List<Vehicle> result = new ArrayList<>();
        List<Vehicle> all = getAllVehiclesInOrder();
        for (Vehicle v : all) {
            if (v.getType().equalsIgnoreCase(type) &&
                v.getStatus().equals("AVAILABLE")) {
                result.add(v);
            }
        }
        return result;
    }
    // ─── IN-ORDER TRAVERSAL ─────────────────────────────────────────
    public List<Vehicle> getAllVehiclesInOrder() {
        List<Vehicle> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(Node node, List<Vehicle> result) {
        if (node == null) return;
        inOrderRec(node.left, result);
        result.add(node.vehicle);
        inOrderRec(node.right, result);
    }

    // ─── COUNT TOTAL VEHICLES ───────────────────────────────────────
    public int countVehicles() {
        return countRec(root);
    }

    private int countRec(Node node) {
        if (node == null) return 0;
        return 1 + countRec(node.left) + countRec(node.right);
    }

    // ─── IS EMPTY ───────────────────────────────────────────────────
    public boolean isEmpty() {
        return root == null;
    }

    // ─── CLEAR BST ──────────────────────────────────────────────────
    public void clear() {
        root = null;
        System.out.println("BST cleared.");
    }

    // ─── MOST AVAILABLE TYPE ────────────────────────────────────────
    public String mostAvailableType() {
        List<Vehicle> all = getAllVehiclesInOrder();
        int car = 0, van = 0, bus = 0, truck = 0;
        for (Vehicle v : all) {
            if (!v.getStatus().equals("AVAILABLE")) continue;
            switch (v.getType().toLowerCase()) {
                case "car"   -> car++;
                case "van"   -> van++;
                case "bus"   -> bus++;
                case "truck" -> truck++;
            }
        }
        int max = Math.max(Math.max(car, van), Math.max(bus, truck));
        if (max == 0) return "None available";
        if (max == car)   return "Car ("   + car   + " available)";
        if (max == van)   return "Van ("   + van   + " available)";
        if (max == bus)   return "Bus ("   + bus   + " available)";
        return                   "Truck (" + truck + " available)";
    }

    // ─── REBALANCE BST ──────────────────────────────────────────────
    public void rebalance() {
        List<Vehicle> sorted = getAllVehiclesInOrder();
        root = null;
        buildBalanced(sorted, 0, sorted.size() - 1);
        System.out.println("BST rebalanced.");
    }

    private void buildBalanced(List<Vehicle> sorted, int start, int end) {
        if (start > end) return;
        int mid = (start + end) / 2;
        insertDirect(sorted.get(mid));
        buildBalanced(sorted, start, mid - 1);
        buildBalanced(sorted, mid + 1, end);
    }

    private void insertDirect(Vehicle vehicle) {
        root = insertRec(root, vehicle);
    }

    public void printTree() {
        System.out.println("\n========== VEHICLE BST ==========");
        printRec(root, "", true);
    }

    private void printRec(Node node, String prefix, boolean isLeft) {
        if (node == null) return;
        System.out.println(prefix + (isLeft ? "├── " : "└── ") +
                node.vehicle.getName() +
                " [Cap: " + node.vehicle.getPassengerCapacity() +
                ", Status: " + node.vehicle.getStatus() + "]");
        printRec(node.left,  prefix + (isLeft ? "│   " : "    "), true);
        printRec(node.right, prefix + (isLeft ? "│   " : "    "), false);
    }
}