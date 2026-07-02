<script setup lang="ts">
import type { ItemPricingRequest } from "../types";
import { upsertItemPricingSchema } from "../validation";
import type { BaseSelectProps } from "~/components/base/Select.vue";
import type { BaseInputNumberProps } from "~/components/base/InputNumber.vue";
import type { BaseSwitchProps } from "~/components/base/Switch.vue";
import {
  numberFormatterRegex,
  numberParserRegex,
} from "~/constants/validation";

const props = defineProps<{
  id?: number | null;
  itemId?: number;
  state: FormState;
}>();

const emit = defineEmits<{ (e: "onSuccess"): void }>();

const isOpen = defineModel<boolean>();
const formRef = ref<any>(null);
const isLoading = ref(false);
const itemService = useItemService();

const handleConfirm = () => {
  formRef.value?.onSubmit();
};

const fields = computed<FormFieldConfig[]>(() => [
  {
    type: FormFieldType.SELECT,
    name: itemPricingFieldNames.priceType,
    label: itemPricingFieldLabels.priceType,
    required: true,
    config: {
      placeholder: placeholders.select(itemPricingFieldLabels.priceType),
      options: priceTypeOptions,
      disabled: props.state === FormState.VIEW || props.id !== null,
    } as BaseSelectProps,
  },
  {
    type: FormFieldType.NUMBER,
    name: itemPricingFieldNames.price,
    label: itemPricingFieldLabels.price,
    required: true,
    config: {
      placeholder: placeholders.enter(itemPricingFieldLabels.price),
      min: 1000,
      step: 1000,
      disabled: props.state === FormState.VIEW,
      formatter: (val: any) => `${val}`.replace(numberFormatterRegex, ","),
      parser: (val: any) => val.replace(numberParserRegex, ""),
    } as BaseInputNumberProps,
  },
  {
    type: FormFieldType.NUMBER,
    name: itemPricingFieldNames.suggestedDeposit,
    label: itemPricingFieldLabels.suggestedDeposit,
    required: false,
    config: {
      placeholder: placeholders.enter(itemPricingFieldLabels.suggestedDeposit),
      min: 0,
      step: 1000,
      disabled: props.state === FormState.VIEW,
      formatter: (val: any) => `${val}`.replace(numberFormatterRegex, ","),
      parser: (val: any) => val.replace(numberParserRegex, ""),
    } as BaseInputNumberProps,
  },
  {
    type: FormFieldType.SWITCH,
    name: itemPricingFieldNames.isActive,
    label: "Active",
    required: false,
    config: {
      disabled: props.state === FormState.VIEW,
    } as BaseSwitchProps,
  },
]);

const formInfo = computed(() => {
  return {
    modalTitle:
      props.state === FormState.ADD
        ? "Add Price Configuration"
        : props.state === FormState.EDIT
          ? "Edit Price Configuration"
          : "Price Configuration Detail",
    successMessage:
      props.state === FormState.ADD
        ? "Added price configuration successfully!"
        : "Updated price configuration successfully!",
    errorMessage:
      props.state === FormState.ADD
        ? "Failed to add price configuration!"
        : "Failed to update price configuration!",
  };
});

const pricingSchema = toTypedSchema(upsertItemPricingSchema);

const handleConfirmUpsert = async (values: ItemPricingRequest) => {
  if (!props.itemId) return;
  isLoading.value = true;

  try {
    const payload = {
      ...values,
      suggestedDeposit: values.suggestedDeposit || 0,
    };

    if (props.state === FormState.ADD) {
      await itemService.addPricing(props.itemId, payload);
    } else if (props.id) {
      await itemService.editPricing(props.itemId, props.id, payload);
    }

    toast.success(formInfo.value.successMessage);
    emit("onSuccess");
    isOpen.value = false;
  } catch (error: any) {
    const msg = error.response?._data?.message || formInfo.value.errorMessage;
    toast.error(msg);
  } finally {
    isLoading.value = false;
  }
};

watch(
  () => isOpen.value,
  async (val) => {
    if (!val) return;

    formRef.value?.resetForm();

    if (props.state !== FormState.ADD && props.id && props.itemId) {
      try {
        isLoading.value = true;
        const res = await itemService.getPricings(props.itemId);
        const pricing = res.find((p) => p.id === props.id);
        if (pricing) {
          formRef.value?.setValues({
            [itemPricingFieldNames.priceType]: pricing.priceType,
            [itemPricingFieldNames.price]: pricing.price,
            [itemPricingFieldNames.suggestedDeposit]:
              pricing.suggestedDeposit ?? 0,
            [itemPricingFieldNames.isActive]: pricing.isActive,
          });
        }
      } catch (error) {
        console.error("Failed to load pricing config details", error);
      } finally {
        isLoading.value = false;
      }
    }
  },
);
</script>

<template>
  <BaseModal
    v-model="isOpen"
    confirmText="Save"
    :title="formInfo.modalTitle"
    :confirmLoading="isLoading"
    :footer="props.state === FormState.VIEW ? null : undefined"
    @onConfirm="handleConfirm"
  >
    <BaseForm
      ref="formRef"
      hide-submit-button
      :fields="fields"
      :validation-schema="pricingSchema"
      @onSubmit="handleConfirmUpsert"
    />
  </BaseModal>
</template>
