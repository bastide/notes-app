/**
 * Vue de gestion des utilisateurs (Admin uniquement)
 * 
 * Permet aux administrateurs de:
 * - Voir la liste de tous les utilisateurs
 * - Créer de nouveaux utilisateurs
 * - Supprimer des utilisateurs
 * - Attribuer des rôles
 */
<template>
  <div class="layout">
    <header class="header">
      <div class="toolbar">
        <button
          class="btn btn-flat btn-round"
          style="color: white"
          @click="router.push('/')"
        >
          <span class="material-icons">arrow_back</span>
        </button>
        <div class="toolbar-title">
          <span class="material-icons sm">people</span>
          Gestion des Utilisateurs
        </div>

        <div class="dropdown">
          <button
            class="btn btn-flat btn-round"
            style="color: white"
            @click="toggleUserMenu"
          >
            <span class="material-icons">person</span>
          </button>
          <div class="dropdown-menu" :class="{ show: showUserMenu }">
            <ul class="list">
              <li class="list-item">
                <div>
                  <div class="text-caption">Connecté en tant que</div>
                  <div class="text-weight-bold">{{ authStore.user?.username }}</div>
                </div>
              </li>
              <hr class="separator" />
              <li class="list-item" @click="handleLogout">
                <span class="material-icons">logout</span>
                Déconnexion
              </li>
            </ul>
          </div>
        </div>
      </div>
    </header>

    <div class="page-container">
      <div class="page">
        <div class="card">
          <div class="card-section">
            <div class="row items-center">
              <div class="col">
                <div class="text-h6">Utilisateurs ({{ usersStore.users.length }})</div>
              </div>
              <div class="col-auto">
                <button
                  class="btn btn-primary"
                  @click="showCreateDialog = true"
                >
                  <span class="material-icons">person_add</span>
                  Nouvel utilisateur
                </button>
              </div>
            </div>
          </div>

          <hr class="separator" />

          <div class="card-section p-none">
            <div class="table-container">
              <table>
                <thead>
                  <tr>
                    <th @click="sortBy('id')" class="sortable" :class="getSortClass('id')">ID</th>
                    <th @click="sortBy('username')" class="sortable" :class="getSortClass('username')">Nom d'utilisateur</th>
                    <th>Rôles</th>
                    <th @click="sortBy('createdAt')" class="sortable" :class="getSortClass('createdAt')">Date de création</th>
                    <th style="text-align: center">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="user in sortedUsers" :key="user.id">
                    <td>{{ user.id }}</td>
                    <td>{{ user.username }}</td>
                    <td>
                      <span
                        v-for="role in user.roles"
                        :key="role"
                        class="chip"
                        :class="{ danger: role.includes('ADMIN') }"
                      >
                        {{ role.replace('ROLE_', '') }}
                      </span>
                    </td>
                    <td>{{ formatDate(user.createdAt) }}</td>
                    <td style="text-align: center">
                      <button
                        class="btn btn-flat btn-round btn-sm"
                        style="color: var(--danger-color)"
                        @click="confirmDelete(user)"
                        :disabled="user.id === authStore.user?.id"
                        :title="user.id === authStore.user?.id ? 'Vous ne pouvez pas supprimer votre propre compte' : 'Supprimer cet utilisateur'"
                      >
                        <span class="material-icons">delete</span>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Dialog de création d'utilisateur -->
    <div class="modal-overlay" :class="{ show: showCreateDialog }" @click.self="showCreateDialog = false">
      <div class="modal" style="min-width: 400px">
        <div class="modal-header">
          <div class="text-h6">Créer un utilisateur</div>
        </div>

        <div class="modal-body">
          <form @submit.prevent="createUser">
            <div class="input-group">
              <label>Nom d'utilisateur</label>
              <div class="input-icon">
                <span class="material-icons">person</span>
                <input
                  v-model="newUser.username"
                  type="text"
                  placeholder="Nom d'utilisateur"
                  required
                  minlength="3"
                />
              </div>
            </div>

            <div class="input-group">
              <label>Mot de passe</label>
              <div class="input-icon">
                <span class="material-icons">lock</span>
                <input
                  v-model="newUser.password"
                  type="password"
                  placeholder="Mot de passe"
                  required
                  minlength="6"
                />
              </div>
            </div>

            <div class="input-group">
              <label class="text-subtitle2">Rôles</label>
              <div class="checkbox-group">
                <div
                  v-for="option in roleOptions"
                  :key="option.value"
                  class="checkbox-item"
                >
                  <input
                    type="checkbox"
                    :id="option.value"
                    :value="option.value"
                    v-model="newUser.roles"
                  />
                  <label :for="option.value">{{ option.label }}</label>
                </div>
              </div>
            </div>
          </form>
        </div>

        <div class="modal-footer">
          <button
            class="btn btn-flat"
            @click="showCreateDialog = false"
          >
            Annuler
          </button>
          <button
            class="btn btn-primary"
            :class="{ loading: usersStore.loading }"
            :disabled="usersStore.loading"
            @click="createUser"
          >
            Créer
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useUsersStore } from '@/stores/users'
import { showNotification } from '@/utils/notifications'

