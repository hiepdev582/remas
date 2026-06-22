<script setup lang="ts">
import type { BaseInputProps } from "~/components/base/Input.vue";
import type { BaseTextAreaProps } from "~/components/base/TextArea.vue";
import { upsertCategoryFieldSchema } from "../../validation";
import type { AddCategoryRequest, EditCategoryRequest } from "../../types";

const props = defineProps<{
  id?: number;
  state: FormState;
}>();

const isLoading = ref(false);
const categoryService = useCategoryService();

const isOpen = defineModel<boolean>();
const formRef = ref<any>(null);

const handleConfirm = () => {
  formRef.value?.onSubmit();
};

//#region State
const upsertCategoryFields = computed<FormFieldConfig[]>(() => [
  {
    type: FormFieldType.TEXT,
    name: categoryFieldNames.name,
    label: categoryFieldLabels.name,
    required: true,
    config: {
      autocomplete: "new-password",
      placeholder: placeholders.enter(categoryFieldLabels.name),
      disabled: props.state === FormState.VIEW,
    } as BaseInputProps,
  },
  {
    type: FormFieldType.AREA,
    name: categoryFieldNames.description,
    label: categoryFieldLabels.description,
    config: {
      autocomplete: "new-password",
      placeholder: placeholders.enter(categoryFieldLabels.description),
      disabled: props.state === FormState.VIEW,
    } as BaseTextAreaProps,
  },
]);

const upsertCategorySchema = toTypedSchema(
  upsertCategoryFieldSchema.getSchema(),
);
//#endregion

//#region Services
const formInfo = computed(() => {
  return {
    modalTitle:
      props.state === FormState.ADD ? "Add Category" : "Edit Category",
    successMessage:
      props.state === FormState.ADD
        ? "Add category successfully!"
        : "Edit category successfully!",
    errorMessage:
      props.state === FormState.ADD
        ? "Add category failed!"
        : "Edit category failed!",
  };
});

const handleConfirmUpsertCategory = async (
  values: AddCategoryRequest | EditCategoryRequest,
) => {
  isLoading.value = true;

  try {
    let response: string;
    if (props.state === FormState.ADD) {
      response = await categoryService.add(values as AddCategoryRequest);
      toast.success(response || formInfo.value.successMessage);
    } else if (props.id) {
      response = await categoryService.edit(
        props.id,
        values as EditCategoryRequest,
      );
      toast.success(response || formInfo.value.successMessage);
    } else {
      toast.errorOccured();
    }
    isOpen.value = false;
  } catch (error: any) {
    const apiMessage =
      error.response?._data?.message || formInfo.value.errorMessage;
    toast.error(apiMessage);
  } finally {
    isLoading.value = false;
  }
};

watch(
  () => props.state,
  async () => {
    formRef.value?.resetForm();

    if (props.state !== FormState.ADD && props.id) {
      const category = await categoryService.get(props.id);
      formRef.value?.setValues(category);
    }
  },
);
//#endregion
</script>

<template>
  <BaseModal
    v-model="isOpen"
    wrapClassName="upsert-category-wrapper"
    confirmText="Save"
    :confirmLoading="isLoading"
    :title="formInfo.modalTitle"
    @onConfirm="handleConfirm"
  >
    <BaseForm
      ref="formRef"
      hide-submit-button
      :fields="upsertCategoryFields"
      :validation-schema="upsertCategorySchema"
      @onSubmit="handleConfirmUpsertCategory"
    />
  </BaseModal>
</template>
