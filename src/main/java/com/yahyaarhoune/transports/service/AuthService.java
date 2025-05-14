package com.yahyaarhoune.transports.service;

import com.yahyaarhoune.transports.dto.RegistrationRequest;
import com.yahyaarhoune.transports.models.Administrateur;
import com.yahyaarhoune.transports.models.Conducteur;
import com.yahyaarhoune.transports.models.Utilisateur; // Your base User class, if applicable
import com.yahyaarhoune.transports.models.UtilisateurStandard;
import com.yahyaarhoune.transports.repository.AdministrateurRepository;
import com.yahyaarhoune.transports.repository.ConducteurRepository;
import com.yahyaarhoune.transports.repository.UtilisateurStandardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService { // Or name it RegistrationService

    private final UtilisateurStandardRepository utilisateurStandardRepository;
    private final ConducteurRepository conducteurRepository;
    private final AdministrateurRepository administrateurRepository;
    // Optional: If you have a base UtilisateurRepository for common email checks
    // private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UtilisateurStandardRepository utilisateurStandardRepository,
                       ConducteurRepository conducteurRepository,
                       AdministrateurRepository administrateurRepository,
                       // UtilisateurRepository utilisateurRepository, // Optional
                       PasswordEncoder passwordEncoder) {
        this.utilisateurStandardRepository = utilisateurStandardRepository;
        this.conducteurRepository = conducteurRepository;
        this.administrateurRepository = administrateurRepository;
        // this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Object registerUser(RegistrationRequest request) {
        // 1. Check if email already exists across all user types
        // This logic might be simpler if you have a base UtilisateurRepository
        // with existsByEmail that checks the base table (if using JOINED inheritance)
        if (utilisateurStandardRepository.existsByEmail(request.getEmail()) ||
                conducteurRepository.existsByEmail(request.getEmail()) ||
                administrateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        // 2. Hash the password
        String hashedPassword = passwordEncoder.encode(request.getMotDePasse());

        // 3. Create and save the appropriate user type based on role
        // It's good to define your roles as constants or an Enum
        String role = request.getRole().toUpperCase(); // Normalize role

        switch (role) {
            case "PASSENGER": // Or "UTILISATEUR_STANDARD" - match what frontend will send
            case "UTILISATEUR_STANDARD":
                UtilisateurStandard standardUser = new UtilisateurStandard();
                standardUser.setNom(request.getNom());
                standardUser.setPrenom(request.getPrenom());
                standardUser.setEmail(request.getEmail());
                standardUser.setMotDePasse(hashedPassword);
                // standardUser.setRole("ROLE_UTILISATEUR_STANDARD"); // If your base Utilisateur entity has a role field
                // Set other UtilisateurStandard specific fields if any from DTO
                return utilisateurStandardRepository.save(standardUser);

            case "DRIVER": // Or "CONDUCTEUR"
            case "CONDUCTEUR":
                Conducteur conducteur = new Conducteur();
                conducteur.setNom(request.getNom());
                conducteur.setPrenom(request.getPrenom());
                conducteur.setEmail(request.getEmail());
                conducteur.setMotDePasse(hashedPassword);
                // conducteur.setRole("ROLE_CONDUCTEUR");
                // Set other Conducteur specific fields if any from DTO (e.g., numeroPermis)
                return conducteurRepository.save(conducteur);

            case "ADMIN": // Or "ADMINISTRATEUR"
            case "ADMINISTRATEUR":
                Administrateur admin = new Administrateur();
                admin.setNom(request.getNom());
                admin.setPrenom(request.getPrenom());
                admin.setEmail(request.getEmail());
                admin.setMotDePasse(hashedPassword);
                // admin.setRole("ROLE_ADMINISTRATEUR");
                // Set other Administrateur specific fields if any from DTO
                return administrateurRepository.save(admin);

            default:
                throw new IllegalArgumentException("Error: Invalid role specified: " + request.getRole());
        }
    }
}