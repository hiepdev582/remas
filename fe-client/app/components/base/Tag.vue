<script setup lang="ts">
import type { TagProps } from "ant-design-vue";

export interface BaseTagProps extends /* @vue-ignore */ TagProps {
  closable?: boolean;
  bordered?: boolean;
}

const props = withDefaults(defineProps<BaseTagProps>(), {
  closable: false,
  bordered: false,
});
</script>

<template>
  <a-tag v-bind="{ ...$attrs, ...props }">
    <template #icon>
      <slot name="icon"></slot>
    </template>
    <slot></slot>
  </a-tag>
</template>
