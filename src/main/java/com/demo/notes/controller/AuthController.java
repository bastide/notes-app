package com.demo.notes.controller;

import com.demo.notes.dto.LoginRequest;
import com.demo.notes.dto.LoginResponse;
import com.demo.notes.model.User;
import com.demo.notes.repository.UserRepository;
import com.demo.notes.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour l'authentification.
 * 
 * Expose les endpoints pour:
 * - Login (génération de token JWT)
 * 
 * Ce contrôleur est accessible sans authentification.
 * 
 * @author Demo
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint de connexion (login).
     * 
     * Processus:
     * 1. Valide les identifiants (username/password)
     * 2. Génère un token JWT
     * 3. Retourne le token avec les informations utilisateur
     * 
     * @param loginRequest Les identifiants de connexion
     * @return Le token JWT et les informations utilisateur
     * 
     *         Exemple de requête:
     *         POST /api/auth/login
     *         {
     *         "username": "user1",
     *         "password": "password"
     *         }
     * 
     *         Exemple de réponse:
     *         {
     *         "token": "eyJhbGciOiJIUzUxMiJ9...",
     *         "type": "Bearer",
     *         "id": 1,
     *         "username": "user1",
     *         "roles": ["ROLE_USER"]
     *         }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authentification via Spring Security
            // Lance une exception si les identifiants sont invalides
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            // Définit l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Génération du token JWT
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtils.generateToken(userDetails);

            // Récupération des informations complètes de l'utilisateur
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Extraction des noms de rôles
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Construction de la réponse
            LoginResponse response = new LoginResponse(
                    jwt,
                    user.getId(),
                    user.getUsername(),
                    roles);

            return ResponseEntity.ok(response);
        } catch (org.springframework.security.core.AuthenticationException e) {
            // Gestion des erreurs d'authentification
            return ResponseEntity
                    .status(401)
                    .body(new ErrorResponse(
                            "Unauthorized",
                            "Nom d'utilisateur ou mot de passe incorrect",
                            System.currentTimeMillis()));
        }
    }

    /**
     * Classe interne pour les réponses d'erreur
     */
    private static class ErrorResponse {
        private String error;
        private String message;
        private long timestamp;

        public ErrorResponse(String error, String message, long timestamp) {
            this.error = error;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
