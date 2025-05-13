package com.yahyaarhoune.transports.controller;

import com.yahyaarhoune.transports.models.Ticket; // Entité
import com.yahyaarhoune.transports.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // Pour la validation de code simple

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        // ATTENTION : Utilisateur et Trajet dans l'objet ticket devraient avoir leurs IDs settés.
        // Le service devrait les fetcher. Le codeValidation devrait être généré par le service.
        // La dateAchat aussi.
        Ticket createdTicket = ticketService.createTicket(
                Math.toIntExact(ticket.getUtilisateur() != null ? ticket.getUtilisateur().getId() : null),
                ticket.getTrajet() != null ? ticket.getTrajet().getId() : null,
                null // Laisser le service générer le code de validation
        );
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Integer id) {
        return ticketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Integer id, @RequestBody Ticket ticketDetails) {
        // La mise à jour de tickets est généralement très limitée (statut ?).
        // Exposer toute l'entité ici est risqué.
        Ticket updatedTicket = ticketService.updateTicket(id, ticketDetails);
        return ResponseEntity.ok(updatedTicket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Integer id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validation/{codeValidation}")
    public ResponseEntity<Ticket> findTicketByCodeValidation(@PathVariable String codeValidation) {
        return ticketService.findByCodeValidation(codeValidation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}