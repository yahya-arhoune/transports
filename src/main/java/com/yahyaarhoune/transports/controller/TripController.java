package com.yahyaarhoune.transports.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trips") // Base path for trip-related endpoints
public class TripController {

    // Placeholder - Replace with actual service call and user ID check later
    @GetMapping("/history/passenger/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getPassengerTripHistory(@PathVariable Long userId) {
        System.out.println("Backend: Received request for trip history for user ID: " + userId);

        // --- Create Fake Trip Data ---
        Map<String, Object> trip1 = new HashMap<>();
        trip1.put("id", 101);
        trip1.put("origine", "Downtown Central");
        trip1.put("destination", "North Suburbs Mall");
        trip1.put("heureDepart", LocalDateTime.now().minusDays(1).toString()); // Yesterday
        trip1.put("heureArrivee", LocalDateTime.now().minusDays(1).plusHours(1).toString());
        trip1.put("statut", "COMPLETED");

        Map<String, Object> trip2 = new HashMap<>();
        trip2.put("id", 102);
        trip2.put("origine", "Airport Terminal B");
        trip2.put("destination", "Grand Hotel");
        trip2.put("heureDepart", LocalDateTime.now().minusHours(5).toString()); // Earlier today
        trip2.put("heureArrivee", LocalDateTime.now().minusHours(4).toString());
        trip2.put("statut", "COMPLETED");

        Map<String, Object> trip3 = new HashMap<>();
        trip3.put("id", 103);
        trip3.put("origine", "West Side Station");
        trip3.put("destination", "University Campus");
        trip3.put("heureDepart", LocalDateTime.now().plusHours(2).toString()); // Upcoming
        trip3.put("heureArrivee", LocalDateTime.now().plusHours(3).toString());
        trip3.put("statut", "UPCOMING");
        // --- End Fake Data ---

        List<Map<String, Object>> fakeTrips = Arrays.asList(trip1, trip2, trip3);

        // Simulate filtering by userId (though not really done here)
        if (userId <= 0) { // Simple check
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(fakeTrips);
    }

    // Add other endpoints later: /trips/{id}, /trips/search etc.
}