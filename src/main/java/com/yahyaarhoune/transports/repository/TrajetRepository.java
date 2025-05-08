package com.yahyaarhoune.transports.repository;

import com.yahyaarhoune.transports.models.Conducteur;
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrajetRepository extends JpaRepository<Trajet, Integer> {

    List<Trajet> findByOrigineAndDestinationAllIgnoreCase(String origine, String destination);

    List<Trajet> findByVehicule(Vehicule vehicule);

    List<Trajet> findByConducteur(Conducteur conducteur);

    List<Trajet> findByHeureDepartBetween(LocalDateTime start, LocalDateTime end);

    List<Trajet> findByHeureDepartAfter(LocalDateTime dateTime);

    // Example: Find trajets for a specific vehicle on a specific day
    // This might require a @Query for more complex date comparisons if just 'between' is not enough
    // @Query("SELECT t FROM Trajet t WHERE t.vehicule = :vehicule AND FUNCTION('DATE', t.heureDepart) = FUNCTION('DATE', :date)")
    // List<Trajet> findByVehiculeAndDate(@Param("vehicule") Vehicule vehicule, @Param("date") LocalDateTime date);
}