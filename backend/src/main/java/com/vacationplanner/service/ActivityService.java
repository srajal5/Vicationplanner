package com.vacationplanner.service;

import com.vacationplanner.model.TripPlan;

import java.util.*;

/**
 * Service for finding activities and attractions at destinations.
 * In the MVP version, this uses mock data instead of real API calls.
 */
public class ActivityService {

    private final Map<String, Map<String, List<ActivityInfo>>> destinationActivities;
    
    /**
     * Constructor for ActivityService.
     * Initializes mock activity data.
     */
    public ActivityService() {
        // Initialize mock activity data for popular destinations
        destinationActivities = new HashMap<>();
        
        // Paris activities
        Map<String, List<ActivityInfo>> parisActivities = new HashMap<>();
        
        List<ActivityInfo> parisSightseeing = new ArrayList<>();
        parisSightseeing.add(new ActivityInfo("Eiffel Tower", "Iconic iron tower with panoramic views", 25.0, 4.7));
        parisSightseeing.add(new ActivityInfo("Louvre Museum", "World's largest art museum & historic monument", 17.0, 4.8));
        parisSightseeing.add(new ActivityInfo("Notre-Dame Cathedral", "Medieval Catholic cathedral", 0.0, 4.6));
        parisSightseeing.add(new ActivityInfo("Arc de Triomphe", "Iconic triumphal arch honoring those who fought for France", 13.0, 4.5));
        parisActivities.put("sightseeing", parisSightseeing);
        
        List<ActivityInfo> parisAdventure = new ArrayList<>();
        parisAdventure.add(new ActivityInfo("Seine River Cruise", "Boat tour along the Seine River", 15.0, 4.4));
        parisAdventure.add(new ActivityInfo("Montmartre Walking Tour", "Explore the artistic neighborhood", 25.0, 4.3));
        parisActivities.put("adventure", parisAdventure);
        
        List<ActivityInfo> parisFood = new ArrayList<>();
        parisFood.add(new ActivityInfo("Le Jules Verne", "Fine dining with Eiffel Tower views", 150.0, 4.6));
        parisFood.add(new ActivityInfo("Parisian Bakery Tour", "Sample the best pastries in Paris", 45.0, 4.7));
        parisFood.add(new ActivityInfo("Wine Tasting Experience", "Sample French wines with a sommelier", 60.0, 4.5));
        parisActivities.put("food", parisFood);
        
        destinationActivities.put("Paris, France", parisActivities);
        
        // London activities
        Map<String, List<ActivityInfo>> londonActivities = new HashMap<>();
        
        List<ActivityInfo> londonSightseeing = new ArrayList<>();
        londonSightseeing.add(new ActivityInfo("Tower of London", "Historic castle on the Thames", 30.0, 4.6));
        londonSightseeing.add(new ActivityInfo("British Museum", "Museum of human history, art, and culture", 0.0, 4.8));
        londonSightseeing.add(new ActivityInfo("Buckingham Palace", "The Queen's official London residence", 30.0, 4.5));
        londonSightseeing.add(new ActivityInfo("London Eye", "Giant Ferris wheel on the South Bank", 27.0, 4.4));
        londonActivities.put("sightseeing", londonSightseeing);
        
        List<ActivityInfo> londonAdventure = new ArrayList<>();
        londonAdventure.add(new ActivityInfo("Thames RIB Experience", "High-speed boat ride on the Thames", 45.0, 4.7));
        londonAdventure.add(new ActivityInfo("The View from The Shard", "Viewing platform at the top of Western Europe's tallest building", 32.0, 4.5));
        londonActivities.put("adventure", londonAdventure);
        
        List<ActivityInfo> londonFood = new ArrayList<>();
        londonFood.add(new ActivityInfo("Borough Market Tour", "Food tour of London's oldest food market", 35.0, 4.6));
        londonFood.add(new ActivityInfo("Afternoon Tea at The Ritz", "Classic British afternoon tea experience", 60.0, 4.8));
        londonFood.add(new ActivityInfo("Gordon Ramsay Restaurant", "Fine dining at celebrity chef restaurant", 120.0, 4.7));
        londonActivities.put("food", londonFood);
        
        destinationActivities.put("London, UK", londonActivities);
        
        // Tokyo activities
        Map<String, List<ActivityInfo>> tokyoActivities = new HashMap<>();
        
        List<ActivityInfo> tokyoSightseeing = new ArrayList<>();
        tokyoSightseeing.add(new ActivityInfo("Tokyo Skytree", "Tallest tower in Japan with observation decks", 20.0, 4.5));
        tokyoSightseeing.add(new ActivityInfo("Senso-ji Temple", "Ancient Buddhist temple in Asakusa", 0.0, 4.7));
        tokyoSightseeing.add(new ActivityInfo("Meiji Shrine", "Shinto shrine dedicated to Emperor Meiji", 0.0, 4.6));
        tokyoSightseeing.add(new ActivityInfo("Tokyo Imperial Palace", "Primary residence of the Emperor of Japan", 0.0, 4.4));
        tokyoActivities.put("sightseeing", tokyoSightseeing);
        
        List<ActivityInfo> tokyoAdventure = new ArrayList<>();
        tokyoAdventure.add(new ActivityInfo("Robot Restaurant Show", "Futuristic cabaret show in Shinjuku", 80.0, 4.2));
        tokyoAdventure.add(new ActivityInfo("Mario Kart City Tour", "Drive through Tokyo dressed as Mario characters", 90.0, 4.8));
        tokyoActivities.put("adventure", tokyoAdventure);
        
        List<ActivityInfo> tokyoFood = new ArrayList<>();
        tokyoFood.add(new ActivityInfo("Tsukiji Outer Market Tour", "Food tour of famous fish market area", 40.0, 4.7));
        tokyoFood.add(new ActivityInfo("Sushi Making Class", "Learn to make sushi with a master chef", 65.0, 4.8));
        tokyoFood.add(new ActivityInfo("Izakaya Hopping in Shinjuku", "Guided tour of traditional Japanese pubs", 70.0, 4.6));
        tokyoActivities.put("food", tokyoFood);
        
        destinationActivities.put("Tokyo, Japan", tokyoActivities);
        
        // Add more destinations with activities
    }
    
