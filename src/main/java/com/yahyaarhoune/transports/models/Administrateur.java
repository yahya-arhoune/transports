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
@Table(name = "administrateurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Administrateur extends AbstractUtilisateur {

    @ElementCollection(fetch = FetchType.EAGER) // EAGER for small collections of basic types
    @CollectionTable(name = "admin_droits_acces", joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "droit")
    private List<String> droitsAcces = new ArrayList<>();
}