<script setup lang="ts">
import type { TextAreaProps } from "ant-design-vue";

export interface BaseTextAreaProps extends /* @vue-ignore */ TextAreaProps {
  modelValue?: string | number;
  placeholder?: string;
  allowClear?: boolean;
  bordered?: boolean;
  disabled?: boolean;
  maxlength?: number;
  status?: InputStatus;
  size?: InputSize;
  autoSize?: boolean;
  readonly?: boolean;
  autocomplete?: string;
}

const props = withDefaults(defineProps<BaseTextAreaProps>(), {
  placeholder: placeholders.enterDefault,
  allowClear: false,
  bordered: true,
  disabled: false,
  size: InputSize.MIDDLE,
  autoSize: false,
  readonly: false,
  autocomplete: "off",
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
  <a-textarea v-bind="{ ...$attrs, ...props, placeholder: props.disabled ? '' : props.placeholder }" v-model:value="value" />
</template>
