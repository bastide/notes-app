package com.demo.notes.dto;

import java.util.List;

/**
 * DTO pour les réponses d'authentification (login).
 * 
 * Contient le token JWT et les informations de base de l'utilisateur
 * connecté, qui seront utilisées par le frontend.
 * 
 * @author Demo
 */
public class LoginResponse {
    
    /**
     * Token JWT pour l'authentification des futures requêtes.
     * Le frontend doit inclure ce token dans l'en-tête Authorization.
     */
    private String token;
    
    /**
     * Type du token (généralement "Bearer").
     */
    private String type = "Bearer";
    
    /**
     * Identifiant de l'utilisateur connecté.
     */
    private Long id;
    
    /**
     * Nom d'utilisateur.
     */
    private String username;
    
    /**
     * Liste des rôles de l'utilisateur.
     * Utilisée par le frontend pour afficher/masquer certaines fonctionnalités.
     */
    private List<String> roles;
    
    /**
     * Constructeur sans le paramètre type (utilise la valeur par défaut "Bearer").
     */
    public LoginResponse(String token, Long id, String username, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    // Constructeur par défaut
    public LoginResponse() {
    }

    // Constructeur avec tous les paramètres
    public LoginResponse(String token, String type, Long id, String username, List<String> roles) {
        this.token = token;
        this.type = type;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    // Getters et Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
