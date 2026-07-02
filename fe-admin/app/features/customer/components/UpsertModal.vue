<script setup lang="ts">
import type { BaseInputProps } from "~/components/base/Input.vue";
import type { BaseInputNumberProps } from "~/components/base/InputNumber.vue";
import type { BaseUploadDocumentsProps } from "~/components/base/UploadDocuments.vue";
import { upsertCustomerFieldSchema } from "../validation";
import type {
  AddCustomerRequest,
  EditCustomerRequest,
  Customer,
} from "../types";

const props = defineProps<{
  id?: number;
  state: FormState;
}>();

const emit = defineEmits<{ (e: "onSuccess"): void }>();

const isLoading = ref(false);
const customerService = useCustomerService();
const customerData = ref<Customer | null>(null);

const isOpen = defineModel<boolean>();
const formRef = ref<any>(null);

const handleConfirm = () => {
  formRef.value?.onSubmit();
};

const upsertCustomerFields = computed<FormFieldConfig[]>(() => {
  const fields: FormFieldConfig[] = [
    {
      type: FormFieldType.TEXT,
      name: customerFieldNames.name,
      label: customerFieldLabels.name,
      required: true,
      config: {
        placeholder: placeholders.enter(customerFieldLabels.name),
        disabled: props.state === FormState.VIEW,
      } as BaseInputProps,
    },
    {
      type: FormFieldType.TEXT,
      name: customerFieldNames.phone,
      label: customerFieldLabels.phone,
      required: true,
      config: {
        placeholder: placeholders.enter(customerFieldLabels.phone),
        disabled: props.state === FormState.VIEW,
      } as BaseInputProps,
    },
    {
      type: FormFieldType.TEXT,
      name: customerFieldNames.identityCard,
      label: customerFieldLabels.identityCard,
      required: false,
      config: {
        placeholder: placeholders.enter(customerFieldLabels.identityCard),
        disabled: props.state === FormState.VIEW,
      } as BaseInputProps,
    },
    {
      type: FormFieldType.TEXT,
      name: customerFieldNames.driverLicense,
      label: customerFieldLabels.driverLicense,
      required: false,
      config: {
        placeholder: placeholders.enter(customerFieldLabels.driverLicense),
        disabled: props.state === FormState.VIEW,
      } as BaseInputProps,
    },
  ];

  if (props.state !== FormState.ADD) {
    fields.push({
      type: FormFieldType.NUMBER,
      name: customerFieldNames.trustScore,
      label: customerFieldLabels.trustScore,
      required: false,
      config: {
        placeholder: placeholders.enter(customerFieldLabels.trustScore),
        min: 0,
        max: 100,
        disabled: props.state === FormState.VIEW,
      } as BaseInputNumberProps,
    });
  }

  fields.push({
    type: FormFieldType.UPLOAD_DOCUMENTS,
    name: customerFieldNames.documents,
    label: customerFieldLabels.documents,
    required: false,
    config: {
      disabled: props.state === FormState.VIEW,
      limitFile: 8,
    } as BaseUploadDocumentsProps,
  });

  return fields;
});

const upsertCustomerSchema = toTypedSchema(
  upsertCustomerFieldSchema.getSchema(),
);

const formInfo = computed(() => {
  return {
    modalTitle:
      props.state === FormState.ADD
        ? "Add Customer"
        : props.state === FormState.EDIT
          ? "Edit Customer"
          : "Customer Details",
    successMessage:
      props.state === FormState.ADD
        ? "Add customer successfully!"
        : "Edit customer successfully!",
    errorMessage:
      props.state === FormState.ADD
        ? "Add customer failed!"
        : "Edit customer failed!",
  };
});

const handleConfirmUpsertCustomer = async (values: any) => {
  isLoading.value = true;

  try {
    const payload = {
      ...values,
      documents: values.documents
        ? values.documents.map((file: any) => ({
            documentType: file.documentType || "OTHER",
            documentNumber: file.documentNumber || "",
            imageUrl: file.url || file.response?.url,
          }))
        : [],
    };

    if (props.state === FormState.ADD) {
      await customerService.add(payload as AddCustomerRequest);
      toast.success(formInfo.value.successMessage);
      emit("onSuccess");
    } else if (props.id) {
      await customerService.edit(props.id, payload as EditCustomerRequest);
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
    if (!isOpen.value) {
      customerData.value = null;
      return;
    }

    formRef.value?.resetForm();

    if (props.state !== FormState.ADD && props.id) {
      try {
        isLoading.value = true;
        const customer = await customerService.get(props.id);
        customerData.value = customer;

        const documents =
          customer.documents?.map((d: any) => ({
            uid: d.id.toString(),
            name: d.documentType,
            status: "done",
            url: d.imageUrl,
            documentType: d.documentType,
            documentNumber: d.documentNumber,
          })) || [];

        formRef.value?.setValues({
          ...customer,
          documents,
        });
      } catch (error) {
        console.error("Failed to load customer details", error);
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
    wrapClassName="upsert-customer-wrapper"
    confirmText="Save"
    :confirmLoading="isLoading"
    :footer="props.state === FormState.VIEW ? null : undefined"
    @onConfirm="handleConfirm"
  >
    <template #title>
      <div class="flex items-center gap-2">
        <span>{{ formInfo.modalTitle }}</span>
        <BaseTag
          v-if="props.state === FormState.VIEW && customerData"
          :color="
            customerData.trustScore >= 80
              ? 'success'
              : customerData.trustScore >= 50
                ? 'warning'
                : 'error'
          "
          class="font-semibold py-0.5 px-2"
        >
          Score: {{ customerData.trustScore }}
        </BaseTag>
      </div>
    </template>

    <BaseForm
      ref="formRef"
      hide-submit-button
      class="mt-4"
      :fields="upsertCustomerFields"
      :validation-schema="upsertCustomerSchema"
      @onSubmit="handleConfirmUpsertCustomer"
    />
  </BaseModal>
</template>
