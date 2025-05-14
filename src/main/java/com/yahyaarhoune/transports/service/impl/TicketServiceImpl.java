package com.yahyaarhoune.transports.service.impl; // Assuming this is your package

import com.yahyaarhoune.transports.exception.ResourceNotFoundException;
import com.yahyaarhoune.transports.models.Ticket;
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.UtilisateurStandard;
import com.yahyaarhoune.transports.repository.TicketRepository;
import com.yahyaarhoune.transports.repository.TrajetRepository;
import com.yahyaarhoune.transports.repository.UtilisateurStandardRepository;
import com.yahyaarhoune.transports.service.TicketService; // Make sure this import is correct
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketServiceImpl implements TicketService { // <<--- REMOVED 'abstract'

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

    // --- IMPLEMENTATION FOR bookTicket ---
    @Override
    @Transactional
    public Ticket bookTicket(Integer utilisateurId, Integer trajetId) {
        UtilisateurStandard utilisateur = utilisateurStandardRepository.findById(utilisateurId) // Assuming ID is Integer here
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", utilisateurId));
        Trajet trajet = trajetRepository.findById(trajetId) // Assuming ID is Integer here
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", "id", trajetId));

        // Optional: Add more booking validations (e.g., user already booked, trip capacity)
        if (ticketRepository.existsByUtilisateurAndTrajet(utilisateur, trajet)) { // Assuming Ticket entity uses 'utilisateur' field
            throw new IllegalStateException("User already has a ticket for this trip.");
        }
        // TODO: Check trip status (e.g., not already departed, not cancelled)
        // TODO: Check trip capacity if applicable

        Ticket ticket = new Ticket();
        // ticket.setId(null); // Not needed if using @GeneratedValue(strategy = GenerationType.IDENTITY)
        ticket.setUtilisateur(utilisateur); // Use the correct setter for your Ticket entity
        ticket.setTrajet(trajet);
        // dateAchat and codeValidation should be handled by @PrePersist in Ticket entity
        // If not, generate them here:
        // ticket.setDateAchat(LocalDateTime.now());
        // ticket.setCodeValidation(UUID.randomUUID().toString().substring(0, 10).toUpperCase());

        return ticketRepository.save(ticket);
    }

    // This method was likely intended for the booking operation without external code validation
    // If `bookTicket` above is the main one, you might not need this specific `createTicket`
    // unless there's a use case for passing `codeValidation` from outside.
    // For now, I'll keep its implementation as you had it, but it might be redundant.
    @Transactional
    @Override
    public Ticket createTicket(Integer utilisateurId, Integer trajetId, String codeValidation) {
        UtilisateurStandard utilisateur = utilisateurStandardRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", utilisateurId));
        Trajet trajet = trajetRepository.findById(trajetId)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", "id", trajetId));

        Ticket ticket = new Ticket();
        ticket.setUtilisateur(utilisateur); // Use correct setter
        ticket.setTrajet(trajet);
        // ticket.setDateAchat(LocalDateTime.now()); // Should be handled by @PrePersist

        if (codeValidation == null || codeValidation.trim().isEmpty()) {
            // Generate if not provided (ensure @PrePersist in Ticket entity doesn't conflict)
            // ticket.setCodeValidation(UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        } else {
            if(ticketRepository.findByCodeValidation(codeValidation).isPresent()){ // Assumes this method exists in repo
                throw new IllegalArgumentException("Le code de validation fourni existe déjà.");
            }
            ticket.setCodeValidation(codeValidation);
        }
        // If dateAchat and codeValidation are set by @PrePersist in Ticket entity,
        // you don't need to set them manually here when saving.
        // The main purpose of this method would be if codeValidation needs to be pre-defined.
        return ticketRepository.save(ticket);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> getTicketById(Integer id) {
        return ticketRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // --- IMPLEMENTATION FOR getTicketsForUser ---
    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsForUser(Integer utilisateurId) {
        UtilisateurStandard utilisateur = utilisateurStandardRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", utilisateurId));
        // Assuming TicketRepository has: List<Ticket> findByUtilisateur(UtilisateurStandard utilisateur);
        return ticketRepository.findByUtilisateur(utilisateur); // Use the correct field name here
    }


    @Override
    @Transactional
    public Ticket updateTicket(Integer id, Ticket ticketDetails) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));

        if (ticketDetails.getCodeValidation() != null && !ticketDetails.getCodeValidation().trim().isEmpty()) {
            if(!ticketDetails.getCodeValidation().equals(existingTicket.getCodeValidation()) &&
                    ticketRepository.findByCodeValidation(ticketDetails.getCodeValidation()).isPresent()){ // Assumes this method exists
                throw new IllegalArgumentException("Le nouveau code de validation existe déjà.");
            }
            existingTicket.setCodeValidation(ticketDetails.getCodeValidation());
        }
        return ticketRepository.save(existingTicket);
    }

    @Override
    @Transactional
    public void deleteTicket(Integer id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket", "id", id);
        }
        ticketRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> findByCodeValidation(String codeValidation) {
        return ticketRepository.findByCodeValidation(codeValidation); // Assumes this method exists
    }
}