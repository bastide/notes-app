package com.demo.notes.security;

import com.demo.notes.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre d'authentification JWT.
 * 
 * Ce filtre intercepte chaque requête HTTP pour:
 * 1. Extraire le token JWT de l'en-tête Authorization
 * 2. Valider le token
 * 3. Charger les détails de l'utilisateur
 * 4. Créer un contexte d'authentification Spring Security
 * 
 * Le filtre est exécuté une seule fois par requête (OncePerRequestFilter).
 * 
 * @author Demo
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    /**
     * Méthode principale du filtre, exécutée pour chaque requête.
     * 
     * @param request La requête HTTP entrante
     * @param response La réponse HTTP sortante
     * @param filterChain La chaîne de filtres à continuer
     * @throws ServletException En cas d'erreur de servlet
     * @throws IOException En cas d'erreur d'I/O
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        try {
            // Extraction du token JWT de la requête
            String jwt = parseJwt(request);
            
            // Si un token est présent et valide
            if (jwt != null && jwtUtils.getUsernameFromToken(jwt) != null) {
                // Extraction du nom d'utilisateur depuis le token
                String username = jwtUtils.getUsernameFromToken(jwt);
                
                // Chargement des détails complets de l'utilisateur
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Validation du token avec les détails de l'utilisateur
                if (jwtUtils.validateToken(jwt, userDetails)) {
                    // Création d'un objet d'authentification Spring Security
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );
                    
                    // Ajout des détails de la requête à l'authentification
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Définition de l'authentification dans le contexte Spring Security
                    // Cela permet aux contrôleurs d'accéder à l'utilisateur connecté
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            // En cas d'erreur, on log mais on ne bloque pas la requête
            // Les endpoints protégés refuseront l'accès
            logger.error("Cannot set user authentication: {}", e);
        }
        
        // Continue la chaîne de filtres
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extrait le token JWT de l'en-tête Authorization.
     * 
     * Le token doit être au format: "Bearer <token>"
     * 
     * @param request La requête HTTP
     * @return Le token JWT ou null s'il n'est pas présent
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        
        // Vérifie que l'en-tête existe et commence par "Bearer "
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            // Retourne le token sans le préfixe "Bearer "
            return headerAuth.substring(7);
        }
        
        return null;
    }
}
