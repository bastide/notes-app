package com.demo.notes.repository;

import com.demo.notes.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des notes.
 * 
 * Fournit des méthodes CRUD de base via JpaRepository et
 * des requêtes personnalisées pour filtrer les notes par utilisateur.
 * 
 * Spring Data JPA génère automatiquement l'implémentation
 * de cette interface au runtime.
 * 
 * @author Demo
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    
    /**
     * Récupère toutes les notes appartenant à un utilisateur spécifique.
     * 
     * Méthode de requête dérivée: Spring génère automatiquement
     * la requête SQL à partir du nom de la méthode.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @return Liste des notes de l'utilisateur, ordonnées par date de mise à jour décroissante
     */
    List<Note> findByUserIdOrderByUpdatedAtDesc(Long userId);
    
    /**
     * Compte le nombre de notes appartenant à un utilisateur.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @return Le nombre de notes de l'utilisateur
     */
    long countByUserId(Long userId);
}
