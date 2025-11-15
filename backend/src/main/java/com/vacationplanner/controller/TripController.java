package com.vacationplanner.controller;

import com.vacationplanner.model.TripPlan;
import com.vacationplanner.model.TripPreferences;
import com.vacationplanner.service.TripPlannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "http://localhost:3000")
public class TripController {

    @Autowired
    private TripPlannerService tripPlannerService;

    @PostMapping("/plan")
    public ResponseEntity<TripPlan> planTrip(@Valid @RequestBody TripPreferences preferences) {
        try {
            TripPlan tripPlan = tripPlannerService.planTrip(preferences);
            return ResponseEntity.ok(tripPlan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripPlan> getTrip(@PathVariable String id) {
        TripPlan tripPlan = tripPlannerService.getTripById(id);
        if (tripPlan != null) {
            return ResponseEntity.ok(tripPlan);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<TripPlan>> getAllTrips() {
        List<TripPlan> trips = tripPlannerService.getAllTrips();
        return ResponseEntity.ok(trips);
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<String> saveTrip(@PathVariable String id) {
        boolean saved = tripPlannerService.saveTrip(id);
        if (saved) {
            return ResponseEntity.ok("Trip saved successfully");
        }
        return ResponseEntity.badRequest().body("Failed to save trip");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTrip(@PathVariable String id) {
        boolean deleted = tripPlannerService.deleteTrip(id);
        if (deleted) {
            return ResponseEntity.ok("Trip deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}
