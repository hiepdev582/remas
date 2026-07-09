<script setup lang="ts">
import type { AvatarProps } from "ant-design-vue";

export interface BaseAvatarProps extends /* @vue-ignore */ AvatarProps {
  size?: number;
  shape?: AvatarShape;
}

const props = withDefaults(defineProps<BaseAvatarProps>(), {
  size: 34,
  shape: AvatarShape.CIRCLE,
});
</script>

<template>
  <a-avatar v-bind="{ ...$attrs, ...props }">
    <slot></slot>
  </a-avatar>
</template>
