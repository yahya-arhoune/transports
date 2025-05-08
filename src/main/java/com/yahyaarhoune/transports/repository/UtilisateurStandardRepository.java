package com.yahyaarhoune.transports.repository;

import com.yahyaarhoune.transports.models.UtilisateurStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UtilisateurStandardRepository extends JpaRepository<UtilisateurStandard, Integer> {

    // Custom query to find a user by email (assuming email is unique)
    Optional<UtilisateurStandard> findByEmail(String email);

    // Find users by name
    List<UtilisateurStandard> findByNomIgnoreCase(String nom);

    List<UtilisateurStandard> findByNomAndPrenomAllIgnoreCase(String nom, String prenom);

    // You can add more custom query methods as needed
    // e.g., find users who have taken a specific trajet (would require a join)
}
