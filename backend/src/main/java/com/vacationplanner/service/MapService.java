package com.vacationplanner.service;

import com.vacationplanner.model.TripPlan;
import com.vacationplanner.util.EnvLoader;
import org.springframework.stereotype.Service;

// ChronoUnit no longer required (related helper removed)

/**
 * Service for handling map-related operations using multiple map providers.
 * This service now generates map HTML content to be used by a frontend.
 */
@Service
public class MapService {

    // Map API configuration from environment variables
    private static final String GOOGLE_MAPS_API_KEY = EnvLoader.getEnv("GOOGLE_MAPS_API_KEY");
    private static final String USE_OPENSTREETMAP = EnvLoader.getEnv("USE_OPENSTREETMAP", "true");
    private static final String OPENSTREETMAP_TILE_SERVER = EnvLoader.getEnv("OPENSTREETMAP_TILE_SERVER", "https://tile.openstreetmap.org");

    // Mapbox API (alternative free option)
    private static final String MAPBOX_API_KEY = EnvLoader.getEnv("MAPBOX_API_KEY");
    // private static final String MAPBOX_BASE_URL = EnvLoader.getEnv("MAPBOX_BASE_URL", "https://api.mapbox.com"); // This variable was unused

    /**
     * Generates the complete HTML document for a map showing the trip route.
     * This can be sent to a frontend to be rendered in a browser or iframe.
     *
     * @param tripPlan The trip plan containing route information.
     * @return A String containing the full HTML for the map.
     */
    public String getMapHtmlForTrip(TripPlan tripPlan) {
        return generateMapHTML(tripPlan);
    }

    /**
     * Generate HTML content with embedded maps showing the trip route.
     * Uses OpenStreetMap with Leaflet by default, or Google Maps if configured.
     *
     * @param tripPlan The trip plan containing route information
     * @return HTML content as a string
     */
    private String generateMapHTML(TripPlan tripPlan) {
        // Use OpenStreetMap if enabled or no Google Maps key available
        boolean useOsm = "true".equalsIgnoreCase(USE_OPENSTREETMAP) || GOOGLE_MAPS_API_KEY == null || GOOGLE_MAPS_API_KEY.isEmpty();
        boolean useMapbox = MAPBOX_API_KEY != null && !MAPBOX_API_KEY.isEmpty();

        if (useOsm) {
            return generateOpenStreetMapHTML(tripPlan);
        } else if (useMapbox) {
            return generateMapboxHTML(tripPlan);
        } else {
            return generateGoogleMapsHTML(tripPlan);
        }
    }

