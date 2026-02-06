package com.demo.notes.controller;

import com.demo.notes.dto.NoteRequest;
import com.demo.notes.dto.NoteResponse;
import com.demo.notes.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des notes.
 * 
 * Expose les endpoints pour:
 * - Créer des notes
 * - Récupérer les notes de l'utilisateur connecté
 * - Modifier ses notes
 * - Supprimer ses notes
 * 
 * Tous ces endpoints nécessitent une authentification.
 * Un utilisateur ne peut accéder qu'à ses propres notes.
 * 
 * @author Demo
 */
@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NoteController {
    
    @Autowired
    private NoteService noteService;
    
    /**
     * Récupère toutes les notes de l'utilisateur connecté.
     * 
     * Les notes sont triées par date de modification décroissante.
     * 
     * @param authentication L'objet d'authentification injecté par Spring Security
     * @return Liste des notes de l'utilisateur
     * 
     * Exemple de requête:
     * GET /api/notes
     * Authorization: Bearer <token>
     * 
     * Exemple de réponse:
     * [
     *   {
     *     "id": 1,
     *     "title": "Ma première note",
     *     "content": "<p>Contenu HTML</p>",
     *     "createdAt": "2024-01-01T10:00:00",
     *     "updatedAt": "2024-01-02T14:30:00",
     *     "userId": 2,
     *     "username": "user1"
     *   },
     *   ...
     * ]
     */
    @GetMapping
    public ResponseEntity<List<NoteResponse>> getUserNotes(Authentication authentication) {
        // Récupération du nom d'utilisateur depuis le contexte de sécurité
        String username = authentication.getName();
        List<NoteResponse> notes = noteService.getUserNotes(username);
        return ResponseEntity.ok(notes);
    }
    
    /**
     * Récupère une note spécifique par son ID.
     * 
     * Vérifie que la note appartient bien à l'utilisateur connecté.
     * 
     * @param id L'identifiant de la note
     * @param authentication L'objet d'authentification
     * @return La note demandée
     * 
     * Exemple de requête:
     * GET /api/notes/1
     * Authorization: Bearer <token>
     */
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable Long id, 
                                                     Authentication authentication) {
        String username = authentication.getName();
        NoteResponse note = noteService.getNoteById(id, username);
        return ResponseEntity.ok(note);
    }
    
    /**
     * Crée une nouvelle note.
     * 
     * La note est automatiquement associée à l'utilisateur connecté.
     * Les validations sur les données sont appliquées automatiquement.
     * 
     * @param request Les données de la note à créer
     * @param authentication L'objet d'authentification
     * @return La note créée
     * 
     * Exemple de requête:
     * POST /api/notes
     * Authorization: Bearer <token>
     * {
     *   "title": "Nouvelle note",
     *   "content": "<p>Contenu de la note avec <strong>formatage</strong></p>"
     * }
     * 
     * Exemple de réponse:
     * {
     *   "id": 5,
     *   "title": "Nouvelle note",
     *   "content": "<p>Contenu de la note avec <strong>formatage</strong></p>",
     *   "createdAt": "2024-01-04T16:00:00",
     *   "updatedAt": "2024-01-04T16:00:00",
     *   "userId": 2,
     *   "username": "user1"
     * }
     */
    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest request,
                                                    Authentication authentication) {
        String username = authentication.getName();
        NoteResponse note = noteService.createNote(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }
    
    /**
     * Met à jour une note existante.
     * 
     * Seul le propriétaire de la note peut la modifier.
     * Les dates de modification sont automatiquement mises à jour.
     * 
     * @param id L'identifiant de la note à modifier
     * @param request Les nouvelles données de la note
     * @param authentication L'objet d'authentification
     * @return La note mise à jour
     * 
     * Exemple de requête:
     * PUT /api/notes/5
     * Authorization: Bearer <token>
     * {
     *   "title": "Titre modifié",
     *   "content": "<p>Contenu modifié</p>"
     * }
     */
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(@PathVariable Long id,
                                                    @Valid @RequestBody NoteRequest request,
                                                    Authentication authentication) {
        String username = authentication.getName();
        NoteResponse note = noteService.updateNote(id, request, username);
        return ResponseEntity.ok(note);
    }
    
    /**
     * Supprime une note.
     * 
     * Seul le propriétaire de la note peut la supprimer.
     * 
     * @param id L'identifiant de la note à supprimer
     * @param authentication L'objet d'authentification
     * @return Réponse sans contenu (204)
     * 
     * Exemple de requête:
     * DELETE /api/notes/5
     * Authorization: Bearer <token>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id, 
                                       Authentication authentication) {
        String username = authentication.getName();
        noteService.deleteNote(id, username);
        return ResponseEntity.noContent().build();
    }
}