const router = useRouter()
const authStore = useAuthStore()
const usersStore = useUsersStore()

// État du dialog de création
const showCreateDialog = ref(false)
const showUserMenu = ref(false)
const newUser = ref({
  username: '',
  password: '',
  roles: ['ROLE_USER']
})

// Options de rôles disponibles
const roleOptions = [
  { label: 'Utilisateur', value: 'ROLE_USER' },
  { label: 'Administrateur', value: 'ROLE_ADMIN' }
]

// Sorting state
const sortColumn = ref('id')
const sortDirection = ref('asc')

/**
 * Toggle user menu
 */
function toggleUserMenu() {
  showUserMenu.value = !showUserMenu.value
}

// Close menu when clicking outside
if (typeof document !== 'undefined') {
  document.addEventListener('click', (e) => {
    if (!e.target.closest('.dropdown')) {
      showUserMenu.value = false
    }
  })
}

/**
 * Sort users by column
 */
function sortBy(column) {
  if (sortColumn.value === column) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortColumn.value = column
    sortDirection.value = 'asc'
  }
}

/**
 * Get sort class for column header
 */
function getSortClass(column) {
  if (sortColumn.value !== column) return ''
  return sortDirection.value === 'asc' ? 'sorted-asc' : 'sorted-desc'
}

/**
 * Sorted users
 */
const sortedUsers = computed(() => {
  const users = [...usersStore.users]
  users.sort((a, b) => {
    let aVal = a[sortColumn.value]
    let bVal = b[sortColumn.value]
    
    if (sortColumn.value === 'createdAt') {
      aVal = new Date(aVal).getTime()
      bVal = new Date(bVal).getTime()
    }
    
    if (aVal < bVal) return sortDirection.value === 'asc' ? -1 : 1
    if (aVal > bVal) return sortDirection.value === 'asc' ? 1 : -1
    return 0
  })
  return users
})

/**
 * Charge les utilisateurs au montage
 */
onMounted(async () => {
  try {
    await usersStore.fetchUsers()
  } catch (error) {
    showNotification({
      type: 'negative',
      message: 'Erreur lors du chargement des utilisateurs'
    })
  }
})

/**
 * Crée un nouvel utilisateur
 */
async function createUser() {
  if (!newUser.value.username || !newUser.value.password) {
    showNotification({
      type: 'warning',
      message: 'Veuillez remplir tous les champs'
    })
    return
  }

  if (newUser.value.roles.length === 0) {
    showNotification({
      type: 'warning',
      message: 'Sélectionnez au moins un rôle'
    })
    return
  }

  try {
    await usersStore.createUser({
      username: newUser.value.username,
      password: newUser.value.password,
      roles: newUser.value.roles
    })

    showNotification({
      type: 'positive',
      message: 'Utilisateur créé avec succès',
      icon: 'check_circle'
    })

    showCreateDialog.value = false
    newUser.value = {
      username: '',
      password: '',
      roles: ['ROLE_USER']
    }
  } catch (error) {
    showNotification({
      type: 'negative',
      message: error.response?.data?.message || 'Erreur lors de la création',
      icon: 'error'
    })
  }
}

/**
 * Demande confirmation avant suppression
 */
function confirmDelete(user) {
  if (confirm(`Êtes-vous sûr de vouloir supprimer l'utilisateur "${user.username}" ?\n\nAttention: Toutes les notes de cet utilisateur seront également supprimées.`)) {
    deleteUser(user)
  }
}

/**
 * Supprime un utilisateur
 */
async function deleteUser(user) {
  try {
    await usersStore.deleteUser(user.id)
    showNotification({
      type: 'positive',
      message: 'Utilisateur supprimé',
      icon: 'check_circle'
    })
  } catch (error) {
    showNotification({
      type: 'negative',
      message: 'Erreur lors de la suppression',
      icon: 'error'
    })
  }
}

/**
 * Gère la déconnexion
 */
function handleLogout() {
  authStore.logout()
  usersStore.reset()
  router.push('/login')
  showNotification({
    type: 'info',
    message: 'Déconnexion réussie'
  })
}

/**
 * Formate une date pour l'affichage
 */
function formatDate(dateString) {
  const date = new Date(dateString)
  return date.toLocaleString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
/* Styles personnalisés si nécessaire */
</style>
