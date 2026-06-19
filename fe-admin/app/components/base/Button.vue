<script setup lang="ts">
import type { ButtonProps } from "ant-design-vue";

export interface BaseButtonProps extends /* @vue-ignore */ ButtonProps {
  block?: boolean;
  danger?: boolean;
  disabled?: boolean;
  loading?: boolean;
  size?: ButtonSize;
  type?: ButtonType;
}

const props = withDefaults(defineProps<BaseButtonProps>(), {
  block: false,
  danger: false,
  disabled: false,
  loading: false,
  size: ButtonSize.MIDDLE,
  type: ButtonType.PRIMARY,
});
</script>

<template>
  <a-button v-bind="{ ...$attrs, ...props }">
    <template #icon>
      <slot name="icon" />
    </template>
    <slot />
  </a-button>
</template>
