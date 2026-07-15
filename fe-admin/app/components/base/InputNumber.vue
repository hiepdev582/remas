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

const defaultFormatter = (val: any) => {
  if (val === null || val === undefined || val === "") return "";
  return `${val}`.replace(numberFormatterRegex, ",");
};

const defaultParser = (val: any) => {
  if (!val) return "";
  return val.replace(numberParserRegex, "");
};
</script>

<template>
  <a-input-number
    v-bind="{
      ...$attrs,
      ...props,
      formatter: defaultFormatter,
      parser: defaultParser,
      placeholder: props.disabled ? '' : props.placeholder,
    }"
    v-model:value="value"
    class="w-full"
    @change="(val: any) => emit('change', val)"
  />
</template>
