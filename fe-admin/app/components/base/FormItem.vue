<script setup lang="ts">
import type { FormItemProps } from "ant-design-vue";

interface BaseFormItemProps extends /* @vue-ignore */ FormItemProps {
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
