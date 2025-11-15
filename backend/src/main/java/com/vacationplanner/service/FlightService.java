package com.vacationplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vacationplanner.util.EnvLoader;
import com.vacationplanner.model.TripPlan;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service for handling flight-related operations using multiple APIs
 * Supports Amadeus API (paid) and AviationStack API (free)
 */
public class FlightService {

    // API configuration from environment variables
    private static final String AMADEUS_CLIENT_ID = EnvLoader.getEnv("AMADEUS_CLIENT_ID");
    private static final String AMADEUS_CLIENT_SECRET = EnvLoader.getEnv("AMADEUS_CLIENT_SECRET");
    private static final String AMADEUS_BASE_URL = EnvLoader.getEnv("AMADEUS_BASE_URL", "https://test.api.amadeus.com");
    
    // AviationStack API (Free alternative)
    private static final String AVIATIONSTACK_API_KEY = EnvLoader.getEnv("AVIATIONSTACK_API_KEY");
    private static final String AVIATIONSTACK_BASE_URL = EnvLoader.getEnv("AVIATIONSTACK_BASE_URL", "http://api.aviationstack.com/v1");
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> airportCodes;
    private final Map<String, Double> destinationBasePrices;
    private final Random random = ThreadLocalRandom.current();

    private String accessToken = null;
    private long tokenExpiryTime = 0;
    
    /**
     * Constructor for FlightService.
     * Initializes mock flight data and airport codes.
     */
    public FlightService() {
        // Initialize mock base prices for popular destinations
        destinationBasePrices = new HashMap<>();
        destinationBasePrices.put("Paris, France", 800.0);
        destinationBasePrices.put("London, UK", 750.0);
        destinationBasePrices.put("Tokyo, Japan", 1200.0);
        destinationBasePrices.put("New York City, USA", 500.0);
        destinationBasePrices.put("Sydney, Australia", 1500.0);
        destinationBasePrices.put("Rome, Italy", 850.0);
        destinationBasePrices.put("Barcelona, Spain", 780.0);
        destinationBasePrices.put("Bali, Indonesia", 1100.0);
        destinationBasePrices.put("Cancun, Mexico", 450.0);
        destinationBasePrices.put("Dubai, UAE", 950.0);
        destinationBasePrices.put("Phuket, Thailand", 900.0);
        destinationBasePrices.put("Santorini, Greece", 920.0);
        destinationBasePrices.put("Maldives", 1300.0);
        destinationBasePrices.put("Costa Rica", 600.0);
        destinationBasePrices.put("Queenstown, New Zealand", 1400.0);
        
        // Initialize airport codes mapping
        airportCodes = new HashMap<>();
        airportCodes.put("Paris, France", "CDG");
        airportCodes.put("London, UK", "LHR");
        airportCodes.put("Tokyo, Japan", "NRT");
        airportCodes.put("New York City, USA", "JFK");
        airportCodes.put("Sydney, Australia", "SYD");
        airportCodes.put("Rome, Italy", "FCO");
        airportCodes.put("Barcelona, Spain", "BCN");
        airportCodes.put("Bali, Indonesia", "DPS");
        airportCodes.put("Cancun, Mexico", "CUN");
        airportCodes.put("Dubai, UAE", "DXB");
        airportCodes.put("Phuket, Thailand", "HKT");
        airportCodes.put("Santorini, Greece", "JTR");
        airportCodes.put("Maldives", "MLE");
        airportCodes.put("Costa Rica", "SJO");
        airportCodes.put("Queenstown, New Zealand", "ZQN");
    }
    
