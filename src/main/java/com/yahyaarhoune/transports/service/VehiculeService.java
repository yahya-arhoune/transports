package com.yahyaarhoune.transports.service;

import com.yahyaarhoune.transports.models.Conducteur;
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.Vehicule;
import com.yahyaarhoune.transports.models.enums.EtatVehicule;
import com.yahyaarhoune.transports.models.enums.TypeVehicule;

import java.util.List;
import java.util.Optional;

public interface VehiculeService {
    Vehicule createVehicule(Vehicule vehicule);
    Optional<Vehicule> getVehiculeById(Integer id);
    List<Vehicule> getAllVehicules();
    Vehicule updateVehicule(Integer id, Vehicule vehiculeDetails);
    void deleteVehicule(Integer id);

    Vehicule updatePositionGPS(Integer vehiculeId, String newPosition);
    Vehicule updateVehiculeEtat(Integer vehiculeId, EtatVehicule newEtat);
    List<Trajet> getTrajetsForVehicule(Integer vehiculeId);
    // Conducteur getConducteurActuelForVehicule(Integer vehiculeId); // This is on Conducteur's vehiculeAssigne
}
