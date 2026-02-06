/**
 * Store Pinia pour la gestion des utilisateurs
 * 
 * Gère:
 * - La liste des utilisateurs (pour les admins)
 * - La création et suppression d'utilisateurs
 * - Le chargement des données utilisateurs
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useUsersStore = defineStore('users', () => {
  // État
  const users = ref([])
  const loading = ref(false)
  const error = ref(null)

  /**
   * Récupère tous les utilisateurs du système
   * 
   * Nécessite le rôle ADMIN
   * 
   * @returns {Promise<Array>} Liste des utilisateurs
   */
  async function fetchUsers() {
    loading.value = true
    error.value = null
    try {
      const response = await axios.get('/api/users')
      users.value = response.data
      return users.value
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors du chargement des utilisateurs'
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Crée un nouvel utilisateur
   * 
   * Nécessite le rôle ADMIN
   * 
   * @param {Object} userData - Données de l'utilisateur {username, password, roles}
   * @returns {Promise<Object>} L'utilisateur créé
   */
  async function createUser(userData) {
    loading.value = true
    error.value = null
    try {
      const response = await axios.post('/api/users', userData)
      users.value.push(response.data)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors de la création de l\'utilisateur'
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Supprime un utilisateur
   * 
   * Nécessite le rôle ADMIN
   * 
   * @param {number} id - ID de l'utilisateur à supprimer
   * @returns {Promise<void>}
   */
  async function deleteUser(id) {
    loading.value = true
    error.value = null
    try {
      await axios.delete(`/api/users/${id}`)
      users.value = users.value.filter(u => u.id !== id)
    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors de la suppression de l\'utilisateur'
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Réinitialise le store
   */
  function reset() {
    users.value = []
    loading.value = false
    error.value = null
  }

  return {
    // State
    users,
    loading,
    error,
    // Actions
    fetchUsers,
    createUser,
    deleteUser,
    reset
  }
})
