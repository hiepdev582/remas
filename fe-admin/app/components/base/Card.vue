<script setup lang="ts">
import type { CardProps } from "ant-design-vue";

export interface BaseCardProps extends /* @vue-ignore */ CardProps {
  title?: string;
  hoverable?: boolean;
}

const props = withDefaults(defineProps<BaseCardProps>(), {
  title: "",
  hoverable: true,
});
</script>

<template>
  <a-card v-bind="{ ...$attrs, ...props }">
    <template #extra>
      <slot name="extra" />
    </template>
    <template #cover>
      <slot name="cover" />
    </template>
    <template #actions>
      <slot name="actions" />
    </template>
    <slot></slot>
  </a-card>
</template>
