package com.yahyaarhoune.transports.models.base;

import com.yahyaarhoune.transports.models.enums.Role; // Ensure this import is correct
// Or keep it if Utilisateur is an interface AbstractUtilisateur implements
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor; // Added for completeness if you use it

import java.time.LocalDate; // Import for dateNaissance

@Getter
@Setter
@NoArgsConstructor // Good to have for JPA entities
@MappedSuperclass
public abstract class AbstractUtilisateur { // Removed 'implements Utilisateur' unless Utilisateur is an interface you defined

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Changed back to Long, more common for IDs

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    private LocalDate dateNaissance; // Added back if you need it

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // Make sure role is not nullable if every user must have one
    private Role role;           // <<< --- ADD THIS FIELD ---
}