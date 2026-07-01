<script setup lang="ts">
import type { PriceType } from "../constants";
import type { Item, ItemPricing } from "../types";
import PricingUpsertModal from "./PricingUpsertModal.vue";

const props = defineProps<{
  item?: Item;
}>();

const isOpen = defineModel<boolean>();

const loading = ref(false);
const itemService = useItemService();
const pricings = ref<ItemPricing[]>([]);

//#region Sub-modal state
const isOpenUpsertModal = ref(false);
const selectedPricingId = ref<number | null>(null);
const formState = ref<FormState>(FormState.ADD);

const tableActions = computed(() => [
  {
    key: "edit",
    icon: tableAction.edit,
    title: "Edit",
    onClick: handleEdit,
  },
  {
    key: "remove",
    icon: tableAction.remove,
    title: "Remove",
    color: color.error,
    onClick: handleDelete,
  },
]);
//#endregion

//#region Methods
const getPricings = async () => {
  if (!props.item?.id) return;
  try {
    loading.value = true;
    const res = await itemService.getPricings(props.item.id);
    pricings.value = res;
  } catch (error) {
    console.error("Failed to load pricings", error);
  } finally {
    loading.value = false;
  }
};

const handleAdd = () => {
  selectedPricingId.value = null;
  formState.value = FormState.ADD;
  isOpenUpsertModal.value = true;
};

const handleEdit = (record: ItemPricing) => {
  selectedPricingId.value = record.id;
  formState.value = FormState.EDIT;
  isOpenUpsertModal.value = true;
};

const handleDeleteConfig = async (record: ItemPricing) => {
  try {
    await itemService.removePricing(props.item!.id!, record.id);
    toast.success("Removed price configuration successfully!");
    await getPricings();
  } catch (error: any) {
    const msg =
      error.response?._data?.message || "Failed to remove price configuration!";
    toast.error(msg);
  }
};

const handleDelete = async (record: ItemPricing) => {
  if (!props.item?.id) return;
  confirmModal.showDeleteConfirm(
    "Remove Price Config",
    `Are you sure you want to remove the ${priceTypeLabels[record.priceType]} pricing?`,
    async () => handleDeleteConfig(record),
  );
};

watch(
  () => isOpen.value,
  (val) => {
    if (val) {
      getPricings();
    }
  },
);
//#endregion
</script>

<template>
  <BaseModal
    v-model="isOpen"
    width="800px"
    :title="`Pricing Configuration - ${item?.name || ''}`"
    :footer="null"
  >
    <div class="space-y-6">
      <!-- Header Action -->
      <div class="flex justify-end">
        <BaseButton class="flex items-center gap-1 rounded" @click="handleAdd">
          <Icon name="material-symbols:add-rounded" class="text-lg" />
          Add Configuration
        </BaseButton>
      </div>

      <!-- Danh sách cấu hình giá -->
      <BaseTable
        bordered
        size="small"
        :dataSource="pricings"
        :columns="itemPricingColumns"
        :loading="loading"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <div v-if="column.key === 'priceType'">
            <BaseTag color="blue" class="font-medium">
              {{ priceTypeLabels[record.priceType as PriceType] }}
            </BaseTag>
          </div>
          <div
            v-if="column.key === 'price'"
            class="font-semibold text-gray-800"
          >
            {{ formatNumber(record.price) }} VND
          </div>
          <div v-if="column.key === 'suggestedDeposit'" class="text-gray-600">
            {{
              record.suggestedDeposit
                ? `${formatNumber(record.suggestedDeposit)} VND`
                : "0 VND"
            }}
          </div>
          <div v-if="column.key === 'isActive'">
            <BaseTag
              :label="record.isActive ? 'Active' : 'Inactive'"
              :color="record.isActive ? 'success' : 'error'"
            >
              {{ record.isActive ? "Active" : "Inactive" }}
            </BaseTag>
          </div>
          <div v-if="column.key === 'action'">
            <div class="flex items-center gap-1">
              <BaseTableAction
                v-for="action of tableActions"
                :key="action.key"
                :title="action.title"
                :icon="action.icon"
                :color="action.color"
                @click="() => action.onClick(record as ItemPricing)"
              />
            </div>
          </div>
        </template>
      </BaseTable>
    </div>

    <!-- Sub-modal for Add/Edit Price -->
    <PricingUpsertModal
      v-model="isOpenUpsertModal"
      :id="selectedPricingId"
      :itemId="item?.id"
      :state="formState"
      @onSuccess="getPricings"
    />
  </BaseModal>
</template>
