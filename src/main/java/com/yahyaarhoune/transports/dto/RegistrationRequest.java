package com.yahyaarhoune.transports.dto;

// You might need validation annotations later (e.g., from jakarta.validation.constraints)
// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Size;

public class RegistrationRequest {

    // @NotBlank
    private String nom;

    // @NotBlank
    private String prenom;

    // @NotBlank
    // @Email
    private String email;

    // @NotBlank
    // @Size(min = 6, max = 40)
    private String motDePasse;

    // @NotBlank // Role is mandatory
    private String role; // e.g., "PASSENGER", "DRIVER", "ADMIN" (match your role names)

    // Add any other common fields needed for all user types during registration
    // For example, if dateDeNaissance is common:
    // private String dateDeNaissance; // Or LocalDate, handle parsing

    // --- Getters and Setters ---
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // public String getDateDeNaissance() { return dateDeNaissance; }
    // public void setDateDeNaissance(String dateDeNaissance) { this.dateDeNaissance = dateDeNaissance; }
}