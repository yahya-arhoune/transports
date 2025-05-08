package com.yahyaarhoune.transports.repository;

import com.yahyaarhoune.transports.models.Feedback;
import com.yahyaarhoune.transports.models.UtilisateurStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    List<Feedback> findByUtilisateur(UtilisateurStandard utilisateur);

    List<Feedback> findByDateAfter(LocalDateTime dateTime);

    // Example: Find feedback containing specific keywords
    List<Feedback> findByMessageContainingIgnoreCase(String keyword);
}