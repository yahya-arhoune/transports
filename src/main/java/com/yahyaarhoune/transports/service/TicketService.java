package com.yahyaarhoune.transports.service;

import com.yahyaarhoune.transports.models.Ticket;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public interface TicketService {
    // Modified to match typical booking flow - service handles code generation
    Ticket bookTicket(Integer utilisateurId, Integer trajetId);

    @Transactional
    Ticket createTicket(Integer utilisateurId, Integer trajetId, String codeValidation);

    // Optional<Ticket> getTicketById(Integer id);





    @Transactional(readOnly = true)
    Optional<Ticket> getTicketById(Long id);

    List<Ticket> getAllTickets(); // For admin or specific use cases


    List<Ticket> getTicketsForUser(Integer utilisateurId);

    Ticket updateTicket(Integer id, Ticket ticketDetails);
    void deleteTicket(Integer id);

    @Transactional(readOnly = true)
    List<Ticket> getTicketsForUser(Long utilisateurId);


    Ticket updateTicket(Long id, Ticket ticketDetails);

    @Transactional
    void deleteTicket(Long id);

    Optional<Ticket> findByCodeValidation(String codeValidation);
}