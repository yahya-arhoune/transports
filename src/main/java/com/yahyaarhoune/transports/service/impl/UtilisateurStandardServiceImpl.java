package com.yahyaarhoune.transports.service.impl;

import com.yahyaarhoune.transports.exception.ResourceNotFoundException;
import com.yahyaarhoune.transports.models.Feedback;
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.UtilisateurStandard;
import com.yahyaarhoune.transports.repository.TicketRepository;
import com.yahyaarhoune.transports.repository.UtilisateurStandardRepository;
import com.yahyaarhoune.transports.service.UtilisateurStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // << IMPORT PasswordEncoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurStandardServiceImpl implements UtilisateurStandardService {

    private final UtilisateurStandardRepository utilisateurStandardRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder; // << DECLARE PasswordEncoder

    @Autowired
    public UtilisateurStandardServiceImpl(UtilisateurStandardRepository utilisateurStandardRepository,
                                          TicketRepository ticketRepository,
                                          PasswordEncoder passwordEncoder /* << INJECT PasswordEncoder */) {
        this.utilisateurStandardRepository = utilisateurStandardRepository;
        this.ticketRepository = ticketRepository;
        this.passwordEncoder = passwordEncoder; // << ASSIGN PasswordEncoder
    }

    @Override
    @Transactional
    public UtilisateurStandard createUtilisateurStandard(UtilisateurStandard utilisateurStandard) {
        // Optional: Add validation - e.g., check if email already exists
        if (utilisateurStandardRepository.findByEmail(utilisateurStandard.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + utilisateurStandard.getEmail());
        }

        // HASH THE PASSWORD!
        utilisateurStandard.setMotDePasse(passwordEncoder.encode(utilisateurStandard.getMotDePasse())); // << UNCOMMENT AND USE

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
        return utilisateurStandardRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurStandard> getAllUtilisateursStandard() {
        return utilisateurStandardRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<UtilisateurStandard> updateUtilisateurStandard(Integer id, UtilisateurStandard utilisateurStandardDetails) {
        return utilisateurStandardRepository.findById(id)
                .map(existingUser -> {
                    // Optional: Add validation - e.g., if email is changed, check for uniqueness
                    if (utilisateurStandardDetails.getEmail() != null &&
                            !utilisateurStandardDetails.getEmail().equals(existingUser.getEmail()) &&
                            utilisateurStandardRepository.findByEmail(utilisateurStandardDetails.getEmail()).isPresent()) {
                        throw new IllegalArgumentException("New email already in use: " + utilisateurStandardDetails.getEmail());
                    }

                    existingUser.setNom(utilisateurStandardDetails.getNom());
                    existingUser.setPrenom(utilisateurStandardDetails.getPrenom());
                    if(utilisateurStandardDetails.getEmail() != null) { // Only update if provided
                        existingUser.setEmail(utilisateurStandardDetails.getEmail());
                    }


                    if (utilisateurStandardDetails.getMotDePasse() != null && !utilisateurStandardDetails.getMotDePasse().isEmpty()) {
                        // HASH THE NEW PASSWORD!
                        existingUser.setMotDePasse(passwordEncoder.encode(utilisateurStandardDetails.getMotDePasse())); // << UNCOMMENT AND USE
                    }
                    return utilisateurStandardRepository.save(existingUser);
                });
    }

    @Override
    @Transactional
    public boolean deleteUtilisateurStandard(Integer id) {
        Optional<UtilisateurStandard> userOpt = utilisateurStandardRepository.findById(id);
        if (userOpt.isPresent()) {
            UtilisateurStandard user = userOpt.get();
            // Assuming TicketRepository has findByUtilisateur method
            ticketRepository.deleteAll(ticketRepository.findByUtilisateur(user));
            // Add similar logic for Feedbacks if needed
            utilisateurStandardRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trajet> getHistoriqueTrajetsForUser(Integer userId) {
        UtilisateurStandard user = utilisateurStandardRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", userId));
        user.getHistoriqueTrajets().size();
        return user.getHistoriqueTrajets();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Feedback> getFeedbacksByUser(Integer userId) {
        UtilisateurStandard user = utilisateurStandardRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", userId));
        user.getFeedbacks().size();
        return user.getFeedbacks();
    }

    // If you need getTicketsByUserId, make sure it's in your UtilisateurStandardService interface
    // and then uncomment this with @Override
    /*
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByUserId(Integer userId) {
        UtilisateurStandard user = utilisateurStandardRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", userId));
        return ticketRepository.findByUtilisateur(user);
    }
    */
}