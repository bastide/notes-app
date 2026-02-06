package com.demo.notes.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO pour les requêtes d'authentification (login).
 * 
 * Contient les informations d'identification de l'utilisateur.
 * Les annotations de validation assurent que les champs sont présents.
 * 
 * @author Demo
 */
public class LoginRequest {
    
    /**
     * Nom d'utilisateur pour la connexion.
     * Ne peut pas être vide ou null.
     */
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String username;
    
    /**
     * Mot de passe en clair (sera vérifié contre le hash en base).
     * Ne peut pas être vide ou null.
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    // Constructeur par défaut
    public LoginRequest() {
    }

    // Constructeur avec tous les paramètres
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters et Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
