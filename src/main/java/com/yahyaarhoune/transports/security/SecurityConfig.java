package com.yahyaarhoune.transports.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // For specifying HTTP methods if needed for more granular security later
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // If you move to JWT and stateless
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays; // For Arrays.asList

// Assuming you will add these later when re-enabling JWT auth
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // If you were using JwtAuthFilter, you would autowire it:
    // @Autowired
    // private JwtAuthFilter jwtAuthFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow requests from your Next.js frontend development server
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // Allow common headers, including Authorization for JWT and Content-Type for JSON
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Cache-Control",
                "Content-Type",
                "X-Requested-With",
                "accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        // Expose headers that the client might need to read (e.g., if you set custom headers in response)
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // Example
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        // Max age for preflight request cache
        configuration.setMaxAge(3600L); // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this to all paths
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // APPLY CORS CONFIGURATION FIRST
                .csrf(csrf -> csrf.disable())
                // When you re-enable security, you'll likely want stateless sessions for JWT:
                // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                // Current state: permits ALL requests (for easier initial development)
                                .anyRequest().permitAll()

                        // EXAMPLE of how you would re-secure it later:
                        /*
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/utilisateurs-standard").permitAll() // Public registration
                        .requestMatchers("/api/public-data/**").permitAll() // Example of other public data
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Example admin-only
                        .requestMatchers("/api/vehicules/**").hasAnyRole("ADMIN", "CONDUCTEUR") // Example
                        .anyRequest().authenticated() // All other requests need authentication
                        */
                );
        // When re-enabling JWT security, you'd add your filter:
        // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}