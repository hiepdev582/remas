<script setup lang="ts">
import type { BaseSelectProps } from "~/components/base/Select.vue";
import type { BaseDatePickerProps } from "~/components/base/DatePicker.vue";
import type { ContractDetail, ContractCollateral, ContractFee } from "../types";
import {
  contractFieldLabels,
  contractFieldNames,
  contractStatusOptions,
  collateralStatusOptions,
  collateralTypeOptions,
  collateralItemStatusOptions,
  feeTypeOptions,
  ContractStatus,
  CollateralStatus,
  CollateralType,
  CollateralItemStatus,
  FeeType,
} from "../constants";
import { upsertContractFieldSchema } from "../validation";

const props = defineProps<{
  id?: number;
  state: FormState;
}>();

const emit = defineEmits<{ (e: "onSuccess"): void }>();

const isLoading = ref(false);
const contractService = useContractService();
const customerService = useCustomerService();
const itemService = useItemService();

const isOpen = defineModel<boolean>();
const formRef = ref<any>(null);

const customers = ref<any[]>([]);
const items = ref<any[]>([]);

const details = ref<ContractDetail[]>([]);
const collaterals = ref<ContractCollateral[]>([]);
const fees = ref<ContractFee[]>([]);

const fetchDropdowns = async () => {
  try {
    const custRes = await customerService.getAll();
    customers.value = custRes.data;

    const itemRes = await itemService.getAll();
    items.value = itemRes.data;
  } catch (err) {
    console.error("Error loading dropdown data:", err);
  }
};

const addDetail = () => {
  details.value.push({
    itemId: undefined as any,
    quantity: 1,
    appliedPriceType: PriceType.DAILY,
    priceApplied: 0,
    subtotal: 0,
    handoverStatus: "",
  });
};

const removeDetail = (index: number) => {
  details.value.splice(index, 1);
};

const handleItemChange = (index: number, itemId: number) => {
  const selectedItem = items.value.find((i) => i.id === itemId);
  if (selectedItem) {
    const detail = details.value[index];
    if (!detail) return;
    detail.priceApplied = 0;
    detail.subtotal = detail.priceApplied * detail.quantity;
  }
};

const updateDetailSubtotal = (index: number) => {
  const detail = details.value[index];
  if (!detail) return;
  detail.subtotal = (detail.priceApplied || 0) * (detail.quantity || 1);
};

const addCollateral = () => {
  collaterals.value.push({
    collateralType: CollateralType.CASH,
    value: 0,
    assetDescription: "",
    status: CollateralItemStatus.HOLDING,
  });
};

const removeCollateral = (index: number) => {
  collaterals.value.splice(index, 1);
};

const addFee = () => {
  fees.value.push({
    feeType: FeeType.DELIVERY,
    amount: 0,
    pickupLocation: "",
    returnLocation: "",
    note: "",
  });
};

const removeFee = (index: number) => {
  fees.value.splice(index, 1);
};

const totalItemPrice = computed(() => {
  return details.value.reduce((sum, d) => sum + (d.subtotal || 0), 0);
});

const totalFeePrice = computed(() => {
  return fees.value.reduce((sum, f) => sum + (f.amount || 0), 0);
});

const finalAmount = computed(() => {
  return totalItemPrice.value + totalFeePrice.value;
});

