package com.demo.notes.security;

import com.demo.notes.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuration de Spring Security.
 * 
 * Cette classe configure:
 * - L'authentification JWT (sans sessions)
 * - Les autorisations d'accès aux endpoints
 * - Le CORS pour les requêtes frontend
 * - L'encodage des mots de passe (BCrypt)
 * - Les filtres de sécurité
 * 
 * @author Demo
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;
    
    /**
     * Crée le filtre d'authentification JWT.
     * 
     * @return L'instance du filtre JWT
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
    
    /**
     * Configure l'encodeur de mots de passe BCrypt.
     * 
     * BCrypt est un algorithme de hachage sécurisé qui inclut
     * automatiquement un salt et est résistant aux attaques par force brute.
     * 
     * @return L'encodeur de mots de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Configure le provider d'authentification.
     * 
     * Utilise notre UserDetailsService personnalisé et l'encodeur BCrypt
     * pour vérifier les identifiants lors de la connexion.
     * 
     * @return Le provider d'authentification configuré
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    /**
     * Expose l'AuthenticationManager comme bean Spring.
     * 
     * Nécessaire pour l'authentification dans le contrôleur d'auth.
     * 
     * @param authConfig La configuration d'authentification Spring
     * @return Le gestionnaire d'authentification
     * @throws Exception En cas d'erreur de configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) 
            throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    /**
     * Configure la chaîne de filtres de sécurité.
     * 
     * Définit:
     * - Les endpoints publics (login, H2 console)
     * - Les endpoints protégés (API)
     * - La gestion de session (stateless pour JWT)
     * - L'ordre des filtres de sécurité
     * 
     * @param http L'objet de configuration HTTP Security
     * @return La chaîne de filtres configurée
     * @throws Exception En cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Désactivation de CSRF (non nécessaire pour une API REST avec JWT)
            .csrf(csrf -> csrf.disable())
            
            // Configuration CORS pour accepter les requêtes du frontend
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configuration du point d'entrée pour les erreurs d'authentification
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(unauthorizedHandler)
            )
            
            // Gestion de session stateless (pas de session HTTP)
            // Les tokens JWT remplacent les sessions traditionnelles
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configuration des autorisations
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics accessibles sans authentification
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/", "/index.html", "/assets/**", "/favicon.ico", "/sw.js", "/manifest.json", "/error").permitAll()
                
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            )
            
            // Désactivation de X-Frame-Options pour la console H2
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        
        // Ajout du provider d'authentification personnalisé
        http.authenticationProvider(authenticationProvider());
        
        // Ajout du filtre JWT avant le filtre d'authentification par défaut
        http.addFilterBefore(
            jwtAuthenticationFilter(), 
            UsernamePasswordAuthenticationFilter.class
        );
        
        return http.build();
    }
    
    /**
     * Configure CORS (Cross-Origin Resource Sharing).
     * 
     * Permet au frontend Vue.js (sur un port différent en développement)
     * d'effectuer des requêtes vers l'API backend.
     * 
     * @return La configuration CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origines autorisées (frontend en développement et production)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:8080"));
        
        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // En-têtes autorisés
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Autoriser l'envoi de credentials (cookies, auth headers)
        configuration.setAllowCredentials(true);
        
        // Appliquer cette configuration à toutes les routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
