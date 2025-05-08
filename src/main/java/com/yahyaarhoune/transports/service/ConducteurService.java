package com.yahyaarhoune.transports.service;

import com.yahyaarhoune.transports.models.Conducteur;
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.Incident;
import com.yahyaarhoune.transports.models.Vehicule;

import java.util.List;
import java.util.Optional;

public interface ConducteurService {
    Conducteur createConducteur(Conducteur conducteur);
    Optional<Conducteur> getConducteurById(Integer id);
    List<Conducteur> getAllConducteurs();
    Conducteur updateConducteur(Integer id, Conducteur conducteurDetails);
    void deleteConducteur(Integer id);

    List<Trajet> getPlanning(Integer conducteurId);
    Vehicule getAssignedVehicule(Integer conducteurId); // Can be null
    Conducteur assignVehicule(Integer conducteurId, Integer vehiculeId);
    Conducteur unassignVehicule(Integer conducteurId);
    List<Incident> getIncidentsSignalesParConducteur(Integer conducteurId);
}