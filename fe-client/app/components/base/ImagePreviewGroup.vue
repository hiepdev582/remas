<script setup lang="ts">
export interface BaseImagePreviewGroupProps {
  preview?: boolean | object;
}

const props = withDefaults(defineProps<BaseImagePreviewGroupProps>(), {
  preview: true,
});
</script>

<template>
  <a-image-preview-group v-bind="{ ...$attrs, ...props }">
    <slot />
  </a-image-preview-group>
</template>
