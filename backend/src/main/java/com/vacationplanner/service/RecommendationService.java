package com.vacationplanner.service;

import com.vacationplanner.model.TripPreferences;

import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for recommending destinations based on user preferences.
 * Uses a rule-based approach to match preferences to suitable destinations.
 */
public class RecommendationService {

    // Maps themes to suitable destinations
    private final Map<String, String[]> themeDestinationMap;
    
    // Maps months to suitable destinations for seasonal recommendations
    private final Map<Month, String[]> seasonalDestinationMap;
    
    /**
     * Constructor for RecommendationService.
     * Initializes the recommendation data.
     */
    public RecommendationService() {
        // Initialize theme-based destination recommendations
        themeDestinationMap = new HashMap<>();
        themeDestinationMap.put("Adventure", new String[]{
                "Queenstown, New Zealand", 
                "Interlaken, Switzerland", 
                "Moab, Utah, USA", 
                "Costa Rica", 
                "Nepal"
        });
        
        themeDestinationMap.put("Relaxation", new String[]{
                "Bali, Indonesia", 
                "Maldives", 
                "Santorini, Greece", 
                "Tulum, Mexico", 
                "Seychelles"
        });
        
        themeDestinationMap.put("Beach", new String[]{
                "Phuket, Thailand", 
                "Cancun, Mexico", 
                "Bora Bora, French Polynesia", 
                "Amalfi Coast, Italy", 
                "Gold Coast, Australia"
        });
        
        themeDestinationMap.put("Nature", new String[]{
                "Banff National Park, Canada", 
                "Patagonia, Argentina/Chile", 
                "Serengeti, Tanzania", 
                "Yosemite National Park, USA", 
                "Amazon Rainforest, Brazil"
        });
        
        themeDestinationMap.put("Food & Culture", new String[]{
                "Tokyo, Japan", 
                "Barcelona, Spain", 
                "New Orleans, USA", 
                "Bangkok, Thailand", 
                "Istanbul, Turkey"
        });
        
        themeDestinationMap.put("City Exploration", new String[]{
                "New York City, USA", 
                "London, UK", 
                "Paris, France", 
                "Singapore", 
                "Dubai, UAE"
        });
        
        themeDestinationMap.put("Historical", new String[]{
                "Rome, Italy", 
                "Athens, Greece", 
                "Cairo, Egypt", 
                "Kyoto, Japan", 
                "Cusco, Peru"
        });
        
        // Initialize seasonal destination recommendations
        seasonalDestinationMap = new HashMap<>();
        seasonalDestinationMap.put(Month.DECEMBER, new String[]{
                "Aspen, Colorado", 
                "Vienna, Austria", 
                "Rovaniemi, Finland", 
                "Sydney, Australia", 
                "Cape Town, South Africa"
        });
        
        seasonalDestinationMap.put(Month.JANUARY, new String[]{
                "Whistler, Canada", 
                "Phuket, Thailand", 
                "Maldives", 
                "Rio de Janeiro, Brazil", 
                "New Zealand"
        });
        
        seasonalDestinationMap.put(Month.FEBRUARY, new String[]{
                "Venice, Italy", 
                "New Orleans, USA", 
                "Bali, Indonesia", 
                "Costa Rica", 
                "Patagonia, Argentina/Chile"
        });
        
        seasonalDestinationMap.put(Month.MARCH, new String[]{
                "Tokyo, Japan", 
                "Amsterdam, Netherlands", 
                "Washington D.C., USA", 
                "Morocco", 
                "Galapagos Islands, Ecuador"
        });
        
        seasonalDestinationMap.put(Month.APRIL, new String[]{
                "Paris, France", 
                "Kyoto, Japan", 
                "Amsterdam, Netherlands", 
                "Seville, Spain", 
                "Marrakech, Morocco"
        });
        
        seasonalDestinationMap.put(Month.MAY, new String[]{
                "Greek Islands", 
                "Barcelona, Spain", 
                "Amalfi Coast, Italy", 
                "Bali, Indonesia", 
                "Machu Picchu, Peru"
        });
        
        seasonalDestinationMap.put(Month.JUNE, new String[]{
                "Santorini, Greece", 
                "Provence, France", 
                "Banff National Park, Canada", 
                "Iceland", 
                "Serengeti, Tanzania"
        });
        
        seasonalDestinationMap.put(Month.JULY, new String[]{
                "Bora Bora, French Polynesia", 
                "Amalfi Coast, Italy", 
                "Maui, Hawaii", 
                "Serengeti, Tanzania", 
                "Iceland"
        });
        
        seasonalDestinationMap.put(Month.AUGUST, new String[]{
                "Bali, Indonesia", 
                "Maldives", 
                "Santorini, Greece", 
                "Dubrovnik, Croatia", 
                "Edinburgh, Scotland"
        });
        
        seasonalDestinationMap.put(Month.SEPTEMBER, new String[]{
                "Santorini, Greece", 
                "Tuscany, Italy", 
                "Bali, Indonesia", 
                "Barcelona, Spain", 
                "Kyoto, Japan"
        });
        
        seasonalDestinationMap.put(Month.OCTOBER, new String[]{
                "New England, USA", 
                "Kyoto, Japan", 
                "Marrakech, Morocco", 
                "Galapagos Islands, Ecuador", 
                "South Africa"
        });
        
        seasonalDestinationMap.put(Month.NOVEMBER, new String[]{
                "New York City, USA", 
                "New Zealand", 
                "Thailand", 
                "Maldives", 
                "Peru"
        });
    }
    
