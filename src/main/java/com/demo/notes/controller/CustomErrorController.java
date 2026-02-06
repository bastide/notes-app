package com.demo.notes.controller;

import com.demo.notes.dto.ErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur personnalisé pour gérer les erreurs.
 * 
 * Remplace le contrôleur d'erreur par défaut de Spring Boot
 * pour fournir des messages d'erreur personnalisés.
 */
@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
        // Récupérer le code de statut
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        // Récupérer le referer pour détecter si c'est une erreur de login
        String referer = request.getHeader("Referer");
        String requestURI = request.getRequestURI();

        // Debug logging
        System.out.println("=== CustomErrorController DEBUG ===");
        System.out.println("Error Status: " + status);
        System.out.println("Request URI: " + requestURI);
        System.out.println("Referer: " + referer);

        String errorMessage;
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            httpStatus = HttpStatus.valueOf(statusCode);

            // Si c'est une erreur 401 et que le referer contient /login
            if (statusCode == 401) {
                if (referer != null && referer.contains("/login")) {
                    errorMessage = "Nom d'utilisateur ou mot de passe incorrect";
                    System.out.println("Using login-specific error message");
                } else {
                    errorMessage = "Authentification requise pour accéder à cette ressource";
                    System.out.println("Using generic auth error message");
                }
            } else if (statusCode == 403) {
                errorMessage = "Accès refusé";
            } else if (statusCode == 404) {
                errorMessage = "Ressource non trouvée";
            } else if (statusCode >= 500) {
                errorMessage = "Erreur serveur interne";
            } else {
                errorMessage = "Une erreur s'est produite";
            }
        } else {
            errorMessage = "Une erreur s'est produite";
        }

        System.out.println("Final error message: " + errorMessage);
        System.out.println("====================================");

        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.getReasonPhrase(),
                errorMessage);

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