    /**
     * Generate HTML content with OpenStreetMap and Leaflet (100% FREE)
     */
    private String generateOpenStreetMapHTML(TripPlan tripPlan) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
            .append("<html>\n")
            .append("<head>\n")
            .append("    <meta charset='utf-8'>\n")
            .append("    <title>Trip Route Map - OpenStreetMap</title>\n")
            .append("    <link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css' />\n")
            .append("    <style> #map { height: 100%; width: 100%; } html, body { height: 100%; margin: 0; padding: 0; } </style>\n")
            .append("</head>\n")
            .append("<body>\n")
            .append("    <div id='map'></div>\n")
            .append("    <script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>\n")
            .append("    <script>\n")
            .append(generateLeafletJavaScript(tripPlan))
            .append("    </script>\n")
            .append("</body>\n")
            .append("</html>");
        return html.toString();
    }

    /**
     * Generate HTML content with Google Maps (PAID)
     */
    private String generateGoogleMapsHTML(TripPlan tripPlan) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
            .append("<html>\n")
            .append("<head>\n")
            .append("    <meta charset='utf-8'>\n")
            .append("    <title>Trip Route Map - Google Maps</title>\n")
            .append("    <style> #map { height: 100%; width: 100%; } html, body { height: 100%; margin: 0; padding: 0; } </style>\n")
            .append("</head>\n")
            .append("<body>\n")
            .append("    <div id='map'></div>\n")
            .append("    <script>\n")
            .append(generateGoogleMapsJavaScript(tripPlan))
            .append("    </script>\n")
            .append("    <script async defer src='https://maps.googleapis.com/maps/api/js?key=")
            .append(GOOGLE_MAPS_API_KEY)
            .append("&callback=initMap'></script>\n")
            .append("</body>\n")
            .append("</html>");
        return html.toString();
    }

    /**
     * Generate HTML content with Mapbox (FREE - 50,000 requests/month)
     */
    private String generateMapboxHTML(TripPlan tripPlan) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
            .append("<html>\n")
            .append("<head>\n")
            .append("    <meta charset='utf-8'>\n")
            .append("    <title>Trip Route Map - Mapbox</title>\n")
            .append("    <script src='https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.js'></script>\n")
            .append("    <link href='https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.css' rel='stylesheet' />\n")
            .append("    <style> #map { height: 100%; width: 100%; } html, body { height: 100%; margin: 0; padding: 0; } </style>\n")
            .append("</head>\n")
            .append("<body>\n")
            .append("    <div id='map'></div>\n")
            .append("    <script>\n")
            .append(generateMapboxJavaScript(tripPlan))
            .append("    </script>\n")
            .append("</body>\n")
            .append("</html>");
        return html.toString();
    }

    /**
     * Generate JavaScript code for Google Maps initialization.
     */
    private String generateGoogleMapsJavaScript(TripPlan tripPlan) {
        String[] coords = getDestinationCoordinates(tripPlan.getDestination());
        String lat = coords[0];
        String lng = coords[1];
        
        StringBuilder js = new StringBuilder();
        js.append("function initMap() {\n")
          .append("    const center = { lat: ").append(lat).append(", lng: ").append(lng).append(" };\n")
          .append("    const map = new google.maps.Map(document.getElementById('map'), {\n")
          .append("        zoom: 10,\n")
          .append("        center: center,\n")
          .append("    });\n")
          .append("    new google.maps.Marker({ position: center, map: map, title: '").append(tripPlan.getDestination()).append("' });\n")
          .append("}\n");
        return js.toString();
    }
    
    /**
     * Generate JavaScript code for Leaflet (OpenStreetMap) initialization.
     */
    private String generateLeafletJavaScript(TripPlan tripPlan) {
        String[] coords = getDestinationCoordinates(tripPlan.getDestination());
        String lat = coords[0];
        String lng = coords[1];

        StringBuilder js = new StringBuilder();
        js.append("const map = L.map('map').setView([").append(lat).append(", ").append(lng).append("], 10);\n")
          .append("L.tileLayer('").append(OPENSTREETMAP_TILE_SERVER).append("/{z}/{x}/{y}.png', {\n")
          .append("    attribution: '© OpenStreetMap contributors'\n")
          .append("}).addTo(map);\n")
          .append("L.marker([").append(lat).append(", ").append(lng).append("]).addTo(map)\n")
          .append("    .bindPopup('<b>").append(tripPlan.getDestination()).append("</b>').openPopup();\n");

        return js.toString();
    }

    /**
     * Generate JavaScript code for Mapbox initialization.
     */
    private String generateMapboxJavaScript(TripPlan tripPlan) {
        String[] coords = getDestinationCoordinates(tripPlan.getDestination());
        String lat = coords[0];
        String lng = coords[1];

        StringBuilder js = new StringBuilder();
        js.append("mapboxgl.accessToken = '").append(MAPBOX_API_KEY).append("';\n")
          .append("const map = new mapboxgl.Map({\n")
          .append("    container: 'map',\n")
          .append("    style: 'mapbox://styles/mapbox/streets-v11',\n")
          .append("    center: [").append(lng).append(", ").append(lat).append("],\n")
          .append("    zoom: 10\n")
          .append("});\n")
          .append("new mapboxgl.Marker()\n")
          .append("    .setLngLat([").append(lng).append(", ").append(lat).append("])\n")
          .append("    .addTo(map);\n");
        return js.toString();
    }
    
    /**
     * Get coordinates for destinations as string array [lat, lng]
     */
    private String[] getDestinationCoordinates(String destination) {
        switch (destination) {
            case "Paris, France": return new String[]{"48.8566", "2.3522"};
            case "London, UK": return new String[]{"51.5074", "-0.1278"};
            case "Tokyo, Japan": return new String[]{"35.6762", "139.6503"};
            case "New York City, USA": return new String[]{"40.7128", "-74.0060"};
            case "Sydney, Australia": return new String[]{"-33.8688", "151.2093"};
            case "Rome, Italy": return new String[]{"41.9028", "12.4964"};
            // Add more cases here
            default: return new String[]{"48.8566", "2.3522"}; // Default to Paris
        }
    }

    // calculateTripDuration was unused; removed to reduce dead code flagged by static analysis
}