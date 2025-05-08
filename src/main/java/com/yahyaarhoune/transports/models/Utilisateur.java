package com.yahyaarhoune.transports.models;

import java.util.List;

public interface Utilisateur {
    Integer getId();
    void setId(Integer id);

    String getNom();
    void setNom(String nom);

    String getPrenom();
    void setPrenom(String prenom);

    String getEmail();
    void setEmail(String email);

    String getMotDePasse();
    void setMotDePasse(String motDePasse);
}