const upsertContractFields = computed<FormFieldConfig[]>(() => {
  const fields: FormFieldConfig[] = [
    {
      type: FormFieldType.SELECT,
      name: contractFieldNames.customerId,
      label: contractFieldLabels.customerId,
      required: true,
      config: {
        placeholder: placeholders.select(contractFieldLabels.customerId),
        options: customers.value.map((c) => ({
          label: `${c.name} (${c.phone})`,
          value: c.id,
        })),
        showSearch: true,
        optionFilterProp: "label",
        disabled: props.state === FormState.VIEW,
      } as BaseSelectProps,
    },
  ];

  if (props.state !== FormState.ADD) {
    fields.push({
      type: FormFieldType.SELECT,
      name: contractFieldNames.status,
      label: contractFieldLabels.status,
      required: true,
      config: {
        placeholder: placeholders.select(contractFieldLabels.status),
        options: contractStatusOptions,
        disabled: props.state === FormState.VIEW,
      } as BaseSelectProps,
    });
  }

  fields.push(
    {
      type: FormFieldType.DATE,
      name: contractFieldNames.startDate,
      label: contractFieldLabels.startDate,
      required: true,
      config: {
        placeholder: placeholders.select(contractFieldLabels.startDate),
        disabled: props.state === FormState.VIEW,
      } as BaseDatePickerProps,
    },
    {
      type: FormFieldType.DATE,
      name: contractFieldNames.expectedReturnDate,
      label: contractFieldLabels.expectedReturnDate,
      required: true,
      config: {
        placeholder: placeholders.select(
          contractFieldLabels.expectedReturnDate,
        ),
        disabled: props.state === FormState.VIEW,
      } as BaseDatePickerProps,
    },
    {
      type: FormFieldType.DATE,
      name: contractFieldNames.actualReturnDate,
      label: contractFieldLabels.actualReturnDate,
      required: false,
      config: {
        placeholder: placeholders.select(contractFieldLabels.actualReturnDate),
        disabled: props.state === FormState.VIEW,
      } as BaseDatePickerProps,
    },
    {
      type: FormFieldType.SELECT,
      name: contractFieldNames.collateralStatus,
      label: contractFieldLabels.collateralStatus,
      required: true,
      config: {
        placeholder: placeholders.select(contractFieldLabels.collateralStatus),
        options: collateralStatusOptions,
        disabled: props.state === FormState.VIEW,
      } as BaseSelectProps,
    },
  );

  return fields;
});

const upsertContractSchema = toTypedSchema(
  upsertContractFieldSchema.getGeneralSchema(),
);

const detailColumns = computed(() => {
  const cols = [
    { title: "Item", key: "itemId", dataIndex: "itemId", width: "15%" },
    { title: "Qty", key: "quantity", dataIndex: "quantity", width: "10%" },
    {
      title: "Price Type",
      key: "appliedPriceType",
      dataIndex: "appliedPriceType",
      width: "15%",
    },
    {
      title: "Price",
      key: "priceApplied",
      dataIndex: "priceApplied",
      width: "15%",
    },
    { title: "Subtotal", key: "subtotal", dataIndex: "subtotal", width: "10%" },
    {
      title: "Handover Description",
      key: "handoverStatus",
      dataIndex: "handoverStatus",
      width: "25%",
    },
  ];
  if (props.state !== FormState.VIEW) {
    cols.push({
      title: "",
      key: "action",
      dataIndex: "action",
      width: "50px",
    } as any);
  }
  return cols;
});

const collateralColumns = computed(() => {
  const cols = [
    {
      title: "Type",
      key: "collateralType",
      dataIndex: "collateralType",
      width: "25%",
    },
    { title: "Value", key: "value", dataIndex: "value", width: "20%" },
    {
      title: "Description",
      key: "assetDescription",
      dataIndex: "assetDescription",
      width: "25%",
    },
    { title: "Status", key: "status", dataIndex: "status", width: "20%" },
  ];
  if (props.state !== FormState.VIEW) {
    cols.push({
      title: "",
      key: "action",
      dataIndex: "action",
      width: "50px",
    } as any);
  }
  return cols;
});

const feeColumns = computed(() => {
  const cols = [
    { title: "Fee Type", key: "feeType", dataIndex: "feeType", width: "20%" },
    { title: "Amount", key: "amount", dataIndex: "amount", width: "15%" },
    {
      title: "Pickup Location",
      key: "pickupLocation",
      dataIndex: "pickupLocation",
      width: "20%",
    },
    {
      title: "Return Location",
      key: "returnLocation",
      dataIndex: "returnLocation",
      width: "20%",
    },
    { title: "Note", key: "note", dataIndex: "note", width: "15%" },
  ];
  if (props.state !== FormState.VIEW) {
    cols.push({
      title: "",
      key: "action",
      dataIndex: "action",
      width: "50px",
    } as any);
  }
  return cols;
});

