package com.demo.notes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant un utilisateur dans le système.
 * 
 * Un utilisateur possède:
 * - Des identifiants de connexion (username, password)
 * - Un ou plusieurs rôles définissant ses permissions
 * - Un ensemble de notes qu'il a créées
 * 
 * Cette classe implémente UserDetails pour l'intégration avec Spring Security.
 * 
 * @author Demo
 */
@Entity
@Table(name = "users")
public class User {
    
    /**
     * Identifiant unique de l'utilisateur (clé primaire).
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nom d'utilisateur pour la connexion.
     * Doit être unique dans le système.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * Mot de passe encodé (BCrypt).
     * Ne doit jamais être renvoyé dans les réponses JSON.
     */
    @JsonIgnore
    @Column(nullable = false)
    private String password;
    
    /**
     * Date et heure de création du compte utilisateur.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Ensemble des rôles attribués à cet utilisateur.
     * Relation Many-to-Many avec chargement eager pour éviter les LazyInitializationException.
     * 
     * La table de jointure user_roles est créée automatiquement.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    /**
     * Ensemble des notes créées par cet utilisateur.
     * Relation One-to-Many bidirectionnelle avec cascade.
     * 
     * Si un utilisateur est supprimé, toutes ses notes sont également supprimées.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Note> notes = new HashSet<>();
    
    /**
     * Hook de pré-persistance pour initialiser la date de création.
     * Appelé automatiquement avant l'insertion en base.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    /**
     * Vérifie si l'utilisateur possède un rôle spécifique.
     * 
     * @param roleName Le nom du rôle à vérifier
     * @return true si l'utilisateur a ce rôle, false sinon
     */
    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    // Constructeur par défaut
    public User() {
    }

    // Constructeur avec tous les paramètres (sauf id auto-généré)
    public User(String username, String password, LocalDateTime createdAt, Set<Role> roles, Set<Note> notes) {
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
        this.roles = roles;
        this.notes = notes;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public void setNotes(Set<Note> notes) {
        this.notes = notes;
    }
}
