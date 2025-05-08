package com.yahyaarhoune.transports.repository;

import com.yahyaarhoune.transports.models.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, Integer> {

    Optional<Administrateur> findByEmail(String email);

    // Add other specific finders for Administrateur if needed
}