const handleConfirm = () => {
  formRef.value?.onSubmit();
};

const handleConfirmUpsertContract = async (formValues: any) => {
  if (details.value.length === 0) {
    toast.error("Please add at least one item to lease");
    return;
  }

  isLoading.value = true;
  const payload = {
    customerId: formValues.customerId,
    startDate: formValues.startDate,
    expectedReturnDate: formValues.expectedReturnDate,
    actualReturnDate: formValues.actualReturnDate || undefined,
    collateralStatus: formValues.collateralStatus || CollateralStatus.NONE,
    status: formValues.status || ContractStatus.RESERVED,
    details: details.value.map((d) => ({
      itemId: d.itemId,
      quantity: d.quantity,
      appliedPriceType: d.appliedPriceType,
      priceApplied: d.priceApplied,
      subtotal: d.subtotal,
      handoverStatus: d.handoverStatus,
    })),
    collaterals: collaterals.value.map((c) => ({
      collateralType: c.collateralType,
      value: c.value,
      assetDescription: c.assetDescription,
      status: c.status,
    })),
    fees: fees.value.map((f) => ({
      feeType: f.feeType,
      amount: f.amount,
      pickupLocation: f.pickupLocation,
      returnLocation: f.returnLocation,
      note: f.note,
    })),
  };

  try {
    if (props.state === FormState.ADD) {
      await contractService.add(payload as any);
      toast.success("Create contract successfully!");
      emit("onSuccess");
      isOpen.value = false;
    } else if (props.id) {
      await contractService.edit(props.id, payload as any);
      toast.success("Update contract successfully!");
      emit("onSuccess");
      isOpen.value = false;
    }
  } catch (error: any) {
    const apiMessage = error.response?._data?.message || "Operation failed!";
    toast.error(apiMessage);
  } finally {
    isLoading.value = false;
  }
};

const resetForm = () => {
  formRef.value?.resetForm();
  details.value = [];
  collaterals.value = [];
  fees.value = [];
};

const getDetailData = async () => {
  isLoading.value = true;
  await fetchDropdowns();

  if (props.state !== FormState.ADD && props.id) {
    try {
      const contract = await contractService.get(props.id);
      formRef.value?.setValues({
        customerId: contract.customerId,
        startDate: contract.startDate || "",
        expectedReturnDate: contract.expectedReturnDate || "",
        actualReturnDate: contract.actualReturnDate || "",
        collateralStatus: contract.collateralStatus,
        status: contract.status,
      });

      details.value = (contract.details || []).map((d) => ({
        itemId: d.itemId,
        quantity: d.quantity,
        appliedPriceType: d.appliedPriceType,
        priceApplied: d.priceApplied,
        subtotal: d.subtotal,
        handoverStatus: d.handoverStatus || "",
      }));

      collaterals.value = (contract.collaterals || []).map((c) => ({
        collateralType: c.collateralType,
        value: c.value,
        assetDescription: c.assetDescription || "",
        status: c.status,
      }));

      fees.value = (contract.fees || []).map((f) => ({
        feeType: f.feeType,
        amount: f.amount,
        pickupLocation: f.pickupLocation || "",
        returnLocation: f.returnLocation || "",
        note: f.note || "",
      }));
    } catch (err) {
      console.error("Failed to load contract details", err);
    }
  }
  isLoading.value = false;
};

watch(
  () => isOpen.value,
  async () => {
    if (!isOpen.value) resetForm();

    await getDetailData();
  },
);

