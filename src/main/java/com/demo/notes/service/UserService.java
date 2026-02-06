package com.demo.notes.service;

import com.demo.notes.dto.CreateUserRequest;
import com.demo.notes.dto.UserResponse;
import com.demo.notes.model.Role;
import com.demo.notes.model.User;
import com.demo.notes.repository.RoleRepository;
import com.demo.notes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service de gestion des utilisateurs.
 * 
 * Fournit les opérations métier pour:
 * - Créer de nouveaux utilisateurs (réservé aux admins)
 * - Supprimer des utilisateurs (réservé aux admins)
 * - Lister tous les utilisateurs
 * - Récupérer les détails d'un utilisateur
 * 
 * @author Demo
 */
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Crée un nouvel utilisateur dans le système.
     * 
     * Cette méthode:
     * 1. Vérifie que le nom d'utilisateur n'existe pas déjà
     * 2. Encode le mot de passe avec BCrypt
     * 3. Attribue les rôles spécifiés (ou ROLE_USER par défaut)
     * 4. Sauvegarde l'utilisateur en base
     * 
     * @param request Les données du nouvel utilisateur
     * @return Les détails de l'utilisateur créé
     * @throws RuntimeException Si le nom d'utilisateur existe déjà ou si un rôle est invalide
     */
    public UserResponse createUser(CreateUserRequest request) {
        // Vérification de l'unicité du nom d'utilisateur
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Ce nom d'utilisateur existe déjà");
        }
        
        // Création de l'utilisateur
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Attribution des rôles
        Set<Role> roles = new HashSet<>();
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            // Par défaut, attribuer le rôle USER
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Rôle USER non trouvé"));
            roles.add(userRole);
        } else {
            // Récupération des rôles demandés
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Rôle non trouvé: " + roleName));
                roles.add(role);
            }
        }
        user.setRoles(roles);
        
        // Sauvegarde en base de données
        User savedUser = userRepository.save(user);
        
        // Conversion en DTO pour la réponse
        return convertToResponse(savedUser);
    }
    
    /**
     * Récupère tous les utilisateurs du système.
     * 
     * @return Liste de tous les utilisateurs
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère un utilisateur par son identifiant.
     * 
     * @param id L'identifiant de l'utilisateur
     * @return Les détails de l'utilisateur
     * @throws RuntimeException Si l'utilisateur n'existe pas
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return convertToResponse(user);
    }
    
    /**
     * Supprime un utilisateur du système.
     * 
     * La suppression est en cascade: toutes les notes de l'utilisateur
     * seront également supprimées.
     * 
     * @param id L'identifiant de l'utilisateur à supprimer
     * @throws RuntimeException Si l'utilisateur n'existe pas
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        userRepository.deleteById(id);
    }
    
    /**
     * Convertit une entité User en DTO UserResponse.
     * 
     * Exclut les informations sensibles (mot de passe) et
     * formate les données pour le frontend.
     * 
     * @param user L'entité utilisateur à convertir
     * @return Le DTO utilisateur
     */
    private UserResponse convertToResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            roleNames,
            user.getCreatedAt().toString()
        );
    }
}
