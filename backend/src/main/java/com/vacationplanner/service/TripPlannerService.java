package com.vacationplanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vacationplanner.model.TripPlan;
import com.vacationplanner.model.TripPreferences;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class TripPlannerService {
    
    private RecommendationService recommendationService;
    private FlightService flightService;
    private HotelService hotelService;
    private ActivityService activityService;
    private TripPlan currentTripPlan;

    @Autowired
    private DatabaseService databaseService;
    
    
    public TripPlannerService() {
        this.recommendationService = new RecommendationService();
        this.flightService = new FlightService();
        this.hotelService = new HotelService();
        this.activityService = new ActivityService();
    }
    
   
    public TripPlan planTrip(TripPreferences preferences) {
      
        // Prefer a user-specified destination if provided; otherwise fall back to recommendations
        String destination = null;
        if (preferences != null && preferences.getDestination() != null && !preferences.getDestination().trim().isEmpty()) {
            destination = preferences.getDestination().trim();
        } else {
            destination = recommendationService.recommendDestination(preferences);
        }

        TripPlan tripPlan = new TripPlan(preferences, destination);
        
       
        TripPlan.Transportation transportation = flightService.findBestFlight(
                preferences.getStartingPoint(), 
                destination, 
                preferences.getStartDate(), 
                preferences.getEndDate(), 
                preferences.getBudgetBreakdown().getTransportationCost());
        tripPlan.setTransportation(transportation);
        
        // Get hotel options
        TripPlan.Accommodation accommodation = hotelService.findBestHotel(
                destination, 
                preferences.getStartDate(), 
                preferences.getEndDate(), 
                preferences.getGroupSize(),
                preferences.getBudgetBreakdown().getAccommodationCost());
        tripPlan.setAccommodation(accommodation);
        
        // Generate daily itineraries
        generateDailyItineraries(tripPlan, preferences);
        
        // Store the current trip plan in memory
        this.currentTripPlan = tripPlan;

        // Persist to database if available
        try {
            if (databaseService != null && databaseService.isConnected()) {
                String savedId = databaseService.saveTripPlan(tripPlan);
                if (savedId != null) {
                    tripPlan.setId(savedId);
                }
            }
        } catch (Exception e) {
            // Log and continue — persistence is best-effort
            System.err.println("Failed to persist trip plan: " + e.getMessage());
        }

        return tripPlan;
    }
    
    /**
     * Generate daily itineraries for the trip plan.
     *
     * @param tripPlan The trip plan to generate itineraries for
     * @param preferences The user's trip preferences
     */
    private void generateDailyItineraries(TripPlan tripPlan, TripPreferences preferences) {
        LocalDate currentDate = preferences.getStartDate();
        int tripDuration = preferences.getTripDurationDays();
        double dailyActivityBudget = tripPlan.getBudgetBreakdown().getActivitiesCost() / tripDuration;
        double dailyFoodBudget = tripPlan.getBudgetBreakdown().getFoodCost() / tripDuration;
        
        for (int day = 1; day <= tripDuration; day++) {
            TripPlan.DailyItinerary itinerary = new TripPlan.DailyItinerary(day, currentDate, dailyActivityBudget + dailyFoodBudget);
            
            // Morning activities (typically sightseeing)
            List<TripPlan.Activity> morningActivities = activityService.findActivities(
                    tripPlan.getDestination(), 
                    "Sightseeing", 
                    dailyActivityBudget * 0.4, // 40% of daily activity budget for morning
                    preferences.getTheme());
            itinerary.setMorningActivities(morningActivities);
            
            // Afternoon activities
            List<TripPlan.Activity> afternoonActivities = activityService.findActivities(
                    tripPlan.getDestination(), 
                    "Activity", 
                    dailyActivityBudget * 0.4, // 40% of daily activity budget for afternoon
                    preferences.getTheme());
            itinerary.setAfternoonActivities(afternoonActivities);
            
            // Evening activities (typically food/cultural)
            List<TripPlan.Activity> eveningActivities = activityService.findActivities(
                    tripPlan.getDestination(), 
                    "Food", 
                    dailyFoodBudget, // Use the daily food budget for evening
                    preferences.getTheme());
            itinerary.setEveningActivities(eveningActivities);
            
            tripPlan.addDailyItinerary(itinerary);
            currentDate = currentDate.plusDays(1);
        }
    }
    
    /**
     * Get the current trip plan.
     *
     * @return The current trip plan
     */
    public TripPlan getCurrentTripPlan() {
        return currentTripPlan;
    }
    
    /**
     * Save the current trip plan.
     *
     * @return true if the save was successful, false otherwise
     */
    public boolean saveTripPlan() {
        if (currentTripPlan == null) return false;
        try {
            if (databaseService != null && databaseService.isConnected()) {
                String id = databaseService.saveTripPlan(currentTripPlan);
                if (id != null) {
                    currentTripPlan.setId(id);
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error saving current trip plan: " + e.getMessage());
        }
        // Fallback: in-memory only
        return false;
    }
    
    /**
     * Export the current trip plan to PDF.
     *
     * @param filePath The path to save the PDF file
     * @return true if the export was successful, false otherwise
     */
    // Export functionality is handled by ExportService and ExportController.
    // The TripPlannerService no longer exposes export helpers; remove unused stubs.
    
    /**
     * Get a trip by ID.
     *
     * @param tripId The trip ID
     * @return The trip plan or null if not found
     */
    public TripPlan getTripById(String tripId) {
        // Try in-memory first
        if (currentTripPlan != null && tripId.equals(currentTripPlan.getId())) {
            return currentTripPlan;
        }
        // Fallback to DB if available
        try {
            if (databaseService != null && databaseService.isConnected()) {
                TripPlan fromDb = databaseService.loadTripPlan(tripId);
                if (fromDb != null) return fromDb;
            }
        } catch (Exception e) {
            System.err.println("Error loading trip from DB: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get all saved trips.
     *
     * @return List of all trips
     */
    public List<TripPlan> getAllTrips() {
        // If DB present, return DB contents; otherwise return in-memory
        try {
            if (databaseService != null && databaseService.isConnected()) {
                return databaseService.getAllTripPlans();
            }
        } catch (Exception e) {
            System.err.println("Error fetching trips from DB: " + e.getMessage());
        }
        List<TripPlan> trips = new ArrayList<>();
        if (currentTripPlan != null) {
            trips.add(currentTripPlan);
        }
        return trips;
    }
    
    /**
     * Save a trip.
     *
     * @param tripId The trip ID to save
     * @return true if saved successfully
     */
    public boolean saveTrip(String tripId) {
        // Attempt to find the trip in-memory
        if (currentTripPlan != null && tripId.equals(currentTripPlan.getId())) {
            return saveTripPlan();
        }

        // If it's not the current in-memory trip, attempt to load and re-save
        try {
            if (databaseService != null && databaseService.isConnected()) {
                TripPlan tp = databaseService.loadTripPlan(tripId);
                if (tp != null) {
                    String id = databaseService.saveTripPlan(tp);
                    return id != null;
                }
            }
        } catch (Exception e) {
            System.err.println("Error saving trip by id: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Delete a trip.
     *
     * @param tripId The trip ID to delete
     * @return true if deleted successfully
     */
    public boolean deleteTrip(String tripId) {
        // If current in-memory trip matches, clear it
        if (currentTripPlan != null && tripId.equals(currentTripPlan.getId())) {
            currentTripPlan = null;
            // Also attempt to delete from DB if available
            try {
                if (databaseService != null && databaseService.isConnected()) {
                    return databaseService.deleteTripPlan(tripId);
                }
            } catch (Exception e) {
                System.err.println("Error deleting trip from DB: " + e.getMessage());
            }
            return true;
        }

        // Otherwise try deleting from DB
        try {
            if (databaseService != null && databaseService.isConnected()) {
                return databaseService.deleteTripPlan(tripId);
            }
        } catch (Exception e) {
            System.err.println("Error deleting trip from DB: " + e.getMessage());
        }
        return false;
    }
}