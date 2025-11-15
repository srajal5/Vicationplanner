package com.vacationplanner.controller;

import com.vacationplanner.model.TripPlan;
import com.vacationplanner.service.BookingService;
import com.vacationplanner.service.TripPlannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
@CrossOrigin(origins = "http://localhost:3000")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private TripPlannerService tripPlannerService;

    @PostMapping("/book")
    public ResponseEntity<BookingService.BookingResult> bookTrip(
            @Valid @RequestBody Map<String, Object> bookingRequest) {
        try {
            String tripId = (String) bookingRequest.get("tripId");
            String travelerName = (String) bookingRequest.get("travelerName");
            String travelerEmail = (String) bookingRequest.get("travelerEmail");
            String travelerPhone = (String) bookingRequest.get("travelerPhone");
            String paymentLast4 = (String) bookingRequest.get("paymentLast4");
            
            // Get trip plan from service
            TripPlan tripPlan = tripPlannerService.getTripById(tripId);
            if (tripPlan == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Validate required fields
            if (travelerName == null || travelerEmail == null || 
                travelerPhone == null || paymentLast4 == null) {
                return ResponseEntity.badRequest().build();
            }
            
            BookingService.BookingResult result = bookingService.bookTrip(
                tripPlan, travelerName, travelerEmail, travelerPhone, paymentLast4);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/availability/{tripId}")
    public ResponseEntity<Map<String, Object>> checkAvailability(@PathVariable String tripId) {
        try {
            Map<String, Object> availability = bookingService.checkAvailability(tripId);
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
}
