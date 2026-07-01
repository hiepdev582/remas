<script setup lang="ts">
import type { SwitchProps } from "ant-design-vue";

export interface BaseSwitchProps extends /* @vue-ignore */ SwitchProps {
  modelValue?: boolean;
  checked?: boolean;
  disabled?: boolean;
}

const props = withDefaults(defineProps<BaseSwitchProps>(), {
  disabled: false,
});

const emit = defineEmits(["update:modelValue", "update:checked", "change"]);

const value = computed({
  get() {
    return props.modelValue !== undefined ? props.modelValue : props.checked;
  },
  set(val) {
    emit("update:modelValue", val);
    emit("update:checked", val);
  },
});
</script>

<template>
  <a-switch
    v-bind="{ ...$attrs, ...props }"
    v-model:checked="value"
    @change="(checked: any, event: any) => emit('change', checked, event)"
  />
</template>
