/**
 * Composant racine de l'application Vue.js
 * 
 * Fournit le layout de base avec le RouterView
 * pour afficher les différentes pages.
 */
<template>
  <router-view />
</template>

<script setup>
// Pas de logique nécessaire, simple point de montage pour le router
</script>

<style>
/* Styles globaux pour l'application */
#app {
  font-family: 'Roboto', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100vh;
}

body {
  margin: 0;
  padding: 0;
}
</style>
