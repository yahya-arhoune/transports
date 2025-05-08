package com.yahyaarhoune.transports.service.impl;

import com.yahyaarhoune.transports.exception.ResourceNotFoundException;
import com.yahyaarhoune.transports.models.Administrateur;
import com.yahyaarhoune.transports.repository.AdministrateurRepository;
import com.yahyaarhoune.transports.service.AdministrateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdministrateurServiceImpl implements AdministrateurService {

    private final AdministrateurRepository administrateurRepository;
    // private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdministrateurServiceImpl(AdministrateurRepository administrateurRepository /*, PasswordEncoder passwordEncoder */) {
        this.administrateurRepository = administrateurRepository;
        // this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Administrateur createAdministrateur(Administrateur administrateur) {
        // administrateur.setMotDePasse(passwordEncoder.encode(administrateur.getMotDePasse()));
        administrateur.setId(null);
        return administrateurRepository.save(administrateur);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Administrateur> getAdministrateurById(Integer id) {
        return administrateurRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Administrateur> getAllAdministrateurs() {
        return administrateurRepository.findAll();
    }

    @Override
    @Transactional
    public Administrateur updateAdministrateur(Integer id, Administrateur administrateurDetails) {
        Administrateur existingAdmin = administrateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrateur", "id", id));
        existingAdmin.setNom(administrateurDetails.getNom());
        existingAdmin.setPrenom(administrateurDetails.getPrenom());
        existingAdmin.setEmail(administrateurDetails.getEmail());
        if (administrateurDetails.getMotDePasse() != null && !administrateurDetails.getMotDePasse().isEmpty() &&
                !administrateurDetails.getMotDePasse().equals(existingAdmin.getMotDePasse())) {
            // existingAdmin.setMotDePasse(passwordEncoder.encode(administrateurDetails.getMotDePasse()));
        }
        existingAdmin.setDroitsAcces(administrateurDetails.getDroitsAcces()); // Remplacement direct de la liste
        return administrateurRepository.save(existingAdmin);
    }

    @Override
    @Transactional
    public void deleteAdministrateur(Integer id) {
        if (!administrateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Administrateur", "id", id);
        }
        administrateurRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Administrateur updateDroitsAcces(Integer adminId, List<String> droitsAcces) {
        Administrateur admin = administrateurRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Administrateur", "id", adminId));
        admin.setDroitsAcces(droitsAcces);
        return administrateurRepository.save(admin);
    }
}