    /**
     * Recommend a destination based on user preferences.
     *
     * @param preferences The user's trip preferences
     * @return A recommended destination
     */
    public String recommendDestination(TripPreferences preferences) {
        // Get the theme from preferences
        String theme = preferences.getTheme();
        
        // Get the month of travel
        Month travelMonth = preferences.getStartDate().getMonth();
        
        // Get destinations matching the theme
        String[] themeDestinations = themeDestinationMap.getOrDefault(theme, new String[0]);
        
        // Get destinations suitable for the travel month
        String[] seasonalDestinations = seasonalDestinationMap.getOrDefault(travelMonth, new String[0]);
        
        // Find a destination that matches both theme and season if possible
        for (String themeDestination : themeDestinations) {
            for (String seasonalDestination : seasonalDestinations) {
                if (themeDestination.equals(seasonalDestination)) {
                    return themeDestination;
                }
            }
        }
        
        // If no perfect match, prioritize theme
        if (themeDestinations.length > 0) {
            // For now, just return the first destination that matches the theme
            // In a real application, we would apply more sophisticated matching logic
            return themeDestinations[0];
        }
        
        // If no theme match, use seasonal recommendation
        if (seasonalDestinations.length > 0) {
            return seasonalDestinations[0];
        }
        
        // Default recommendation if nothing else matches
        return "Paris, France";
    }
    
    /**
     * Get all destinations that match a specific theme.
     *
     * @param theme The vacation theme
     * @return An array of matching destinations
     */
    public List<String> getDestinationsByTheme(String theme) {
        return Arrays.asList(themeDestinationMap.getOrDefault(normalizeTheme(theme), new String[0]));
    }

    /**
     * Get destinations by theme as a list (test-friendly).
     */
    public List<String> getDestinationsByThemeList(String theme) {
        return getDestinationsByTheme(theme);
    }
    
    /**
     * Get all destinations that are suitable for a specific month.
     *
     * @param month The month of travel
     * @return An array of matching destinations
     */
    public String[] getDestinationsByMonth(Month month) {
        return seasonalDestinationMap.getOrDefault(month, new String[0]);
    }

    /**
     * Overload that accepts numeric month value (1-12).
     */
    public List<String> getDestinationsByMonth(int monthValue) {
        Month month = Month.of(Math.max(1, Math.min(12, monthValue)));
        return Arrays.asList(getDestinationsByMonth(month));
    }

    /**
     * Recommend multiple destinations based on preferences (simple heuristic).
     */
    public List<String> recommendDestinations(TripPreferences preferences) {
        List<String> themed = getDestinationsByTheme(preferences.getTheme());
        List<String> seasonal = getDestinationsByMonth(preferences.getStartDate().getMonthValue());
        // Merge with preference to keep order
        java.util.LinkedHashSet<String> combined = new java.util.LinkedHashSet<>();
        combined.addAll(themed);
        combined.addAll(seasonal);
        if (combined.isEmpty()) {
            combined.add(recommendDestination(preferences));
        }
        return new java.util.ArrayList<>(combined);
    }

    private String normalizeTheme(String theme) {
        if (theme == null) return "";
        // Map common lowercase inputs to our keys
        switch (theme.trim().toLowerCase()) {
            case "adventure": return "Adventure";
            case "relaxation": return "Relaxation";
            case "beach": return "Beach";
            case "nature": return "Nature";
            case "food":
            case "food & culture":
            case "food and culture": return "Food & Culture";
            case "city":
            case "city exploration": return "City Exploration";
            case "historical": return "Historical";
            default: return theme;
        }
    }
}