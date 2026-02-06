package com.demo.notes.exception;

import com.demo.notes.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour l'API REST.
 * 
 * Cette classe intercepte les exceptions levées par les contrôleurs
 * et les transforme en réponses HTTP appropriées avec des messages
 * d'erreur clairs pour le frontend.
 * 
 * @author Demo
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Gère les erreurs de validation des requêtes.
     * 
     * Intercepte les erreurs de validation des @Valid sur les DTOs
     * et renvoie un détail des champs en erreur.
     * 
     * @param ex L'exception de validation
     * @return Réponse 400 avec les détails des erreurs de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // Parcours de toutes les erreurs de validation
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }
    
    /**
     * Gère les erreurs d'authentification (identifiants invalides).
     * 
     * @param ex L'exception d'authentification
     * @return Réponse 401 avec message d'erreur
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(
            "Authentication Failed",
            "Nom d'utilisateur ou mot de passe incorrect"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    /**
     * Gère les erreurs lorsqu'un utilisateur n'est pas trouvé.
     * 
     * @param ex L'exception utilisateur non trouvé
     * @return Réponse 404 avec message d'erreur
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            "User Not Found",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Gère les erreurs d'accès refusé (permissions insuffisantes).
     * 
     * @param ex L'exception d'accès refusé
     * @return Réponse 403 avec message d'erreur
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(
            "Access Denied",
            "Vous n'avez pas les permissions nécessaires pour effectuer cette action"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Gère toutes les autres exceptions non spécifiquement traitées.
     * 
     * @param ex L'exception générique
     * @return Réponse 500 ou 400 selon le type d'erreur
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(
            "Error",
            ex.getMessage()
        );
        
        // Si le message indique une erreur métier, renvoyer 400
        // Sinon, renvoyer 500 pour les erreurs serveur
        if (ex.getMessage().contains("non trouvé") || 
            ex.getMessage().contains("existe déjà") ||
            ex.getMessage().contains("non autorisé")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
