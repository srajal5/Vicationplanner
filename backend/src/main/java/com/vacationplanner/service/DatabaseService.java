package com.vacationplanner.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.vacationplanner.model.TripPlan;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
public class DatabaseService {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> tripCollection;
    private boolean connected = false;

    @Value("${MONGODB_URI:mongodb://localhost:27017}")
    private String mongoUri;

    /**
     * Initialize the MongoDB connection after construction.
     */
    @PostConstruct
    public void init() {
        try {
            // Create client from configured URI (can point to Atlas)
            mongoClient = MongoClients.create(mongoUri);
            database = mongoClient.getDatabase("vacationplanner");
            tripCollection = database.getCollection("trips");
            connected = true;
            System.out.println("Connected to MongoDB successfully: " + mongoUri);
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
            System.err.println("Operating in offline mode - trips will not be saved");
        }
    }
    
    /**
     * Save a trip plan to the database.
     *
     * @param tripPlan The trip plan to save
     * @return The ID of the saved trip plan, or null if saving failed
     */
    public String saveTripPlan(TripPlan tripPlan) {
        if (!connected) {
            System.err.println("Cannot save trip plan - not connected to database");
            return null;
        }
        
        try {
            // Convert TripPlan to Document
            Document tripDoc = tripPlanToDocument(tripPlan);
            
            // Insert or update the document
            if (tripPlan.getId() != null && !tripPlan.getId().isEmpty()) {
                try {
                    new ObjectId(tripPlan.getId()); // Validate the ID
                    tripDoc.put("_id", new ObjectId(tripPlan.getId()));
                    tripCollection.replaceOne(Filters.eq("_id", new ObjectId(tripPlan.getId())), tripDoc);
                    return tripPlan.getId();
                } catch (IllegalArgumentException e) {
                    // Invalid ObjectId, generate a new one
                    tripCollection.insertOne(tripDoc);
                    return tripDoc.getObjectId("_id").toString();
                }
            } else {
                tripCollection.insertOne(tripDoc);
                return tripDoc.getObjectId("_id").toString();
            }
        } catch (Exception e) {
            System.err.println("Error saving trip plan: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Load a trip plan from the database.
     *
     * @param tripId The ID of the trip plan to load
     * @return The loaded trip plan, or null if loading failed
     */
    public TripPlan loadTripPlan(String tripId) {
        if (!connected) {
            System.err.println("Cannot load trip plan - not connected to database");
            return null;
        }
        
        try {
            Document tripDoc = tripCollection.find(Filters.eq("_id", new ObjectId(tripId))).first();
            if (tripDoc == null) {
                return null;
            }
            
            return documentToTripPlan(tripDoc);
        } catch (Exception e) {
            System.err.println("Error loading trip plan: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get all saved trip plans.
     *
     * @return A list of all saved trip plans
     */
    public List<TripPlan> getAllTripPlans() {
        List<TripPlan> tripPlans = new ArrayList<>();
        
        if (!connected) {
            System.err.println("Cannot load trip plans - not connected to database");
            return tripPlans;
        }
        
        try {
            for (Document tripDoc : tripCollection.find()) {
                TripPlan tripPlan = documentToTripPlan(tripDoc);
                tripPlans.add(tripPlan);
            }
        } catch (Exception e) {
            System.err.println("Error loading trip plans: " + e.getMessage());
        }
        
        return tripPlans;
    }
    
    /**
     * Delete a trip plan from the database.
     *
     * @param tripId The ID of the trip plan to delete
     * @return True if deletion was successful, false otherwise
     */
    public boolean deleteTripPlan(String tripId) {
        if (!connected) {
            System.err.println("Cannot delete trip plan - not connected to database");
            return false;
        }
        
        try {
            tripCollection.deleteOne(Filters.eq("_id", new ObjectId(tripId)));
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting trip plan: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close the database connection.
     */
    @PreDestroy
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            connected = false;
        }
    }
    
    /**
     * Convert a TripPlan object to a MongoDB Document.
     *
     * @param tripPlan The trip plan to convert
     * @return The MongoDB Document
     */
    private Document tripPlanToDocument(TripPlan tripPlan) {
        Document doc = new Document();
        
        // Basic trip information
        doc.put("destination", tripPlan.getDestination());
        doc.put("totalBudget", tripPlan.getTotalBudget());
        doc.put("currency", tripPlan.getCurrency());
        doc.put("startDate", tripPlan.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        doc.put("endDate", tripPlan.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        doc.put("theme", tripPlan.getTheme());
        doc.put("groupSize", tripPlan.getGroupSize());
        
        // Transportation
        if (tripPlan.getTransportation() != null) {
            Document transportDoc = new Document();
            transportDoc.put("type", tripPlan.getTransportation().getType());
            transportDoc.put("origin", tripPlan.getTransportation().getOrigin());
            transportDoc.put("destination", tripPlan.getTransportation().getDestination());
            transportDoc.put("departureDate", tripPlan.getTransportation().getDepartureDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            transportDoc.put("returnDate", tripPlan.getTransportation().getReturnDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            transportDoc.put("provider", tripPlan.getTransportation().getProvider());
            transportDoc.put("cost", tripPlan.getTransportation().getCost());
            doc.put("transportation", transportDoc);
        }
        
        // Accommodation
        if (tripPlan.getAccommodation() != null) {
            Document accomDoc = new Document();
            accomDoc.put("name", tripPlan.getAccommodation().getName());
            accomDoc.put("type", tripPlan.getAccommodation().getType());
            accomDoc.put("location", tripPlan.getAccommodation().getLocation());
            accomDoc.put("checkInDate", tripPlan.getAccommodation().getCheckInDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            accomDoc.put("checkOutDate", tripPlan.getAccommodation().getCheckOutDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            accomDoc.put("cost", tripPlan.getAccommodation().getCost());
            accomDoc.put("rating", tripPlan.getAccommodation().getRating());
            doc.put("accommodation", accomDoc);
        }
        
        // Daily Itineraries
        if (tripPlan.getDailyItineraries() != null && !tripPlan.getDailyItineraries().isEmpty()) {
            List<Document> itineraryDocs = new ArrayList<>();
            
            for (TripPlan.DailyItinerary itinerary : tripPlan.getDailyItineraries()) {
                Document itineraryDoc = new Document();
                itineraryDoc.put("day", itinerary.getDay());
                itineraryDoc.put("date", itinerary.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                
                // Activities
                List<Document> activityDocs = new ArrayList<>();
                for (TripPlan.Activity activity : itinerary.getActivities()) {
                    Document activityDoc = new Document();
                    activityDoc.put("name", activity.getName());
                    activityDoc.put("description", activity.getDescription());
                    activityDoc.put("cost", activity.getCost());
                    activityDoc.put("rating", activity.getRating());
                    activityDocs.add(activityDoc);
                }
                itineraryDoc.put("activities", activityDocs);
                
                itineraryDocs.add(itineraryDoc);
            }
            
            doc.put("dailyItineraries", itineraryDocs);
        }
        
        // Budget Breakdown
        if (tripPlan.getBudgetBreakdown() != null) {
            Document budgetDoc = new Document();
            budgetDoc.put("transportationCost", tripPlan.getBudgetBreakdown().getTransportationCost());
            budgetDoc.put("accommodationCost", tripPlan.getBudgetBreakdown().getAccommodationCost());
            budgetDoc.put("foodCost", tripPlan.getBudgetBreakdown().getFoodCost());
            budgetDoc.put("activitiesCost", tripPlan.getBudgetBreakdown().getActivitiesCost());
            budgetDoc.put("miscCost", tripPlan.getBudgetBreakdown().getMiscCost());
            budgetDoc.put("totalCost", tripPlan.getBudgetBreakdown().getTotalCost());
            doc.put("budgetBreakdown", budgetDoc);
        }
        
        return doc;
    }
    
    /**
     * Convert a MongoDB Document to a TripPlan object.
     *
     * @param doc The MongoDB Document
     * @return The TripPlan object
     */
    private TripPlan documentToTripPlan(Document doc) {
        TripPlan tripPlan = new TripPlan();
        
        // Set ID
        tripPlan.setId(doc.getObjectId("_id").toString());
        
        // Basic trip information
        tripPlan.setDestination(doc.getString("destination"));
        tripPlan.setTotalBudget(doc.getDouble("totalBudget"));
        tripPlan.setCurrency(doc.getString("currency"));
        tripPlan.setStartDate(LocalDate.parse(doc.getString("startDate")));
        tripPlan.setEndDate(LocalDate.parse(doc.getString("endDate")));
        tripPlan.setTheme(doc.getString("theme"));
        tripPlan.setGroupSize(doc.getInteger("groupSize"));
        
        // Transportation
        Document transportDoc = (Document) doc.get("transportation");
        if (transportDoc != null) {
            TripPlan.Transportation transportation = new TripPlan.Transportation(
                    transportDoc.getString("type"),
                    transportDoc.getString("origin"),
                    transportDoc.getString("destination"),
                    LocalDate.parse(transportDoc.getString("departureDate")),
                    LocalDate.parse(transportDoc.getString("returnDate")),
                    transportDoc.getString("provider"),
                    transportDoc.getDouble("cost")
            );
            tripPlan.setTransportation(transportation);
        }
        
        // Accommodation
        Document accomDoc = (Document) doc.get("accommodation");
        if (accomDoc != null) {
            TripPlan.Accommodation accommodation = new TripPlan.Accommodation(
                    accomDoc.getString("name"),
                    accomDoc.getString("type"),
                    accomDoc.getString("location"),
                    accomDoc.getDouble("cost"),
                    accomDoc.getDouble("rating"),
                    "Mock Hotel Provider"
            );
            accommodation.setCheckInDate(LocalDate.parse(accomDoc.getString("checkInDate")));
            accommodation.setCheckOutDate(LocalDate.parse(accomDoc.getString("checkOutDate")));
            tripPlan.setAccommodation(accommodation);
        }
        
        // Daily Itineraries
        List<Document> itineraryDocs = (List<Document>) doc.get("dailyItineraries");
        if (itineraryDocs != null) {
            List<TripPlan.DailyItinerary> itineraries = new ArrayList<>();
            
            for (Document itineraryDoc : itineraryDocs) {
                TripPlan.DailyItinerary itinerary = new TripPlan.DailyItinerary(
                        itineraryDoc.getInteger("day"),
                        LocalDate.parse(itineraryDoc.getString("date")),
                        0.0 // Default daily budget
                );
                
                // Activities
                List<Document> activityDocs = (List<Document>) itineraryDoc.get("activities");
                if (activityDocs != null) {
                    for (Document activityDoc : activityDocs) {
                        TripPlan.Activity activity = new TripPlan.Activity(
                                activityDoc.getString("name"),
                                activityDoc.getString("description"),
                                activityDoc.getDouble("cost"),
                                activityDoc.getDouble("rating")
                        );
                        itinerary.addActivity(activity);
                    }
                }
                
                itineraries.add(itinerary);
            }
            
            tripPlan.setDailyItineraries(itineraries);
        }
        
        // Budget Breakdown
        Document budgetDoc = (Document) doc.get("budgetBreakdown");
        if (budgetDoc != null) {
            TripPlan.BudgetBreakdown budgetBreakdown = new TripPlan.BudgetBreakdown(
                    budgetDoc.getDouble("transportationCost"),
                    budgetDoc.getDouble("accommodationCost"),
                    budgetDoc.getDouble("foodCost"),
                    budgetDoc.getDouble("activitiesCost"),
                    budgetDoc.getDouble("miscCost")
            );
            tripPlan.setBudgetBreakdown(budgetBreakdown);
        }
        
        return tripPlan;
    }
    
    /**
     * Check if the database connection is active.
     *
     * @return True if connected, false otherwise
     */
    public boolean isConnected() {
        return connected;
    }
}