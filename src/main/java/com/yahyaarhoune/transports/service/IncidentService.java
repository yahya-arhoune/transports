package com.yahyaarhoune.transports.service;

import com.yahyaarhoune.transports.models.Incident;
import com.yahyaarhoune.transports.models.enums.StatutIncident;
import java.util.List;
import java.util.Optional;

public interface IncidentService {
    Incident createIncident(String type, String description, Integer conducteurId, Integer trajetId); // trajetId can be null
    Optional<Incident> getIncidentById(Integer id);
    List<Incident> getAllIncidents();
    Incident updateIncident(Integer id, Incident incidentDetails);
    void deleteIncident(Integer id);
    Incident updateIncidentStatut(Integer incidentId, StatutIncident newStatut);
}