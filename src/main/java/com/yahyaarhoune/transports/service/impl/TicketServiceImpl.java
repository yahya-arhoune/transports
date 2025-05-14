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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // Pour la génération de code

@Service
public abstract class TicketServiceImpl implements TicketService {

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

    @Transactional
    @Override
    public Ticket createTicket(Integer utilisateurId, Integer trajetId, String codeValidation) {
        UtilisateurStandard utilisateur = utilisateurStandardRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", utilisateurId));
        Trajet trajet = trajetRepository.findById(trajetId)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", "id", trajetId));

        Ticket ticket = new Ticket();
        ticket.setId(null); // Laisser la base de données générer l'ID
        ticket.setUtilisateur(utilisateur);
        ticket.setTrajet(trajet);
        ticket.setDateAchat(LocalDateTime.now());

        // Générer un code de validation si non fourni ou s'assurer de son unicité si fourni
        if (codeValidation == null || codeValidation.trim().isEmpty()) {
            ticket.setCodeValidation(UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        } else {
            // Optionnel : vérifier l'unicité du codeValidation fourni s'il est permis au client de le spécifier
            if(ticketRepository.findByCodeValidation(codeValidation).isPresent()){
                throw new IllegalArgumentException("Le code de validation fourni existe déjà.");
            }
            ticket.setCodeValidation(codeValidation);
        }
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

    @Override
    @Transactional
    public Ticket updateTicket(Integer id, Ticket ticketDetails) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));

        // Les tickets sont souvent immuables après création, sauf peut-être pour un statut.
        // Pour cet exemple, nous permettons de changer le code de validation (peu courant).
        if (ticketDetails.getCodeValidation() != null && !ticketDetails.getCodeValidation().trim().isEmpty()) {
            // Optionnel : vérifier l'unicité du nouveau codeValidation
            if(!ticketDetails.getCodeValidation().equals(existingTicket.getCodeValidation()) &&
                    ticketRepository.findByCodeValidation(ticketDetails.getCodeValidation()).isPresent()){
                throw new IllegalArgumentException("Le nouveau code de validation existe déjà.");
            }
            existingTicket.setCodeValidation(ticketDetails.getCodeValidation());
        }
        // La date d'achat, l'utilisateur et le trajet ne devraient généralement pas être modifiés.
        // Si vous avez un champ "statut" sur le ticket, vous pourriez le mettre à jour ici.

        return ticketRepository.save(existingTicket);
    }

    @Override
    @Transactional
    public void deleteTicket(Integer id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket", "id", id);
        }
        // La suppression de tickets peut avoir des implications (remboursements, etc.)
        ticketRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> findByCodeValidation(String codeValidation) {
        return ticketRepository.findByCodeValidation(codeValidation);
    }
}