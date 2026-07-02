<script setup lang="ts">
import type { TooltipProps } from "ant-design-vue";

export interface BaseTooltipProps extends /* @vue-ignore */ TooltipProps {
  arrow?: boolean;
}

const props = withDefaults(defineProps<BaseTooltipProps>(), {
  arrow: false,
});
</script>

<template>
  <a-tooltip v-bind="{ ...$attrs, ...props }">
    <template #title v-if="$slots.title">
      <slot name="title" />
    </template>
    <slot />
  </a-tooltip>
</template>
