<script setup lang="ts">
import type { SegmentedProps } from "ant-design-vue";

export interface BaseSegmentedProps extends /* @vue-ignore */ SegmentedProps {
  modelValue?: string | number;
  options?: string[];
  block?: boolean;
}

const props = withDefaults(defineProps<BaseSegmentedProps>(), {
  block: false,
});

const emit = defineEmits(["update:modelValue"]);

const value = computed({
  get() {
    return props.modelValue;
  },
  set(value) {
    emit("update:modelValue", value);
  },
});
</script>

<template>
  <a-segmented v-bind="{ ...$attrs, ...props }" v-model:value="value">
  </a-segmented>
</template>
