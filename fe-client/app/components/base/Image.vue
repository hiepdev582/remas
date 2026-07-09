<script setup lang="ts">
import type { ImageProps } from "ant-design-vue";

export interface BaseImageProps extends /* @vue-ignore */ ImageProps {
  src?: string;
  alt?: string;
  width?: string | number;
  height?: string | number;
  preview?: boolean | object;
}

const props = withDefaults(defineProps<BaseImageProps>(), {
  preview: true,
});
</script>

<template>
  <a-image v-bind="{ ...$attrs, ...props }" />
</template>
