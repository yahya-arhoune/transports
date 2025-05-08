package com.yahyaarhoune.transports.controller;

import com.yahyaarhoune.transports.models.Feedback;
import com.yahyaarhoune.transports.models.Ticket;
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.UtilisateurStandard;
import com.yahyaarhoune.transports.service.UtilisateurStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; // Import Optional

@RestController
@RequestMapping("/api/utilisateurs-standard")
public class UtilisateurStandardController {

    private final UtilisateurStandardService utilisateurStandardService;

    @Autowired
    public UtilisateurStandardController(UtilisateurStandardService utilisateurStandardService) {
        this.utilisateurStandardService = utilisateurStandardService;
    }

    @PostMapping
    public ResponseEntity<UtilisateurStandard> createUtilisateurStandard(@RequestBody UtilisateurStandard utilisateurStandard) {
        UtilisateurStandard createdUser = utilisateurStandardService.createUtilisateurStandard(utilisateurStandard);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurStandard> getUtilisateurStandardById(@PathVariable Integer id) {
        return utilisateurStandardService.getUtilisateurStandardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UtilisateurStandard>> getAllUtilisateursStandard() {
        List<UtilisateurStandard> users = utilisateurStandardService.getAllUtilisateursStandard();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurStandard> updateUtilisateurStandard(
            @PathVariable Integer id,
            @RequestBody UtilisateurStandard utilisateurStandardDetails) {
        Optional<UtilisateurStandard> optionalUpdatedUser = utilisateurStandardService.updateUtilisateurStandard(id, utilisateurStandardDetails);

        return optionalUpdatedUser
                .map(ResponseEntity::ok) // If user is present in Optional, wrap in ResponseEntity.ok()
                .orElse(ResponseEntity.notFound().build()); // If Optional is empty (e.g., user not found by service, though your service throws)
        // Or if the update operation itself could result in an empty Optional.
        // Given your service's current update logic, it should always contain the user
        // if found, or throw ResourceNotFoundException.
        // So, an alternative and perhaps more direct way if service guarantees
        // to throw or return value:
        // UtilisateurStandard updatedUser = utilisateurStandardService.updateUtilisateurStandard(id, utilisateurStandardDetails)
        //        .orElseThrow(() -> new YourCustomException("Update failed or user not found post-update")); // Or handle differently
        // return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilisateurStandard(@PathVariable Integer id) {
        boolean deleted = utilisateurStandardService.deleteUtilisateurStandard(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            // This case implies the service returned false, meaning user might not have been found
            // but didn't throw ResourceNotFoundException. Adjust based on service contract.
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{utilisateurId}/historique-trajets")
    public ResponseEntity<List<Trajet>> getHistoriqueTrajets(@PathVariable Integer utilisateurId) {
        // Corrected method name based on your interface
        List<Trajet> trajets = utilisateurStandardService.getHistoriqueTrajetsForUser(utilisateurId);
        return ResponseEntity.ok(trajets);
    }

    @GetMapping("/{utilisateurId}/feedbacks")
    public ResponseEntity<List<Feedback>> getFeedbacks(@PathVariable Integer utilisateurId) {
        // Corrected method name based on your interface
        List<Feedback> feedbacks = utilisateurStandardService.getFeedbacksByUser(utilisateurId);
        return ResponseEntity.ok(feedbacks);
    }

    // This endpoint will cause a "Cannot resolve method" error until getTicketsByUserId is
    // added to the UtilisateurStandardService interface and implemented.
    // To fix temporarily, you can comment it out or implement the service method.
    /*
    @GetMapping("/{utilisateurId}/tickets")
    public ResponseEntity<List<Ticket>> getTickets(@PathVariable Integer utilisateurId) {
        // This line will fail if getTicketsByUserId is not in the service interface
        List<Ticket> tickets = utilisateurStandardService.getTicketsByUserId(utilisateurId);
        return ResponseEntity.ok(tickets);
    }
    */
}
