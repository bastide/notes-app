package com.demo.notes.dto;

/**
 * DTO pour les réponses contenant les informations d'une note.
 * 
 * Version simplifiée de l'entité Note, optimisée pour le transfert
 * vers le frontend. Contient toutes les informations nécessaires
 * à l'affichage d'une note.
 * 
 * @author Demo
 */
public class NoteResponse {
    
    /**
     * Identifiant unique de la note.
     */
    private Long id;
    
    /**
     * Titre de la note.
     */
    private String title;
    
    /**
     * Contenu de la note en format HTML.
     */
    private String content;
    
    /**
     * Date de création au format ISO 8601.
     */
    private String createdAt;
    
    /**
     * Date de dernière modification au format ISO 8601.
     */
    private String updatedAt;
    
    /**
     * Identifiant de l'utilisateur propriétaire.
     */
    private Long userId;
    
    /**
     * Nom d'utilisateur du propriétaire.
     */
    private String username;

    // Constructeur par défaut
    public NoteResponse() {
    }

    // Constructeur avec tous les paramètres
    public NoteResponse(Long id, String title, String content, String createdAt, String updatedAt, Long userId, String username) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.username = username;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
