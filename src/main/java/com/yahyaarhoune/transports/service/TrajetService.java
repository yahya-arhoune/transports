package com.yahyaarhoune.transports.service;

import com.yahyaarhoune.transports.dto.TrajetCreationRequestDTO;
import com.yahyaarhoune.transports.models.Incident;
import com.yahyaarhoune.transports.models.Ticket; // << ADD IMPORT
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.UtilisateurStandard;

import java.util.List;
import java.util.Optional;

public interface TrajetService {
    Trajet createTrajet(Trajet trajet);
    List<Trajet> getAllTrajets();
    Optional<Trajet> getTrajetById(Integer id);
    Optional<Trajet> updateTrajet(Integer id, Trajet trajetDetails);
    boolean deleteTrajet(Integer id);
    List<UtilisateurStandard> getPassagersForTrajet(Integer trajetId);
    List<Incident> getIncidentsForTrajet(Integer trajetId);

    // --- ADD THESE MISSING METHODS ---
    List<Ticket> getTicketsForTrajet(Integer trajetId);
    Trajet addPassagerToTrajet(Integer trajetId, Integer utilisateurId);
    Trajet removePassagerFromTrajet(Integer trajetId, Integer utilisateurId);
    Trajet createTrajetFromDTO(TrajetCreationRequestDTO trajetDto);
    // ---------------------------------
}