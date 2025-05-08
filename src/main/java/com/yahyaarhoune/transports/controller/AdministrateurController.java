package com.yahyaarhoune.transports.controller;

import com.yahyaarhoune.transports.models.Administrateur; // Entité
import com.yahyaarhoune.transports.service.AdministrateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administrateurs")
public class AdministrateurController {

    private final AdministrateurService administrateurService;

    @Autowired
    public AdministrateurController(AdministrateurService administrateurService) {
        this.administrateurService = administrateurService;
    }

    @PostMapping
    public ResponseEntity<Administrateur> createAdministrateur(@RequestBody Administrateur administrateur) {
        Administrateur createdAdmin = administrateurService.createAdministrateur(administrateur);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrateur> getAdministrateurById(@PathVariable Integer id) {
        return administrateurService.getAdministrateurById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Administrateur>> getAllAdministrateurs() {
        return ResponseEntity.ok(administrateurService.getAllAdministrateurs());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrateur> updateAdministrateur(@PathVariable Integer id, @RequestBody Administrateur administrateurDetails) {
        Administrateur updatedAdmin = administrateurService.updateAdministrateur(id, administrateurDetails);
        return ResponseEntity.ok(updatedAdmin);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdministrateur(@PathVariable Integer id) {
        administrateurService.deleteAdministrateur(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{adminId}/droits-acces")
    public ResponseEntity<Administrateur> updateDroitsAcces(@PathVariable Integer adminId, @RequestBody List<String> droitsAcces) {
        // Accepter directement List<String> est ok ici car ce n'est pas une entité complexe.
        Administrateur admin = administrateurService.updateDroitsAcces(adminId, droitsAcces);
        return ResponseEntity.ok(admin);
    }
}