    /**
     * Get activities for a specific destination and theme.
     *
     * @param destination The destination location
     * @param theme The activity theme (sightseeing, adventure, food, etc.)
     * @param maxBudget The maximum budget for activities
     * @param count The number of activities to return
     * @return A list of Activity objects
     */
    public List<TripPlan.Activity> getActivities(String destination, String theme, double maxBudget, int count) {
        return findActivities(destination, theme, maxBudget, count);
    }
    
    /**
     * Get activities by destination, theme, and budget with a specified count.
     *
     * @param destination The destination location
     * @param theme The activity theme (sightseeing, adventure, food, etc.)
     * @param dailyBudget The daily budget for activities
     * @param count The number of activities to return
     * @return A list of Activity objects
     */
    public List<TripPlan.Activity> getActivitiesByDestination(String destination, String theme, double dailyBudget, int count) {
        return findActivities(destination, theme, dailyBudget, count);
    }
    
    /**
     * Find activities for a specific destination and theme.
     *
     * @param destination The destination location
     * @param theme The activity theme (sightseeing, adventure, food, etc.)
     * @param maxBudget The maximum budget for activities
     * @param userTheme The user's preferred theme
     * @return A list of Activity objects
     */
    public List<TripPlan.Activity> findActivities(String destination, String theme, double maxBudget, String userTheme) {
        // Default to 2 activities per category
        int count = 2;
        return findActivities(destination, theme, maxBudget, count);
    }
    
    /**
     * Find activities for a specific destination and theme.
     *
     * @param destination The destination location
     * @param theme The activity theme (sightseeing, adventure, food, etc.)
     * @param maxBudget The maximum budget for activities
     * @param count The number of activities to return
     * @return A list of Activity objects
     */
    public List<TripPlan.Activity> findActivities(String destination, String theme, double maxBudget, int count) {
        // In a real application, this would call an external API like Google Places or TripAdvisor
        // For the MVP, we'll use our mock activity data
        
        // Get activities for the destination or create generic ones if not found
        Map<String, List<ActivityInfo>> themeActivities = destinationActivities.getOrDefault(destination, createGenericActivities());
        
        // Get activities for the theme or use sightseeing as default
        List<ActivityInfo> activities = themeActivities.getOrDefault(theme.toLowerCase(), 
                                                                   themeActivities.getOrDefault("sightseeing", new ArrayList<>()));
        
        // If still no activities found, create generic ones
        if (activities.isEmpty()) {
            activities = createGenericActivities().get("sightseeing");
        }
        
        // Filter activities by budget
        List<ActivityInfo> affordableActivities = new ArrayList<>();
        double budgetPerActivity = maxBudget / count;
        
        for (ActivityInfo activity : activities) {
            if (activity.price <= budgetPerActivity * 1.5) { // Allow some flexibility in budget
                affordableActivities.add(activity);
            }
        }
        
        // If no affordable activities found, take the cheapest options
        if (affordableActivities.isEmpty()) {
            activities.sort(Comparator.comparingDouble(a -> a.price));
            affordableActivities = activities.subList(0, Math.min(count, activities.size()));
        }
        
        // Shuffle and select the requested number of activities
        Collections.shuffle(affordableActivities);
        List<ActivityInfo> selectedActivities = affordableActivities.subList(0, Math.min(count, affordableActivities.size()));
        
        // Convert to Activity objects
        List<TripPlan.Activity> result = new ArrayList<>();
        for (ActivityInfo info : selectedActivities) {
            TripPlan.Activity activity = new TripPlan.Activity(
                    info.name,
                    "Activity",
                    "Location",
                    info.description,
                    info.price,
                    120 // Default duration in minutes
            );
            activity.setRating(info.rating);
            result.add(activity);
        }
        
        return result;
    }
    
