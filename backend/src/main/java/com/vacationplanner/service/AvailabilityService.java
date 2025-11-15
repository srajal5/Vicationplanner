package com.vacationplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vacationplanner.model.TripPlan;
import com.vacationplanner.util.EnvLoader;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service for checking real-time availability of flights and hotels.
 * Provides asynchronous availability checking with status callbacks.
 */
public class AvailabilityService {
    
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;
    
    // API Configuration from environment variables
    // Keep only the API key; client id/secret are not used in the current implementation
    private static final String AMADEUS_API_KEY = EnvLoader.getEnv("AMADEUS_API_KEY");
    private static final String AMADEUS_BASE_URL = EnvLoader.getEnv("AMADEUS_BASE_URL", "https://test.api.amadeus.com");
    
    private static final String BOOKING_API_KEY = EnvLoader.getEnv("BOOKING_API_KEY");
    private static final String BOOKING_BASE_URL = EnvLoader.getEnv("BOOKING_BASE_URL", "https://booking-com.p.rapidapi.com/v1");
    
    /**
     * Availability status enumeration
     */
    public enum AvailabilityStatus {
        CHECKING,
        AVAILABLE,
        LIMITED,
        UNAVAILABLE,
        ERROR
    }
    
    /**
     * Availability result class
     */
    public static class AvailabilityResult {
        private final AvailabilityStatus status;
        private final String message;
        private final int availableCount;
        private final LocalDateTime lastChecked;
        
        public AvailabilityResult(AvailabilityStatus status, String message, int availableCount) {
            this.status = status;
            this.message = message;
            this.availableCount = availableCount;
            this.lastChecked = LocalDateTime.now();
        }
        
        public AvailabilityStatus getStatus() { return status; }
        public String getMessage() { return message; }
        public int getAvailableCount() { return availableCount; }
        public LocalDateTime getLastChecked() { return lastChecked; }
    }
    
    /**
     * Callback interface for availability updates
     */
    public interface AvailabilityCallback {
        void onFlightAvailabilityUpdate(AvailabilityResult result);
        void onHotelAvailabilityUpdate(AvailabilityResult result);
    }
    
