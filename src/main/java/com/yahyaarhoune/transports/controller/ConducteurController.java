package com.yahyaarhoune.transports.controller;

import com.yahyaarhoune.transports.models.Conducteur; // Entité
import com.yahyaarhoune.transports.models.Incident;  // Entité
import com.yahyaarhoune.transports.models.Trajet;    // Entité
import com.yahyaarhoune.transports.models.Vehicule;  // Entité
import com.yahyaarhoune.transports.service.ConducteurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // Pour des requêtes simples comme l'ID du véhicule

@RestController
@RequestMapping("/api/conducteurs")
public class ConducteurController {

    private final ConducteurService conducteurService;

    @Autowired
    public ConducteurController(ConducteurService conducteurService) {
        this.conducteurService = conducteurService;
    }

    @PostMapping
    public ResponseEntity<Conducteur> createConducteur(@RequestBody Conducteur conducteur) {
        Conducteur createdConducteur = conducteurService.createConducteur(conducteur);
        return new ResponseEntity<>(createdConducteur, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conducteur> getConducteurById(@PathVariable Integer id) {
        return conducteurService.getConducteurById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Conducteur>> getAllConducteurs() {
        return ResponseEntity.ok(conducteurService.getAllConducteurs());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conducteur> updateConducteur(@PathVariable Integer id, @RequestBody Conducteur conducteurDetails) {
        Conducteur updatedConducteur = conducteurService.updateConducteur(id, conducteurDetails);
        return ResponseEntity.ok(updatedConducteur);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConducteur(@PathVariable Integer id) {
        conducteurService.deleteConducteur(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{conducteurId}/planning")
    public ResponseEntity<List<Trajet>> getPlanning(@PathVariable Integer conducteurId) {
        return ResponseEntity.ok(conducteurService.getPlanning(conducteurId));
    }

    @GetMapping("/{conducteurId}/vehicule-assigne")
    public ResponseEntity<Vehicule> getAssignedVehicule(@PathVariable Integer conducteurId) {
        Vehicule vehicule = conducteurService.getAssignedVehicule(conducteurId);
        if (vehicule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehicule);
    }

    @PutMapping("/{conducteurId}/assign-vehicule/{vehiculeId}") // Path variable est plus RESTful pour lier des ressources
    public ResponseEntity<Conducteur> assignVehicule(@PathVariable Integer conducteurId, @PathVariable Integer vehiculeId) {
        Conducteur conducteur = conducteurService.assignVehicule(conducteurId, vehiculeId);
        return ResponseEntity.ok(conducteur);
    }
    // Alternative si vehiculeId est dans le corps:
    // @PutMapping("/{conducteurId}/assign-vehicule")
    // public ResponseEntity<Conducteur> assignVehicule(@PathVariable Integer conducteurId, @RequestBody Map<String, Integer> payload) {
    //     Integer vehiculeId = payload.get("vehiculeId");
    //     if (vehiculeId == null) return ResponseEntity.badRequest().build();
    //     Conducteur conducteur = conducteurService.assignVehicule(conducteurId, vehiculeId);
    //     return ResponseEntity.ok(conducteur);
    // }


    @DeleteMapping("/{conducteurId}/unassign-vehicule")
    public ResponseEntity<Conducteur> unassignVehicule(@PathVariable Integer conducteurId) {
        Conducteur conducteur = conducteurService.unassignVehicule(conducteurId);
        return ResponseEntity.ok(conducteur);
    }

    @GetMapping("/{conducteurId}/incidents-signales")
    public ResponseEntity<List<Incident>> getIncidentsSignales(@PathVariable Integer conducteurId) {
        return ResponseEntity.ok(conducteurService.getIncidentsSignalesParConducteur(conducteurId));
    }
}