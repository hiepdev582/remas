<script setup lang="ts">
import type { FormProps } from "ant-design-vue";
import Input from "./Input.vue";
import InputPassword from "./InputPassword.vue";
import TextArea from "./TextArea.vue";

interface BaseFormProps extends /* @vue-ignore */ FormProps {
  fields: FormFieldConfig[];
  validationSchema: any;
  submitButtonText?: string;
  loading?: boolean;
  colon?: boolean;
  disabled?: boolean;
  layout?: FormLayout;
  scrollToFirstError?: boolean;
}

const props = withDefaults(defineProps<BaseFormProps>(), {
  submitButtonText: "Submit",
  loading: false,
  colon: true,
  disabled: false,
  layout: FormLayout.VERTICAL,
  scrollToFirstError: false,
});

const emit = defineEmits(["onSubmit"]);

const { handleSubmit } = useForm({
  validationSchema: props.validationSchema,
});

const formValues = ref<Record<string, any>>({});

const componentMappers = {
  [FormFieldType.TEXT]: Input,
  [FormFieldType.PASSWORD]: InputPassword,
  [FormFieldType.AREA]: TextArea,
};

props.fields.forEach((field) => {
  const { value } = useField(field.name);
  value.value = "";
  formValues.value[field.name] = value;
});

const onSubmit = handleSubmit((values) => emit("onSubmit", values));
</script>

<template>
  <a-form
    :colon
    :disabled
    :layout
    :scrollToFirstError
    @submit.prevent="onSubmit"
  >
    <template v-for="field in fields" :key="field.name">
      <BaseFormItem
        :name="field.name"
        :label="field.label"
        :required="field.required"
      >
        <component
          v-model="formValues[field.name]"
          :is="componentMappers[field.type]"
          :placeholder="field.placeholder"
        />
      </BaseFormItem>
    </template>
    <!-- Action -->
    <BaseFormItem name="submitButton" class="mt-8">
      <BaseButton block htmlType="submit" :loading>
        {{ submitButtonText }}
      </BaseButton>
    </BaseFormItem>
  </a-form>
</template>

<style lang="css" scoped>
.ant-form-vertical :deep(.ant-form-item-label) {
  padding-bottom: 4px;
}

:deep(.ant-form-item) {
  margin-bottom: 18px;
}
</style>
