package com.yahyaarhoune.transports.repository;

import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.UtilisateurStandard; // If using for findByListePassagersContaining
import com.yahyaarhoune.transports.models.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrajetRepository extends JpaRepository<Trajet, Long> {

    // For getAvailableTrips
    List<Trajet> findByStatutInAndHeureDepartAfter(List<String> statuses, LocalDateTime departureTime);

    // For getPassengerTripHistory - Example if Trajet has a ManyToMany with UtilisateurStandard
    // @Query("SELECT t FROM Trajet t JOIN t.listePassagers p WHERE p.id = :passengerId")
    // List<Trajet> findByListePassagersId(@Param("passengerId") Long passengerId);

    // For getPassengerTripHistory - Simpler placeholder: find all trajets where a ticket exists for this user for that trajet
    // This requires a more complex query involving the Ticket entity.
    // A simple placeholder if you add a direct (less ideal) passengerId column to Trajet for testing:
    // List<Trajet> findBySomePassengerIdentifier(Long passengerId);

    // YOU NEED TO DEFINE A METHOD HERE TO GET A PASSENGER'S TRIPS
    // The implementation depends on your data model (Ticket entity, or listePassagers in Trajet)
    // Example if you decide to query through tickets (preferred):
    @Query("SELECT tick.trajet FROM Ticket tick WHERE tick.utilisateur.id = :passengerId") // Assuming Ticket has 'utilisateur' field
    List<Trajet> findTrajetsByPassengerId(@Param("passengerId") Long passengerId);

    // If Trajet has a @ManyToMany List<UtilisateurStandard> listePassagers;
    // List<Trajet> findByListePassagersContaining(UtilisateurStandard passenger);
    List<Trajet> findByVehicule(Vehicule vehicule);

}