package com.yahyaarhoune.transports.repository;

import com.yahyaarhoune.transports.models.Conducteur;
import com.yahyaarhoune.transports.models.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConducteurRepository extends JpaRepository<Conducteur, Integer> {

    Optional<Conducteur> findByEmail(String email);

    Optional<Conducteur> findByVehiculeAssigne(Vehicule vehicule);

    List<Conducteur> findByVehiculeAssigneId(Integer vehiculeId);

    // Example: Find conductors not currently assigned to any vehicle
    List<Conducteur> findByVehiculeAssigneIsNull();
}