    /**
     * Constructor for AvailabilityService
     */
    public AvailabilityService() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
        this.executorService = Executors.newFixedThreadPool(4);
    }
    
    /**
     * Check real-time availability for a complete trip plan
     * 
     * @param tripPlan The trip plan to check availability for
     * @param callback Callback for receiving availability updates
     */
    public void checkTripAvailability(TripPlan tripPlan, AvailabilityCallback callback) {
        // Check flight availability asynchronously
        checkFlightAvailabilityAsync(tripPlan, callback);
        
        // Check hotel availability asynchronously
        checkHotelAvailabilityAsync(tripPlan, callback);
    }
    
    /**
     * Check flight availability asynchronously
     * 
     * @param tripPlan The trip plan containing flight information
     * @param callback Callback for receiving flight availability updates
     */
    public void checkFlightAvailabilityAsync(TripPlan tripPlan, AvailabilityCallback callback) {
        CompletableFuture.supplyAsync(() -> {
            callback.onFlightAvailabilityUpdate(new AvailabilityResult(
                AvailabilityStatus.CHECKING, "Checking flight availability...", 0));
            
            return checkFlightAvailability(tripPlan);
        }, executorService).thenAccept(callback::onFlightAvailabilityUpdate);
    }
    
    /**
     * Check hotel availability asynchronously
     * 
     * @param tripPlan The trip plan containing hotel information
     * @param callback Callback for receiving hotel availability updates
     */
    public void checkHotelAvailabilityAsync(TripPlan tripPlan, AvailabilityCallback callback) {
        CompletableFuture.supplyAsync(() -> {
            callback.onHotelAvailabilityUpdate(new AvailabilityResult(
                AvailabilityStatus.CHECKING, "Checking hotel availability...", 0));
            
            return checkHotelAvailability(tripPlan);
        }, executorService).thenAccept(callback::onHotelAvailabilityUpdate);
    }
    
    /**
     * Check flight availability synchronously
     * 
     * @param tripPlan The trip plan containing flight information
     * @return AvailabilityResult with flight availability status
     */
    private AvailabilityResult checkFlightAvailability(TripPlan tripPlan) {
        if (AMADEUS_API_KEY != null && !AMADEUS_API_KEY.isEmpty() && !"YOUR_AMADEUS_API_KEY".equals(AMADEUS_API_KEY)) {
            try {
                return checkFlightAvailabilityWithAPI(tripPlan);
            } catch (Exception e) {
                System.err.println("Flight availability check failed: " + e.getMessage());
                return new AvailabilityResult(AvailabilityStatus.ERROR, 
                    "Unable to check flight availability: " + e.getMessage(), 0);
            }
        } else {
            // Mock availability check when no API key is configured
            return mockFlightAvailability(tripPlan);
        }
    }
    
    /**
     * Check hotel availability synchronously
     * 
     * @param tripPlan The trip plan containing hotel information
     * @return AvailabilityResult with hotel availability status
     */
    private AvailabilityResult checkHotelAvailability(TripPlan tripPlan) {
        if (!"YOUR_BOOKING_API_KEY".equals(BOOKING_API_KEY)) {
            try {
                return checkHotelAvailabilityWithAPI(tripPlan);
            } catch (Exception e) {
                System.err.println("Hotel availability check failed: " + e.getMessage());
                return new AvailabilityResult(AvailabilityStatus.ERROR, 
                    "Unable to check hotel availability: " + e.getMessage(), 0);
            }
        } else {
            // Mock availability check when no API key is configured
            return mockHotelAvailability(tripPlan);
        }
    }
    
    /**
     * Check flight availability using Amadeus API
     * 
     * @param tripPlan The trip plan containing flight information
     * @return AvailabilityResult with real flight availability
     * @throws IOException If API call fails
     */
    private AvailabilityResult checkFlightAvailabilityWithAPI(TripPlan tripPlan) throws IOException {
        TripPlan.Transportation transport = tripPlan.getTransportation();
        if (transport == null) {
            return new AvailabilityResult(AvailabilityStatus.ERROR, "No transportation information", 0);
        }
        
        // Format dates for API
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String departureDate = transport.getDepartureDate().format(formatter);
        
        // Build API URL for flight availability check
        String url = AMADEUS_BASE_URL + "/shopping/flight-offers" +
                "?originLocationCode=" + getAirportCode(transport.getOrigin()) +
                "&destinationLocationCode=" + getAirportCode(transport.getDestination()) +
                "&departureDate=" + departureDate +
                "&adults=1" +
                "&max=10";
        
        HttpGet request = new HttpGet(url);
        request.addHeader("Authorization", "Bearer " + AMADEUS_API_KEY);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            try {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (response.getCode() == 200) {
                    return parseFlightAvailabilityResponse(responseBody);
                } else {
                    return new AvailabilityResult(AvailabilityStatus.ERROR, 
                        "API returned error: " + response.getCode(), 0);
                }
            } catch (ParseException e) {
                return new AvailabilityResult(AvailabilityStatus.ERROR, 
                    "Error parsing flight availability response: " + e.getMessage(), 0);
            }
        }
    }
    
    /**
     * Check hotel availability using Booking.com API
     * 
     * @param tripPlan The trip plan containing hotel information
     * @return AvailabilityResult with real hotel availability
     * @throws IOException If API call fails
     */
    private AvailabilityResult checkHotelAvailabilityWithAPI(TripPlan tripPlan) throws IOException {
        TripPlan.Accommodation accommodation = tripPlan.getAccommodation();
        if (accommodation == null) {
            return new AvailabilityResult(AvailabilityStatus.ERROR, "No accommodation information", 0);
        }
        
        // Format dates for API
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String checkIn = accommodation.getCheckInDate().format(formatter);
        String checkOut = accommodation.getCheckOutDate().format(formatter);
        
        // Build API URL for hotel availability check
        String url = BOOKING_BASE_URL + "/hotels/search" +
                "?dest_id=" + getDestinationId(tripPlan.getDestination()) +
                "&checkin_date=" + checkIn +
                "&checkout_date=" + checkOut +
                "&adults_number=2" +
                "&room_number=1" +
                "&order_by=popularity";
        
        HttpGet request = new HttpGet(url);
        request.addHeader("X-RapidAPI-Key", BOOKING_API_KEY);
        request.addHeader("X-RapidAPI-Host", "booking-com.p.rapidapi.com");
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            try {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (response.getCode() == 200) {
                    return parseHotelAvailabilityResponse(responseBody);
                } else {
                    return new AvailabilityResult(AvailabilityStatus.ERROR, 
                        "API returned error: " + response.getCode(), 0);
                }
            } catch (ParseException e) {
                return new AvailabilityResult(AvailabilityStatus.ERROR, 
                    "Error parsing hotel availability response: " + e.getMessage(), 0);
            }
        }
    }
    
    /**
     * Parse flight availability response from Amadeus API
     * 
     * @param responseBody JSON response from API
     * @return AvailabilityResult with parsed flight availability
     */
    private AvailabilityResult parseFlightAvailabilityResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode data = root.path("data");
            
            if (data.isArray()) {
                int availableFlights = data.size();
                
                if (availableFlights == 0) {
                    return new AvailabilityResult(AvailabilityStatus.UNAVAILABLE, 
                        "No flights available for selected dates", 0);
                } else if (availableFlights < 5) {
                    return new AvailabilityResult(AvailabilityStatus.LIMITED, 
                        "Limited flights available (" + availableFlights + " options)", availableFlights);
                } else {
                    return new AvailabilityResult(AvailabilityStatus.AVAILABLE, 
                        "Multiple flights available (" + availableFlights + " options)", availableFlights);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing flight availability response: " + e.getMessage());
        }
        
        return new AvailabilityResult(AvailabilityStatus.ERROR, "Unable to parse availability data", 0);
    }
    
    /**
     * Parse hotel availability response from Booking.com API
     * 
     * @param responseBody JSON response from API
     * @return AvailabilityResult with parsed hotel availability
     */
    private AvailabilityResult parseHotelAvailabilityResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode result = root.path("result");
            
            if (result.isArray()) {
                int availableHotels = result.size();
                
                if (availableHotels == 0) {
                    return new AvailabilityResult(AvailabilityStatus.UNAVAILABLE, 
                        "No hotels available for selected dates", 0);
                } else if (availableHotels < 10) {
                    return new AvailabilityResult(AvailabilityStatus.LIMITED, 
                        "Limited hotels available (" + availableHotels + " options)", availableHotels);
                } else {
                    return new AvailabilityResult(AvailabilityStatus.AVAILABLE, 
                        "Multiple hotels available (" + availableHotels + " options)", availableHotels);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing hotel availability response: " + e.getMessage());
        }
        
        return new AvailabilityResult(AvailabilityStatus.ERROR, "Unable to parse availability data", 0);
    }
    
    /**
     * Mock flight availability for testing without API keys
     * 
     * @param tripPlan The trip plan
     * @return Mock AvailabilityResult for flights
     */
    private AvailabilityResult mockFlightAvailability(TripPlan tripPlan) {
        // Simulate API delay
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Mock availability based on destination popularity
        String destination = tripPlan.getDestination().toLowerCase();
        if (destination.contains("paris") || destination.contains("london") || destination.contains("tokyo")) {
            return new AvailabilityResult(AvailabilityStatus.AVAILABLE, 
                "Multiple flights available (Mock: 15+ options)", 15);
        } else if (destination.contains("sydney") || destination.contains("rome")) {
            return new AvailabilityResult(AvailabilityStatus.LIMITED, 
                "Limited flights available (Mock: 3 options)", 3);
        } else {
            return new AvailabilityResult(AvailabilityStatus.AVAILABLE, 
                "Flights available (Mock: 8 options)", 8);
        }
    }
    
    /**
     * Mock hotel availability for testing without API keys
     * 
     * @param tripPlan The trip plan
     * @return Mock AvailabilityResult for hotels
     */
    private AvailabilityResult mockHotelAvailability(TripPlan tripPlan) {
        // Simulate API delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Mock availability based on season and destination
        LocalDate checkIn = tripPlan.getAccommodation() != null ? 
            tripPlan.getAccommodation().getCheckInDate() : LocalDate.now();
        
        // Summer months (June-August) have limited availability
        if (checkIn.getMonthValue() >= 6 && checkIn.getMonthValue() <= 8) {
            return new AvailabilityResult(AvailabilityStatus.LIMITED, 
                "Limited hotels available during peak season (Mock: 5 options)", 5);
        } else {
            return new AvailabilityResult(AvailabilityStatus.AVAILABLE, 
                "Multiple hotels available (Mock: 25+ options)", 25);
        }
    }
    
    /**
     * Get airport code for a city (simplified mapping)
     * 
     * @param city The city name
     * @return Airport code
     */
    private String getAirportCode(String city) {
        // This would typically use a comprehensive airport database
        if (city.toLowerCase().contains("paris")) return "CDG";
        if (city.toLowerCase().contains("london")) return "LHR";
        if (city.toLowerCase().contains("tokyo")) return "NRT";
        if (city.toLowerCase().contains("new york")) return "JFK";
        if (city.toLowerCase().contains("sydney")) return "SYD";
        if (city.toLowerCase().contains("rome")) return "FCO";
        return "CDG"; // Default
    }
    
    /**
     * Get destination ID for Booking.com API (simplified mapping)
     * 
     * @param destination The destination name
     * @return Destination ID
     */
    private String getDestinationId(String destination) {
        // This would typically use Booking.com's location API
        if (destination.toLowerCase().contains("paris")) return "-1456928";
        if (destination.toLowerCase().contains("london")) return "-2601889";
        if (destination.toLowerCase().contains("tokyo")) return "-246227";
        if (destination.toLowerCase().contains("new york")) return "20088325";
        if (destination.toLowerCase().contains("sydney")) return "-1603135";
        if (destination.toLowerCase().contains("rome")) return "-126693";
        return "-1456928"; // Default to Paris
    }
    
    /**
     * Shutdown the executor service
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            httpClient.close();
        } catch (IOException e) {
            System.err.println("Error closing HTTP client: " + e.getMessage());
        }
    }
}
