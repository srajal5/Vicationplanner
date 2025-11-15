package com.vacationplanner.service;

import org.springframework.stereotype.Service;
import com.vacationplanner.model.TripPlan;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service

/**
 * Service for handling bookings (flight + hotel) for a trip.
 * This implementation uses mock booking with confirmation numbers and
 * is structured to be extended with real API calls later.
 */
public class BookingService {

    public static class BookingResult {
        private final boolean success;
        private final String flightConfirmation;
        private final String hotelConfirmation;
        private final String message;
        private final LocalDateTime timestamp;

        public BookingResult(boolean success, String flightConfirmation, String hotelConfirmation, String message) {
            this.success = success;
            this.flightConfirmation = flightConfirmation;
            this.hotelConfirmation = hotelConfirmation;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }

        public boolean isSuccess() { return success; }
        public String getFlightConfirmation() { return flightConfirmation; }
        public String getHotelConfirmation() { return hotelConfirmation; }
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }

    public Map<String, Object> checkAvailability(String tripId) {
        Map<String, Object> availability = new HashMap<>();
        // Mock implementation - in a real system, this would check real availability
        availability.put("isAvailable", true);
        availability.put("message", "Trip is available for booking");
        availability.put("lastChecked", LocalDateTime.now().toString());
        return availability;
    }

    /**
     * Book the entire trip (flight + hotel). This is a mock implementation that
     * generates confirmation numbers and returns success.
     */
    public BookingResult bookTrip(TripPlan tripPlan, String travelerName, String email, String phone, String paymentLast4) {
        if (tripPlan == null) {
            return new BookingResult(false, null, null, "No trip plan available to book.");
        }

        // In a real implementation, call the appropriate APIs here and persist records.
        String flightConf = bookFlight(tripPlan);
        String hotelConf = bookHotel(tripPlan);

        String msg = String.format("Booking confirmed for %s. Flight: %s, Hotel: %s. Payment ****%s",
                travelerName != null ? travelerName : "Traveler",
                flightConf,
                hotelConf,
                paymentLast4 != null ? paymentLast4 : "0000");

        return new BookingResult(true, flightConf, hotelConf, msg);
    }

    /**
     * Mock flight booking.
     */
    public String bookFlight(TripPlan tripPlan) {
        return "FL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Mock hotel booking.
     */
    public String bookHotel(TripPlan tripPlan) {
        return "HT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
