import java.util.*;

/**
 * ============================================================
 * CLASS - Reservation
 * ============================================================
 */
class Reservation {
    String guestName;
    String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

/**
 * ============================================================
 * CLASS - BookingRequestQueue
 * ============================================================
 */
class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.add(r);
    }

    public Reservation getRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

/**
 * ============================================================
 * CLASS - RoomInventory
 * ============================================================
 */
class RoomInventory {

    private Map<String, Integer> rooms = new HashMap<>();

    public RoomInventory() {
        rooms.put("Single", 5);
        rooms.put("Double", 3);
        rooms.put("Suite", 2);
    }

    public boolean allocate(String roomType) {
        int count = rooms.getOrDefault(roomType, 0);
        if (count > 0) {
            rooms.put(roomType, count - 1);
            return true;
        }
        return false;
    }

    public Map<String, Integer> getRooms() {
        return rooms;
    }
}

/**
 * ============================================================
 * CLASS - RoomAllocationService
 * ============================================================
 */
class RoomAllocationService {

    private Map<String, Integer> counters = new HashMap<>();

    public String allocateRoom(Reservation r, RoomInventory inventory) {

        if (!inventory.allocate(r.roomType)) {
            return null;
        }

        int num = counters.getOrDefault(r.roomType, 0) + 1;
        counters.put(r.roomType, num);

        return r.roomType + "-" + num;
    }
}

/**
 * ============================================================
 * CLASS - ConcurrentBookingProcessor
 * ============================================================
 */
class ConcurrentBookingProcessor implements Runnable {

    private BookingRequestQueue bookingQueue;
    private RoomInventory inventory;
    private RoomAllocationService allocationService;

    public ConcurrentBookingProcessor(
            BookingRequestQueue bookingQueue,
            RoomInventory inventory,
            RoomAllocationService allocationService
    ) {
        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.allocationService = allocationService;
    }

    @Override
    public void run() {

        while (true) {

            Reservation reservation;

            // 🔒 Synchronize queue access
            synchronized (bookingQueue) {
                if (bookingQueue.isEmpty()) {
                    break;
                }
                reservation = bookingQueue.getRequest();
            }

            // 🔒 Synchronize inventory allocation
            synchronized (inventory) {
                String roomId = allocationService.allocateRoom(reservation, inventory);

                if (roomId != null) {
                    System.out.println("Booking confirmed for Guest: " +
                            reservation.guestName +
                            ", Room ID: " + roomId);
                }
            }
        }
    }
}

/**
 * ============================================================
 * MAIN CLASS - BookMyStayApp ✅
 * ============================================================
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Concurrent Booking Simulation");

        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();

        // Add booking requests
        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Double"));
        bookingQueue.addRequest(new Reservation("Kural", "Suite"));
        bookingQueue.addRequest(new Reservation("Subha", "Single"));

        // Create threads
        Thread t1 = new Thread(
                new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService)
        );

        Thread t2 = new Thread(
                new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService)
        );

        // Start threads
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        // Print remaining inventory
        System.out.println("\nRemaining Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.getRooms().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}