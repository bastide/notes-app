package com.demo.notes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
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
}
