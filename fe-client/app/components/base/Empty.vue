<script setup lang="ts">
import type { EmptyProps } from "ant-design-vue";

export interface BaseEmptyProps extends /* @vue-ignore */ EmptyProps {
  description?: string;
}

const props = defineProps<BaseEmptyProps>();
</script>

<template>
  <a-empty v-bind="{ ...$attrs, ...props }">
    <slot />
  </a-empty>
</template>
