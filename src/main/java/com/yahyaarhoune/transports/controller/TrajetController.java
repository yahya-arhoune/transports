package com.yahyaarhoune.transports.controller;

import com.yahyaarhoune.transports.models.*;
import com.yahyaarhoune.transports.service.TrajetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.yahyaarhoune.transports.dto.TrajetCreationRequestDTO;

// import java.time.LocalDateTime; // Not directly needed in controller if Trajet object has them
import java.util.List;
import java.util.Optional; // Import Optional

@RestController
@RequestMapping("/api/trajets")
public class TrajetController {

    private final TrajetService trajetService;

    @Autowired
    public TrajetController(TrajetService trajetService) {
        this.trajetService = trajetService;
    }

    @PostMapping
    public ResponseEntity<Trajet> createTrajet(@RequestBody TrajetCreationRequestDTO trajetDto) {
        Trajet createdTrajet = trajetService.createTrajetFromDTO(trajetDto);
        return new ResponseEntity<>(createdTrajet, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trajet> getTrajetById(@PathVariable Integer id) {
        return trajetService.getTrajetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Trajet>> getAllTrajets() {
        List<Trajet> trajets = trajetService.getAllTrajets();
        return ResponseEntity.ok(trajets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trajet> updateTrajet(@PathVariable Integer id, @RequestBody Trajet trajetDetails) {
        Optional<Trajet> optionalUpdatedTrajet = trajetService.updateTrajet(id, trajetDetails);
        return optionalUpdatedTrajet
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Or handle based on service contract for empty Optional
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrajet(@PathVariable Integer id) {
        boolean deleted = trajetService.deleteTrajet(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            // This implies the service returned false (e.g., trajet not found but didn't throw)
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{trajetId}/passagers")
    public ResponseEntity<List<UtilisateurStandard>> getPassagersForTrajet(@PathVariable Integer trajetId) {
        List<UtilisateurStandard> passagers = trajetService.getPassagersForTrajet(trajetId);
        return ResponseEntity.ok(passagers);
    }

    @GetMapping("/{trajetId}/incidents")
    public ResponseEntity<List<Incident>> getIncidentsForTrajet(@PathVariable Integer trajetId) {
        List<Incident> incidents = trajetService.getIncidentsForTrajet(trajetId);
        return ResponseEntity.ok(incidents);
    }

    // --- Methods below require additions to TrajetService interface ---

    // To fix "Cannot resolve method 'getTicketsForTrajet'", add to TrajetService:
    // List<Ticket> getTicketsForTrajet(Integer trajetId);
    @GetMapping("/{trajetId}/tickets")
    public ResponseEntity<List<Ticket>> getTicketsForTrajet(@PathVariable Integer trajetId) {
        // This will compile once getTicketsForTrajet is in the TrajetService interface
        List<Ticket> tickets = trajetService.getTicketsForTrajet(trajetId);
        return ResponseEntity.ok(tickets);
    }

    // To fix "Cannot resolve method 'addPassagerToTrajet'", add to TrajetService:
    // Trajet addPassagerToTrajet(Integer trajetId, Integer utilisateurId);
    @PostMapping("/{trajetId}/passagers/{utilisateurId}")
    public ResponseEntity<Trajet> addPassagerToTrajet(@PathVariable Integer trajetId, @PathVariable Integer utilisateurId) {
        // This will compile once addPassagerToTrajet is in the TrajetService interface
        Trajet trajet = trajetService.addPassagerToTrajet(trajetId, utilisateurId);
        return ResponseEntity.ok(trajet);
    }

    // To fix "Cannot resolve method 'removePassagerFromTrajet'", add to TrajetService:
    // Trajet removePassagerFromTrajet(Integer trajetId, Integer utilisateurId);
    @DeleteMapping("/{trajetId}/passagers/{utilisateurId}")
    public ResponseEntity<Trajet> removePassagerFromTrajet(@PathVariable Integer trajetId, @PathVariable Integer utilisateurId) {
        // This will compile once removePassagerFromTrajet is in the TrajetService interface
        Trajet trajet = trajetService.removePassagerFromTrajet(trajetId, utilisateurId);
        return ResponseEntity.ok(trajet);
    }

}