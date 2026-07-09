<script setup lang="ts">
export interface BaseImagePreviewGroupProps {
  preview?: boolean | object;
}

const props = defineProps<BaseImagePreviewGroupProps>();
</script>

<template>
  <a-image-preview-group v-bind="{ ...$attrs, ...props }">
    <slot />
  </a-image-preview-group>
</template>
