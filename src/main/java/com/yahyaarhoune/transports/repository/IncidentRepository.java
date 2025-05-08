package com.yahyaarhoune.transports.repository;

import com.yahyaarhoune.transports.models.Conducteur;
import com.yahyaarhoune.transports.models.Incident;
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.enums.StatutIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Integer> {

    List<Incident> findByStatut(StatutIncident statut);

    List<Incident> findByTrajet(Trajet trajet);

    List<Incident> findBySignalePar(Conducteur conducteur);

    List<Incident> findByTypeIgnoreCase(String type);

    List<Incident> findByDateHeureBetween(LocalDateTime start, LocalDateTime end);
}