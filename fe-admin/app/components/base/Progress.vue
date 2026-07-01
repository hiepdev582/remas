<script setup lang="ts">
import type { ProgressProps } from "ant-design-vue";

export interface BaseProgressProps extends /* @vue-ignore */ ProgressProps {
  percent?: number;
  type?: ProgressType;
  width?: number;
  showInfo?: boolean;
}

const props = withDefaults(defineProps<BaseProgressProps>(), {
  percent: 0,
  type: ProgressType.LINE,
  width: 80,
  showInfo: true,
});
</script>

<template>
  <a-progress v-bind="{ ...$attrs, ...props }" />
</template>
