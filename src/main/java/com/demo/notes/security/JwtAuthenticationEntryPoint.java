package com.demo.notes.security;

import com.demo.notes.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Point d'entrée pour gérer les erreurs d'authentification.
 * 
 * Cette classe est invoquée lorsqu'un utilisateur non authentifié
 * tente d'accéder à une ressource protégée. Elle renvoie une réponse
 * JSON avec un code 401 (Unauthorized).
 * 
 * @author Demo
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Gère les tentatives d'accès non autorisées.
     * 
     * Renvoie une réponse JSON standardisée avec:
     * - Code HTTP 401 (Unauthorized)
     * - Message d'erreur descriptif
     * 
     * @param request       La requête HTTP
     * @param response      La réponse HTTP
     * @param authException L'exception d'authentification levée
     * @throws IOException      En cas d'erreur d'écriture de la réponse
     * @throws ServletException En cas d'erreur de servlet
     */
    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {

        // Définition du code de statut HTTP 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Définition du type de contenu JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Déterminer le message d'erreur en fonction de la requête
        String errorMessage;
        String requestURI = request.getRequestURI();
        String referer = request.getHeader("Referer");

        // Debug logging
        System.out.println("=== JwtAuthenticationEntryPoint DEBUG ===");
        System.out.println("Request URI: " + requestURI);
        System.out.println("Referer: " + referer);
        System.out.println("Exception: " + authException.getMessage());
        System.out.println("Exception class: " + authException.getClass().getName());

        // Check if this is a login request or error redirect from login
        boolean isLoginRequest = (requestURI != null && requestURI.contains("/login")) ||
                (referer != null && referer.contains("/login"));
        System.out.println("Is login request: " + isLoginRequest);

        if (isLoginRequest) {
            // Message spécifique pour les échecs de connexion
            errorMessage = "Nom d'utilisateur ou mot de passe incorrect";
            System.out.println("Using login-specific error message");
        } else {
            // Message générique pour les autres cas (accès non autorisé)
            errorMessage = "Authentification requise pour accéder à cette ressource";
            System.out.println("Using generic error message");
        }

        System.out.println("Final error message: " + errorMessage);
        System.out.println("========================================");

        // Création de la réponse d'erreur
        ErrorResponse errorResponse = new ErrorResponse(
                "Unauthorized",
                errorMessage);

        // Conversion de l'objet en JSON et écriture dans la réponse
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
