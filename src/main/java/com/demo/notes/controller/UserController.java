package com.demo.notes.controller;

import com.demo.notes.dto.CreateUserRequest;
import com.demo.notes.dto.UserResponse;
import com.demo.notes.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des utilisateurs.
 * 
 * Expose les endpoints pour:
 * - Créer des utilisateurs (ADMIN uniquement)
 * - Lister les utilisateurs (ADMIN uniquement)
 * - Supprimer des utilisateurs (ADMIN uniquement)
 * 
 * Tous ces endpoints nécessitent le rôle ROLE_ADMIN.
 * 
 * @author Demo
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Récupère la liste de tous les utilisateurs.
     * 
     * Accès réservé aux administrateurs.
     * 
     * @return Liste de tous les utilisateurs
     * 
     * Exemple de requête:
     * GET /api/users
     * Authorization: Bearer <token>
     * 
     * Exemple de réponse:
     * [
     *   {
     *     "id": 1,
     *     "username": "admin",
     *     "roles": ["ROLE_ADMIN", "ROLE_USER"],
     *     "createdAt": "2024-01-01T10:00:00"
     *   },
     *   ...
     * ]
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Récupère un utilisateur par son ID.
     * 
     * Accès réservé aux administrateurs.
     * 
     * @param id L'identifiant de l'utilisateur
     * @return Les détails de l'utilisateur
     * 
     * Exemple de requête:
     * GET /api/users/1
     * Authorization: Bearer <token>
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Crée un nouvel utilisateur.
     * 
     * Accès réservé aux administrateurs.
     * Les validations sur les données sont appliquées automatiquement.
     * 
     * @param request Les données du nouvel utilisateur
     * @return L'utilisateur créé
     * 
     * Exemple de requête:
     * POST /api/users
     * Authorization: Bearer <token>
     * {
     *   "username": "newuser",
     *   "password": "password123",
     *   "roles": ["ROLE_USER"]
     * }
     * 
     * Exemple de réponse:
     * {
     *   "id": 4,
     *   "username": "newuser",
     *   "roles": ["ROLE_USER"],
     *   "createdAt": "2024-01-04T15:30:00"
     * }
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    /**
     * Supprime un utilisateur.
     * 
     * Accès réservé aux administrateurs.
     * La suppression est en cascade: toutes les notes de l'utilisateur
     * seront également supprimées.
     * 
     * @param id L'identifiant de l'utilisateur à supprimer
     * @return Réponse sans contenu (204)
     * 
     * Exemple de requête:
     * DELETE /api/users/4
     * Authorization: Bearer <token>
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
