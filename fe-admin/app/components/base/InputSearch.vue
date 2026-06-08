<script setup lang="ts">
import type { InputProps } from "ant-design-vue";

interface BaseInputSearchProps extends /* @vue-ignore */ InputProps {
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
}

const props = withDefaults(defineProps<BaseInputSearchProps>(), {
  placeholder: placeholders.enterDefault,
  addonAfter: "",
  addonBefore: "",
  allowClear: false,
  bordered: true,
  disabled: false,
  showCount: false,
  size: InputSize.MIDDLE,
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
  <a-input-search v-bind="{ ...$attrs, ...props }" v-model:value="value">
    <template v-if="$slots.prefix" #prefix>
      <slot name="prefix" />
    </template>
    <template v-if="$slots.suffix" #suffix>
      <slot name="suffix" />
    </template>
    <template #clearIcon>
      <Icon name="mdi:clear" :size="18" />
    </template>
  </a-input-search>
</template>
