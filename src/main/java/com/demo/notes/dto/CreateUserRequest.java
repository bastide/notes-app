package com.demo.notes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * DTO pour les requêtes de création d'utilisateur.
 * 
 * Utilisé par les administrateurs pour créer de nouveaux comptes.
 * Contient les validations nécessaires pour assurer la qualité des données.
 * 
 * @author Demo
 */
public class CreateUserRequest {
    
    /**
     * Nom d'utilisateur souhaité.
     * Doit contenir entre 3 et 50 caractères.
     */
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;
    
    /**
     * Mot de passe pour le nouveau compte.
     * Doit contenir au moins 6 caractères.
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
    
    /**
     * Ensemble des noms de rôles à attribuer à l'utilisateur.
     * Peut être vide (un rôle par défaut sera attribué).
     */
    private Set<String> roles;

    // Constructeur par défaut
    public CreateUserRequest() {
    }

    // Constructeur avec tous les paramètres
    public CreateUserRequest(String username, String password, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
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

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
