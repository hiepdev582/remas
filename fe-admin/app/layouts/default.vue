<script setup lang="ts"></script>

<template>
  <a-layout>
    <AppSidebar />
    <a-layout>
      <AppHeader />
      <AppContent>
        <slot />
      </AppContent>
      <AppFooter />
    </a-layout>
  </a-layout>
</template>
