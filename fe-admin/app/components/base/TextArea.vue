<script setup lang="ts">
import type { TextAreaProps } from "ant-design-vue";

interface BaseTextAreaProps extends /* @vue-ignore */ TextAreaProps {
  modelValue?: string | number;
  placeholder?: string;
  allowClear?: boolean;
  bordered?: boolean;
  disabled?: boolean;
  maxlength?: number;
  status?: InputStatus;
  size?: InputSize;
  autosize?: boolean;
}

const props = withDefaults(defineProps<BaseTextAreaProps>(), {
  placeholder: placeholders.enterDefault,
  allowClear: false,
  bordered: true,
  disabled: false,
  size: InputSize.MIDDLE,
  autosize: false,
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
  <a-textarea v-bind="{ ...$attrs, ...props }" v-model:value="value" />
</template>
