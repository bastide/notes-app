/**
 * Point d'entrée principal de l'application Vue.js
 * 
 * Initialise:
 * - L'application Vue
 * - Le router Vue Router
 * - Le store Pinia
 */
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// Import des styles personnalisés
import './styles/main.css'

const app = createApp(App)

// Activation des devtools
app.config.devtools = true

// Configuration Pinia pour la gestion d'état
app.use(createPinia())

// Configuration Vue Router
app.use(router)

// Montage de l'application
app.mount('#app')