const modalTitle = computed(() => {
  if (props.state === FormState.ADD) return "Add Contract";
  if (props.state === FormState.EDIT) return "Edit Contract";
  return "Contract Details";
});
</script>

<template>
  <BaseModal
    v-model="isOpen"
    width="1000px"
    confirmText="Save"
    wrapClassName="upsert-contract-wrapper"
    :title="modalTitle"
    :confirmLoading="isLoading"
    :footer="props.state === FormState.VIEW ? null : undefined"
    @onConfirm="handleConfirm"
  >
    <BaseForm
      ref="formRef"
      hide-submit-button
      class="mt-4 grid grid-cols-1 md:grid-cols-2 gap-x-4 gap-y-1"
      :fields="upsertContractFields"
      :validation-schema="upsertContractSchema"
      @onSubmit="handleConfirmUpsertContract"
    />

    <div class="mt-2">
      <!-- Details (Items) -->
      <div class="mt-6">
        <div class="flex justify-between items-center mb-2">
          <h3 class="section-title m-0">Leased Items</h3>
          <BaseButton v-if="props.state !== FormState.VIEW" @click="addDetail">
            Add Item
          </BaseButton>
        </div>

        <BaseTable
          size="small"
          :dataSource="details"
          :columns="detailColumns"
          :pagination="false"
        >
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.key === 'itemId'">
              <BaseSelect
                v-model="record.itemId"
                class="w-full"
                :placeholder="placeholders.select(contractFieldLabels.itemId)"
                :options="
                  items.map((item) => ({
                    label: item.name,
                    value: item.id,
                  }))
                "
                :disabled="props.state === FormState.VIEW"
                @change="(val: any) => handleItemChange(index, val)"
              />
            </template>
            <template v-else-if="column.key === 'quantity'">
              <BaseInputNumber
                v-model="record.quantity"
                class="w-full"
                :min="1"
                :disabled="props.state === FormState.VIEW"
                @change="() => updateDetailSubtotal(index)"
              />
            </template>
            <template v-else-if="column.key === 'appliedPriceType'">
              <BaseSelect
                v-model="record.appliedPriceType"
                class="w-full"
                :options="priceTypeOptions"
                :disabled="props.state === FormState.VIEW"
              />
            </template>
            <template v-else-if="column.key === 'priceApplied'">
              <BaseInputNumber
                v-model="record.priceApplied"
                class="w-full"
                :min="0"
                :step="1000"
                :disabled="props.state === FormState.VIEW"
                @change="() => updateDetailSubtotal(index)"
              />
            </template>
            <template v-else-if="column.key === 'subtotal'">
              <span class="font-semibold"
                >{{ record.subtotal.toLocaleString() }}đ</span
              >
            </template>
            <template v-else-if="column.key === 'handoverStatus'">
              <BaseInput
                v-model="record.handoverStatus"
                :placeholder="
                  placeholders.enter(contractFieldLabels.handoverStatus)
                "
                :disabled="props.state === FormState.VIEW"
              />
            </template>
            <template v-else-if="column.key === 'action'">
              <BaseTableAction
                title="Remove"
                icon="material-symbols:delete-outline-rounded"
                color="#ef4444"
                :onClick="() => removeDetail(index)"
              />
            </template>
          </template>
        </BaseTable>
      </div>

      <!-- Collaterals -->
      <div class="mt-6">
        <div class="flex justify-between items-center mb-2">
          <h3 class="section-title m-0">Collaterals</h3>
          <BaseButton
            v-if="props.state !== FormState.VIEW"
            @click="addCollateral"
          >
            Add Collateral
          </BaseButton>
        </div>

        <BaseTable
          size="small"
          :dataSource="collaterals"
          :columns="collateralColumns"
          :pagination="false"
        >
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.key === 'collateralType'">
              <BaseSelect
                v-model="record.collateralType"
                class="w-full"
                :options="collateralTypeOptions"
                :disabled="props.state === FormState.VIEW"
              />
            </template>
            <template v-else-if="column.key === 'value'">
              <BaseInputNumber
                v-model="record.value"
                class="w-full"
                :min="0"
                :step="1000"
                :disabled="props.state === FormState.VIEW"
              />
            </template>
            <template v-else-if="column.key === 'assetDescription'">
              <BaseInput
                v-model="record.assetDescription"
                :placeholder="
                  placeholders.enter(contractFieldLabels.assetDescription)
                "
                :disabled="props.state === FormState.VIEW"
              />
            </template>
            <template v-else-if="column.key === 'status'">
              <BaseSelect
                v-model="record.status"
                class="w-full"
                :options="collateralItemStatusOptions"
                :disabled="props.state === FormState.VIEW"
              />
            </template>
            <template v-else-if="column.key === 'action'">
              <BaseTableAction
                title="Remove"
                icon="material-symbols:delete-outline-rounded"
                color="#ef4444"
                :onClick="() => removeCollateral(index)"
              />
            </template>
          </template>
        </BaseTable>
      </div>

      <!-- Fees -->
      <div class="mt-6">
        <div class="flex justify-between items-center mb-2">
          <h3 class="section-title m-0">Additional Fees</h3>
          <BaseButton v-if="props.state !== FormState.VIEW" @click="addFee">
            Add Fee
          </BaseButton>
        </div>

        <BaseTable
          size="small"
          :dataSource="fees"
          :columns="feeColumns"
          :pagination="false"
        >
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.key === 'feeType'">
              <BaseSelect
                v-model="record.feeType"
                class="w-full"
                :options="feeTypeOptions"
                :disabled="props.state === FormState.VIEW"
              />
            </template>
            <template v-else-if="column.key === 'amount'">
              <BaseInputNumber
                v-model="record.amount"
                class="w-full"
                :min="0"
                :step="1000"
                :disabled="props.state === FormState.VIEW"
              />
            </template>
            <template v-else-if="column.key === 'pickupLocation'">
              <BaseInput
                v-model="record.pickupLocation"
                :placeholder="
                  placeholders.enter(contractFieldLabels.pickupLocation)
                "
                :disabled="props.state === FormState.VIEW"
              />
            </template>
            <template v-else-if="column.key === 'returnLocation'">
              <BaseInput
                v-model="record.returnLocation"
                :placeholder="
                  placeholders.enter(contractFieldLabels.returnLocation)
                "
                :disabled="props.state === FormState.VIEW"
              />
            </template>
            <template v-else-if="column.key === 'note'">
              <BaseInput
                v-model="record.note"
                :placeholder="placeholders.enter(contractFieldLabels.note)"
                :disabled="props.state === FormState.VIEW"
              />
            </template>
            <template v-else-if="column.key === 'action'">
              <BaseTableAction
                title="Remove"
                icon="material-symbols:delete-outline-rounded"
                color="#ef4444"
                :onClick="() => removeFee(index)"
              />
            </template>
          </template>
        </BaseTable>
      </div>

      <!-- Pricing Summary -->
      <div class="mt-6 border-t pt-4 flex flex-col items-end gap-1">
        <div class="text-sm">
          Total Items Price:
          <span class="font-semibold"
            >{{ totalItemPrice.toLocaleString() }}đ</span
          >
        </div>
        <div class="text-sm">
          Total Fees:
          <span class="font-semibold"
            >{{ totalFeePrice.toLocaleString() }}đ</span
          >
        </div>
        <div class="text-lg font-bold text-primary mt-1">
          Final Amount: {{ finalAmount.toLocaleString() }}đ
        </div>
      </div>
    </div>
  </BaseModal>
</template>

<style lang="css" scoped>
.section-title {
  font-size: 15px;
  font-weight: 600;
  margin-top: 16px;
  padding-left: 8px;
  margin-bottom: 8px;
  color: var(--color-primary);
  border-left: 3px solid var(--color-primary);
}
</style>
