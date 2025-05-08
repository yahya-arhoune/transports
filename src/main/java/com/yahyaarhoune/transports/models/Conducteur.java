package com.yahyaarhoune.transports.models;

import com.yahyaarhoune.transports.models.base.AbstractUtilisateur;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conducteurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conducteur extends AbstractUtilisateur {

    @OneToMany(mappedBy = "conducteur")
    private List<Trajet> planning = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "vehicule_assigne_id", referencedColumnName = "id")
    private Vehicule vehiculeAssigne;

    @OneToMany(mappedBy = "signalePar")
    private List<Incident> incidentsSignales = new ArrayList<>();
}