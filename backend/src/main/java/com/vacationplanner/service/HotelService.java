package com.vacationplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vacationplanner.model.TripPlan;
import com.vacationplanner.util.EnvLoader;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
// org.apache.hc.core5.http.NameValuePair not used
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
// org.apache.hc.core5.http.message.BasicNameValuePair not used
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Service for handling hotel-related operations using multiple APIs
 * Supports Booking.com API (paid) and Hotels.com RapidAPI (free)
 */
public class HotelService {

    // API configuration from environment variables
    private static final String BOOKING_API_KEY = EnvLoader.getEnv("BOOKING_API_KEY");
    private static final String BOOKING_BASE_URL = EnvLoader.getEnv("BOOKING_BASE_URL", "https://distribution-xml.booking.com");
    
    // Hotels.com RapidAPI (Free alternative)
    private static final String HOTELS_API_KEY = EnvLoader.getEnv("HOTELS_API_KEY");
    private static final String HOTELS_BASE_URL = EnvLoader.getEnv("HOTELS_BASE_URL", "https://hotels-com-provider.p.rapidapi.com");
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Integer> destinationIds;
    private final Map<String, Double> hotelBasePrices;
    
    // HTTP client for API calls (created per-request in helper methods)
    
    /**
     * Constructor for HotelService.
     * Initializes destination ID mappings and base prices for popular destinations.
     */
    public HotelService() {
        // Initialize destination ID mappings for Booking.com API
        destinationIds = new HashMap<>();
        destinationIds.put("Paris, France", -1456928);
        destinationIds.put("London, UK", -2601889);
        destinationIds.put("Tokyo, Japan", -246227);
        destinationIds.put("New York City, USA", 20088325);
        destinationIds.put("Sydney, Australia", -1603135);
        destinationIds.put("Rome, Italy", -126693);
        destinationIds.put("Queenstown, New Zealand", -2140479);
        
        // Initialize base hotel prices for mock data
        hotelBasePrices = new HashMap<>();
        hotelBasePrices.put("Paris, France", 150.0);
        hotelBasePrices.put("London, UK", 180.0);
        hotelBasePrices.put("Tokyo, Japan", 120.0);
        hotelBasePrices.put("New York City, USA", 200.0);
        hotelBasePrices.put("Sydney, Australia", 160.0);
        hotelBasePrices.put("Rome, Italy", 140.0);
        hotelBasePrices.put("Barcelona, Spain", 130.0);
        hotelBasePrices.put("Bali, Indonesia", 80.0);
        hotelBasePrices.put("Cancun, Mexico", 110.0);
        hotelBasePrices.put("Dubai, UAE", 250.0);
        hotelBasePrices.put("Phuket, Thailand", 90.0);
        hotelBasePrices.put("Santorini, Greece", 220.0);
        hotelBasePrices.put("Maldives", 400.0);
        hotelBasePrices.put("Costa Rica", 100.0);
        hotelBasePrices.put("Queenstown, New Zealand", 170.0);
        
    // Initialize JSON mapper for API calls (HTTP clients created per-request)
    }
    
