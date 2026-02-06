/**
 * Configuration du router Vue Router
 * 
 * Définit les routes de l'application:
 * - Login: Page de connexion (public)
 * - Notes: Page principale de gestion des notes (authentifié)
 * - Users: Page de gestion des utilisateurs (admin uniquement)
 * 
 * Implémente des guards de navigation pour protéger
 * les routes nécessitant une authentification.
 */
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      name: 'notes',
      component: () => import('@/views/NotesView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/users',
      name: 'users',
      component: () => import('@/views/UsersView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      // Route par défaut - redirige vers les notes
      path: '/:pathMatch(.*)*',
      redirect: '/'
    }
  ]
})

/**
 * Guard de navigation global
 * 
 * Vérifie l'authentification avant chaque navigation:
 * - Si la route nécessite une auth et l'utilisateur n'est pas connecté → login
 * - Si la route nécessite le rôle admin et l'utilisateur n'est pas admin → notes
 * - Si l'utilisateur est connecté et va sur /login → notes
 */
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const isAuthenticated = authStore.isAuthenticated
  const isAdmin = authStore.isAdmin

  if (to.meta.requiresAuth && !isAuthenticated) {
    // Route protégée et utilisateur non connecté
    next('/login')
  } else if (to.meta.requiresAdmin && !isAdmin) {
    // Route admin et utilisateur sans droits admin
    next('/')
  } else if (to.path === '/login' && isAuthenticated) {
    // Utilisateur connecté essaie d'accéder au login
    next('/')
  } else {
    // Autoriser la navigation
    next()
  }
})

export default router
