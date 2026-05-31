package com.caravan.datastructures;

import com.caravan.model.Vehicle;
import java.util.ArrayList;
import java.util.List;

public class VehicleBST {

    private static class Node {
        Vehicle v;
        Node left, right;

        Node(Vehicle v) {
            this.v = v;
        }
    }

    private Node root;

    public void insert(Vehicle v) {
        root = insertRec(root, v);
        System.out.println("inserted: " + v.getName());
    }

    private Node insertRec(Node node, Vehicle v) {
        if(node == null) return new Node(v);
        if(v.getCapacity() < node.v.getCapacity())
            node.left = insertRec(node.left, v);
        else if(v.getCapacity() > node.v.getCapacity())
            node.right = insertRec(node.right, v);
        return node;
    }

    public void delete(int cap) {
        root = deleteRec(root, cap);
        System.out.println("removed vehicle with cap=" + cap);
    }

    private Node deleteRec(Node node, int cap) {
        if(node == null) return null;
        if(cap < node.v.getCapacity())
            node.left = deleteRec(node.left, cap);
        else if(cap > node.v.getCapacity())
            node.right = deleteRec(node.right, cap);
        else {
            if(node.left == null) return node.right;
            if(node.right == null) return node.left;
            Node mn = getMin(node.right);
            node.v = mn.v;
            node.right = deleteRec(node.right, mn.v.getCapacity());
        }
        return node;
    }

    private Node getMin(Node node) {
        while(node.left != null) node = node.left;
        return node;
    }

    public Vehicle findBestFit(int needed) {
        Vehicle best = null;
        Node curr = root;
        while(curr != null) {
            if(curr.v.getCapacity() >= needed && curr.v.getStatus().equals("AVAILABLE")) {
                best = curr.v;
                curr = curr.left;
            } else {
                curr = curr.right;
            }
        }
        return best;
    }

    public Vehicle findClosestFit(int needed) {
        Vehicle closest = null;
        int minDiff = Integer.MAX_VALUE;
        for(Vehicle v : getAllInOrder()) {
            if(!v.getStatus().equals("AVAILABLE")) continue;
            if(v.getCapacity() < needed) continue;
            int diff = v.getCapacity() - needed;
            if(diff < minDiff) {
                minDiff = diff;
                closest = v;
            }
        }
        return closest;
    }

    public Vehicle findAnimalCompatible(int needed, String animalType) {
        Vehicle best = null;
        int minDiff = Integer.MAX_VALUE;
        for(Vehicle v : getAllInOrder()) {
            if(!v.getStatus().equals("AVAILABLE")) continue;
            if(!v.isAnimalOk()) continue;
            if(v.getCapacity() < needed) continue;
            if(animalType != null && !animalType.equalsIgnoreCase(v.getAnimalType())) continue;
            int diff = v.getCapacity() - needed;
            if(diff < minDiff) {
                minDiff = diff;
                best = v;
            }
        }
        return best;
    }

    public List<Vehicle> findByType(String type) {
        List<Vehicle> res = new ArrayList<>();
        for(Vehicle v : getAllInOrder()) {
            if(v.getType().equalsIgnoreCase(type) && v.getStatus().equals("AVAILABLE"))
                res.add(v);
        }
        return res;
    }

    public List<Vehicle> getAllInOrder() {
        List<Vehicle> res = new ArrayList<>();
        inOrder(root, res);
        return res;
    }

    private void inOrder(Node node, List<Vehicle> res) {
        if(node == null) return;
        inOrder(node.left, res);
        res.add(node.v);
        inOrder(node.right, res);
    }

    public int countVehicles() {
        return countRec(root);
    }

    private int countRec(Node node) {
        if(node == null) return 0;
        return 1 + countRec(node.left) + countRec(node.right);
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        root = null;
        System.out.println("BST cleared.");
    }

    public String mostAvailableType() {
        int car = 0, van = 0, bus = 0, truck = 0;
        for(Vehicle v : getAllInOrder()) {
            if(!v.getStatus().equals("AVAILABLE")) continue;
            switch(v.getType().toLowerCase()) {
                case "car"   -> car++;
                case "van"   -> van++;
                case "bus"   -> bus++;
                case "truck" -> truck++;
            }
        }
        int max = Math.max(Math.max(car, van), Math.max(bus, truck));
        if(max == 0) return "none";
        if(max == car)   return "Car("   + car   + ")";
        if(max == van)   return "Van("   + van   + ")";
        if(max == bus)   return "Bus("   + bus   + ")";
        return                  "Truck(" + truck + ")";
    }

    public void rebalance() {
        List<Vehicle> sorted = getAllInOrder();
        root = null;
        buildBalanced(sorted, 0, sorted.size() - 1);
        System.out.println("BST rebalanced.");
    }

    private void buildBalanced(List<Vehicle> sorted, int start, int end) {
        if(start > end) return;
        int mid = (start + end) / 2;
        root = insertRec(root, sorted.get(mid));
        buildBalanced(sorted, start, mid - 1);
        buildBalanced(sorted, mid + 1, end);
    }

    public void printTree() {
        System.out.println("\n===== VEHICLE BST =====");
        printRec(root, "", true);
    }

    private void printRec(Node node, String prefix, boolean isLeft) {
        if(node == null) return;
        System.out.println(prefix + (isLeft ? "├── " : "└── ")
            + node.v.getName() + " [cap=" + node.v.getCapacity()
            + " status=" + node.v.getStatus() + "]");
        printRec(node.left,  prefix + (isLeft ? "│   " : "    "), true);
        printRec(node.right, prefix + (isLeft ? "│   " : "    "), false);
    }
}