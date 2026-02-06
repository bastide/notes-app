/**
 * Vue principale de gestion des notes
 * 
 * Affiche:
 * - La liste des notes de l'utilisateur
 * - Un éditeur de texte riche (Quill) pour créer/modifier des notes
 * - Des actions pour supprimer les notes
 * 
 * Permet à l'utilisateur authentifié de gérer ses notes (CRUD).
 */
<template>
  <div class="layout">
    <!-- En-tête de l'application -->
    <header class="header">
      <div class="toolbar">
        <div class="toolbar-title">
          <span class="material-icons sm">note</span>
          Mes Notes
        </div>

        <button
          v-if="authStore.isAdmin"
          class="btn btn-flat"
          style="color: white"
          @click="router.push('/users')"
        >
          <span class="material-icons">people</span>
          Gestion utilisateurs
        </button>

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
        <div class="row">
          <!-- Colonne gauche: Liste des notes -->
          <div class="col-12 col-md-4">
            <div class="card">
              <div class="card-section">
                <div class="text-h6">
                  Mes Notes ({{ notesStore.notes.length }})
                </div>
              </div>

              <hr class="separator" />

              <div class="card-section p-none">
                <button
                  class="btn btn-primary full-width"
                  @click="createNewNote"
                  style="border-radius: 0"
                >
                  <span class="material-icons">add</span>
                  Nouvelle note
                </button>
              </div>

              <hr class="separator" />

              <ul v-if="notesStore.notes.length > 0" class="list">
                <li
                  v-for="note in notesStore.notes"
                  :key="note.id"
                  class="list-item note-item"
                  :class="{ active: selectedNote?.id === note.id }"
                  @click="selectNote(note)"
                >
                  <div style="flex: 1">
                    <div class="text-weight-bold">
                      {{ note.title }}
                    </div>
                    <div class="text-caption">
                      {{ formatDate(note.updatedAt) }}
                    </div>
                  </div>
                  <button
                    class="btn btn-flat btn-round btn-sm"
                    style="color: var(--danger-color)"
                    @click.stop="confirmDelete(note)"
                  >
                    <span class="material-icons">delete</span>
                  </button>
                </li>
              </ul>

              <div v-else class="card-section text-center text-grey-7">
                <span class="material-icons xl" style="color: var(--text-lighter)">note_add</span>
                <div class="mt-sm">Aucune note</div>
                <div class="text-caption">Créez votre première note!</div>
              </div>
            </div>
          </div>

          <!-- Colonne droite: Éditeur de note -->
          <div class="col-12 col-md-8">
            <div v-if="selectedNote || isCreating" class="card">
              <div class="card-section">
                <div class="input-group">
                  <label>Titre de la note</label>
                  <input
                    v-model="noteTitle"
                    type="text"
                    placeholder="Titre de la note"
                    required
                  />
                </div>
              </div>

              <div class="card-section">
                <div class="text-subtitle2 mb-sm">Contenu</div>
                <QuillEditor
                  v-model:content="noteContent"
                  contentType="html"
                  theme="snow"
                  :toolbar="toolbarOptions"
                  style="min-height: 400px;"
                />
              </div>

              <div class="card-actions">
                <button
                  class="btn btn-flat"
                  @click="cancelEdit"
                >
                  Annuler
                </button>
                <button
                  class="btn btn-primary"
                  :class="{ loading: notesStore.loading }"
                  :disabled="notesStore.loading"
                  @click="saveNote"
                >
                  Enregistrer
                </button>
              </div>
            </div>

            <div v-else class="card text-center p-xl">
              <span class="material-icons" style="font-size: 64px; color: var(--text-lighter)">edit_note</span>
              <div class="text-h6 text-grey-7 mt-md">
                Sélectionnez une note ou créez-en une nouvelle
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useNotesStore } from '@/stores/notes'
import { showNotification } from '@/utils/notifications'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

const router = useRouter()
const authStore = useAuthStore()
const notesStore = useNotesStore()

// État de l'éditeur
const selectedNote = ref(null)
const isCreating = ref(false)
const noteTitle = ref('')
const noteContent = ref('')
const showUserMenu = ref(false)

// Configuration de la barre d'outils Quill
const toolbarOptions = [
  ['bold', 'italic', 'underline', 'strike'],
  ['blockquote', 'code-block'],
  [{ 'header': 1 }, { 'header': 2 }],
  [{ 'list': 'ordered'}, { 'list': 'bullet' }],
  [{ 'indent': '-1'}, { 'indent': '+1' }],
  [{ 'size': ['small', false, 'large', 'huge'] }],
  [{ 'color': [] }, { 'background': [] }],
  [{ 'align': [] }],
  ['clean']
]

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
 * Charge les notes au montage du composant
 */
onMounted(async () => {
  try {
    await notesStore.fetchNotes()
  } catch (error) {
    showNotification({
      type: 'negative',
      message: 'Erreur lors du chargement des notes'
    })
  }
})

/**
 * Prépare la création d'une nouvelle note
 */
function createNewNote() {
  selectedNote.value = null
  isCreating.value = true
  noteTitle.value = ''
  noteContent.value = ''
}

/**
 * Sélectionne une note existante pour l'édition
 */
function selectNote(note) {
  selectedNote.value = note
  isCreating.value = false
  noteTitle.value = note.title
  noteContent.value = note.content
}

/**
 * Annule l'édition ou la création
 */
function cancelEdit() {
  selectedNote.value = null
  isCreating.value = false
  noteTitle.value = ''
  noteContent.value = ''
}

/**
 * Enregistre la note (création ou mise à jour)
 */
async function saveNote() {
  if (!noteTitle.value || !noteContent.value) {
    showNotification({
      type: 'warning',
      message: 'Veuillez remplir tous les champs'
    })
    return
  }

  const noteData = {
    title: noteTitle.value,
    content: noteContent.value
  }

  try {
    if (isCreating.value) {
      // Création d'une nouvelle note
      await notesStore.createNote(noteData)
      showNotification({
        type: 'positive',
        message: 'Note créée avec succès',
        icon: 'check_circle'
      })
    } else {
      // Mise à jour d'une note existante
      await notesStore.updateNote(selectedNote.value.id, noteData)
      showNotification({
        type: 'positive',
        message: 'Note mise à jour avec succès',
        icon: 'check_circle'
      })
    }
    cancelEdit()
  } catch (error) {
    showNotification({
      type: 'negative',
      message: 'Erreur lors de l\'enregistrement',
      icon: 'error'
    })
  }
}

/**
 * Demande confirmation avant suppression
 */
function confirmDelete(note) {
  if (confirm(`Êtes-vous sûr de vouloir supprimer la note "${note.title}" ?`)) {
    deleteNote(note)
  }
}

/**
 * Supprime une note
 */
async function deleteNote(note) {
  try {
    await notesStore.deleteNote(note.id)
    showNotification({
      type: 'positive',
      message: 'Note supprimée',
      icon: 'check_circle'
    })
    if (selectedNote.value?.id === note.id) {
      cancelEdit()
    }
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
  notesStore.reset()
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
.note-item {
  border-left: 3px solid transparent;
  transition: all 0.3s;
}

.note-item:hover {
  border-left-color: var(--primary-color);
  background-color: rgba(0, 0, 0, 0.03);
}

.note-item.active {
  border-left-color: var(--primary-color);
  background-color: rgba(25, 118, 210, 0.1);
}
</style>
