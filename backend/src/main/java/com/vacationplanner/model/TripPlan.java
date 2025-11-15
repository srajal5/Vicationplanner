package com.vacationplanner.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TripPlan {
    private String id;
    private TripPreferences preferences;
    private String destination;
    private String theme;
    private Integer groupSize;
   
    private Transportation transportation;
    private Accommodation accommodation;
    private List<DailyItinerary> dailyItineraries;
    private BudgetBreakdown budgetBreakdown;
    private LocalDate createdDate;

    
    public TripPlan(TripPreferences preferences, String destination) {
        this.preferences = preferences;
        this.destination = destination;
        this.dailyItineraries = new ArrayList<>();
        this.createdDate = LocalDate.now();
        this.budgetBreakdown = new BudgetBreakdown(preferences.getBudget());
        this.budgetBreakdown.setCurrency(preferences.getCurrency());
    }

    
    public TripPlan() {
        this.dailyItineraries = new ArrayList<>();
        this.createdDate = LocalDate.now();
    }

    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TripPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(TripPreferences preferences) {
        this.preferences = preferences;
    }
    
    /**
     * Get the start date of the trip.
     *
     * @return The start date from preferences
     */
    public LocalDate getStartDate() {
        return preferences != null ? preferences.getStartDate() : null;
    }
    
    /**
     * Get the end date of the trip.
     *
     * @return The end date from preferences
     */
    public LocalDate getEndDate() {
        return preferences != null ? preferences.getEndDate() : null;
    }
    
    /**
     * Set the start date of the trip.
     *
     * @param startDate The start date to set
     */
    public void setStartDate(LocalDate startDate) {
        if (preferences != null) {
            preferences.setStartDate(startDate);
        }
    }
    
    /**
     * Set the end date of the trip.
     *
     * @param endDate The end date to set
     */
    public void setEndDate(LocalDate endDate) {
        if (preferences != null) {
            preferences.setEndDate(endDate);
        }
    }
    
    /**
     * Get the theme of the trip.
     *
     * @return The theme from preferences
     */
    public String getTheme() {
        return preferences != null ? preferences.getTheme() : theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
        if (preferences != null) {
            preferences.setTheme(theme);
        }
    }
    
    /**
     * Get the group size of the trip.
     *
     * @return The group size from preferences
     */
    public int getGroupSize() {
        return preferences != null ? preferences.getGroupSize() : groupSize != null ? groupSize : 0;
    }
    
    public void setGroupSize(Integer groupSize) {
        this.groupSize = groupSize;
        if (preferences != null && groupSize != null) {
            preferences.setGroupSize(groupSize);
        }
    }
    
    /**
     * Get the total budget for the trip.
     *
     * @return The total budget from budget breakdown
     */
    public double getTotalBudget() {
        return budgetBreakdown != null ? budgetBreakdown.getTotalBudget() : 0.0;
    }
    
    /**
     * Set the total budget for the trip.
     *
     * @param totalBudget The total budget to set
     */
    public void setTotalBudget(Double totalBudget) {
        if (budgetBreakdown != null) {
            budgetBreakdown.setTotalBudget(totalBudget);
        }
    }
    
    /**
     * Get the currency used for the trip.
     *
     * @return The currency from budget breakdown
     */
    public String getCurrency() {
        return budgetBreakdown != null ? budgetBreakdown.getCurrency() : "USD";
    }
    
    /**
     * Set the currency used for the trip.
     *
     * @param currency The currency to set
     */
    public void setCurrency(String currency) {
        if (budgetBreakdown != null) {
            budgetBreakdown.setCurrency(currency);
        }
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    public Transportation getTransportation() {
        return transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    public List<DailyItinerary> getDailyItineraries() {
        return dailyItineraries;
    }
    
    /**
     * Get the daily itineraries as an itinerary.
     * This is an alias for getDailyItineraries for compatibility.
     *
     * @return The list of daily itineraries
     */
    public List<DailyItinerary> getItinerary() {
        return dailyItineraries;
    }

    public void setDailyItineraries(List<DailyItinerary> dailyItineraries) {
        this.dailyItineraries = dailyItineraries;
    }

    public void addDailyItinerary(DailyItinerary itinerary) {
        this.dailyItineraries.add(itinerary);
    }

    public BudgetBreakdown getBudgetBreakdown() {
        return budgetBreakdown;
    }

    public void setBudgetBreakdown(BudgetBreakdown budgetBreakdown) {
        this.budgetBreakdown = budgetBreakdown;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Inner class representing transportation details for the trip.
     */
    public static class Transportation {
        private String type; // Flight, Train, Bus, etc.
        private String departureLocation;
        private String arrivalLocation;
        private LocalDate departureDate;
        private LocalDate returnDate;
        private String provider;
        private double cost;

        public Transportation() {
        }

        public Transportation(String type, String departureLocation, String arrivalLocation,
                             LocalDate departureDate, LocalDate returnDate, String provider, double cost) {
            this.type = type;
            this.departureLocation = departureLocation;
            this.arrivalLocation = arrivalLocation;
            this.departureDate = departureDate;
            this.returnDate = returnDate;
            this.provider = provider;
            this.cost = cost;
        }

        // Getters and setters

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDepartureLocation() {
            return departureLocation;
        }
        
        /**
         * Get the origin location.
         * This is an alias for getDepartureLocation for compatibility.
         *
         * @return The departure location
         */
        public String getOrigin() {
            return departureLocation;
        }

        public void setDepartureLocation(String departureLocation) {
            this.departureLocation = departureLocation;
        }

        public String getArrivalLocation() {
            return arrivalLocation;
        }
        
        /**
         * Get the destination location.
         * This is an alias for getArrivalLocation for compatibility.
         *
         * @return The arrival location
         */
        public String getDestination() {
            return arrivalLocation;
        }

        public void setArrivalLocation(String arrivalLocation) {
            this.arrivalLocation = arrivalLocation;
        }

        public LocalDate getDepartureDate() {
            return departureDate;
        }

        public void setDepartureDate(LocalDate departureDate) {
            this.departureDate = departureDate;
        }

        public LocalDate getReturnDate() {
            return returnDate;
        }

        public void setReturnDate(LocalDate returnDate) {
            this.returnDate = returnDate;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }
    }

    /**
     * Inner class representing accommodation details for the trip.
     */
    public static class Accommodation {
        private String name;
        private String type; // Hotel, Hostel, Apartment, etc.
        private String address;
        private double costPerNight;
        private double rating;
        private String provider;
        private Map<String, Object> amenities;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private int nights;

        public Accommodation() {
            this.amenities = new HashMap<>();
        }

        public Accommodation(String name, String type, String address, double costPerNight, double rating, String provider) {
            this.name = name;
            this.type = type;
            this.address = address;
            this.costPerNight = costPerNight;
            this.rating = rating;
            this.provider = provider;
            this.amenities = new HashMap<>();
        }

        // Getters and setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public double getCostPerNight() {
            return costPerNight;
        }

        public void setCostPerNight(double costPerNight) {
            this.costPerNight = costPerNight;
        }
        
        /**
         * Get the total cost of accommodation.
         *
         * @return The total cost (costPerNight * nights)
         */
        public double getCost() {
            return costPerNight * nights;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public Map<String, Object> getAmenities() {
            return amenities;
        }

        public void setAmenities(Map<String, Object> amenities) {
            this.amenities = amenities;
        }

        public void addAmenity(String key, Object value) {
            this.amenities.put(key, value);
        }
        
        /**
         * Get the location of the accommodation.
         * This is an alias for getAddress for compatibility.
         *
         * @return The address
         */
        public String getLocation() {
            return address;
        }
        
        /**
         * Get the check-in date.
         *
         * @return The check-in date
         */
        public LocalDate getCheckInDate() {
            return checkInDate;
        }
        
        /**
         * Set the check-in date.
         *
         * @param checkInDate The check-in date
         */
        public void setCheckInDate(LocalDate checkInDate) {
            this.checkInDate = checkInDate;
            updateNights();
        }
        
        /**
         * Get the check-out date.
         *
         * @return The check-out date
         */
        public LocalDate getCheckOutDate() {
            return checkOutDate;
        }
        
        /**
         * Set the check-out date.
         *
         * @param checkOutDate The check-out date
         */
        public void setCheckOutDate(LocalDate checkOutDate) {
            this.checkOutDate = checkOutDate;
            updateNights();
        }
        
        /**
         * Update the number of nights based on check-in and check-out dates.
         */
        private void updateNights() {
            if (checkInDate != null && checkOutDate != null) {
                this.nights = (int) (checkOutDate.toEpochDay() - checkInDate.toEpochDay());
            }
        }
        
        /**
         * Get the number of nights.
         *
         * @return The number of nights
         */
        public int getNights() {
            return nights;
        }
        
        /**
         * Set the number of nights.
         *
         * @param nights The number of nights
         */
        public void setNights(int nights) {
            this.nights = nights;
        }
        
        /**
         * Get the total cost of the accommodation.
         *
         * @return The cost per night multiplied by the number of nights
         */
        public double getTotalCost() {
            return costPerNight * (nights > 0 ? nights : 1);
        }
    }

    /**
     * Inner class representing a daily itinerary for the trip.
     */
    public static class DailyItinerary {
        private int day;
        private LocalDate date;
        private List<Activity> morningActivities;
        private List<Activity> afternoonActivities;
        private List<Activity> eveningActivities;
        private double dailyBudget;
        private double dailyCost;

        public DailyItinerary() {
            this.morningActivities = new ArrayList<>();
            this.afternoonActivities = new ArrayList<>();
            this.eveningActivities = new ArrayList<>();
        }

        public DailyItinerary(int day, LocalDate date, double dailyBudget) {
            this.day = day;
            this.date = date;
            this.dailyBudget = dailyBudget;
            this.morningActivities = new ArrayList<>();
            this.afternoonActivities = new ArrayList<>();
            this.eveningActivities = new ArrayList<>();
        }
        
        /**
         * Add an activity to the appropriate time slot based on its start time.
         *
         * @param activity The activity to add
         */
        public void addActivity(Activity activity) {
            if (activity == null) return;
            
            String startTimeStr = activity.getStartTime();
            if (startTimeStr == null || startTimeStr.isEmpty()) {
                // Default to morning if no time specified
                morningActivities.add(activity);
                return;
            }
            
            try {
                // Parse time string (assuming format like "09:00" or "09:00 AM")
                LocalTime startTime = parseTimeString(startTimeStr);
                
                // Morning: 5:00 - 11:59
                if (startTime.isBefore(LocalTime.NOON)) {
                    morningActivities.add(activity);
                }
                // Afternoon: 12:00 - 17:59
                else if (startTime.isBefore(LocalTime.of(18, 0))) {
                    afternoonActivities.add(activity);
                }
                // Evening: 18:00 - 4:59
                else {
                    eveningActivities.add(activity);
                }
            } catch (Exception e) {
                // If parsing fails, default to morning
                morningActivities.add(activity);
            }
        }
        
        /**
         * Parse a time string to LocalTime.
         * Supports formats like "09:00", "09:00 AM", "9:00 PM"
         *
         * @param timeStr The time string to parse
         * @return The parsed LocalTime
         */
        private LocalTime parseTimeString(String timeStr) {
            if (timeStr == null || timeStr.isEmpty()) {
                return LocalTime.of(9, 0); // Default to 9:00 AM
            }
            
            timeStr = timeStr.trim().toUpperCase();
            
            // Handle AM/PM format
            if (timeStr.contains("AM") || timeStr.contains("PM")) {
                boolean isPM = timeStr.contains("PM");
                String timePart = timeStr.replace("AM", "").replace("PM", "").trim();
                
                String[] parts = timePart.split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                
                if (isPM && hour != 12) {
                    hour += 12;
                } else if (!isPM && hour == 12) {
                    hour = 0;
                }
                
                return LocalTime.of(hour, minute);
            } else {
                // Handle 24-hour format
                String[] parts = timeStr.split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                
                return LocalTime.of(hour, minute);
            }
        }

        // Getters and setters

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public List<Activity> getMorningActivities() {
            return morningActivities;
        }

        public void setMorningActivities(List<Activity> morningActivities) {
            this.morningActivities = morningActivities;
        }

        public void addMorningActivity(Activity activity) {
            this.morningActivities.add(activity);
        }

        public List<Activity> getAfternoonActivities() {
            return afternoonActivities;
        }

        public void setAfternoonActivities(List<Activity> afternoonActivities) {
            this.afternoonActivities = afternoonActivities;
        }

        public void addAfternoonActivity(Activity activity) {
            this.afternoonActivities.add(activity);
        }

        public List<Activity> getEveningActivities() {
            return eveningActivities;
        }

        public void setEveningActivities(List<Activity> eveningActivities) {
            this.eveningActivities = eveningActivities;
        }

        public void addEveningActivity(Activity activity) {
            this.eveningActivities.add(activity);
        }

        public double getDailyBudget() {
            return dailyBudget;
        }

        public void setDailyBudget(double dailyBudget) {
            this.dailyBudget = dailyBudget;
        }
        
        /**
         * Get the daily cost of activities.
         *
         * @return The total cost of all activities for the day
         */
        public double getDailyCost() {
            return dailyCost;
        }
        
        /**
         * Set the daily cost of activities.
         *
         * @param dailyCost The total cost of all activities for the day
         */
        public void setDailyCost(double dailyCost) {
            this.dailyCost = dailyCost;
        }
        
        /**
         * Get all activities for the day.
         *
         * @return A combined list of morning, afternoon, and evening activities
         */
        public List<Activity> getActivities() {
            List<Activity> allActivities = new ArrayList<>();
            allActivities.addAll(morningActivities);
            allActivities.addAll(afternoonActivities);
            allActivities.addAll(eveningActivities);
            return allActivities;
        }
    }

    /**
     * Inner class representing an activity in the itinerary.
     */
    public static class Activity {
        private String name;
        private String type; // Sightseeing, Food, Adventure, etc.
        private String location;
        private String description;
        private double cost;
        private int durationMinutes;
        private String startTime;
        private String endTime;
        private double rating;

        public Activity() {
        }

        public Activity(String name, String type, String location, String description, double cost, int durationMinutes) {
            this.name = name;
            this.type = type;
            this.location = location;
            this.description = description;
            this.cost = cost;
            this.durationMinutes = durationMinutes;
            this.rating = 0.0;
        }
        
        public Activity(String name, String description, double cost, double rating) {
            this.name = name;
            this.description = description;
            this.cost = cost;
            this.rating = rating;
        }

        // Getters and setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public int getDurationMinutes() {
            return durationMinutes;
        }

        public void setDurationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
        }
        
        /**
         * Get the start time of the activity.
         *
         * @return The start time as a string (e.g., "09:00 AM")
         */
        public String getStartTime() {
            return startTime;
        }
        
        /**
         * Set the start time of the activity.
         *
         * @param startTime The start time as a string
         */
        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }
        
        /**
         * Get the end time of the activity.
         *
         * @return The end time as a string (e.g., "11:00 AM")
         */
        public String getEndTime() {
            return endTime;
        }
        
        /**
         * Set the end time of the activity.
         *
         * @param endTime The end time as a string
         */
        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
        
        /**
         * Get the rating of the activity.
         *
         * @return The activity rating
         */
        public double getRating() {
            return rating;
        }
        
        /**
         * Set the rating of the activity.
         *
         * @param rating The rating to set
         */
        public void setRating(double rating) {
            this.rating = rating;
        }
    }

    /**
     * Inner class representing the budget breakdown for the trip.
     */
    public static class BudgetBreakdown {
        private double totalBudget;
        private double transportationCost;
        private double accommodationCost;
        private double foodCost;
        private double activitiesCost;
        private double miscellaneousCost;
        private String currency;

        public BudgetBreakdown() {
        }

        public BudgetBreakdown(double totalBudget) {
            this.totalBudget = totalBudget;
            // Default allocation based on typical travel expense distribution
            this.transportationCost = totalBudget * 0.4; // 40% for transportation
            this.accommodationCost = totalBudget * 0.3;  // 30% for accommodation
            this.foodCost = totalBudget * 0.15;          // 15% for food
            this.activitiesCost = totalBudget * 0.1;     // 10% for activities
            this.miscellaneousCost = totalBudget * 0.05; // 5% for miscellaneous
            this.currency = "USD"; // Default currency
        }
        
        /**
         * Constructor for BudgetBreakdown with specific cost allocations.
         *
         * @param transportationCost The cost for transportation
         * @param accommodationCost The cost for accommodation
         * @param foodCost The cost for food
         * @param activitiesCost The cost for activities
         * @param miscellaneousCost The cost for miscellaneous expenses
         */
        public BudgetBreakdown(double transportationCost, double accommodationCost, double foodCost, 
                              double activitiesCost, double miscellaneousCost) {
            this.transportationCost = transportationCost;
            this.accommodationCost = accommodationCost;
            this.foodCost = foodCost;
            this.activitiesCost = activitiesCost;
            this.miscellaneousCost = miscellaneousCost;
            this.totalBudget = transportationCost + accommodationCost + foodCost + activitiesCost + miscellaneousCost;
            this.currency = "USD"; // Default currency
        }

        // Getters and setters

        public double getTotalBudget() {
            return totalBudget;
        }

        public void setTotalBudget(double totalBudget) {
            this.totalBudget = totalBudget;
        }

        public double getTransportationCost() {
            return transportationCost;
        }

        public void setTransportationCost(double transportationCost) {
            this.transportationCost = transportationCost;
        }

        public double getAccommodationCost() {
            return accommodationCost;
        }

        public void setAccommodationCost(double accommodationCost) {
            this.accommodationCost = accommodationCost;
        }

        public double getFoodCost() {
            return foodCost;
        }

        public void setFoodCost(double foodCost) {
            this.foodCost = foodCost;
        }

        public double getActivitiesCost() {
            return activitiesCost;
        }

        public void setActivitiesCost(double activitiesCost) {
            this.activitiesCost = activitiesCost;
        }

        public double getMiscellaneousCost() {
            return miscellaneousCost;
        }

        public void setMiscellaneousCost(double miscellaneousCost) {
            this.miscellaneousCost = miscellaneousCost;
        }
        
        /**
         * Get the miscellaneous cost (alias for getMiscellaneousCost).
         *
         * @return The miscellaneous cost
         */
        public double getMiscCost() {
            return miscellaneousCost;
        }

        /**
         * Calculate the remaining budget after all allocations.
         *
         * @return The remaining budget amount
         */
        public double getRemainingBudget() {
            return totalBudget - (transportationCost + accommodationCost + foodCost + activitiesCost + miscellaneousCost);
        }

        /**
         * Check if the budget allocations are within the total budget.
         *
         * @return true if the sum of all allocations is less than or equal to the total budget
         */
        public boolean isWithinBudget() {
            return getRemainingBudget() >= 0;
        }
        
        /**
         * Get the currency used for the budget.
         *
         * @return The currency code
         */
        public String getCurrency() {
            return currency;
        }
        
        /**
         * Set the currency used for the budget.
         *
         * @param currency The currency code
         */
        public void setCurrency(String currency) {
            this.currency = currency;
        }
        
        /**
         * Get the total cost of the trip.
         *
         * @return The sum of all costs
         */
        public double getTotalCost() {
            return transportationCost + accommodationCost + foodCost + activitiesCost + miscellaneousCost;
        }
    }
}