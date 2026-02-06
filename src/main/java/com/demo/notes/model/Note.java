package com.demo.notes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entité représentant une note dans le système.
 *
 * Chaque note appartient à un utilisateur et contient:
 * - Un titre
 * - Un contenu en format HTML (texte riche)
 * - Des métadonnées (dates de création et modification)
 *
 * Les notes sont liées à leur propriétaire via une relation Many-to-One.
 *
 * @author Demo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notes")
@EntityListeners(AuditingEntityListener.class)
public class Note {

    /**
     * Identifiant unique de la note (clé primaire).
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Titre de la note.
     * Ne peut pas être null et doit contenir au moins un caractère.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Contenu de la note en format HTML.
     * Peut contenir du texte riche avec mise en forme.
     * Stocké dans une colonne TEXT pour supporter de grands contenus.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Date et heure de création de la note.
     * Automatiquement définie lors de la persistance initiale.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date et heure de la dernière modification.
     * Automatiquement mise à jour lors de chaque modification.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Utilisateur propriétaire de cette note.
     * Relation Many-to-One: plusieurs notes peuvent appartenir à un utilisateur.
     * Le propriétaire ne peut pas être null.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    /**
     * Hook de pré-persistance pour initialiser les dates.
     * Appelé automatiquement avant l'insertion en base.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Hook de pré-mise à jour pour actualiser la date de modification.
     * Appelé automatiquement avant chaque update en base.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
