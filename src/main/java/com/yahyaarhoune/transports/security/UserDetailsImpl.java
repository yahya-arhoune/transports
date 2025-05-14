package com.yahyaarhoune.transports.security; // Or your security package

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

// This class will wrap your actual user entity or hold its key details
public class UserDetailsImpl implements UserDetails {

    private Long id; // <<--- STORE THE DATABASE ID
    private String username; // This will be the email
    private String password; // Hashed password
    private Collection<? extends GrantedAuthority> authorities;
    // You can add other frequently needed user details like nom, prenom if desired

    // Constructor that takes your actual user entity
    public UserDetailsImpl(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = email;
        this.password = password;
        this.authorities = authorities;
    }

    // --- Getter for ID ---
    public Long getId() {
        return id;
    }

    // --- Implement UserDetails methods ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username; // which is email
    }

    // --- Account status methods (implement as needed or return true) ---
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}