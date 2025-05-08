package com.yahyaarhoune.transports.service;

import com.yahyaarhoune.transports.models.Feedback;
import java.util.List;
import java.util.Optional;

public interface FeedbackService {
    Feedback createFeedback(Integer utilisateurId, String message);
    Optional<Feedback> getFeedbackById(Integer id);
    List<Feedback> getAllFeedbacks();
    // Update for feedback is usually not provided
    void deleteFeedback(Integer id);
}