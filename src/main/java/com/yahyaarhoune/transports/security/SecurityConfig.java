package com.yahyaarhoune.transports.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.Customizer; // No longer strictly needed if not using httpBasic for anything
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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