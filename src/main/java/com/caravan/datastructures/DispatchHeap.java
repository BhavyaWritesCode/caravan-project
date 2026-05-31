package com.caravan.datastructures;

import com.caravan.model.TripRequest;
import com.caravan.database.TripRequestDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class DispatchHeap {

    private final PriorityBlockingQueue<TripRequest> heap;
    private static final int EXPIRY_HOURS = 24;

    public DispatchHeap() {
        heap = new PriorityBlockingQueue<>(
            11,
            Comparator.comparingInt(TripRequest::getPriority).reversed()
        );
    }

    public void addRequest(TripRequest req) {
        heap.offer(req);
        System.out.println("added to queue -> id=" + req.getId() + " priority=" + req.getPriority());
    }

    public TripRequest pollNext() {
        TripRequest req = heap.poll();
        if(req == null) {
            System.out.println("queue empty, nothing to dispatch");
        } else {
            System.out.println("dispatching id=" + req.getId() + " priority=" + req.getPriority());
        }
        return req;
    }

    public TripRequest peekNext() {
        return heap.peek();
    }

    public int size() { return heap.size(); }
    public boolean isEmpty() { return heap.isEmpty(); }

    public void markUrgent(int reqId) {
        List<TripRequest> tmp = new ArrayList<>(heap);
        heap.clear();
        for(TripRequest r : tmp) {
            if(r.getId() == reqId) {
                r.setPriority(99);
                System.out.println("id=" + reqId + " marked urgent");
            }
            heap.offer(r);
        }
    }

    public void updatePriority(int reqId, int newP) {
        List<TripRequest> tmp = new ArrayList<>(heap);
        heap.clear();
        boolean found = false;
        for(TripRequest r : tmp) {
            if(r.getId() == reqId) {
                r.setPriority(newP);
                found = true;
                System.out.println("priority updated id=" + reqId + " -> " + newP);
            }
            heap.offer(r);
        }
        if(!found)
            System.out.println("id=" + reqId + " not found in queue");
    }

    public void removeExpired() {
        List<TripRequest> tmp = new ArrayList<>(heap);
        heap.clear();
        int count = 0;
        for(TripRequest r : tmp) {
            if(r.getCreatedAt().plusHours(EXPIRY_HOURS).isAfter(LocalDateTime.now())) {
                heap.offer(r);
            } else {
                count++;
                System.out.println("expired, removed id=" + r.getId());
            }
        }
        System.out.println("total expired removed=" + count);
    }

    public void loadFromDB() {
        TripRequestDAO dao = new TripRequestDAO();
        List<TripRequest> pending = dao.getPendingRequests();
        for(TripRequest r : pending)
            heap.offer(r);
        System.out.println("loaded " + pending.size() + " requests from db");
    }

    public void printQueue() {
        if(heap.isEmpty()) {
            System.out.println("queue is empty");
            return;
        }
        List<TripRequest> sorted = new ArrayList<>(heap);
        sorted.sort(Comparator.comparingInt(TripRequest::getPriority).reversed());

        System.out.println("\n===== DISPATCH QUEUE =====");
        System.out.printf("%-5s %-20s %-20s %-8s %-8s%n",
            "ID", "Pickup", "Drop", "Pax", "Priority");
        System.out.println("-".repeat(65));

        for(TripRequest r : sorted) {
            System.out.printf("%-5d %-20s %-20s %-8d %-8d%n",
                r.getId(),
                r.getPickup(),
                r.getDrop(),
                r.getPaxCount(),
                r.getPriority());
        }
        System.out.println("=".repeat(65));
    }
}