    /**
     * Find the best hotel option based on the provided criteria.
     * Enhanced version with real API integration and fallback to mock data.
     *
     * @param destination The destination location
     * @param checkInDate The check-in date
     * @param checkOutDate The check-out date
     * @param maxBudgetPerNight The maximum budget per night
     * @param preferredCategory The preferred hotel category
     * @return An Accommodation object with hotel details
     */
    public TripPlan.Accommodation findBestHotel(String destination, LocalDate checkInDate, 
                                              LocalDate checkOutDate, double maxBudgetPerNight, 
                                              String preferredCategory) {
        // Try real API first, fallback to mock data if API fails
        try {
            TripPlan.Accommodation realHotel = searchHotelsWithAPI(destination, checkInDate, checkOutDate, maxBudgetPerNight, preferredCategory);
            if (realHotel != null) {
                return realHotel;
            }
        } catch (Exception e) {
            System.err.println("API call failed, using mock data: " + e.getMessage());
        }
        
        // Fallback to mock hotel data if API fails
        
        // Get hotels for the destination or create generic ones if not found
        Map<String, List<HotelInfo>> destinationHotels = new HashMap<>();
        List<HotelInfo> parisHotels = new ArrayList<>();
        parisHotels.add(new HotelInfo("Grand Hotel Paris", "Luxury", 350.0, 4.8, "City Center"));
        parisHotels.add(new HotelInfo("Eiffel View Inn", "Mid-range", 180.0, 4.2, "Near Eiffel Tower"));
        parisHotels.add(new HotelInfo("Paris Budget Stay", "Budget", 90.0, 3.5, "15th Arrondissement"));
        destinationHotels.put("Paris, France", parisHotels);
        
        List<HotelInfo> londonHotels = new ArrayList<>();
        londonHotels.add(new HotelInfo("The Savoy", "Luxury", 400.0, 4.9, "The Strand"));
        londonHotels.add(new HotelInfo("London Bridge Hotel", "Mid-range", 200.0, 4.3, "Near London Bridge"));
        londonHotels.add(new HotelInfo("Budget Inn London", "Budget", 100.0, 3.6, "Kensington"));
        destinationHotels.put("London, UK", londonHotels);
        
        List<HotelInfo> tokyoHotels = new ArrayList<>();
        tokyoHotels.add(new HotelInfo("Tokyo Luxury Palace", "Luxury", 380.0, 4.8, "Ginza"));
        tokyoHotels.add(new HotelInfo("Shinjuku Central Hotel", "Mid-range", 220.0, 4.4, "Shinjuku"));
        tokyoHotels.add(new HotelInfo("Tokyo Budget Pod", "Budget", 80.0, 3.7, "Asakusa"));
        destinationHotels.put("Tokyo, Japan", tokyoHotels);
        
        List<HotelInfo> nycHotels = new ArrayList<>();
        nycHotels.add(new HotelInfo("Plaza Hotel", "Luxury", 450.0, 4.7, "Central Park South"));
        nycHotels.add(new HotelInfo("Midtown Comfort Inn", "Mid-range", 250.0, 4.1, "Midtown Manhattan"));
        nycHotels.add(new HotelInfo("NYC Budget Stay", "Budget", 120.0, 3.4, "Queens"));
        destinationHotels.put("New York City, USA", nycHotels);
        
        List<HotelInfo> sydneyHotels = new ArrayList<>();
        sydneyHotels.add(new HotelInfo("Sydney Harbour View", "Luxury", 380.0, 4.8, "Circular Quay"));
        sydneyHotels.add(new HotelInfo("Bondi Beach Hotel", "Mid-range", 210.0, 4.3, "Bondi"));
        sydneyHotels.add(new HotelInfo("Sydney Budget Inn", "Budget", 95.0, 3.6, "Surry Hills"));
        destinationHotels.put("Sydney, Australia", sydneyHotels);
        
        List<HotelInfo> romeHotels = new ArrayList<>();
        romeHotels.add(new HotelInfo("Roman Luxury Suites", "Luxury", 320.0, 4.7, "Near Colosseum"));
        romeHotels.add(new HotelInfo("Trevi Fountain Inn", "Mid-range", 170.0, 4.2, "City Center"));
        romeHotels.add(new HotelInfo("Roma Budget Rooms", "Budget", 85.0, 3.5, "Termini Area"));
        destinationHotels.put("Rome, Italy", romeHotels);
        
        List<HotelInfo> hotels = destinationHotels.getOrDefault(destination, createGenericHotels());
        
        // Filter hotels by category and budget
        List<HotelInfo> suitableHotels = new ArrayList<>();
        long stayDuration = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        double dailyBudget = maxBudgetPerNight;
        
        for (HotelInfo hotel : hotels) {
            if ((preferredCategory == null || hotel.category.equalsIgnoreCase(preferredCategory)) 
                    && hotel.pricePerNight <= dailyBudget) {
                suitableHotels.add(hotel);
            }
        }
        
        // If no suitable hotels found, take the cheapest option
        HotelInfo selectedHotel;
        if (suitableHotels.isEmpty()) {
            selectedHotel = hotels.stream()
                    .min((h1, h2) -> Double.compare(h1.pricePerNight, h2.pricePerNight))
                    .orElse(hotels.get(0));
        } else {
            // Select the hotel with the best rating among suitable options
            selectedHotel = suitableHotels.stream()
                    .max((h1, h2) -> Double.compare(h1.rating, h2.rating))
                    .orElse(suitableHotels.get(0));
        }
        
    // Create and return the accommodation object
        TripPlan.Accommodation accommodation = new TripPlan.Accommodation(
                selectedHotel.name,
                "Hotel",
                destination,
                selectedHotel.pricePerNight,
                selectedHotel.rating,
                "Mock Hotel Provider"
        );
        accommodation.setCheckInDate(checkInDate);
        accommodation.setCheckOutDate(checkOutDate);
        accommodation.setNights((int) stayDuration);
        return accommodation;
    }

