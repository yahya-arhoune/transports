package com.yahyaarhoune.transports.controller;

import com.yahyaarhoune.transports.models.UtilisateurStandard; // Make sure this import exists
import com.yahyaarhoune.transports.repository.UtilisateurStandardRepository; // <<--- IMPORT YOUR REPOSITORY
import com.yahyaarhoune.transports.security.JwtUtil;

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
    @Autowired // Inject the PasswordEncoder bean
    private PasswordEncoder passwordEncoder;

    @Autowired // <<--- UNCOMMENTED: Inject your repository
    private UtilisateurStandardRepository utilisateurStandardRepository;
    // --- END OF NECESSARY DEPENDENCIES ---


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequestPayload) {
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


    // vvv --- REGISTRATION METHOD CORRECTED --- vvv
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registrationPayload) {
        String nom = registrationPayload.get("nom");
        String prenom = registrationPayload.get("prenom");
        String email = registrationPayload.get("email");
        String motDePasse = registrationPayload.get("motDePasse"); // Key name must match JSON

        // --- Basic Validation ---
        if (nom == null || prenom == null || email == null || motDePasse == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required registration fields (nom, prenom, email, motDePasse)"));
        }
        // TODO: Add more validation (email format, password complexity, check if email exists etc.)
        // Example: if (utilisateurStandardRepository.existsByEmail(email)) { return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email already exists")); }


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
            // !!! UNCOMMENTED: Call repository save method !!!
            utilisateurStandardRepository.save(user);

            System.out.println("Successfully saved user: " + email); // Update log

            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // Return 201 Created

        } catch (Exception e) {
            // Handle potential errors during saving (e.g., database constraints)
            // Log the full exception for debugging
            System.err.println("Registration failed for email: " + email);
            e.printStackTrace(); // Print stack trace to console
            // Return a generic server error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Registration failed due to an internal error."));
        }
    }
    // ^^^ --- END OF REGISTRATION METHOD --- ^^^
}