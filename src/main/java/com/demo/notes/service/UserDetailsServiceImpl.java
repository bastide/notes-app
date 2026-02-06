package com.demo.notes.service;

import com.demo.notes.model.User;
import com.demo.notes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service d'implémentation de UserDetailsService pour Spring Security.
 * 
 * Cette classe est responsable de charger les détails d'un utilisateur
 * depuis la base de données lors de l'authentification.
 * 
 * Spring Security utilise ce service pour:
 * - Vérifier les identifiants lors du login
 * - Charger les rôles et permissions de l'utilisateur
 * - Valider les tokens JWT
 * 
 * @author Demo
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Charge un utilisateur par son nom d'utilisateur.
     * 
     * Cette méthode est appelée automatiquement par Spring Security
     * lors de l'authentification. Elle récupère l'utilisateur depuis
     * la base de données et convertit ses informations en objet UserDetails.
     * 
     * @param username Le nom d'utilisateur à rechercher
     * @return Les détails de l'utilisateur pour Spring Security
     * @throws UsernameNotFoundException Si l'utilisateur n'existe pas
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Recherche de l'utilisateur dans la base de données
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("Utilisateur non trouvé: " + username)
                );
        
        // Conversion des rôles en GrantedAuthority pour Spring Security
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
        
        // Création et retour de l'objet UserDetails
        // Spring Security utilisera ces informations pour l'authentification
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
