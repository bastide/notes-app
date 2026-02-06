/**
 * Store Pinia pour l'authentification
 * 
 * Gère:
 * - L'état de connexion de l'utilisateur
 * - Le token JWT
 * - Les informations utilisateur (id, username, roles)
 * - Les actions de login/logout
 * - La persistance dans le localStorage
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useAuthStore = defineStore('auth', () => {
  // État
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  // Getters (computed)
  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => {
    return user.value?.roles?.includes('ROLE_ADMIN') || false
  })

  /**
   * Action de connexion
   * 
   * @param {string} username - Nom d'utilisateur
   * @param {string} password - Mot de passe
   * @returns {Promise} Promise résolue avec les données utilisateur
   */
  async function login(username, password) {
    try {
      const response = await axios.post('/api/auth/login', {
        username,
        password
      })

      // Sauvegarde du token et des infos utilisateur
      token.value = response.data.token
      user.value = {
        id: response.data.id,
        username: response.data.username,
        roles: response.data.roles
      }

      // Persistance dans le localStorage
      localStorage.setItem('token', token.value)
      localStorage.setItem('user', JSON.stringify(user.value))

      // Configuration du header Authorization pour toutes les futures requêtes
      axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`

      return response.data
    } catch (error) {
      throw error
    }
  }

  /**
   * Action de déconnexion
   * 
   * Nettoie le token, les infos utilisateur et le localStorage
   */
  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    delete axios.defaults.headers.common['Authorization']
  }

  /**
   * Initialisation de l'intercepteur axios
   * 
   * Configure le header Authorization si un token existe
   */
  function initializeAuth() {
    if (token.value) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
    }

    // Intercepteur pour gérer les erreurs 401 (non autorisé)
    axios.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          // Token expiré ou invalide - déconnexion automatique
          logout()
          window.location.href = '/login'
        }
        return Promise.reject(error)
      }
    )
  }

  // Initialisation au chargement du store
  initializeAuth()

  return {
    // State
    token,
    user,
    // Getters
    isAuthenticated,
    isAdmin,
    // Actions
    login,
    logout
  }
})
