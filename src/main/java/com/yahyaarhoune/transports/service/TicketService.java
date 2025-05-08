package com.yahyaarhoune.transports.service;

import com.yahyaarhoune.transports.models.Ticket;
import java.util.List;
import java.util.Optional;

public interface TicketService {
    Ticket createTicket(Integer utilisateurId, Integer trajetId, String codeValidation); // codeValidation might be auto-generated
    Optional<Ticket> getTicketById(Integer id);
    List<Ticket> getAllTickets(); // For admin or specific use cases
    Ticket updateTicket(Integer id, Ticket ticketDetails); // Limited use for tickets
    void deleteTicket(Integer id); // Limited use

    Optional<Ticket> findByCodeValidation(String codeValidation);
    // boolean validateTicketByCode(String codeValidation); // Might change ticket status
}