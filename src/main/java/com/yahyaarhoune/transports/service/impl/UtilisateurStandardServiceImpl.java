package com.yahyaarhoune.transports.service.impl;

import com.yahyaarhoune.transports.exception.ResourceNotFoundException;
import com.yahyaarhoune.transports.models.Feedback;
// import com.yahyaarhoune.transports.models.Ticket; // Not needed if getTickets is removed
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.UtilisateurStandard;
import com.yahyaarhoune.transports.repository.TicketRepository; // Keep if needed for delete logic
import com.yahyaarhoune.transports.repository.UtilisateurStandardRepository;
import com.yahyaarhoune.transports.service.UtilisateurStandardService;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurStandardServiceImpl implements UtilisateurStandardService {

    private final UtilisateurStandardRepository utilisateurStandardRepository;
    private final TicketRepository ticketRepository; // Keep if you still need it for deleting tickets of a user
    // private final PasswordEncoder passwordEncoder;

    @Autowired
    public UtilisateurStandardServiceImpl(UtilisateurStandardRepository utilisateurStandardRepository,
                                          TicketRepository ticketRepository
            /*, PasswordEncoder passwordEncoder */) {
        this.utilisateurStandardRepository = utilisateurStandardRepository;
        this.ticketRepository = ticketRepository; // Still needed for delete user logic
        // this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UtilisateurStandard createUtilisateurStandard(UtilisateurStandard utilisateurStandard) {
        // TODO: Add validation - e.g., check if email already exists
        // TODO: HASH THE PASSWORD!
        // utilisateurStandard.setMotDePasse(passwordEncoder.encode(utilisateurStandard.getMotDePasse()));
        utilisateurStandard.setId(null);
        return utilisateurStandardRepository.save(utilisateurStandard);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UtilisateurStandard> getUtilisateurStandardById(Integer id) {
        return utilisateurStandardRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UtilisateurStandard> getUtilisateurStandardByEmail(String email) {
        // Ensure findByEmail exists in UtilisateurStandardRepository
        return utilisateurStandardRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurStandard> getAllUtilisateursStandard() {
        return utilisateurStandardRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<UtilisateurStandard> updateUtilisateurStandard(Integer id, UtilisateurStandard utilisateurStandardDetails) { // Returns Optional<UtilisateurStandard>
        return utilisateurStandardRepository.findById(id)
                .map(existingUser -> {
                    // TODO: Add validation - e.g., if email is changed, check for uniqueness
                    existingUser.setNom(utilisateurStandardDetails.getNom());
                    existingUser.setPrenom(utilisateurStandardDetails.getPrenom());
                    existingUser.setEmail(utilisateurStandardDetails.getEmail());

                    if (utilisateurStandardDetails.getMotDePasse() != null && !utilisateurStandardDetails.getMotDePasse().isEmpty()) {
                        // TODO: HASH THE NEW PASSWORD!
                        // existingUser.setMotDePasse(passwordEncoder.encode(utilisateurStandardDetails.getMotDePasse()));
                    }
                    return utilisateurStandardRepository.save(existingUser);
                }); // .map handles the Optional return
    }

    @Override
    @Transactional
    public boolean deleteUtilisateurStandard(Integer id) { // Returns boolean
        Optional<UtilisateurStandard> userOpt = utilisateurStandardRepository.findById(id);
        if (userOpt.isPresent()) {
            UtilisateurStandard user = userOpt.get();
            // Clean up related entities like tickets.
            // This assumes ticketRepository.findByUtilisateur(user) returns List<Ticket>
            // And that deleteAll is appropriate.
            ticketRepository.deleteAll(ticketRepository.findByUtilisateur(user));
            // Add similar logic for Feedbacks if needed, and if a FeedbackRepository method exists.
            // e.g., feedbackRepository.deleteAll(feedbackRepository.findByUtilisateur(user));

            utilisateurStandardRepository.deleteById(id);
            return true;
        }
        return false; // Or throw ResourceNotFoundException if you prefer not to return boolean
        // but the interface now dictates boolean.
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trajet> getHistoriqueTrajetsForUser(Integer userId) { // Matches interface name
        UtilisateurStandard user = utilisateurStandardRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", userId));
        user.getHistoriqueTrajets().size(); // Trigger loading
        return user.getHistoriqueTrajets();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Feedback> getFeedbacksByUser(Integer userId) { // Matches interface name
        UtilisateurStandard user = utilisateurStandardRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", userId));
        user.getFeedbacks().size(); // Trigger loading
        return user.getFeedbacks();
    }

    // The method for getting tickets (e.g., getTicketsByUserId) is NOT in the provided interface.
    // If you need it, you MUST add it to the UtilisateurStandardService interface first.
    // Then you can uncomment and implement it here with @Override.
    /*
    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByUserId(Integer userId) { // Assuming this would be the name in interface
        UtilisateurStandard user = utilisateurStandardRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", userId));
        return ticketRepository.findByUtilisateur(user);
    }
    */
}