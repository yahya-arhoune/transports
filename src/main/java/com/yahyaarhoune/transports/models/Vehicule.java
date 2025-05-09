package com.yahyaarhoune.transports.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yahyaarhoune.transports.models.enums.EtatVehicule;
import com.yahyaarhoune.transports.models.enums.TypeVehicule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int capacite;
    private String positionGPS; // Could be a more complex type or two doubles (lat/lon)

    @OneToMany(mappedBy = "vehicule")
    @JsonManagedReference("vehicule-trajets")
    private List<Trajet> listeTrajets = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeVehicule type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatVehicule etat;

    @OneToOne(mappedBy = "vehiculeAssigne", fetch = FetchType.LAZY)
    private Conducteur conducteurActuel; // The conductor currently assigned to this vehicle
}
