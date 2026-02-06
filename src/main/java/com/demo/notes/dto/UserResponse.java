package com.demo.notes.dto;

import java.util.Set;

/**
 * DTO pour les réponses contenant les informations d'un utilisateur.
 * 
 * Version simplifiée de l'entité User, sans le mot de passe et
 * les informations sensibles. Utilisée pour renvoyer les données
 * utilisateur au frontend.
 * 
 * @author Demo
 */
public class UserResponse {
    
    /**
     * Identifiant unique de l'utilisateur.
     */
    private Long id;
    
    /**
     * Nom d'utilisateur.
     */
    private String username;
    
    /**
     * Ensemble des noms de rôles attribués à l'utilisateur.
     */
    private Set<String> roles;
    
    /**
     * Date de création du compte (au format ISO 8601).
     */
    private String createdAt;

    // Constructeur par défaut
    public UserResponse() {
    }

    // Constructeur avec tous les paramètres
    public UserResponse(Long id, String username, Set<String> roles, String createdAt) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