    /**
     * Overload used by tests: accepts group size and total budget for stay.
     * Splits total budget into per-night budget and infers a category.
     */
    public TripPlan.Accommodation findBestHotel(String destination,
                                                LocalDate checkInDate,
                                                LocalDate checkOutDate,
                                                int groupSize,
                                                double maxTotalBudget) {
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (nights <= 0) nights = 1;
        double maxPerNight = maxTotalBudget / nights;
        String inferredCategory = groupSize >= 4 ? "Mid-range" : "Budget";
        return findBestHotel(destination, checkInDate, checkOutDate, maxPerNight, inferredCategory);
    }
    
    /**
     * Calculate realistic hotel price with seasonal and category factors
     */
    private double calculateRealisticHotelPrice(double basePrice, String category, LocalDate checkInDate) {
        // Category adjustment
        double categoryFactor = 1.0;
        switch (category.toLowerCase()) {
            case "luxury":
                categoryFactor = 2.0;
                break;
            case "mid-range":
                categoryFactor = 1.0;
                break;
            case "budget":
                categoryFactor = 0.6;
                break;
        }
        
        // Seasonal adjustment
        int month = checkInDate.getMonthValue();
        double seasonalFactor = (month >= 6 && month <= 8) || (month == 12) ? 1.4 : 1.0;
        
        return basePrice * categoryFactor * seasonalFactor;
    }
    
    /**
     * Search hotels using available APIs (Hotels.com first, then Booking.com)
     */
    private TripPlan.Accommodation searchHotelsWithAPI(String destination, LocalDate checkInDate, 
                                                      LocalDate checkOutDate, double maxBudgetPerNight, 
                                                      String preferredCategory) throws IOException {
        
        // Try Hotels.com RapidAPI first (free)
        if (HOTELS_API_KEY != null && !HOTELS_API_KEY.isEmpty()) {
            try {
                TripPlan.Accommodation hotel = searchWithHotelsAPI(destination, checkInDate, checkOutDate, maxBudgetPerNight, preferredCategory);
                if (hotel != null) {
                    return hotel;
                }
            } catch (Exception e) {
                System.err.println("Hotels.com API failed: " + e.getMessage());
            }
        }
        
        // Try Booking.com API as fallback (paid)
        if (BOOKING_API_KEY != null && !BOOKING_API_KEY.isEmpty()) {
            try {
                return searchWithBookingAPI(destination, checkInDate, checkOutDate, maxBudgetPerNight, preferredCategory);
            } catch (Exception e) {
                System.err.println("Booking.com API failed: " + e.getMessage());
            }
        }
        
        return null; // All APIs failed, will fallback to mock data
    }
    