    /**
     * Find the best flight option based on the provided criteria.
     * Enhanced version with real API integration and fallback to mock data.
     *
     * @param origin The departure location
     * @param destination The arrival location
     * @param departureDate The departure date
     * @param returnDate The return date
     * @param maxBudget The maximum budget for flights
     * @return A Transportation object with flight details
     */
    public TripPlan.Transportation findBestFlight(String origin, String destination, 
                                                LocalDate departureDate, LocalDate returnDate, 
                                                double maxBudget) {
        // Try real API first, fallback to mock data if API fails
        try {
            TripPlan.Transportation realFlight = searchFlightsWithAPI(origin, destination, departureDate, returnDate, maxBudget);
            if (realFlight != null) {
                return realFlight;
            }
        } catch (Exception e) {
            System.err.println("API call failed, using mock data: " + e.getMessage());
        }
        
        // Fallback to mock data generation
        
        // Get base price for the destination or use a default
        double basePrice = destinationBasePrices.getOrDefault(destination, 1000.0);
        
        // Adjust price based on advance booking (closer dates are more expensive)
        LocalDate today = LocalDate.now();
        int daysUntilDeparture = (int) (departureDate.toEpochDay() - today.toEpochDay());
        double advanceBookingFactor = Math.max(0.7, Math.min(1.5, 2.0 - (daysUntilDeparture / 30.0)));
        
        // Adjust price based on season (peak seasons are more expensive)
        int month = departureDate.getMonthValue();
        double seasonalFactor = (month >= 6 && month <= 8) || (month == 12) ? 1.3 : 1.0;
        
        // Calculate final price
        double flightPrice = basePrice * advanceBookingFactor * seasonalFactor;
        
        // Ensure price is within budget
        if (flightPrice > maxBudget) {
            flightPrice = maxBudget * 0.95; // Slightly under budget
        }
        
        // Create mock flight details
        String[] airlines = {"Delta Airlines", "United Airlines", "American Airlines", "British Airways", "Lufthansa", "Emirates", "Singapore Airlines"};
        String airline = airlines[random.nextInt(airlines.length)];
        
        // Create and return the transportation object
        return new TripPlan.Transportation(
                "Flight",
                origin,
                destination,
                departureDate,
                returnDate,
                airline,
                flightPrice
        );
    }
    
    /**
     * Get the estimated flight price for a destination.
     *
     * @param origin The departure location
     * @param destination The arrival location
     * @param departureDate The departure date
     * @return The estimated price
     */
    public double getEstimatedFlightPrice(String origin, String destination, LocalDate departureDate) {
        // Similar logic to findBestFlight but only returns the price
        double basePrice = destinationBasePrices.getOrDefault(destination, 1000.0);
        
        LocalDate today = LocalDate.now();
        int daysUntilDeparture = (int) (departureDate.toEpochDay() - today.toEpochDay());
        double advanceBookingFactor = Math.max(0.7, Math.min(1.5, 2.0 - (daysUntilDeparture / 30.0)));
        
        int month = departureDate.getMonthValue();
        double seasonalFactor = (month >= 6 && month <= 8) || (month == 12) ? 1.3 : 1.0;
        
        return basePrice * advanceBookingFactor * seasonalFactor;
    }

    /**
     * Alias method expected by tests.
     */
    public double estimateFlightPrice(String origin, String destination, LocalDate departureDate) {
        return getEstimatedFlightPrice(origin, destination, departureDate);
    }
    
    /**
     * Search flights using available APIs (AviationStack first, then Amadeus)
     */
    private TripPlan.Transportation searchFlightsWithAPI(String origin, String destination, 
                                                        LocalDate departureDate, LocalDate returnDate, 
                                                        double maxBudget) throws IOException, ParseException {
        
        // Try AviationStack API first (free)
        if (AVIATIONSTACK_API_KEY != null && !AVIATIONSTACK_API_KEY.isEmpty()) {
            try {
                TripPlan.Transportation flight = searchWithAviationStack(origin, destination, departureDate, returnDate, maxBudget);
                if (flight != null) {
                    return flight;
                }
            } catch (Exception e) {
                System.err.println("AviationStack API failed: " + e.getMessage());
            }
        }
        
        // Try Amadeus API as fallback (paid)
        if (AMADEUS_CLIENT_ID != null && !AMADEUS_CLIENT_ID.isEmpty()) {
            try {
                return searchWithAmadeus(origin, destination, departureDate, returnDate, maxBudget);
            } catch (Exception e) {
                System.err.println("Amadeus API failed: " + e.getMessage());
            }
        }
        
        return null; // All APIs failed, will fallback to mock data
    }
    
