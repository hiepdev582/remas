<script setup lang="ts">
import type { DatePickerProps } from "ant-design-vue";
import { computed } from "vue";
import dayjs from "dayjs";

export type BaseDatePickerProps = DatePickerProps & {
  modelValue?: string;
};

const props = defineProps({
  modelValue: {
    type: String,
    default: "",
  },
  placeholder: {
    type: String,
    default: "",
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  showTime: {
    type: [Boolean, Object],
    default: true,
  },
  format: {
    type: String,
    default: "YYYY-MM-DD HH:mm",
  },
  valueFormat: {
    type: String,
    default: "YYYY-MM-DDTHH:mm:ss",
  },
});

const emit = defineEmits(["update:modelValue", "change"]);

const value = computed({
  get() {
    return props.modelValue ? dayjs(props.modelValue) : null;
  },
  set(val) {
    const formatted = val ? dayjs(val).format(props.valueFormat) : "";
    emit("update:modelValue", formatted);
  },
});

const handleChange = (val: any) => {
  value.value = val;
  emit("change", val);
};
</script>

<template>
  <ClientOnly>
    <a-date-picker
      v-bind="{
        ...$attrs,
        ...(props as any),
        placeholder: props.disabled ? '' : props.placeholder,
      }"
      :value="value as any"
      class="w-full"
      @change="handleChange"
    />
  </ClientOnly>
</template>
