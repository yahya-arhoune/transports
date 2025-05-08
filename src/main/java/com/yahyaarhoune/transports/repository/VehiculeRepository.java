package com.yahyaarhoune.transports.repository;

import com.yahyaarhoune.transports.models.Vehicule;
import com.yahyaarhoune.transports.models.enums.EtatVehicule;
import com.yahyaarhoune.transports.models.enums.TypeVehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Integer> {

    List<Vehicule> findByType(TypeVehicule type);

    List<Vehicule> findByEtat(EtatVehicule etat);

    List<Vehicule> findByTypeAndEtat(TypeVehicule type, EtatVehicule etat);

    Optional<Vehicule> findByPositionGPS(String positionGPS); // Assuming positionGPS can be unique for lookup

    // Find vehicles with capacity greater than a certain value
    List<Vehicule> findByCapaciteGreaterThan(int capacite);
}
