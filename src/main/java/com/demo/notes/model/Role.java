package com.demo.notes.model;

import jakarta.persistence.*;

/**
 * Entité représentant un rôle dans le système.
 * 
 * Les rôles définissent les permissions des utilisateurs:
 * - ROLE_USER: Accès de base, gestion de ses propres notes
 * - ROLE_ADMIN: Accès administrateur, gestion des utilisateurs
 * 
 * Un utilisateur peut avoir plusieurs rôles (relation Many-to-Many).
 * 
 * @author Demo
 */
@Entity
@Table(name = "roles")
public class Role {
    
    /**
     * Identifiant unique du rôle (clé primaire).
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nom du rôle.
     * Doit suivre la convention Spring Security: commencer par "ROLE_"
     * Exemples: ROLE_USER, ROLE_ADMIN
     * 
     * Le nom est unique dans le système.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;
    
    /**
     * Constructeur pratique pour créer un rôle avec seulement son nom.
     * 
     * @param name Le nom du rôle
     */
    public Role(String name) {
        this.name = name;
    }

    // Constructeur par défaut
    public Role() {
    }

    // Constructeur avec tous les paramètres (sauf id auto-généré)
    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