    /**
     * Search flights using AviationStack API (Free)
     */
    private TripPlan.Transportation searchWithAviationStack(String origin, String destination,
                                                           LocalDate departureDate, LocalDate returnDate,
                                                           double maxBudget) throws IOException {
        
        String originCode = getAirportCode(origin);
        String destinationCode = getAirportCode(destination);
        
        if (originCode == null || destinationCode == null) {
            return null;
        }
        
        // AviationStack provides flight schedules, we'll use it to get realistic flight data
        String url = AVIATIONSTACK_BASE_URL + "/flights?access_key=" + AVIATIONSTACK_API_KEY +
                    "&dep_iata=" + originCode + "&arr_iata=" + destinationCode + "&limit=5";
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            
            try (CloseableHttpResponse response = client.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (response.getCode() == 200) {
                    return parseAviationStackResponse(responseBody, origin, destination, departureDate, returnDate, maxBudget);
                }
            } catch (ParseException e) {
                System.err.println("Error parsing AviationStack API response: " + e.getMessage());
            }
        }
        
        return null;
    }
    
    /**
     * Search flights using Amadeus API (Paid)
     */
    private TripPlan.Transportation searchWithAmadeus(String origin, String destination, 
                                                     LocalDate departureDate, LocalDate returnDate, 
                                                     double maxBudget) throws IOException, ParseException {
        // Get access token first
        if (!isTokenValid()) {
            getAccessToken();
        }
        
        // Get airport codes
        String originCode = getAirportCode(origin);
        String destinationCode = getAirportCode(destination);
        
        if (originCode == null || destinationCode == null) {
            return null; // Fallback to mock data
        }
        
        // Build API request
        String url = AMADEUS_BASE_URL + "/v2/shopping/flight-offers?" +
                    "originLocationCode=" + originCode +
                    "&destinationLocationCode=" + destinationCode +
                    "&departureDate=" + departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                    "&returnDate=" + returnDate.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                    "&adults=1&max=5";
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("Authorization", "Bearer " + accessToken);
            
            try (CloseableHttpResponse response = client.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (response.getCode() == 200) {
                    return parseAmadeusResponse(responseBody, origin, destination, departureDate, returnDate, maxBudget);
                }
            } catch (ParseException e) {
                System.err.println("Error parsing Amadeus API response: " + e.getMessage());
            }
        }
        
        return null; // API call failed, will fallback to mock data
    }
    
    /**
     * Get access token from Amadeus API
     */
    private void getAccessToken() throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(AMADEUS_BASE_URL + "/v1/security/oauth2/token");
            
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "client_credentials"));
            params.add(new BasicNameValuePair("client_id", AMADEUS_CLIENT_ID));
            params.add(new BasicNameValuePair("client_secret", AMADEUS_CLIENT_SECRET));
            
            post.setEntity(new UrlEncodedFormEntity(params));
            
            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (response.getCode() == 200) {
                    JsonNode json = objectMapper.readTree(responseBody);
                    accessToken = json.get("access_token").asText();
                    int expiresIn = json.get("expires_in").asInt();
                    tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000L);
                }
            } catch (ParseException e) {
                System.err.println("Error parsing Amadeus token response: " + e.getMessage());
            }
        }
    }
    
    /**
     * Check if current access token is valid
     */
    private boolean isTokenValid() {
        return accessToken != null && System.currentTimeMillis() < tokenExpiryTime;
    }
    
    /**
     * Get airport code for a city/destination
     */
    private String getAirportCode(String location) {
        return airportCodes.get(location);
    }
    
    /**
     * Parse AviationStack API response
     */
    private TripPlan.Transportation parseAviationStackResponse(String responseBody, String origin, String destination,
                                                              LocalDate departureDate, LocalDate returnDate, double maxBudget) {
        try {
            JsonNode json = objectMapper.readTree(responseBody);
            JsonNode data = json.get("data");
            
            if (data != null && data.isArray() && data.size() > 0) {
                // Get first available flight for airline info
                JsonNode flight = data.get(0);
                String airline = "Unknown Airline";
                
                if (flight.has("airline") && flight.get("airline").has("name")) {
                    airline = flight.get("airline").get("name").asText();
                }
                
                // Generate realistic price based on route and mock data
                double basePrice = destinationBasePrices.getOrDefault(destination, 1000.0);
                double finalPrice = calculateRealisticPrice(basePrice, departureDate);
                
                if (finalPrice <= maxBudget) {
                    return new TripPlan.Transportation(
                        "Flight",
                        origin,
                        destination,
                        departureDate,
                        returnDate,
                        airline,
                        finalPrice
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing AviationStack response: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Parse Amadeus API response and return best option within budget
     */
    private TripPlan.Transportation parseAmadeusResponse(String responseBody, String origin, String destination,
                                                        LocalDate departureDate, LocalDate returnDate, double maxBudget) {
        try {
            JsonNode json = objectMapper.readTree(responseBody);
            JsonNode offers = json.get("data");
            
            if (offers != null && offers.isArray() && offers.size() > 0) {
                // Find the best offer within budget
                for (JsonNode offer : offers) {
                    JsonNode price = offer.get("price");
                    if (price != null) {
                        double totalPrice = price.get("total").asDouble();
                        
                        if (totalPrice <= maxBudget) {
                            // Extract airline information
                            String airline = "Unknown Airline";
                            JsonNode itineraries = offer.get("itineraries");
                            if (itineraries != null && itineraries.isArray() && itineraries.size() > 0) {
                                JsonNode segments = itineraries.get(0).get("segments");
                                if (segments != null && segments.isArray() && segments.size() > 0) {
                                    JsonNode carrierCode = segments.get(0).get("carrierCode");
                                    if (carrierCode != null) {
                                        airline = getAirlineName(carrierCode.asText());
                                    }
                                }
                            }
                            
                            return new TripPlan.Transportation(
                                "Flight",
                                origin,
                                destination,
                                departureDate,
                                returnDate,
                                airline,
                                totalPrice
                            );
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing flight response: " + e.getMessage());
        }
        
        return null; // No suitable flights found within budget
    }
    
    /**
     * Calculate realistic flight price with seasonal and advance booking factors
     */
    private double calculateRealisticPrice(double basePrice, LocalDate departureDate) {
        LocalDate today = LocalDate.now();
        int daysUntilDeparture = (int) (departureDate.toEpochDay() - today.toEpochDay());
        double advanceBookingFactor = Math.max(0.7, Math.min(1.5, 2.0 - (daysUntilDeparture / 30.0)));
        
        int month = departureDate.getMonthValue();
        double seasonalFactor = (month >= 6 && month <= 8) || (month == 12) ? 1.3 : 1.0;
        
        return basePrice * advanceBookingFactor * seasonalFactor;
    }
    
    /**
     * Convert airline code to airline name
     */
    private String getAirlineName(String carrierCode) {
        Map<String, String> airlines = new HashMap<>();
        airlines.put("AA", "American Airlines");
        airlines.put("DL", "Delta Airlines");
        airlines.put("UA", "United Airlines");
        airlines.put("BA", "British Airways");
        airlines.put("LH", "Lufthansa");
        airlines.put("EK", "Emirates");
        airlines.put("SQ", "Singapore Airlines");
        airlines.put("AF", "Air France");
        airlines.put("KL", "KLM");
        airlines.put("LX", "Swiss International");
        
        return airlines.getOrDefault(carrierCode, carrierCode + " Airlines");
    }
}