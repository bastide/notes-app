import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

/**
 * Configuration Vite pour le frontend Vue.js
 * 
 * Configure:
 * - Le plugin Vue 3
 * - Les alias de chemins
 * - Le serveur de développement
 * - La construction de production
 */
export default defineConfig({
  plugins: [
    vue()
  ],
  define: {
    __VUE_PROD_DEVTOOLS__: true
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      // Redirection des appels API vers le backend Spring Boot en développement
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false
  }
})
