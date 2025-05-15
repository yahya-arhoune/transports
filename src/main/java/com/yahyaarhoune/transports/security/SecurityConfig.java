package com.yahyaarhoune.transports.security; // Ensure this matches your package

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Optional
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy; // <<--- IMPORT THIS
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Optional: if you plan to use @PreAuthorize, @Secured
public class SecurityConfig {

    @Autowired // <<--- UNCOMMENT AND INJECT
    private JwtAuthFilter jwtAuthFilter;

    // UserDetailsService is typically configured into AuthenticationManagerBuilder or HttpSecurity,
    // direct injection here might not be needed for HttpSecurity config itself unless you use it
    // to configure AuthenticationManagerBuilder directly.
    // @Autowired
    // private UserDetailsService userDetailsService;


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
        // Allow ALL origins for DEVELOPMENT - BE CAREFUL IN PRODUCTION
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // <<--- MODIFIED FOR EXPO GO
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Cache-Control", "Content-Type", "X-Requested-With", "accept",
                "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .csrf(AbstractHttpConfigurer::disable)
                // --- SET SESSION MANAGEMENT TO STATELESS ---
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll() // Login, register
                        .requestMatchers(HttpMethod.GET, "/api/trips/available").permitAll() // Example: Allow browsing available trips

                        // Authenticated endpoints
                        .requestMatchers("/api/tickets/**").authenticated()
                        .requestMatchers("/api/trips/history/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/trips").authenticated() // Creating a trip
                        .requestMatchers(HttpMethod.GET, "/api/trips/{id}").authenticated() // Viewing specific trip details
                        // Add more specific rules if needed, e.g., for admin or driver roles
                        // .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Default: all other unmatche    d requests need authentication (if any are left)
                        .anyRequest().authenticated()
                )
                // --- ADD THE JWT FILTER TO THE CHAIN ---
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}