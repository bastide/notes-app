package com.demo.notes.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilitaire pour la génération et la validation des tokens JWT.
 * 
 * Cette classe gère:
 * - La création de tokens JWT signés
 * - L'extraction des informations (claims) depuis un token
 * - La validation de l'authenticité et de la validité des tokens
 * 
 * Les tokens incluent le nom d'utilisateur et une date d'expiration.
 * 
 * @author Demo
 */
@Component
public class JwtUtils {
    
    /**
     * Clé secrète pour signer les tokens JWT.
     * Récupérée depuis application.properties.
     */
    @Value("${jwt.secret}")
    private String secret;
    
    /**
     * Durée de validité d'un token en millisecondes.
     * Récupérée depuis application.properties (par défaut 24h).
     */
    @Value("${jwt.expiration}")
    private Long expiration;
    
    /**
     * Obtient la clé secrète sous forme de SecretKey pour JJWT.
     * 
     * @return La clé secrète encodée
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Extrait le nom d'utilisateur depuis un token JWT.
     * 
     * @param token Le token JWT
     * @return Le nom d'utilisateur (subject du token)
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    /**
     * Extrait la date d'expiration depuis un token JWT.
     * 
     * @param token Le token JWT
     * @return La date d'expiration
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * Extrait un claim spécifique depuis un token JWT.
     * 
     * @param token Le token JWT
     * @param claimsResolver Fonction pour extraire le claim désiré
     * @param <T> Type du claim à extraire
     * @return La valeur du claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extrait tous les claims depuis un token JWT.
     * 
     * @param token Le token JWT
     * @return Tous les claims du token
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Vérifie si un token est expiré.
     * 
     * @param token Le token JWT à vérifier
     * @return true si le token est expiré, false sinon
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * Génère un token JWT pour un utilisateur.
     * 
     * @param userDetails Les détails de l'utilisateur
     * @return Le token JWT généré
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }
    
    /**
     * Crée un token JWT avec les claims spécifiés.
     * 
     * Le token contient:
     * - Les claims personnalisés (si fournis)
     * - Le subject (nom d'utilisateur)
     * - La date d'émission
     * - La date d'expiration
     * 
     * @param claims Claims supplémentaires à inclure
     * @param subject Le nom d'utilisateur
     * @return Le token JWT signé
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Valide un token JWT.
     * 
     * Vérifie que:
     * - Le nom d'utilisateur dans le token correspond à celui fourni
     * - Le token n'est pas expiré
     * 
     * @param token Le token JWT à valider
     * @param userDetails Les détails de l'utilisateur à vérifier
     * @return true si le token est valide, false sinon
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
