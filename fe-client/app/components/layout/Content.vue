<script setup lang="ts"></script>

<template>
  <a-layout-content
    :style="{
      marginTop: '24px',
      marginInline: '24px',
      paddingBlock: '14px',
      paddingInline: '18px',
      backgroundColor: '#fff',
      borderRadius: 'var(--border-radius)',
      boxShadow:
        '0 4px 6px -1px rgb(0 0 0 / 0.05), 0 2px 4px -2px rgb(0 0 0 / 0.05)',
      overflow: 'auto',
      height: 'calc(100vh - 24px - 40px)',
    }"
  >
    <slot />
  </a-layout-content>
</template>
