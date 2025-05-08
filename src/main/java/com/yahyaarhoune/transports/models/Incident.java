package com.yahyaarhoune.transports.models;

import com.yahyaarhoune.transports.models.enums.StatutIncident;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String type; // e.g., "Panne", "Accident", "Retard"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private LocalDateTime dateHeure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conducteur_signal_id") // Who reported it
    private Conducteur signalePar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutIncident statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajet_id") // Incident related to which Trajet
    private Trajet trajet;
}
