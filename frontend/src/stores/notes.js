/**
 * Store Pinia pour la gestion des notes
 * 
 * Gère:
 * - La liste des notes de l'utilisateur
 * - Les opérations CRUD sur les notes
 * - Le chargement et la mise en cache des notes
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useNotesStore = defineStore('notes', () => {
  // État
  const notes = ref([])
  const loading = ref(false)
  const error = ref(null)

  /**
   * Récupère toutes les notes de l'utilisateur connecté
   * 
   * @returns {Promise<Array>} Liste des notes
   */
  async function fetchNotes() {
    loading.value = true
    error.value = null
    try {
      const response = await axios.get('/api/notes')
      notes.value = response.data
      return notes.value
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors du chargement des notes'
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Récupère une note spécifique par son ID
   * 
   * @param {number} id - ID de la note
   * @returns {Promise<Object>} La note
   */
  async function fetchNoteById(id) {
    loading.value = true
    error.value = null
    try {
      const response = await axios.get(`/api/notes/${id}`)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors du chargement de la note'
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Crée une nouvelle note
   * 
   * @param {Object} noteData - Données de la note {title, content}
   * @returns {Promise<Object>} La note créée
   */
  async function createNote(noteData) {
    loading.value = true
    error.value = null
    try {
      const response = await axios.post('/api/notes', noteData)
      notes.value.unshift(response.data) // Ajoute en début de liste
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors de la création de la note'
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Met à jour une note existante
   * 
   * @param {number} id - ID de la note
   * @param {Object} noteData - Nouvelles données {title, content}
   * @returns {Promise<Object>} La note mise à jour
   */
  async function updateNote(id, noteData) {
    loading.value = true
    error.value = null
    try {
      const response = await axios.put(`/api/notes/${id}`, noteData)
      // Met à jour la note dans la liste locale
      const index = notes.value.findIndex(n => n.id === id)
      if (index !== -1) {
        notes.value[index] = response.data
      }
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors de la mise à jour de la note'
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Supprime une note
   * 
   * @param {number} id - ID de la note à supprimer
   * @returns {Promise<void>}
   */
  async function deleteNote(id) {
    loading.value = true
    error.value = null
    try {
      await axios.delete(`/api/notes/${id}`)
      // Retire la note de la liste locale
      notes.value = notes.value.filter(n => n.id !== id)
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors de la suppression de la note'
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Réinitialise le store
   */
  function reset() {
    notes.value = []
    loading.value = false
    error.value = null
  }

  return {
    // State
    notes,
    loading,
    error,
    // Actions
    fetchNotes,
    fetchNoteById,
    createNote,
    updateNote,
    deleteNote,
    reset
  }
})
