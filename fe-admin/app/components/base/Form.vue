<script setup lang="ts">
import type { FormProps } from "ant-design-vue";
import Input from "./Input.vue";
import InputPassword from "./InputPassword.vue";
import TextArea from "./TextArea.vue";
import Select from "./Select.vue";
import UploadPictures from "./UploadPictures.vue";
import InputNumber from "./InputNumber.vue";
import Switch from "./Switch.vue";
import UploadDocuments from "./UploadDocuments.vue";
import DatePicker from "./DatePicker.vue";

//#region Props & emits
export interface BaseFormProps extends /* @vue-ignore */ FormProps {
  fields: FormFieldConfig[];
  validationSchema: any;
  submitButtonText?: string;
  loading?: boolean;
  colon?: boolean;
  disabled?: boolean;
  layout?: FormLayout;
  scrollToFirstError?: boolean;
  hideSubmitButton?: boolean;
}

const props = withDefaults(defineProps<BaseFormProps>(), {
  submitButtonText: "Submit",
  loading: false,
  colon: true,
  disabled: false,
  layout: FormLayout.VERTICAL,
  scrollToFirstError: true,
  hideSubmitButton: false,
});

const emit = defineEmits(["onSubmit"]);
//#endregion

//#region State
const getInitValues = (fieldType: FormFieldType) => {
  switch (fieldType) {
    case FormFieldType.UPLOAD_PICTURES:
    case FormFieldType.UPLOAD_DOCUMENTS:
      return [];
    case FormFieldType.SELECT:
      return undefined;
    case FormFieldType.NUMBER:
      return undefined;
    case FormFieldType.SWITCH:
      return true;
    default:
      return "";
  }
};

const initialValues = props.fields.reduce(
  (acc, field) => {
    acc[field.name] = getInitValues(field.type);
    return acc;
  },
  {} as Record<string, any>,
);

const { handleSubmit, resetForm, setValues } = useForm({
  validationSchema: computed(() => props.validationSchema),
  initialValues,
});

const formValues = ref<Record<string, any>>({});

const componentMappers = {
  [FormFieldType.TEXT]: Input,
  [FormFieldType.PASSWORD]: InputPassword,
  [FormFieldType.AREA]: TextArea,
  [FormFieldType.SELECT]: Select,
  [FormFieldType.UPLOAD_PICTURES]: UploadPictures,
  [FormFieldType.NUMBER]: InputNumber,
  [FormFieldType.SWITCH]: Switch,
  [FormFieldType.UPLOAD_DOCUMENTS]: UploadDocuments,
  [FormFieldType.DATE]: DatePicker,
};

props.fields.forEach((field) => {
  const { value } = useField(field.name);
  formValues.value[field.name] = value;
});

const onSubmit = handleSubmit(
  (values) => emit("onSubmit", values),
  ({ errors }) => {
    if (props.scrollToFirstError && errors && Object.keys(errors).length > 0) {
      const firstErrorField = Object.keys(errors)[0];
      nextTick(() => {
        const formEl = formRef.value?.$el;
        if (formEl) {
          const fieldContainer = formEl.querySelector(
            `[data-field-name="${firstErrorField}"]`,
          );
          if (fieldContainer) {
            const element = fieldContainer.querySelector(
              "input:not([disabled]):not([readonly]), textarea:not([disabled]):not([readonly]), .ant-select-selection-search-input",
            );
            if (element) {
              element.scrollIntoView({ behavior: "smooth", block: "center" });
              (element as HTMLElement).focus?.();
            } else {
              fieldContainer.scrollIntoView({
                behavior: "smooth",
                block: "center",
              });
            }
          }
        }
      });
    }
  },
);

defineExpose({
  onSubmit,
  resetForm,
  setValues,
  formValues,
});
//#endregion

//#region Utils
const formRef = ref<any>(null);

const getFocusableElements = (): HTMLElement[] => {
  if (!formRef.value) return [];
  const formElements = formRef.value.$el;

  return Array.from(
    formElements.querySelectorAll(
      "input:not([disabled]):not([readonly]), textarea:not([disabled]):not([readonly])",
    ),
  );
};

const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === "Tab") {
    const focusableEls = getFocusableElements();
    if (focusableEls.length === 0) return;

    const firstFocusableEl = focusableEls[0];
    const lastFocusableEl = focusableEls[focusableEls.length - 1];

    if (
      firstFocusableEl &&
      document.activeElement === lastFocusableEl &&
      !event.shiftKey
    ) {
      event.preventDefault();
      firstFocusableEl.focus();
    } else if (
      lastFocusableEl &&
      document.activeElement === firstFocusableEl &&
      event.shiftKey
    ) {
      event.preventDefault();
      lastFocusableEl.focus();
    }
  }
};
//#endregion

//#region Life circle
onMounted(() => {
  nextTick(() => {
    const focusableElements = getFocusableElements();
    if (focusableElements.length > 0) {
      focusableElements[0]?.focus();
    }
  });
});
//#endregion
</script>

<template>
  <a-form
    ref="formRef"
    :colon
    :disabled
    :layout
    :scrollToFirstError
    @submit.prevent="onSubmit"
    @keydown="handleKeyDown"
  >
    <template v-for="field in fields" :key="field.name">
      <div :data-field-name="field.name">
        <BaseFormItem
          :name="field.name"
          :label="field.label"
          :required="field.required"
        >
          <component
            v-bind="field.config as any"
            v-model="formValues[field.name]"
            :is="componentMappers[field.type]"
          />
        </BaseFormItem>
      </div>
    </template>
    <!-- Action -->
    <slot name="actions" :submit="onSubmit" :loading="loading">
      <BaseFormItem v-if="!hideSubmitButton" name="submitButton" class="mt-8">
        <BaseButton block htmlType="submit" :loading>
          {{ submitButtonText }}
        </BaseButton>
      </BaseFormItem>
    </slot>
  </a-form>
</template>

<style lang="css" scoped>
.ant-form-vertical :deep(.ant-form-item-label) {
  padding-bottom: 4px;
}

:deep(.ant-form-item) {
  margin-bottom: 18px;
}

:deep(.ant-form-item-explain-error) {
  font-size: 13px;
}
</style>
