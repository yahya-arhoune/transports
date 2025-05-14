package com.yahyaarhoune.transports.service;

import com.yahyaarhoune.transports.models.Ticket;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    // Modified to match typical booking flow - service handles code generation
    Ticket bookTicket(Integer utilisateurId, Integer trajetId);

    @Transactional
    Ticket createTicket(Integer utilisateurId, Integer trajetId, String codeValidation);

    Optional<Ticket> getTicketById(Integer id);
    List<Ticket> getAllTickets(); // For admin or specific use cases

    // This method is needed by the controller for "My Tickets"
    List<Ticket> getTicketsForUser(Integer utilisateurId);

    // updateTicket and deleteTicket are less common for standard tickets, but keep if needed
    Ticket updateTicket(Integer id, Ticket ticketDetails);
    void deleteTicket(Integer id);

    Optional<Ticket> findByCodeValidation(String codeValidation);
}