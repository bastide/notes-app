package com.demo.notes.service;

import com.demo.notes.dto.NoteRequest;
import com.demo.notes.dto.NoteResponse;
import com.demo.notes.model.Note;
import com.demo.notes.model.User;
import com.demo.notes.repository.NoteRepository;
import com.demo.notes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des notes.
 * 
 * Fournit les opérations métier pour:
 * - Créer des notes
 * - Récupérer les notes d'un utilisateur
 * - Modifier des notes
 * - Supprimer des notes
 * 
 * Chaque opération vérifie que l'utilisateur a le droit
 * d'effectuer l'action sur la note (propriété).
 * 
 * @author Demo
 */
@Service
@Transactional
public class NoteService {
    
    @Autowired
    private NoteRepository noteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Crée une nouvelle note pour un utilisateur.
     * 
     * @param request Les données de la note à créer
     * @param username Le nom de l'utilisateur créant la note
     * @return La note créée
     * @throws UsernameNotFoundException Si l'utilisateur n'existe pas
     */
    public NoteResponse createNote(NoteRequest request, String username) {
        // Récupération de l'utilisateur
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        
        // Création de la note
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setUser(user);
        
        // Sauvegarde en base
        Note savedNote = noteRepository.save(note);
        
        // Conversion en DTO
        return convertToResponse(savedNote);
    }
    
    /**
     * Récupère toutes les notes d'un utilisateur.
     * 
     * Les notes sont triées par date de modification décroissante
     * (les plus récentes en premier).
     * 
     * @param username Le nom de l'utilisateur
     * @return Liste des notes de l'utilisateur
     * @throws UsernameNotFoundException Si l'utilisateur n'existe pas
     */
    @Transactional(readOnly = true)
    public List<NoteResponse> getUserNotes(String username) {
        // Récupération de l'utilisateur
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        
        // Récupération et conversion des notes
        return noteRepository.findByUserIdOrderByUpdatedAtDesc(user.getId())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère une note spécifique par son ID.
     * 
     * Vérifie que la note appartient bien à l'utilisateur demandeur.
     * 
     * @param noteId L'identifiant de la note
     * @param username Le nom de l'utilisateur
     * @return La note demandée
     * @throws RuntimeException Si la note n'existe pas ou n'appartient pas à l'utilisateur
     */
    @Transactional(readOnly = true)
    public NoteResponse getNoteById(Long noteId, String username) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note non trouvée"));
        
        // Vérification de la propriété
        if (!note.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Accès non autorisé à cette note");
        }
        
        return convertToResponse(note);
    }
    
    /**
     * Met à jour une note existante.
     * 
     * Seul le propriétaire de la note peut la modifier.
     * Les dates de modification sont automatiquement mises à jour.
     * 
     * @param noteId L'identifiant de la note à modifier
     * @param request Les nouvelles données de la note
     * @param username Le nom de l'utilisateur
     * @return La note mise à jour
     * @throws RuntimeException Si la note n'existe pas ou n'appartient pas à l'utilisateur
     */
    public NoteResponse updateNote(Long noteId, NoteRequest request, String username) {
        // Récupération de la note
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note non trouvée"));
        
        // Vérification de la propriété
        if (!note.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Accès non autorisé à cette note");
        }
        
        // Mise à jour des champs
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        
        // Sauvegarde (les dates sont automatiquement mises à jour par @PreUpdate)
        Note updatedNote = noteRepository.save(note);
        
        return convertToResponse(updatedNote);
    }
    
    /**
     * Supprime une note.
     * 
     * Seul le propriétaire de la note peut la supprimer.
     * 
     * @param noteId L'identifiant de la note à supprimer
     * @param username Le nom de l'utilisateur
     * @throws RuntimeException Si la note n'existe pas ou n'appartient pas à l'utilisateur
     */
    public void deleteNote(Long noteId, String username) {
        // Récupération de la note
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note non trouvée"));
        
        // Vérification de la propriété
        if (!note.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Accès non autorisé à cette note");
        }
        
        // Suppression
        noteRepository.delete(note);
    }
    
    /**
     * Convertit une entité Note en DTO NoteResponse.
     * 
     * @param note L'entité note à convertir
     * @return Le DTO note
     */
    private NoteResponse convertToResponse(Note note) {
        return new NoteResponse(
            note.getId(),
            note.getTitle(),
            note.getContent(),
            note.getCreatedAt().toString(),
            note.getUpdatedAt().toString(),
            note.getUser().getId(),
            note.getUser().getUsername()
        );
    }
}
