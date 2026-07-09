<script setup lang="ts">
import type { FormItemProps } from "ant-design-vue";

export interface BaseFormItemProps extends /* @vue-ignore */ FormItemProps {
  name: string;
  label?: string;
  colon?: boolean;
  required?: boolean;
}

const props = withDefaults(defineProps<BaseFormItemProps>(), {
  label: "",
  colon: true,
  required: false,
});

const errorMessage = useFieldError(() => props.name);
</script>

<template>
  <a-form-item
    :label
    :colon
    :required
    :help="errorMessage"
    :validate-status="errorMessage ? FormItemStatus.ERROR : ''"
  >
    <slot />
  </a-form-item>
</template>

<style scoped>
:deep(.ant-form-item-required::before) {
  display: none !important;
}

:deep(.ant-form-item-required::after) {
  display: inline-block !important;
  content: " *" !important;
  color: var(--color-error);
  margin-left: 4px !important;
  font-family: SimSun, sans-serif !important;
}
</style>
