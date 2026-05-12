package com.caravan.datastructures;

import com.caravan.model.TripRequest;
import com.caravan.database.TripRequestDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
public class DispatchHeap {

    // Max-Heap — highest priority request always on top
    // Using PriorityBlockingQueue for thread safety (Phase 2)
    private final PriorityBlockingQueue<TripRequest> heap;

    // How long before a request expires (in hours)
    private static final int EXPIRY_HOURS = 24;

    public DispatchHeap() {
        heap = new PriorityBlockingQueue<>(
            11,
            Comparator.comparingInt(TripRequest::getPriority).reversed()
        );
    }

    // ─── ADD REQUEST ────────────────────────────────────────────────
    public void addRequest(TripRequest request) {
        heap.offer(request);
        System.out.println(" Request added to queue: ID=" + request.getId() +
                           " Priority=" + request.getPriority());
    }

    // ─── POLL HIGHEST PRIORITY ──────────────────────────────────────
    public TripRequest pollNext() {
        TripRequest request = heap.poll();
        if (request == null) {
            System.out.println(" Queue is empty — no requests to dispatch.");
        } else {
            System.out.println("Dispatching request ID=" + request.getId() +
                               " Priority=" + request.getPriority());
        }
        return request;
    }

    // ─── PEEK TOP REQUEST ───────────────────────────────────────────
    public TripRequest peekNext() {
        return heap.peek();
    }

    // ─── SIZE & IS EMPTY ────────────────────────────────────────────
    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // ─── URGENT OVERRIDE ────────────────────────────────────────────
    // Forces a request to priority 99 so it jumps to top immediately
    public void markAsUrgent(int requestId) {
        List<TripRequest> temp = new ArrayList<>(heap);
        heap.clear();
        for (TripRequest r : temp) {
            if (r.getId() == requestId) {
                r.setPriority(99); // highest possible priority
                System.out.println("Request ID=" + requestId + 
                                   " marked as URGENT — moved to top!");
            }
            heap.offer(r);
        }
    }

    // ─── UPDATE PRIORITY ────────────────────────────────────────────
    public void updatePriority(int requestId, int newPriority) {
        List<TripRequest> temp = new ArrayList<>(heap);
        heap.clear();
        boolean found = false;
        for (TripRequest r : temp) {
            if (r.getId() == requestId) {
                r.setPriority(newPriority);
                found = true;
                System.out.println("Priority updated for Request ID=" + 
                                   requestId + " → " + newPriority);
            }
            heap.offer(r);
        }
        if (!found) {
            System.out.println("Request ID=" + requestId + " not found in queue.");
        }
    }
    // ─── REMOVE EXPIRED REQUESTS ────────────────────────────────────
    public void removeExpiredRequests() {
        List<TripRequest> temp = new ArrayList<>(heap);
        heap.clear();
        int removed = 0;
        for (TripRequest r : temp) {
            if (r.getCreatedAt().plusHours(EXPIRY_HOURS).isAfter(LocalDateTime.now())) {
                heap.offer(r);
            } else {
                removed++;
                System.out.println(" Expired request removed: ID=" + r.getId());
            }
        }
        System.out.println(" Expired requests removed: " + removed);
    }

    // ─── BULK LOAD FROM DATABASE ────────────────────────────────────
    public void loadFromDatabase() {
        TripRequestDAO dao = new TripRequestDAO();
        List<TripRequest> pending = dao.getPendingRequests();
        for (TripRequest r : pending) {
            heap.offer(r);
        }
        System.out.println("Loaded " + pending.size() + 
                           " pending requests from database into heap.");
    }

    // ─── PRINT ALL REQUESTS IN QUEUE ────────────────────────────────
    public void printQueue() {
        if (heap.isEmpty()) {
            System.out.println("Dispatch queue is empty.");
            return;
        }

        // Drain to sorted list for display
        List<TripRequest> sorted = new ArrayList<>(heap);
        sorted.sort(Comparator.comparingInt(TripRequest::getPriority).reversed());

        System.out.println("\n========== DISPATCH QUEUE ==========");
        System.out.printf("%-5s %-20s %-20s %-10s %-10s%n",
                "ID", "Pickup", "Drop", "Passengers", "Priority");
        System.out.println("-".repeat(70));

        for (TripRequest r : sorted) {
            System.out.printf("%-5d %-20s %-20s %-10d %-10d%n",
                    r.getId(),
                    r.getPickupLocation(),
                    r.getDropLocation(),
                    r.getPassengerCount(),
                    r.getPriority());
        }
        System.out.println("=".repeat(70) + "\n");
    }
}