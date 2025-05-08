package com.yahyaarhoune.transports.service.impl;

import com.yahyaarhoune.transports.exception.ResourceNotFoundException;
import com.yahyaarhoune.transports.models.Conducteur;
import com.yahyaarhoune.transports.models.Incident;
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.Vehicule;
import com.yahyaarhoune.transports.repository.ConducteurRepository;
import com.yahyaarhoune.transports.repository.IncidentRepository;
import com.yahyaarhoune.transports.repository.VehiculeRepository;
import com.yahyaarhoune.transports.service.ConducteurService;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConducteurServiceImpl implements ConducteurService {

    private final ConducteurRepository conducteurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final IncidentRepository incidentRepository;
    // private final PasswordEncoder passwordEncoder;

    @Autowired
    public ConducteurServiceImpl(ConducteurRepository conducteurRepository,
                                 VehiculeRepository vehiculeRepository,
                                 IncidentRepository incidentRepository
            /*, PasswordEncoder passwordEncoder */) {
        this.conducteurRepository = conducteurRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.incidentRepository = incidentRepository;
        // this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Conducteur createConducteur(Conducteur conducteur) {
        // conducteur.setMotDePasse(passwordEncoder.encode(conducteur.getMotDePasse()));
        conducteur.setId(null);
        conducteur.setVehiculeAssigne(null); // Un nouveau conducteur n'a pas de véhicule assigné initialement
        return conducteurRepository.save(conducteur);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conducteur> getConducteurById(Integer id) {
        return conducteurRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conducteur> getAllConducteurs() {
        return conducteurRepository.findAll();
    }

    @Override
    @Transactional
    public Conducteur updateConducteur(Integer id, Conducteur conducteurDetails) {
        Conducteur existingConducteur = conducteurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur", "id", id));
        existingConducteur.setNom(conducteurDetails.getNom());
        existingConducteur.setPrenom(conducteurDetails.getPrenom());
        existingConducteur.setEmail(conducteurDetails.getEmail());
        if (conducteurDetails.getMotDePasse() != null && !conducteurDetails.getMotDePasse().isEmpty() &&
                !conducteurDetails.getMotDePasse().equals(existingConducteur.getMotDePasse())) {
            // existingConducteur.setMotDePasse(passwordEncoder.encode(conducteurDetails.getMotDePasse()));
        }
        // Ne pas mettre à jour vehiculeAssigne ici, utiliser les méthodes dédiées.
        return conducteurRepository.save(existingConducteur);
    }

    @Override
    @Transactional
    public void deleteConducteur(Integer id) {
        Conducteur conducteur = conducteurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur", "id", id));
        // Logique pour dé-assigner le véhicule si nécessaire
        if (conducteur.getVehiculeAssigne() != null) {
            Vehicule vehicule = conducteur.getVehiculeAssigne();
            // Si Vehicule a une référence à conducteurActuel, la mettre à null
            // vehicule.setConducteurActuel(null); // Si la relation est bidirectionnelle gérée sur Vehicule
            // vehiculeRepository.save(vehicule);
            conducteur.setVehiculeAssigne(null);
        }
        // Gérer les trajets assignés ? Les incidents signalés ?
        conducteurRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trajet> getPlanning(Integer conducteurId) {
        Conducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur", "id", conducteurId));
        conducteur.getPlanning().size(); // Déclencher chargement
        return conducteur.getPlanning();
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicule getAssignedVehicule(Integer conducteurId) {
        Conducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur", "id", conducteurId));
        return conducteur.getVehiculeAssigne();
    }

    @Override
    @Transactional
    public Conducteur assignVehicule(Integer conducteurId, Integer vehiculeId) {
        Conducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur", "id", conducteurId));
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", vehiculeId));
        // Logique pour vérifier si le véhicule ou le conducteur est déjà assigné
        conducteur.setVehiculeAssigne(vehicule);
        // Si Vehicule a conducteurActuel
        // vehicule.setConducteurActuel(conducteur);
        // vehiculeRepository.save(vehicule);
        return conducteurRepository.save(conducteur);
    }

    @Override
    @Transactional
    public Conducteur unassignVehicule(Integer conducteurId) {
        Conducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur", "id", conducteurId));
        Vehicule vehicule = conducteur.getVehiculeAssigne();
        if (vehicule != null) {
            // Si Vehicule a conducteurActuel
            // vehicule.setConducteurActuel(null);
            // vehiculeRepository.save(vehicule);
            conducteur.setVehiculeAssigne(null);
            return conducteurRepository.save(conducteur);
        }
        return conducteur;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Incident> getIncidentsSignalesParConducteur(Integer conducteurId) {
        Conducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new ResourceNotFoundException("Conducteur", "id", conducteurId));
        return incidentRepository.findBySignalePar(conducteur);
    }
}