/**
 * Vue de connexion (Login)
 * 
 * Permet à l'utilisateur de s'authentifier avec:
 * - Nom d'utilisateur
 * - Mot de passe
 * 
 * Après une connexion réussie, redirige vers la page des notes.
 */
<template>
  <div class="layout">
    <div class="page-container">
      <div class="page flex flex-center bg-gradient">
        <div class="card login-card">
          <div class="card-section text-center">
            <div class="text-h4 text-primary mb-md">
              <span class="material-icons xl">note</span>
            </div>
            <div class="text-h5 text-weight-bold">Notes App</div>
            <div class="text-subtitle2 text-grey-7">Connectez-vous pour continuer</div>
          </div>

          <div class="card-section">
            <form @submit.prevent="handleLogin">
              <div class="input-group">
                <label>Nom d'utilisateur</label>
                <div class="input-icon">
                  <span class="material-icons">person</span>
                  <input
                    v-model="username"
                    type="text"
                    placeholder="Nom d'utilisateur"
                    required
                  />
                </div>
              </div>

              <div class="input-group">
                <label>Mot de passe</label>
                <div class="input-icon">
                  <span class="material-icons">lock</span>
                  <input
                    v-model="password"
                    type="password"
                    placeholder="Mot de passe"
                    required
                  />
                </div>
              </div>

              <div v-if="errorMessage" class="text-negative text-center mb-md">
                {{ errorMessage }}
              </div>

              <button
                type="submit"
                class="btn btn-primary btn-lg full-width"
                :class="{ loading: loading }"
                :disabled="loading"
              >
                Se connecter
              </button>
            </form>
          </div>

          <div class="card-section text-center text-caption text-grey-7">
            <p>Comptes de test:</p>
            <p><strong>admin</strong> / password (administrateur)</p>
            <p><strong>user1</strong> / password (utilisateur)</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { showNotification } from '@/utils/notifications'

const router = useRouter()
const authStore = useAuthStore()

// État du formulaire
const username = ref('')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')

/**
 * Gère la soumission du formulaire de connexion
 */
async function handleLogin() {
  // Clear previous errors
  errorMessage.value = ''
  
  // Validation côté client
  if (!username.value || !password.value) {
    errorMessage.value = 'Veuillez remplir tous les champs'
    showNotification({
      type: 'warning',
      message: 'Veuillez remplir tous les champs',
      icon: 'warning'
    })
    return
  }

  if (username.value.length < 3) {
    errorMessage.value = 'Le nom d\'utilisateur doit contenir au moins 3 caractères'
    return
  }

  loading.value = true

  try {
    await authStore.login(username.value, password.value)
    
    showNotification({
      type: 'positive',
      message: `Bienvenue ${username.value}!`,
      icon: 'check_circle'
    })

    router.push('/')
  } catch (error) {
    // Gestion détaillée des erreurs
    if (error.response) {
      // Erreur de réponse du serveur
      const status = error.response.status
      const serverMessage = error.response.data?.message
      
      if (status === 401 || status === 403) {
        errorMessage.value = 'Nom d\'utilisateur ou mot de passe incorrect'
        showNotification({
          type: 'negative',
          message: 'Identifiants invalides',
          icon: 'error'
        })
      } else if (status === 500) {
        errorMessage.value = 'Erreur serveur. Veuillez réessayer plus tard.'
        showNotification({
          type: 'negative',
          message: 'Erreur serveur',
          icon: 'error'
        })
      } else {
        errorMessage.value = serverMessage || 'Erreur de connexion'
        showNotification({
          type: 'negative',
          message: serverMessage || 'Erreur de connexion',
          icon: 'error'
        })
      }
    } else if (error.request) {
      // Erreur réseau (pas de réponse du serveur)
      errorMessage.value = 'Impossible de contacter le serveur. Vérifiez votre connexion.'
      showNotification({
        type: 'negative',
        message: 'Erreur réseau',
        icon: 'error'
      })
    } else {
      // Autre erreur
      errorMessage.value = 'Une erreur inattendue s\'est produite'
      showNotification({
        type: 'negative',
        message: 'Erreur inattendue',
        icon: 'error'
      })
    }
    
    // Clear password on error for security
    password.value = ''
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.bg-gradient {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 100%;
  max-width: 400px;
  border-radius: 12px;
}

.text-primary {
  color: var(--primary-color);
}
</style>
