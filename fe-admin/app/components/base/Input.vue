<script setup lang="ts">
import type { InputProps } from "ant-design-vue";

export interface BaseInputProps extends /* @vue-ignore */ InputProps {
  modelValue?: string | number;
  placeholder?: string;
  addonAfter?: string;
  addonBefore?: string;
  allowClear?: boolean;
  bordered?: boolean;
  disabled?: boolean;
  maxlength?: number;
  showCount?: boolean;
  status?: InputStatus;
  size?: InputSize;
  readonly?: boolean;
  autocomplete?: string;
}

const props = withDefaults(defineProps<BaseInputProps>(), {
  placeholder: placeholders.enterDefault,
  addonAfter: "",
  addonBefore: "",
  allowClear: false,
  bordered: true,
  disabled: false,
  showCount: false,
  size: InputSize.MIDDLE,
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
  <a-input v-bind="{ ...$attrs, ...props, placeholder: props.disabled ? '' : props.placeholder }" v-model:value="value">
    <template v-if="$slots.prefix" #prefix>
      <slot name="prefix" />
    </template>
    <template v-if="$slots.suffix" #suffix>
      <slot name="suffix" />
    </template>
    <template #clearIcon>
      <Icon name="mdi:clear" :size="18" />
    </template>
  </a-input>
</template>
