import java.util.*;

/**
 * ============================================================
 * CLASS - InvalidBookingException
 * ============================================================
 */
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

/**
 * ============================================================
 * CLASS - RoomInventory (Minimal for validation)
 * ============================================================
 */
class RoomInventory {

    private Set<String> validRoomTypes;

    public RoomInventory() {
        validRoomTypes = new HashSet<>();
        validRoomTypes.add("Single");
        validRoomTypes.add("Double");
        validRoomTypes.add("Suite");
    }

    public boolean isValidRoomType(String roomType) {
        return validRoomTypes.contains(roomType);
    }
}

/**
 * ============================================================
 * CLASS - BookingRequestQueue (Dummy for structure)
 * ============================================================
 */
class BookingRequestQueue {
    public void addRequest(String guestName, String roomType) {
        // No logic needed for this use case
    }
}

/**
 * ============================================================
 * CLASS - ReservationValidator
 * ============================================================
 */
class ReservationValidator {

    public void validate(
            String guestName,
            String roomType,
            RoomInventory inventory
    ) throws InvalidBookingException {

        // Validate guest name
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        // Validate room type
        if (!inventory.isValidRoomType(roomType)) {
            throw new InvalidBookingException("Invalid room type selected.");
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

        System.out.println("Booking Validation");

        Scanner scanner = new Scanner(System.in);

        RoomInventory inventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        try {
            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter room type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();

            // VALIDATION STEP
            validator.validate(guestName, roomType, inventory);

            // If valid → add to queue
            bookingQueue.addRequest(guestName, roomType);

            System.out.println("Booking request accepted.");

        } catch (InvalidBookingException e) {

            System.out.println("Booking failed: " + e.getMessage());

        } finally {
            scanner.close();
        }
    }
}