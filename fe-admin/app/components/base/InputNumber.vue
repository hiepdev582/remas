<script setup lang="ts">
import type { InputNumberProps } from "ant-design-vue";

export interface BaseInputNumberProps extends /* @vue-ignore */ InputNumberProps {
  modelValue?: number | string;
  placeholder?: string;
  disabled?: boolean;
  min?: number;
  max?: number;
}

const props = withDefaults(defineProps<BaseInputNumberProps>(), {
  disabled: false,
  min: 0,
});

const emit = defineEmits(["update:modelValue", "change"]);

const value = computed({
  get() {
    return props.modelValue;
  },
  set(val) {
    emit("update:modelValue", val);
  },
});
</script>

<template>
  <a-input-number
    v-bind="{ ...$attrs, ...props, placeholder: props.disabled ? '' : props.placeholder }"
    v-model:value="value"
    class="w-full"
    @change="(val: any) => emit('change', val)"
  />
</template>
