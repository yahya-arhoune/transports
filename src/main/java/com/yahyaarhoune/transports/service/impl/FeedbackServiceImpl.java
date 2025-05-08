package com.yahyaarhoune.transports.service.impl;

import com.yahyaarhoune.transports.exception.ResourceNotFoundException;
import com.yahyaarhoune.transports.models.Feedback;
import com.yahyaarhoune.transports.models.UtilisateurStandard;
import com.yahyaarhoune.transports.repository.FeedbackRepository;
import com.yahyaarhoune.transports.repository.UtilisateurStandardRepository;
import com.yahyaarhoune.transports.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UtilisateurStandardRepository utilisateurStandardRepository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository,
                               UtilisateurStandardRepository utilisateurStandardRepository) {
        this.feedbackRepository = feedbackRepository;
        this.utilisateurStandardRepository = utilisateurStandardRepository;
    }

    @Override
    @Transactional
    public Feedback createFeedback(Integer utilisateurId, String message) {
        UtilisateurStandard utilisateur = utilisateurStandardRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("UtilisateurStandard", "id", utilisateurId));

        Feedback feedback = new Feedback();
        feedback.setId(null); // Laisser la base de données générer l'ID
        feedback.setUtilisateur(utilisateur);
        feedback.setMessage(message);
        feedback.setDate(LocalDateTime.now()); // Définir la date actuelle
        return feedbackRepository.save(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Feedback> getFeedbackById(Integer id) {
        return feedbackRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    // La mise à jour de feedback n'est généralement pas une fonctionnalité standard.
    // Si elle était nécessaire, la logique serait similaire à celle des autres entités.

    @Override
    @Transactional
    public void deleteFeedback(Integer id) {
        if (!feedbackRepository.existsById(id)) {
            throw new ResourceNotFoundException("Feedback", "id", id);
        }
        feedbackRepository.deleteById(id);
    }
}