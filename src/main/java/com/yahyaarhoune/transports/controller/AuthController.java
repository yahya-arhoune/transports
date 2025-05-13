package com.yahyaarhoune.transports.controller;

// --- Model Imports ---
import com.yahyaarhoune.transports.models.Administrateur;
import com.yahyaarhoune.transports.models.Conducteur;
import com.yahyaarhoune.transports.models.UtilisateurStandard;
import com.yahyaarhoune.transports.models.enums.Role; // <<< ENSURE THIS PATH IS CORRECT FOR YOUR Role.java

// --- Repository Imports ---
import com.yahyaarhoune.transports.repository.AdministrateurRepository;
import com.yahyaarhoune.transports.repository.ConducteurRepository;
import com.yahyaarhoune.transports.repository.UtilisateurStandardRepository;

// --- Security Imports ---
import com.yahyaarhoune.transports.security.JwtUtil;

// --- Spring Framework Imports ---
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// --- Java Util Imports ---
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // --- Autowired Dependencies ---
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UtilisateurStandardRepository utilisateurStandardRepository;

    @Autowired
    private AdministrateurRepository administrateurRepository; // Assuming this exists and is autowired

    @Autowired
    private ConducteurRepository conducteurRepository;     // Assuming this exists and is autowired

    // --- LOGIN ENDPOINT ---
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequestPayload) {
        String email = loginRequestPayload.get("email");
        String password = loginRequestPayload.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required."));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String authenticatedEmail = userDetails.getUsername();
            String jwt = jwtUtil.generateTokenFromUsername(authenticatedEmail);

            Object loggedInUserEntity = null;
            // Fetch the specific user entity to return full details including role
            // This logic assumes your UserDetailsServiceImpl loads the correct type or
            // you can determine type from authorities.
            if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_UTILISATEUR_STANDARD"))) {
                Optional<UtilisateurStandard> standardOpt = utilisateurStandardRepository.findByEmail(authenticatedEmail);
                if (standardOpt.isPresent()) {
                    UtilisateurStandard user = standardOpt.get();
                    user.setMotDePasse(null); // Never send password hash back
                    loggedInUserEntity = user;
                }
            } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATEUR"))) {
                Optional<Administrateur> adminOpt = administrateurRepository.findByEmail(authenticatedEmail);
                if (adminOpt.isPresent()) {
                    Administrateur admin = adminOpt.get();
                    admin.setMotDePasse(null);
                    loggedInUserEntity = admin;
                }
            } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CONDUCTEUR"))) {
                Optional<Conducteur> conducteurOpt = conducteurRepository.findByEmail(authenticatedEmail);
                if (conducteurOpt.isPresent()) {
                    Conducteur conducteur = conducteurOpt.get();
                    conducteur.setMotDePasse(null);
                    loggedInUserEntity = conducteur;
                }
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("token", jwt);
            responseBody.put("type", "Bearer");

            if (loggedInUserEntity != null) {
                responseBody.put("user", loggedInUserEntity);
                System.out.println("Login successful for: " + authenticatedEmail + ". Returning user data with role.");
            } else {
                // Fallback if entity couldn't be re-fetched (should be rare if auth succeeded)
                System.err.println("WARNING: User authenticated but couldn't be re-fetched for response body. Email: " + authenticatedEmail);
                Map<String, String> partialUser = new HashMap<>();
                partialUser.put("email", authenticatedEmail);
                // Consider trying to extract role from userDetails.getAuthorities() here as a fallback
                responseBody.put("user", partialUser);
            }

            return ResponseEntity.ok(responseBody);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
        } catch (Exception e) {
            System.err.println("Authentication failed unexpectedly for email: " + email);
            e.printStackTrace(); // For server-side debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Authentication failed: " + e.getMessage()));
        }
    }

    // --- REGISTRATION ENDPOINT ---
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registrationPayload) {
        String nom = registrationPayload.get("nom");
        String prenom = registrationPayload.get("prenom");
        String email = registrationPayload.get("email");
        String motDePasse = registrationPayload.get("motDePasse"); // Key in JSON from client

        // Basic Validation
        if (nom == null || prenom == null || email == null || motDePasse == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required registration fields (nom, prenom, email, motDePasse)"));
        }

        // TODO: Add more robust validation:
        // 1. Check if email already exists using utilisateurStandardRepository.existsByEmail(email)
        //    if (utilisateurStandardRepository.existsByEmail(email)) {
        //        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email address already in use."));
        //    }
        // 2. Validate email format.
        // 3. Validate password strength.

        // Create the user object AFTER validation
        UtilisateurStandard user = new UtilisateurStandard();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setMotDePasse(passwordEncoder.encode(motDePasse)); // Hash the password
        user.setRole(Role.UTILISATEUR_STANDARD);              // Set the role

        try {
            utilisateurStandardRepository.save(user); // Save the new user
            System.out.println("Successfully saved user: " + email + " with role: " + user.getRole());

            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Catch database errors (e.g., unique constraint violation if email check missed)
            // or other unexpected errors during save.
            System.err.println("Registration failed for email during save: " + email);
            e.printStackTrace(); // For server-side debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Registration failed due to an internal server error."));
        }
    }
}