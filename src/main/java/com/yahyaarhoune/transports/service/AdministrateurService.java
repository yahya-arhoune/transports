package com.yahyaarhoune.transports.service;

import com.yahyaarhoune.transports.models.Administrateur;

import java.util.List;
import java.util.Optional;

public interface AdministrateurService {
    Administrateur createAdministrateur(Administrateur admin);
    List<Administrateur> getAllAdministrateurs();
    Optional<Administrateur> getAdministrateurById(Integer id);
    Administrateur updateAdministrateur(Integer id, Administrateur adminDetails);
    void deleteAdministrateur(Integer id);
    Administrateur updateDroitsAcces(Integer adminId, List<String> droits);
}