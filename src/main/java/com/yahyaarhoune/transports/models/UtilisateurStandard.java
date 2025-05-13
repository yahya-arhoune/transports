package com.yahyaarhoune.transports.models;
import com.yahyaarhoune.transports.models.enums.Role;
import com.yahyaarhoune.transports.models.base.AbstractUtilisateur;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utilisateurs_standard")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurStandard extends AbstractUtilisateur {

    @ManyToMany
    @JoinTable(
            name = "utilisateur_trajet_historique",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "trajet_id")
    )
    private List<Trajet> historiqueTrajets = new ArrayList<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks = new ArrayList<>();

    // Assuming UtilisateurStandard can also buy tickets
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();
}