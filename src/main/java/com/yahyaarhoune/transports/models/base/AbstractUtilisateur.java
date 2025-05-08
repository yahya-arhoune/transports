package com.yahyaarhoune.transports.models.base;

import com.yahyaarhoune.transports.models.Utilisateur;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass // Ensures this class's fields are part of its subclasses' tables
public abstract class AbstractUtilisateur implements Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String motDePasse; // In a real app, this would be hashed
}
