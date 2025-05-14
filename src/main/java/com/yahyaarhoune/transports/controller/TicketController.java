package com.yahyaarhoune.transports.controller;

import com.yahyaarhoune.transports.models.Ticket;
import com.yahyaarhoune.transports.service.TicketService;
import com.yahyaarhoune.transports.security.UserDetailsImpl; // Assuming you have this

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookTicket(@RequestBody Map<String, Integer> requestPayload, Authentication authentication) { // Changed Long to Integer for trajetId
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not authenticated"));
        }

        Integer trajetId = requestPayload.get("trajetId"); // Expect Integer from payload
        if (trajetId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "trajetId is required."));
        }

        try {
            Object principal = authentication.getPrincipal();
            Integer utilisateurId; // Changed to Integer

            if (principal instanceof UserDetailsImpl) {
                // Assuming UserDetailsImpl.getId() returns Long, we need to convert
                // If UserDetailsImpl.getId() returns Integer, this cast is not needed.
                Long principalId = ((UserDetailsImpl) principal).getId();
                if (principalId > Integer.MAX_VALUE || principalId < Integer.MIN_VALUE) {
                    throw new ArithmeticException("User ID is too large to fit in an Integer.");
                }
                utilisateurId = principalId.intValue(); // Convert Long to Integer
            } else {
                System.err.println("Unexpected principal type in bookTicket: " + (principal != null ? principal.getClass().getName() : "null"));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Unexpected principal type. Unable to determine user ID."));
            }

            Ticket bookedTicket = ticketService.bookTicket(utilisateurId, trajetId);

            // Nullify sensitive data - use getUtilisateur()
            if (bookedTicket.getUtilisateur() != null) {
                bookedTicket.getUtilisateur().setMotDePasse(null);
            }
            if (bookedTicket.getTrajet() != null && bookedTicket.getTrajet().getConducteur() != null) {
                bookedTicket.getTrajet().getConducteur().setMotDePasse(null);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(bookedTicket);
        } catch (UsernameNotFoundException | ArithmeticException e) { // Catch ArithmeticException for ID conversion
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            System.err.println("Error booking ticket: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("message", e.getMessage());
            if (e.getMessage().toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
            } else if (e.getMessage().toLowerCase().contains("already has a ticket") || e.getMessage().toLowerCase().contains("already booked")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
            }
            return ResponseEntity.badRequest().body(errorBody);
        }
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<?> getMyTickets(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not authenticated"));
        }
        try {
            Object principal = authentication.getPrincipal();
            Integer utilisateurId; // Changed to Integer

            if (principal instanceof UserDetailsImpl) {
                Long principalId = ((UserDetailsImpl) principal).getId();
                if (principalId > Integer.MAX_VALUE || principalId < Integer.MIN_VALUE) {
                    throw new ArithmeticException("User ID is too large to fit in an Integer.");
                }
                utilisateurId = principalId.intValue();
            } else {
                System.err.println("Unexpected principal type in getMyTickets: " + (principal != null ? principal.getClass().getName() : "null"));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Unexpected principal type. Unable to determine user ID."));
            }

            List<Ticket> tickets = ticketService.getTicketsForUser(utilisateurId);

            tickets.forEach(ticket -> {
                // Use getUtilisateur()
                if (ticket.getUtilisateur() != null) ticket.getUtilisateur().setMotDePasse(null);
                if (ticket.getTrajet() != null) {
                    if (ticket.getTrajet().getConducteur() != null) {
                        ticket.getTrajet().getConducteur().setMotDePasse(null);
                    }
                }
            });
            return ResponseEntity.ok(tickets);
        } catch (UsernameNotFoundException | ArithmeticException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            System.err.println("Error fetching user tickets: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Integer id) { // Parameter is Integer
        return ticketService.getTicketById(id)
                .map(ticket -> {
                    if (ticket.getUtilisateur() != null) ticket.getUtilisateur().setMotDePasse(null); // Use getUtilisateur()
                    if (ticket.getTrajet() != null && ticket.getTrajet().getConducteur() != null) ticket.getTrajet().getConducteur().setMotDePasse(null);
                    return ResponseEntity.ok(ticket);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        tickets.forEach(ticket -> {
            if (ticket.getUtilisateur() != null) ticket.getUtilisateur().setMotDePasse(null); // Use getUtilisateur()
            if (ticket.getTrajet() != null && ticket.getTrajet().getConducteur() != null) ticket.getTrajet().getConducteur().setMotDePasse(null);
        });
        return ResponseEntity.ok(tickets);
    }

    // ... (PUT and DELETE endpoints - ensure ID types are Integer if Ticket ID is Integer) ...
    // Example for update:
    // @PutMapping("/{id}")
    // public ResponseEntity<Ticket> updateTicket(@PathVariable Integer id, @RequestBody Ticket ticketDetails) { ... }

}