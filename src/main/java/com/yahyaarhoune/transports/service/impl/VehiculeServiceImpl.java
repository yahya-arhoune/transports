package com.yahyaarhoune.transports.service.impl;

import com.yahyaarhoune.transports.exception.ResourceNotFoundException;
import com.yahyaarhoune.transports.models.Conducteur;
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.Vehicule;
import com.yahyaarhoune.transports.models.enums.EtatVehicule;
import com.yahyaarhoune.transports.repository.ConducteurRepository; // Pour dé-assigner le conducteur lors de la suppression du véhicule
import com.yahyaarhoune.transports.repository.TrajetRepository;
import com.yahyaarhoune.transports.repository.VehiculeRepository;
import com.yahyaarhoune.transports.service.VehiculeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculeServiceImpl implements VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final TrajetRepository trajetRepository;
    private final ConducteurRepository conducteurRepository;


    @Autowired
    public VehiculeServiceImpl(VehiculeRepository vehiculeRepository,
                               TrajetRepository trajetRepository,
                               ConducteurRepository conducteurRepository) {
        this.vehiculeRepository = vehiculeRepository;
        this.trajetRepository = trajetRepository;
        this.conducteurRepository = conducteurRepository;
    }

    @Override
    @Transactional
    public Vehicule createVehicule(Vehicule vehicule) {
        vehicule.setId(null); // Laisser la base de données générer l'ID
        // Les listes (listeTrajets) et objets liés (conducteurActuel) sont gérés par d'autres opérations.
        return vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehicule> getVehiculeById(Integer id) {
        return vehiculeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> getAllVehicules() {
        return vehiculeRepository.findAll();
    }

    @Override
    @Transactional
    public Vehicule updateVehicule(Integer id, Vehicule vehiculeDetails) {
        Vehicule existingVehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", id));

        existingVehicule.setCapacite(vehiculeDetails.getCapacite());
        existingVehicule.setPositionGPS(vehiculeDetails.getPositionGPS());
        existingVehicule.setType(vehiculeDetails.getType());
        existingVehicule.setEtat(vehiculeDetails.getEtat());
        // conducteurActuel est généralement géré par le service Conducteur (assignVehicule/unassignVehicule)
        // listeTrajets est une conséquence des trajets créés utilisant ce véhicule.
        return vehiculeRepository.save(existingVehicule);
    }

    @Override
    @Transactional
    public void deleteVehicule(Integer id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", id));

        // Logique métier avant suppression :
        // 1. Dé-assigner le conducteur si un conducteur est assigné à ce véhicule.
        Optional<Conducteur> assignedConducteurOpt = conducteurRepository.findByVehiculeAssigne(vehicule);
        if (assignedConducteurOpt.isPresent()) {
            Conducteur assignedConducteur = assignedConducteurOpt.get();
            assignedConducteur.setVehiculeAssigne(null);
            conducteurRepository.save(assignedConducteur);
        }

        // 2. Gérer les trajets qui utilisent ce véhicule.
        // Option A : Empêcher la suppression si des trajets actifs l'utilisent.
        // Option B : Mettre le champ 'vehicule' à null pour ces trajets (si nullable).
        // Option C : Supprimer en cascade les trajets (si la logique métier le permet, mais dangereux).
        List<Trajet> trajetsUtilisantVehicule = trajetRepository.findByVehicule(vehicule);
        if (!trajetsUtilisantVehicule.isEmpty()) {
            // Pour cet exemple, on va les dissocier (mettre vehicule à null)
            // Cela nécessite que Trajet.vehicule soit nullable et qu'il n'y ait pas de contrainte FK stricte
            // ou que la base de données permette SET NULL on delete.
            // Attention, cela peut ne pas être la meilleure approche pour tous les cas.
            for (Trajet trajet : trajetsUtilisantVehicule) {
                trajet.setVehicule(null); // Nécessite que la relation soit modifiable et nullable
                // trajetRepository.save(trajet); // Si Trajet est propriétaire et cascade non définie
            }
            // Si on ne sauvegarde pas chaque trajet, il faut s'assurer que la dissociation est bien persistée
            // ou que la suppression du véhicule en cascade gère cela (moins probable pour ManyToOne sans mappedBy).
            // Souvent, on empêcherait la suppression si des trajets existent.
            // throw new IllegalStateException("Impossible de supprimer le véhicule, il est utilisé par des trajets.");

        }


        vehiculeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Vehicule updatePositionGPS(Integer vehiculeId, String newPosition) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", vehiculeId));
        vehicule.setPositionGPS(newPosition);
        return vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional
    public Vehicule updateVehiculeEtat(Integer vehiculeId, EtatVehicule newEtat) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", vehiculeId));
        vehicule.setEtat(newEtat);
        return vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trajet> getTrajetsForVehicule(Integer vehiculeId) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", vehiculeId));
        // S'assurer que la collection est initialisée si lazy loading et que le service retourne l'entité
        // Ici, on utilise une méthode de repository dédiée, donc c'est géré.
        return trajetRepository.findByVehicule(vehicule);
    }
}
