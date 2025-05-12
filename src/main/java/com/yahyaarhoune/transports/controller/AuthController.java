package com.yahyaarhoune.transports.controller;

import com.yahyaarhoune.transports.models.UtilisateurStandard; // <<--- ADD Import for your entity
import com.yahyaarhoune.transports.security.JwtUtil;
// import com.yahyaarhoune.transports.service.UtilisateurStandardService; // <<--- ADD Import for your service (if used)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // <<--- ADD Import for HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder; // <<--- ADD Import for PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import java.util.Collections; // Not strictly needed now
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    // --- NECESSARY DEPENDENCIES FOR REGISTRATION ---
    @Autowired // Inject the PasswordEncoder bean defined in your SecurityConfig
    private PasswordEncoder passwordEncoder;

    // @Autowired // <<--- UNCOMMENT and Inject your user service/repository
    // private UtilisateurStandardService utilisateurStandardService;
    // OR
    // @Autowired
    // private UtilisateurStandardRepository utilisateurStandardRepository;
    // --- END OF NECESSARY DEPENDENCIES ---


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequestPayload) {
        // ... existing login logic (no changes needed here) ...
        String email = loginRequestPayload.get("email");
        String password = loginRequestPayload.get("password");

        if (email == null || password == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Email and password are required.");
            // Add other fields if desired
            return ResponseEntity.badRequest().body(body);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateTokenFromUsername(userDetails.getUsername());
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("token", jwt);
            responseBody.put("type", "Bearer");
            return ResponseEntity.ok(responseBody);
        } catch (BadCredentialsException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Invalid credentials");
            // Add other fields if desired
            return ResponseEntity.status(401).body(body);
        } catch (Exception e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Authentication failed: " + e.getMessage());
            // Add other fields if desired
            return ResponseEntity.status(500).body(body);
        }
    }


    // vvv --- REGISTRATION METHOD UNCOMMENTED --- vvv
    @PostMapping("/register") // Ensure this annotation exists and is not commented
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registrationPayload) {
        String nom = registrationPayload.get("nom");
        String prenom = registrationPayload.get("prenom");
        String email = registrationPayload.get("email");
        // Key name must match JSON ("motDePasse" or "password")
        String motDePasse = registrationPayload.get("motDePasse");

        // --- Basic Validation ---
        if (nom == null || prenom == null || email == null || motDePasse == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required registration fields (nom, prenom, email, motDePasse)"));
        }
        // Add more validation (email format, password complexity, check if email exists etc.)
        // Example: if (utilisateurStandardService.existsByEmail(email)) { return ... }


        // --- Create and save user ---
        UtilisateurStandard user = new UtilisateurStandard();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        // !!! IMPORTANT: Hash the password using the injected encoder !!!
        user.setMotDePasse(passwordEncoder.encode(motDePasse));
        // Set default role if applicable in your model
        // user.setRole("ROLE_PASSENGER"); // Example if base class has role

        try {
            // !!! IMPORTANT: Replace with your actual service/repository call to save the user !!!
            // Example:
            // UtilisateurStandard savedUser = utilisateurStandardService.createUtilisateurStandard(user);
            // OR directly using repository (less ideal usually):
            // utilisateurStandardRepository.save(user);

            System.out.println("Attempting to register user (SAVING LOGIC REQUIRED): " + email); // Add log

            // If saving works (no exception thrown)
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully!");
            // Return 201 Created status for successful resource creation
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            // Handle potential errors during saving (e.g., email already exists constraint violation)
            System.err.println("Registration failed for email: " + email + " Error: " + e.getMessage());
            // Return a more specific error if possible (e.g., 409 Conflict if email exists)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }
    // ^^^ --- END OF REGISTRATION METHOD --- ^^^
}