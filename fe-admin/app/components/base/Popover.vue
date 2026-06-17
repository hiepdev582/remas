<script setup lang="ts">
import type { PopoverProps } from "ant-design-vue";

export interface BasePopoverProps extends /* @vue-ignore */ PopoverProps {
  title?: string;
  placement?: PopoverPlacement;
}

const props = withDefaults(defineProps<BasePopoverProps>(), {
  title: "",
  placement: PopoverPlacement.TOP,
});
</script>

<template>
  <a-popover v-bind="{ ...$attrs, ...props }">
    <template #content>
      <slot name="content" />
    </template>
    <slot />
  </a-popover>
</template>
