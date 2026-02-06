package com.demo.notes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
