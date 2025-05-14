package com.yahyaarhoune.transports.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private LocalDateTime dateAchat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajet_id", nullable = false)
    private Trajet trajet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private UtilisateurStandard utilisateur; // Assuming standard users buy tickets

    @Column(nullable = false, unique = true)
    private String codeValidation;

    // --- ADD THIS LIFECYCLE CALLBACK METHOD ---
    @PrePersist // From jakarta.persistence.PrePersist
    protected void onCreate() {
        System.out.println("--- Ticket @PrePersist: onCreate called ---"); // For debugging
        if (this.dateAchat == null) { // Set only if not already set (though usually it would be null here)
            this.dateAchat = LocalDateTime.now();
            System.out.println("Ticket @PrePersist: dateAchat set to " + this.dateAchat);
        }
        if (this.codeValidation == null || this.codeValidation.trim().isEmpty()) { // Set only if not already set or empty
            this.codeValidation = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
            System.out.println("Ticket @PrePersist: codeValidation set to " + this.codeValidation);
        }
    }
    // --- END LIFECYCLE CALLBACK ---
}