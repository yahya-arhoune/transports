package com.yahyaarhoune.transports.service.impl;

import com.yahyaarhoune.transports.exception.ResourceNotFoundException;
import com.yahyaarhoune.transports.models.Conducteur;
import com.yahyaarhoune.transports.models.Incident;
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.enums.StatutIncident;
import com.yahyaarhoune.transports.repository.ConducteurRepository;
import com.yahyaarhoune.transports.repository.IncidentRepository;
import com.yahyaarhoune.transports.repository.TrajetRepository;
import com.yahyaarhoune.transports.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final ConducteurRepository conducteurRepository;
    private final TrajetRepository trajetRepository;

    @Autowired
    public IncidentServiceImpl(IncidentRepository incidentRepository,
                               ConducteurRepository conducteurRepository,
                               TrajetRepository trajetRepository) {
        this.incidentRepository = incidentRepository;
        this.conducteurRepository = conducteurRepository;
        this.trajetRepository = trajetRepository;
    }

    @Override
    @Transactional
    public Incident createIncident(String type, String description, Integer conducteurId, Integer trajetId) {
        Conducteur signalePar = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur", "id", conducteurId));

        Incident incident = new Incident();
        incident.setId(null); // Laisser la base de données générer l'ID
        incident.setType(type);
        incident.setDescription(description);
        incident.setSignalePar(signalePar);
        incident.setDateHeure(LocalDateTime.now());
        incident.setStatut(StatutIncident.EN_ATTENTE); // Statut initial par défaut

        if (trajetId != null) {
            Trajet trajet = trajetRepository.findById(trajetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trajet", "id", trajetId));
            incident.setTrajet(trajet);
        } else {
            incident.setTrajet(null);
        }
        return incidentRepository.save(incident);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Incident> getIncidentById(Integer id) {
        return incidentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    @Override
    @Transactional
    public Incident updateIncident(Integer id, Incident incidentDetails) {
        Incident existingIncident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", "id", id));

        existingIncident.setType(incidentDetails.getType());
        existingIncident.setDescription(incidentDetails.getDescription());
        existingIncident.setStatut(incidentDetails.getStatut());

        // signalePar et dateHeure ne sont généralement pas modifiés après la création.
        // Le trajet associé pourrait être modifié ou dissocié.
        if (incidentDetails.getTrajet() != null && incidentDetails.getTrajet().getId() != null) {
            Trajet trajet = trajetRepository.findById(incidentDetails.getTrajet().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trajet", "id", incidentDetails.getTrajet().getId()));
            existingIncident.setTrajet(trajet);
        } else if (incidentDetails.getTrajet() == null && existingIncident.getTrajet() != null) {
            // Permettre de dissocier le trajet
            existingIncident.setTrajet(null);
        }
        return incidentRepository.save(existingIncident);
    }

    @Override
    @Transactional
    public void deleteIncident(Integer id) {
        if (!incidentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Incident", "id", id);
        }
        incidentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Incident updateIncidentStatut(Integer incidentId, StatutIncident newStatut) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", "id", incidentId));
        incident.setStatut(newStatut);
        return incidentRepository.save(incident);
    }
}