package com.yahyaarhoune.transports.service;

import com.yahyaarhoune.transports.models.Feedback;
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.UtilisateurStandard;

import java.util.List;
import java.util.Optional;

public interface UtilisateurStandardService {
    UtilisateurStandard createUtilisateurStandard(UtilisateurStandard utilisateur);
    List<UtilisateurStandard> getAllUtilisateursStandard();
    Optional<UtilisateurStandard> getUtilisateurStandardById(Integer id);
    Optional<UtilisateurStandard> getUtilisateurStandardByEmail(String email);
    Optional<UtilisateurStandard> updateUtilisateurStandard(Integer id, UtilisateurStandard utilisateurDetails);
    boolean deleteUtilisateurStandard(Integer id);
    List<Trajet> getHistoriqueTrajetsForUser(Integer userId);
    List<Feedback> getFeedbacksByUser(Integer userId);
}
