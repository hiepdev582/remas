<script setup lang="ts">
import type { SelectProps } from "ant-design-vue";

export interface BaseSelectOption {
  label: string;
  value: any;
  [key: string]: any;
}

export interface BaseSelectProps extends /* @vue-ignore */ SelectProps {
  modelValue?: any;
  options?: BaseSelectOption[];
  placeholder?: string;
  disabled?: boolean;
  allowClear?: boolean;
  showSearch?: boolean;
  popupClassName?: string;
}

const props = withDefaults(defineProps<BaseSelectProps>(), {
  options: () => [],
  placeholder: placeholders.selectDefault,
  disabled: false,
  allowClear: true,
  showSearch: true,
  popupClassName: "",
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

const filterOption = (input: string, option: any) => {
  return option?.label?.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};
</script>

<template>
  <a-select
    v-bind="{ ...$attrs, ...props, placeholder: props.disabled ? '' : props.placeholder }"
    v-model:value="value"
    :filterOption="filterOption"
    @change="(val: any) => emit('change', val)"
  />
</template>
