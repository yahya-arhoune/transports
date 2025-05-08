package com.yahyaarhoune.transports.controller;

import com.yahyaarhoune.transports.models.Trajet;   // Entité
import com.yahyaarhoune.transports.models.Vehicule; // Entité
import com.yahyaarhoune.transports.models.enums.EtatVehicule;
import com.yahyaarhoune.transports.service.VehiculeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // Pour les mises à jour partielles simples

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    @Autowired
    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @PostMapping
    public ResponseEntity<Vehicule> createVehicule(@RequestBody Vehicule vehicule) {
        Vehicule createdVehicule = vehiculeService.createVehicule(vehicule);
        return new ResponseEntity<>(createdVehicule, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicule> getVehiculeById(@PathVariable Integer id) {
        return vehiculeService.getVehiculeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Vehicule>> getAllVehicules() {
        List<Vehicule> vehicules = vehiculeService.getAllVehicules();
        return ResponseEntity.ok(vehicules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicule> updateVehicule(@PathVariable Integer id, @RequestBody Vehicule vehiculeDetails) {
        Vehicule updatedVehicule = vehiculeService.updateVehicule(id, vehiculeDetails);
        return ResponseEntity.ok(updatedVehicule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicule(@PathVariable Integer id) {
        vehiculeService.deleteVehicule(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{vehiculeId}/position-gps")
    public ResponseEntity<Vehicule> updatePositionGPS(@PathVariable Integer vehiculeId, @RequestBody Map<String, String> payload) {
        String newPosition = payload.get("newPositionGPS"); // S'assurer que la clé correspond
        if (newPosition == null) {
            return ResponseEntity.badRequest().build();
        }
        Vehicule vehicule = vehiculeService.updatePositionGPS(vehiculeId, newPosition);
        return ResponseEntity.ok(vehicule);
    }

    @PatchMapping("/{vehiculeId}/etat")
    public ResponseEntity<Vehicule> updateVehiculeEtat(@PathVariable Integer vehiculeId, @RequestBody Map<String, String> payload) {
        String etatStr = payload.get("newEtat"); // S'assurer que la clé correspond
        if (etatStr == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            EtatVehicule newEtat = EtatVehicule.valueOf(etatStr.toUpperCase());
            Vehicule vehicule = vehiculeService.updateVehiculeEtat(vehiculeId, newEtat);
            return ResponseEntity.ok(vehicule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{vehiculeId}/trajets")
    public ResponseEntity<List<Trajet>> getTrajetsForVehicule(@PathVariable Integer vehiculeId) {
        List<Trajet> trajets = vehiculeService.getTrajetsForVehicule(vehiculeId);
        return ResponseEntity.ok(trajets);
    }
}
