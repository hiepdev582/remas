<script setup lang="ts">
import type { BadgeProps } from "ant-design-vue";

export interface BaseBadgeProps extends /* @vue-ignore */ BadgeProps {
  count?: number | string;
  dot?: boolean;
  showZero?: boolean;
  overflowCount?: number;
  color?: string;
}

const props = withDefaults(defineProps<BaseBadgeProps>(), {
  showZero: false,
});
</script>

<template>
  <a-badge v-bind="{ ...$attrs, ...props }">
    <slot />
  </a-badge>
</template>
