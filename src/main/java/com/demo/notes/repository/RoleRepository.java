package com.demo.notes.repository;

import com.demo.notes.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour la gestion des rôles.
 * 
 * Permet de récupérer les rôles par leur nom, ce qui est utile
 * lors de l'attribution de rôles aux utilisateurs.
 * 
 * @author Demo
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Recherche un rôle par son nom.
     * 
     * @param name Le nom du rôle (ex: "ROLE_USER", "ROLE_ADMIN")
     * @return Optional contenant le rôle s'il existe, sinon Optional.empty()
     */
    Optional<Role> findByName(String name);
    
    /**
     * Vérifie si un rôle avec ce nom existe.
     * 
     * @param name Le nom du rôle à vérifier
     * @return true si le rôle existe, false sinon
     */
    boolean existsByName(String name);
}