    /**
     * Estimate the cost of an activity for a specific destination and theme.
     *
     * @param destination The destination location
     * @param theme The activity theme
     * @return The estimated cost of the activity
     */
    public double estimateActivityCost(String destination, String theme) {
        // Get activities for the destination or create generic ones if not found
        Map<String, List<ActivityInfo>> themeActivities = destinationActivities.getOrDefault(destination, createGenericActivities());
        
        // Get activities for the theme or use sightseeing as default
        List<ActivityInfo> activities = themeActivities.getOrDefault(theme.toLowerCase(), 
                                                                   themeActivities.getOrDefault("sightseeing", new ArrayList<>()));
        
        // If still no activities found, create generic ones
        if (activities.isEmpty()) {
            activities = createGenericActivities().get("sightseeing");
        }
        
        // Calculate average cost
        double totalCost = 0.0;
        for (ActivityInfo activity : activities) {
            totalCost += activity.price;
        }
        
        return activities.isEmpty() ? 50.0 : totalCost / activities.size(); // Default to $50 if no activities found
    }
    
    /**
     * Get the estimated activities cost for a destination.
     *
     * @param destination The destination location
     * @param theme The activity theme
     * @param days The number of days
     * @param activitiesPerDay The number of activities per day
     * @return The estimated total cost for activities
     */
    public double getEstimatedActivitiesCost(String destination, String theme, int days, int activitiesPerDay) {
        Map<String, List<ActivityInfo>> themeActivities = destinationActivities.getOrDefault(destination, createGenericActivities());
        
        List<ActivityInfo> activities = themeActivities.getOrDefault(theme.toLowerCase(), 
                                                                   themeActivities.getOrDefault("sightseeing", new ArrayList<>()));
        
        if (activities.isEmpty()) {
            activities = createGenericActivities().get("sightseeing");
        }
        
        // Calculate average price per activity
        double avgPrice = activities.stream()
                .mapToDouble(a -> a.price)
                .average()
                .orElse(20.0);
        
        return avgPrice * days * activitiesPerDay;
    }
    
    /**
     * Create generic activity options for destinations without specific data.
     *
     * @return A map of generic activities by theme
     */
    private Map<String, List<ActivityInfo>> createGenericActivities() {
        Map<String, List<ActivityInfo>> genericActivities = new HashMap<>();
        
        List<ActivityInfo> sightseeing = new ArrayList<>();
        sightseeing.add(new ActivityInfo("City Tour", "Guided tour of main attractions", 30.0, 4.5));
        sightseeing.add(new ActivityInfo("Museum Visit", "Local history and art museum", 15.0, 4.3));
        sightseeing.add(new ActivityInfo("Historic District Walk", "Self-guided tour of historic area", 0.0, 4.2));
        genericActivities.put("sightseeing", sightseeing);
        
        List<ActivityInfo> adventure = new ArrayList<>();
        adventure.add(new ActivityInfo("Outdoor Excursion", "Nature adventure outside the city", 45.0, 4.6));
        adventure.add(new ActivityInfo("Local Experience", "Unique local activity", 35.0, 4.4));
        genericActivities.put("adventure", adventure);
        
        List<ActivityInfo> food = new ArrayList<>();
        food.add(new ActivityInfo("Local Cuisine Dinner", "Traditional local food experience", 40.0, 4.5));
        food.add(new ActivityInfo("Food Tour", "Sample various local specialties", 35.0, 4.7));
        genericActivities.put("food", food);
        
        List<ActivityInfo> relaxation = new ArrayList<>();
        relaxation.add(new ActivityInfo("Spa Day", "Relaxing spa treatment", 80.0, 4.8));
        relaxation.add(new ActivityInfo("Park Visit", "Relaxing time in local park", 0.0, 4.3));
        genericActivities.put("relaxation", relaxation);
        
        return genericActivities;
    }
    
    /**
     * Inner class to store activity information.
     */
    private static class ActivityInfo {
        private final String name;
        private final String description;
        private final double price;
        private final double rating;
        
        public ActivityInfo(String name, String description, double price, double rating) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.rating = rating;
        }
    }
}