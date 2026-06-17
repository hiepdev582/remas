<script setup lang="ts"></script>

<template>
  <a-layout-content
    :style="{
      marginTop: '20px',
      marginBottom: '0px',
      marginInline: '24px',
      paddingBlock: '14px',
      paddingInline: '18px',
      backgroundColor: '#fff',
      borderRadius: 'var(--border-radius)',
    }"
  >
    <slot />
  </a-layout-content>
</template>
