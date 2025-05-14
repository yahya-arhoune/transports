package com.yahyaarhoune.transports.service.impl;

import com.yahyaarhoune.transports.exception.ResourceNotFoundException;
import com.yahyaarhoune.transports.models.Ticket;
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.UtilisateurStandard;
import com.yahyaarhoune.transports.repository.TicketRepository;
import com.yahyaarhoune.transports.repository.TrajetRepository;
import com.yahyaarhoune.transports.repository.UtilisateurStandardRepository;
import com.yahyaarhoune.transports.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
// UUID and LocalDateTime are handled by @PrePersist in Ticket entity

@Service
public  class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UtilisateurStandardRepository utilisateurStandardRepository;
    private final TrajetRepository trajetRepository;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository,
                             UtilisateurStandardRepository utilisateurStandardRepository,
                             TrajetRepository trajetRepository) {
        this.ticketRepository = ticketRepository;
        this.utilisateurStandardRepository = utilisateurStandardRepository;
        this.trajetRepository = trajetRepository;
    }


    @Override
    public Ticket bookTicket(Integer utilisateurId, Integer trajetId) {
        UtilisateurStandard utilisateur = utilisateurStandardRepository.findById(Math.toIntExact(utilisateurId))
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", utilisateurId));
        Trajet trajet = trajetRepository.findById(Long.valueOf(trajetId))
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", "id", trajetId));

        if (ticketRepository.existsByUtilisateurAndTrajet(utilisateur, trajet)) { // Assumes Ticket entity field is 'utilisateur'
            throw new IllegalStateException("User already has a ticket for this trip.");
        }
        // TODO: Add other booking validations (trip status, capacity)

        Ticket newTicket = new Ticket();
        newTicket.setUtilisateur(utilisateur); // Field in Ticket.java is 'utilisateur'
        newTicket.setTrajet(trajet);
        // @PrePersist in Ticket entity will set dateAchat and codeValidation

        System.out.println("TicketServiceImpl: About to save new ticket. UserID: " + utilisateurId + ", TrajetID: " + trajetId +
                ", DateAchat (before @PrePersist): " + newTicket.getDateAchat() +
                ", CodeValidation (before @PrePersist): " + newTicket.getCodeValidation());
        Ticket savedTicket = null;
        try {
            savedTicket = ticketRepository.save(newTicket);
        } catch (Exception e) {
            System.err.println("TicketServiceImpl: EXCEPTION DURING ticketRepository.save()!");
            e.printStackTrace();
            throw e;
        }

        if (savedTicket == null) {
            System.err.println("TicketServiceImpl: ticketRepository.save(newTicket) RETURNED NULL!");
            throw new RuntimeException("Failed to save ticket: Repository returned null without an exception.");
        }

        System.out.println("TicketServiceImpl: Saved ticket. ID: " + savedTicket.getId() +
                ", CodeValidation: " + savedTicket.getCodeValidation() +
                ", DateAchat: " + savedTicket.getDateAchat());
        return savedTicket;

    }

    @Override
    public Ticket createTicket(Integer utilisateurId, Integer trajetId, String codeValidation) {
        UtilisateurStandard utilisateur = utilisateurStandardRepository.findById(Math.toIntExact(utilisateurId))
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", utilisateurId));
        Trajet trajet = trajetRepository.findById(Long.valueOf(trajetId))
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", "id", trajetId));

        Ticket ticket = new Ticket();
        ticket.setUtilisateur(utilisateur);
        ticket.setTrajet(trajet);

        if (codeValidation != null && !codeValidation.trim().isEmpty()) {
            if(ticketRepository.findByCodeValidation(codeValidation).isPresent()){
                throw new IllegalArgumentException("Le code de validation fourni existe déjà.");
            }
            ticket.setCodeValidation(codeValidation);
        }
        // If codeValidation is null here, @PrePersist in Ticket entity should generate it.
        // @PrePersist should also set dateAchat.
        return ticketRepository.save(ticket);

    }

    @Override
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(Math.toIntExact(id));

    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> getTicketsForUser(Integer utilisateurId) {
        UtilisateurStandard utilisateur = utilisateurStandardRepository.findById(Math.toIntExact(utilisateurId))
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", utilisateurId));
        return ticketRepository.findByUtilisateur(utilisateur); // Make sure this method exists in TicketRepository

    }

    @Override
    public Ticket updateTicket(Integer id, Ticket ticketDetails) {
        Ticket existingTicket = ticketRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));

        if (ticketDetails.getCodeValidation() != null && !ticketDetails.getCodeValidation().trim().isEmpty()) {
            if(!ticketDetails.getCodeValidation().equals(existingTicket.getCodeValidation()) &&
                    ticketRepository.findByCodeValidation(ticketDetails.getCodeValidation()).isPresent()){
                throw new IllegalArgumentException("Le nouveau code de validation existe déjà.");
            }
            existingTicket.setCodeValidation(ticketDetails.getCodeValidation());
        }
        // Add other updatable fields if necessary
        return ticketRepository.save(existingTicket);

    }

    @Override
    public void deleteTicket(Integer id) {
        if (!ticketRepository.existsById(Math.toIntExact(id))) {
            throw new ResourceNotFoundException("Ticket", "id", id);
        }
        ticketRepository.deleteById(Math.toIntExact(id));
    }





    @Override
    public List<Ticket> getTicketsForUser(Long utilisateurId) {
        return List.of();
    }

    @Override
    public Ticket updateTicket(Long id, Ticket ticketDetails) {
        return null;
    }

    @Override
    public void deleteTicket(Long id) {

    }

    @Override
    public Optional<Ticket> findByCodeValidation(String codeValidation) {
        return Optional.empty();
    }
}