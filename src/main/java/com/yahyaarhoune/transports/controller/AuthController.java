package com.yahyaarhoune.transports.controller;

import com.yahyaarhoune.transports.security.JwtUtil;// Your JWT utility class

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections; // For an empty map if needed
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequestPayload) {
        // Extract email and password from the Map
        String email = loginRequestPayload.get("email"); // Key must match what client sends
        String password = loginRequestPayload.get("password"); // Key must match

        if (email == null || password == null) {
            // Create a response body for bad request
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", System.currentTimeMillis());
            body.put("status", 400);
            body.put("error", "Bad Request");
            body.put("message", "Email and password are required.");
            body.put("path", "/api/auth/login");
            return ResponseEntity.badRequest().body(body);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateTokenFromUsername(userDetails.getUsername()); // Assuming username is the email

            // Create a Map for the response body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("token", jwt);
            responseBody.put("type", "Bearer");
            // You could add more user details here if needed by fetching the user from repository
            // responseBody.put("email", userDetails.getUsername());

            return ResponseEntity.ok(responseBody);

        } catch (BadCredentialsException e) {
            // Create a response body for unauthorized
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", System.currentTimeMillis());
            body.put("status", 401);
            body.put("error", "Unauthorized");
            body.put("message", "Invalid credentials");
            body.put("path", "/api/auth/login");
            return ResponseEntity.status(401).body(body);
        } catch (Exception e) {
            // Catch other potential exceptions during authentication
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", System.currentTimeMillis());
            body.put("status", 500);
            body.put("error", "Internal Server Error");
            body.put("message", "Authentication failed: " + e.getMessage());
            body.put("path", "/api/auth/login");
            return ResponseEntity.status(500).body(body);
        }
    }

    // If you had a registration endpoint here and wanted to avoid DTOs for it:
    /*
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registrationPayload) {
        String nom = registrationPayload.get("nom");
        String prenom = registrationPayload.get("prenom");
        String email = registrationPayload.get("email");
        String motDePasse = registrationPayload.get("motDePasse");

        // ... (validation logic for all fields) ...

        // Create new user's account
        UtilisateurStandard user = new UtilisateurStandard();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setMotDePasse(passwordEncoder.encode(motDePasse)); // HASH password!

        // utilisateurStandardService.createUtilisateurStandard(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);
    }
    */
}