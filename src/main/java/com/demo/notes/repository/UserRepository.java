package com.demo.notes.repository;

import com.demo.notes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour la gestion des utilisateurs.
 * 
 * Fournit des méthodes pour rechercher les utilisateurs par nom
 * et vérifier leur existence, essentielles pour l'authentification
 * et la gestion des comptes.
 * 
 * @author Demo
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     * 
     * Utilisé principalement lors de l'authentification pour
     * charger les détails de l'utilisateur.
     * 
     * @param username Le nom d'utilisateur à rechercher
     * @return Optional contenant l'utilisateur s'il existe, sinon Optional.empty()
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Vérifie si un utilisateur avec ce nom existe déjà.
     * 
     * Utile lors de la création de nouveaux comptes pour
     * s'assurer de l'unicité du nom d'utilisateur.
     * 
     * @param username Le nom d'utilisateur à vérifier
     * @return true si un utilisateur avec ce nom existe, false sinon
     */
    boolean existsByUsername(String username);
}
