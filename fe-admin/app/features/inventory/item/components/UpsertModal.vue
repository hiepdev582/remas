<script setup lang="ts">
import type { BaseInputProps } from "~/components/base/Input.vue";
import type {
  BaseSelectOption,
  BaseSelectProps,
} from "~/components/base/Select.vue";
import { upsertItemFieldSchema } from "../validation";
import type { AddItemRequest, EditItemRequest } from "../types";
import { ItemStatus } from "../constants";

const props = defineProps<{
  id?: number;
  state: FormState;
}>();

const emit = defineEmits<{ (e: "onSuccess"): void }>();

const isLoading = ref(false);
const itemService = useItemService();
const categoryService = useCategoryService();

const isOpen = defineModel<boolean>();
const formRef = ref<any>(null);

const categoriesList = ref<BaseSelectOption[]>([]);

const handleConfirm = () => {
  formRef.value?.onSubmit();
};

const getAllCategories = async () => {
  try {
    const res = await categoryService.getAll();
    categoriesList.value = res.data.map((c) => ({
      label: c.name,
      value: c.id,
    }));
  } catch (error) {
    console.error("Failed to load categories", error);
  }
};

//#region State
const upsertItemFields = computed<FormFieldConfig[]>(() => {
  const fields: FormFieldConfig[] = [
    {
      type: FormFieldType.TEXT,
      name: itemFieldNames.name,
      label: itemFieldLabels.name,
      required: true,
      config: {
        autocomplete: "new-password",
        placeholder: placeholders.enter(itemFieldLabels.name),
        disabled: props.state === FormState.VIEW,
      } as BaseInputProps,
    },
    {
      type: FormFieldType.SELECT,
      name: itemFieldNames.categoryId,
      label: itemFieldLabels.categoryId,
      required: true,
      config: {
        placeholder: placeholders.select(itemFieldLabels.categoryId),
        options: categoriesList.value,
        disabled: props.state === FormState.VIEW,
      } as BaseSelectProps,
    },
    {
      type: FormFieldType.AREA,
      name: itemFieldNames.description,
      label: itemFieldLabels.description,
      required: false,
      config: {
        placeholder: placeholders.enter(itemFieldLabels.description),
        disabled: props.state === FormState.VIEW,
      } as any,
    },
    {
      type: FormFieldType.UPLOAD_PICTURES,
      name: itemFieldNames.pictures,
      label: itemFieldLabels.pictures,
      required: false,
      config: {
        disabled: props.state === FormState.VIEW,
      } as any,
    },
  ];

  if (props.state !== FormState.ADD) {
    fields.push({
      type: FormFieldType.SELECT,
      name: itemFieldNames.status,
      label: itemFieldLabels.status,
      required: true,
      config: {
        placeholder: placeholders.select(itemFieldLabels.status),
        options: [
          { label: "Available", value: ItemStatus.AVAILABLE },
          { label: "Rented", value: ItemStatus.RENTED },
          { label: "Maintenance", value: ItemStatus.MAINTENANCE },
        ],
        disabled: props.state === FormState.VIEW,
      } as BaseSelectProps,
    });
  }

  return fields;
});

const upsertItemSchema = toTypedSchema(upsertItemFieldSchema.getSchema());
//#endregion

//#region Services
const formInfo = computed(() => {
  return {
    modalTitle:
      props.state === FormState.ADD
        ? "Add Item"
        : props.state === FormState.EDIT
          ? "Edit Item"
          : "Item Detail",
    successMessage:
      props.state === FormState.ADD
        ? "Add item successfully!"
        : "Edit item successfully!",
    errorMessage:
      props.state === FormState.ADD ? "Add item failed!" : "Edit item failed!",
  };
});

const isPicturesUploading = computed(() => {
  const pictures = formRef.value?.formValues?.pictures;
  if (!pictures || !Array.isArray(pictures)) return false;
  return pictures.some((file: any) => file.status === "uploading");
});

const handleConfirmUpsertItem = async (
  values: AddItemRequest | EditItemRequest,
) => {
  isLoading.value = true;

  const payload = {
    ...values,
    pictures: values.pictures
      ? values.pictures
          .map((file: any) => ({
            url: file.response?.url || file.url,
            note: file.note || "",
          }))
          .filter((pic: any) => pic.url)
      : [],
  };

  try {
    if (props.state === FormState.ADD) {
      await itemService.add(payload as AddItemRequest);
      toast.success(formInfo.value.successMessage);
      emit("onSuccess");
    } else if (props.id) {
      await itemService.edit(props.id, payload as EditItemRequest);
      toast.success(formInfo.value.successMessage);
      emit("onSuccess");
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
  () => isOpen.value,
  async () => {
    if (!isOpen.value) return;

    formRef.value?.resetForm();
    await getAllCategories();

    if (props.state !== FormState.ADD && props.id) {
      const item = await itemService.get(props.id);
      formRef.value?.setValues({
        name: item.name,
        description: item.description,
        categoryId: item.categoryId,
        status: item.status,
        pictures: item.pictures
          ? item.pictures.map((pic: any, index: number) => ({
              uid: `-${index}`,
              name: pic.url.substring(pic.url.lastIndexOf("/") + 1),
              status: "done",
              url: pic.url,
              note: pic.note,
            }))
          : [],
      });
    }
  },
);
//#endregion
</script>

<template>
  <BaseModal
    v-model="isOpen"
    wrapClassName="upsert-item-wrapper"
    confirmText="Save"
    :confirmLoading="isLoading || isPicturesUploading"
    :title="formInfo.modalTitle"
    :footer="props.state === FormState.VIEW ? null : undefined"
    @onConfirm="handleConfirm"
  >
    <BaseForm
      ref="formRef"
      hide-submit-button
      :fields="upsertItemFields"
      :validation-schema="upsertItemSchema"
      @onSubmit="handleConfirmUpsertItem"
    />
  </BaseModal>
</template>
