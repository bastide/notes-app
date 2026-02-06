package com.demo.notes.dto;

/**
 * DTO pour les réponses d'erreur standardisées.
 * 
 * Fournit un format uniforme pour renvoyer les erreurs au frontend,
 * facilitant leur traitement côté client.
 * 
 * @author Demo
 */
public class ErrorResponse {
    
    /**
     * Code d'erreur ou type d'erreur.
     */
    private String error;
    
    /**
     * Message d'erreur descriptif pour l'utilisateur.
     */
    private String message;
    
    /**
     * Timestamp de l'erreur.
     */
    private long timestamp;
    
    /**
     * Constructeur simplifié sans timestamp.
     * Le timestamp est automatiquement défini à la date/heure actuelle.
     */
    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    // Constructeur par défaut
    public ErrorResponse() {
    }

    // Constructeur avec tous les paramètres
    public ErrorResponse(String error, String message, long timestamp) {
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters et Setters
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
