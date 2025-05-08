package com.yahyaarhoune.transports.controller;

import com.yahyaarhoune.transports.models.Incident; // Entité
import com.yahyaarhoune.transports.models.enums.StatutIncident;
import com.yahyaarhoune.transports.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // Pour la mise à jour de statut

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    @Autowired
    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    public ResponseEntity<Incident> createIncident(@RequestBody Incident incident) {
        // ATTENTION : Conducteur (signalePar) et Trajet dans l'objet incident devraient avoir leurs IDs settés.
        // Le service devrait les fetcher. dateHeure et statut initial devraient être settés par le service.
        Incident createdIncident = incidentService.createIncident(
                incident.getType(),
                incident.getDescription(),
                incident.getSignalePar() != null ? incident.getSignalePar().getId() : null,
                incident.getTrajet() != null ? incident.getTrajet().getId() : null
        );
        return new ResponseEntity<>(createdIncident, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Integer id) {
        return incidentService.getIncidentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Incident>> getAllIncidents() {
        List<Incident> incidents = incidentService.getAllIncidents();
        return ResponseEntity.ok(incidents);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incident> updateIncident(@PathVariable Integer id, @RequestBody Incident incidentDetails) {
        Incident updatedIncident = incidentService.updateIncident(id, incidentDetails);
        return ResponseEntity.ok(updatedIncident);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Integer id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{incidentId}/statut")
    public ResponseEntity<Incident> updateIncidentStatut(@PathVariable Integer incidentId, @RequestBody Map<String, String> statusPayload) {
        String statutStr = statusPayload.get("statut");
        if (statutStr == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            StatutIncident newStatut = StatutIncident.valueOf(statutStr.toUpperCase());
            Incident incident = incidentService.updateIncidentStatut(incidentId, newStatut);
            return ResponseEntity.ok(incident);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}