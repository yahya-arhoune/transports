package com.yahyaarhoune.transports.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Keep CSRF disabled if needed for API
                .authorizeHttpRequests(auth -> auth
                        // This line now permits ALL requests to ANY endpoint unconditionally
                        .anyRequest().permitAll()
                );
        // .httpBasic(Customizer.withDefaults()); // httpBasic is now irrelevant if all requests are permitted, but leaving it doesn't hurt

        return http.build();
    }

    // IMPORTANT: You still NEED a PasswordEncoder bean if your services
    // are trying to hash passwords during user creation/update, even if login isn't enforced here.
    /*
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    */
}