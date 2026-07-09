<script setup lang="ts">
export interface BaseCheckableTagProps {
  modelValue?: boolean;
}

const props = withDefaults(defineProps<BaseCheckableTagProps>(), {
  modelValue: false,
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
  <a-checkable-tag v-bind="$attrs" class="select-none" v-model:checked="value">
    <slot></slot>
  </a-checkable-tag>
</template>
