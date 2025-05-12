package com.yahyaarhoune.transports.controller;

// --- NECESSARY IMPORTS (Ensure these match your project) ---
import com.yahyaarhoune.transports.models.Administrateur;
import com.yahyaarhoune.transports.models.Conducteur;
import com.yahyaarhoune.transports.models.UtilisateurStandard;
import com.yahyaarhoune.transports.repository.AdministrateurRepository;
import com.yahyaarhoune.transports.repository.ConducteurRepository;
import com.yahyaarhoune.transports.repository.UtilisateurStandardRepository;
import com.yahyaarhoune.transports.security.JwtUtil;
// --- END IMPORTS ---

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
import java.util.Optional; // Import Optional

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- Inject ALL user repositories needed for login response ---
    @Autowired
    private UtilisateurStandardRepository utilisateurStandardRepository;
    @Autowired
    private AdministrateurRepository administrateurRepository; // Assuming it exists
    @Autowired
    private ConducteurRepository conducteurRepository; // Assuming it exists
    // --- END INJECTIONS ---


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequestPayload) {
        String email = loginRequestPayload.get("email");
        String password = loginRequestPayload.get("password");

        if (email == null || password == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Email and password are required.");
            return ResponseEntity.badRequest().body(body);
        }
        try {
            // Step 1: Authenticate using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Step 2: Get UserDetails (contains username/email)
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String authenticatedEmail = userDetails.getUsername(); // Email used for login

            // Step 3: Generate JWT Token
            String jwt = jwtUtil.generateTokenFromUsername(authenticatedEmail);

            // --- Step 4: Fetch the full user entity based on email ---
            // This is needed to return user details in the response.
            // We need to check which type of user logged in.
            Object loggedInUserEntity = null;
            Optional<UtilisateurStandard> standardOpt = utilisateurStandardRepository.findByEmail(authenticatedEmail);
            if (standardOpt.isPresent()) {
                loggedInUserEntity = standardOpt.get();
                // Nullify password before sending back to client
                ((UtilisateurStandard)loggedInUserEntity).setMotDePasse(null);
            } else {
                Optional<Administrateur> adminOpt = administrateurRepository.findByEmail(authenticatedEmail);
                if (adminOpt.isPresent()) {
                    loggedInUserEntity = adminOpt.get();
                    ((Administrateur)loggedInUserEntity).setMotDePasse(null);
                } else {
                    Optional<Conducteur> conducteurOpt = conducteurRepository.findByEmail(authenticatedEmail);
                    if (conducteurOpt.isPresent()) {
                        loggedInUserEntity = conducteurOpt.get();
                        ((Conducteur)loggedInUserEntity).setMotDePasse(null);
                    }
                }
            }
            // --- End Fetch User Entity ---

            // Step 5: Build the Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("token", jwt);
            responseBody.put("type", "Bearer");

            if (loggedInUserEntity != null) {
                responseBody.put("user", loggedInUserEntity); // <<--- ADDED USER OBJECT
                System.out.println("Login successful for: " + authenticatedEmail + ". Returning user data.");
            } else {
                // This case should ideally not happen if authentication succeeded,
                // but handle defensively. Maybe just return token?
                System.err.println("WARNING: User authenticated but couldn't be re-fetched for response body. Email: " + authenticatedEmail);
                // Decide if you want to return an error or just the token
                // return ResponseEntity.status(500).body(Map.of("message", "Login succeeded but failed to retrieve user details."));
            }

            return ResponseEntity.ok(responseBody); // Return token AND user

        } catch (BadCredentialsException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Invalid credentials");
            return ResponseEntity.status(401).body(body);
        } catch (Exception e) {
            System.err.println("Authentication failed unexpectedly for email: " + email);
            e.printStackTrace(); // Log the full stack trace for debugging
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Authentication failed: " + e.getMessage());
            return ResponseEntity.status(500).body(body);
        }
    }


    // vvv --- REGISTRATION METHOD (No changes from previous corrected version) --- vvv
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registrationPayload) {
        String nom = registrationPayload.get("nom");
        String prenom = registrationPayload.get("prenom");
        String email = registrationPayload.get("email");
        String motDePasse = registrationPayload.get("motDePasse");

        if (nom == null || prenom == null || email == null || motDePasse == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required registration fields (nom, prenom, email, motDePasse)"));
        }
        // TODO: Add more validation

        UtilisateurStandard user = new UtilisateurStandard();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setMotDePasse(passwordEncoder.encode(motDePasse));

        try {
            utilisateurStandardRepository.save(user);
            System.out.println("Successfully saved user: " + email);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("Registration failed for email: " + email);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Registration failed due to an internal error."));
        }
    }
    // ^^^ --- END OF REGISTRATION METHOD --- ^^^
}