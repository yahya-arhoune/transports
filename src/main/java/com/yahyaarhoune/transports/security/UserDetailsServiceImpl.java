package com.yahyaarhoune.transports.security;

import com.yahyaarhoune.transports.models.Administrateur;
import com.yahyaarhoune.transports.models.Conducteur;
import com.yahyaarhoune.transports.models.UtilisateurStandard;
import com.yahyaarhoune.transports.repository.AdministrateurRepository;
import com.yahyaarhoune.transports.repository.ConducteurRepository;
import com.yahyaarhoune.transports.repository.UtilisateurStandardRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurStandardRepository utilisateurStandardRepository;
    private final AdministrateurRepository administrateurRepository;
    private final ConducteurRepository conducteurRepository;

    @Autowired
    public UserDetailsServiceImpl(UtilisateurStandardRepository utilisateurStandardRepository,
                                  AdministrateurRepository administrateurRepository,
                                  ConducteurRepository conducteurRepository) {
        this.utilisateurStandardRepository = utilisateurStandardRepository;
        this.administrateurRepository = administrateurRepository;
        this.conducteurRepository = conducteurRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try to find in UtilisateurStandard table
        Optional<UtilisateurStandard> standardUserOpt = utilisateurStandardRepository.findByEmail(email);
        if (standardUserOpt.isPresent()) {
            UtilisateurStandard user = standardUserOpt.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_UTILISATEUR_STANDARD")); // Example role
            // Add more specific roles if your UtilisateurStandard entity has them
            return new User(user.getEmail(), user.getMotDePasse(), authorities);
        }

        // Try to find in Administrateur table
        Optional<Administrateur> adminUserOpt = administrateurRepository.findByEmail(email);
        if (adminUserOpt.isPresent()) {
            Administrateur user = adminUserOpt.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRATEUR")); // Example role
            // You could get more specific roles from admin.getDroitsAcces() if they map to Spring Security roles
            // admin.getDroitsAcces().forEach(droit -> authorities.add(new SimpleGrantedAuthority("ROLE_" + droit.toUpperCase())));
            return new User(user.getEmail(), user.getMotDePasse(), authorities);
        }

        // Try to find in Conducteur table
        Optional<Conducteur> conducteurUserOpt = conducteurRepository.findByEmail(email);
        if (conducteurUserOpt.isPresent()) {
            Conducteur user = conducteurUserOpt.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_CONDUCTEUR")); // Example role
            return new User(user.getEmail(), user.getMotDePasse(), authorities);
        }

        // If not found in any table
        throw new UsernameNotFoundException("User Not Found with email: " + email);
    }
}