    /**
     * Search hotels using Hotels.com RapidAPI (Free)
     */
    private TripPlan.Accommodation searchWithHotelsAPI(String destination, LocalDate checkInDate,
                                                       LocalDate checkOutDate, double maxBudgetPerNight,
                                                       String preferredCategory) throws IOException {
        
        // Hotels.com API endpoint for hotel search
        String url = HOTELS_BASE_URL + "/v1/hotels/search?" +
                    "destination=" + destination.replace(" ", "%20") +
                    "&checkin=" + checkInDate +
                    "&checkout=" + checkOutDate +
                    "&adults=2" +
                    "&rooms=1";
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("X-RapidAPI-Key", HOTELS_API_KEY);
            request.setHeader("X-RapidAPI-Host", "hotels-com-provider.p.rapidapi.com");
            
            try (CloseableHttpResponse response = client.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (response.getCode() == 200) {
                    return parseHotelsAPIResponse(responseBody, destination, checkInDate, checkOutDate, maxBudgetPerNight, preferredCategory);
                }
            } catch (ParseException e) {
                System.err.println("Error parsing Hotels.com API response: " + e.getMessage());
            }
        }
        
        return null;
    }
    
    /**
     * Search hotels using Booking.com API (Paid)
     */
    private TripPlan.Accommodation searchWithBookingAPI(String destination, LocalDate checkInDate, 
                                                        LocalDate checkOutDate, double maxBudgetPerNight, 
                                                        String preferredCategory) throws IOException {
        Integer destinationId = destinationIds.get(destination);
        if (destinationId == null) {
            return null; // Fallback to mock data
        }
        
        // Build API request URL
        String url = BOOKING_BASE_URL + "/v1/hotels/search?" +
                    "dest_id=" + destinationId +
                    "&checkin_date=" + checkInDate +
                    "&checkout_date=" + checkOutDate +
                    "&adults_number=2" +
                    "&room_number=1" +
                    "&units=metric" +
                    "&temperature_unit=c";
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("X-RapidAPI-Key", BOOKING_API_KEY);
            request.setHeader("X-RapidAPI-Host", "booking-com.p.rapidapi.com");
            
            try (CloseableHttpResponse response = client.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (response.getCode() == 200) {
                    return parseBookingAPIResponse(responseBody, destination, checkInDate, checkOutDate, maxBudgetPerNight, preferredCategory);
                }
            } catch (ParseException e) {
                System.err.println("Error parsing Booking.com API response: " + e.getMessage());
            }
        }
        
        return null; // API call failed, will fallback to mock data
    }
    
    /**
     * Parse Hotels.com API response
     */
    private TripPlan.Accommodation parseHotelsAPIResponse(String responseBody, String destination,
                                                          LocalDate checkInDate, LocalDate checkOutDate,
                                                          double maxBudgetPerNight, String preferredCategory) {
        try {
            JsonNode json = objectMapper.readTree(responseBody);
            JsonNode hotels = json.get("data");
            
            if (hotels != null && hotels.isArray() && hotels.size() > 0) {
                // Find first hotel within budget
                for (JsonNode hotel : hotels) {
                    String hotelName = "Unknown Hotel";
                    if (hotel.has("name")) {
                        hotelName = hotel.get("name").asText();
                    }
                    
                    // Generate realistic price based on destination and category
                    double basePrice = hotelBasePrices.getOrDefault(destination, 150.0);
                    double finalPrice = calculateRealisticHotelPrice(basePrice, preferredCategory, checkInDate);
                    
                    if (finalPrice <= maxBudgetPerNight) {
                        TripPlan.Accommodation accommodation = new TripPlan.Accommodation(
                            hotelName,
                            "Hotel",
                            destination,
                            finalPrice,
                            4.0, // Default rating
                            preferredCategory
                        );
                        accommodation.setCheckInDate(checkInDate);
                        accommodation.setCheckOutDate(checkOutDate);
                        return accommodation;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing Hotels.com response: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Parse Booking.com API response and return best option within budget
     */
    private TripPlan.Accommodation parseBookingAPIResponse(String responseBody, String destination,
                                                           LocalDate checkInDate, LocalDate checkOutDate,
                                                           double maxBudgetPerNight, String preferredCategory) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode results = root.path("result");
            
            if (results.isArray() && results.size() > 0) {
                // Find first hotel within budget
                for (JsonNode hotel : results) {
                    JsonNode priceBreakdown = hotel.path("price_breakdown");
                    double pricePerNight = priceBreakdown.path("gross_price").asDouble();
                    
                    if (pricePerNight <= maxBudgetPerNight) {
                        String hotelName = hotel.path("hotel_name").asText();
                        String address = hotel.path("address").asText();
                        double rating = hotel.path("review_score").asDouble();
                        
                        String type = preferredCategory != null && !preferredCategory.isEmpty() ? preferredCategory : "Hotel";
                        TripPlan.Accommodation accommodation = new TripPlan.Accommodation(
                            hotelName,
                            type,
                            address,
                            pricePerNight,
                            rating,
                            "Booking.com - " + destination
                        );
                        accommodation.setCheckInDate(checkInDate);
                        accommodation.setCheckOutDate(checkOutDate);
                        accommodation.setNights((int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate));
                        return accommodation;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing Booking.com response: " + e.getMessage());
        }
        
        return null; // No suitable hotels found within budget
    }
    
    /**
     * Get the estimated accommodation price for a destination.
     *
     * @param destination The destination location
     * @param category The hotel category (Luxury, Mid-range, Budget)
     * @param nights The number of nights
     * @return The estimated total price
     */
    public double getEstimatedAccommodationPrice(String destination, String category, int nights) {
        double basePrice = hotelBasePrices.getOrDefault(destination, 150.0);
        double categoryFactor = 1.0;
        
        switch (category.toLowerCase()) {
            case "luxury":
                categoryFactor = 2.0;
                break;
            case "mid-range":
                categoryFactor = 1.0;
                break;
            case "budget":
                categoryFactor = 0.6;
                break;
        }
        
        return basePrice * categoryFactor * nights;
    }

    /**
     * Overload expected by tests to estimate by dates and group size.
     */
    public double estimateAccommodationPrice(String destination, LocalDate checkInDate,
                                             LocalDate checkOutDate, int groupSize) {
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (nights <= 0) nights = 1;
        // Assume mid-range default category and slight adjustment by group size
        double perNight = hotelBasePrices.getOrDefault(destination, 150.0);
        double groupFactor = Math.max(1.0, 1.0 + (groupSize - 2) * 0.1);
        return perNight * groupFactor * nights;
    }
    
    /**
     * Create generic hotels for destinations not in our predefined list
     */
    private List<HotelInfo> createGenericHotels() {
        List<HotelInfo> hotels = new ArrayList<>();
        hotels.add(new HotelInfo("Luxury Resort", "Luxury", 300.0, 4.5, "City Center"));
        hotels.add(new HotelInfo("Comfort Inn", "Mid-range", 150.0, 4.0, "Downtown"));
        hotels.add(new HotelInfo("Budget Lodge", "Budget", 80.0, 3.5, "Outskirts"));
        return hotels;
    }
    
    /**
     * Inner class to represent hotel information
     */
    private static class HotelInfo {
        String name;
        String category;
        double pricePerNight;
        double rating;
        String location;
        
        HotelInfo(String name, String category, double pricePerNight, double rating, String location) {
            this.name = name;
            this.category = category;
            this.pricePerNight = pricePerNight;
            this.rating = rating;
            this.location = location;
        }
    }
}