package com.demo.notes.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO pour les requêtes de création et modification de notes.
 * 
 * Contient les données nécessaires pour créer ou mettre à jour une note.
 * Les validations assurent que les champs obligatoires sont présents.
 * 
 * @author Demo
 */
public class NoteRequest {
    
    /**
     * Titre de la note.
     * Ne peut pas être vide ou null.
     */
    @NotBlank(message = "Le titre est obligatoire")
    private String title;
    
    /**
     * Contenu de la note en format HTML (texte riche).
     * Ne peut pas être vide ou null.
     */
    @NotBlank(message = "Le contenu est obligatoire")
    private String content;

    // Constructeur par défaut
    public NoteRequest() {
    }

    // Constructeur avec tous les paramètres
    public NoteRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getters et Setters
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
}
