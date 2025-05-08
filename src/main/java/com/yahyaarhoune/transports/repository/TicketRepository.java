package com.yahyaarhoune.transports.repository;

import com.yahyaarhoune.transports.models.Ticket;
import com.yahyaarhoune.transports.models.Trajet;
import com.yahyaarhoune.transports.models.UtilisateurStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    Optional<Ticket> findByCodeValidation(String codeValidation);

    List<Ticket> findByUtilisateur(UtilisateurStandard utilisateur);

    List<Ticket> findByTrajet(Trajet trajet);

    List<Ticket> findByDateAchatBetween(LocalDateTime start, LocalDateTime end);
}