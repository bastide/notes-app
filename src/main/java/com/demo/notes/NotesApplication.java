package com.demo.notes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Classe principale de l'application Notes.
 * 
 * Cette application démontre l'utilisation de Spring Security pour:
 * - L'authentification basée sur JWT
 * - L'autorisation avec des rôles (USER et ADMIN)
 * - La gestion de notes avec opérations CRUD
 * - La gestion des utilisateurs (réservée aux administrateurs)
 * 
 * @author Demo
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class NotesApplication {

    /**
     * Point d'entrée de l'application.
     * 
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(NotesApplication.class, args);
    }
}
