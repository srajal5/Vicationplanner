package com.vacationplanner.controller;

import com.vacationplanner.model.TripPlan;
import com.vacationplanner.service.ExportService;
import com.vacationplanner.service.TripPlannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "http://localhost:3000")
public class ExportController {

    @Autowired
    private ExportService exportService;
    
    @Autowired
    private TripPlannerService tripPlannerService;

    @GetMapping("/pdf/{tripId}")
    public ResponseEntity<Resource> exportToPdf(@PathVariable String tripId) {
        try {
            // Get trip plan first
            TripPlan tripPlan = tripPlannerService.getTripById(tripId);
            if (tripPlan == null) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = exportService.exportToPdf(tripPlan, "trip-plan-" + tripId + ".pdf");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"trip-plan-" + tripId + ".pdf\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/excel/{tripId}")
    public ResponseEntity<Resource> exportToExcel(@PathVariable String tripId) {
        try {
            // Get trip plan first
            TripPlan tripPlan = tripPlannerService.getTripById(tripId);
            if (tripPlan == null) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = exportService.exportToExcel(tripPlan, "trip-plan-" + tripId + ".xlsx");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"trip-plan-" + tripId + ".xlsx\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
