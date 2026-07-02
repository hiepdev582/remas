<script setup lang="ts">
import {
  AdvancedFilterFieldType,
  type AdvancedFilterConfig,
  type FilterAction,
} from "~/types/filter";
import type { TablePagination } from "~/types/pagination";
import type {
  TableAction,
  TableAPIParams,
  TableFilterParam,
} from "~/types/table";
import type { Item } from "../types";
import type { ItemStatus } from "#imports";
import type { BaseSelectOption } from "~/components/base/Select.vue";

//#region Config
const dataSource = ref<Item[]>([]);

const pagination = ref<TablePagination>({ ...tablePaginationDefault });

const loading = ref(false);

const categoryOptions = ref<BaseSelectOption[]>([]);
const ownerOptions = ref<BaseSelectOption[]>([]);

const itemFiltersConfig = computed<AdvancedFilterConfig[]>(() => [
  {
    key: "categoryId",
    label: itemFieldLabels.categoryId,
    type: AdvancedFilterFieldType.SELECT,
    placeholder: placeholders.select(itemFieldLabels.categoryId),
    options: categoryOptions.value,
    defaultValue: undefined,
  },
  {
    key: "ownerId",
    label: itemFieldLabels.ownerId,
    type: AdvancedFilterFieldType.SELECT,
    placeholder: placeholders.select(itemFieldLabels.ownerId),
    options: ownerOptions.value,
    defaultValue: undefined,
  },
]);
//#endregion

//#region Permission
const { isSuperAdmin } = usePermission();
//#endregion

//#region Filters
const isOpenUpsertModal = ref(false);
const searchValue = ref<string>("");
const filterValues = ref<Record<string, any>>({});

const handleClickAdd = () => {
  formState.value = FormState.ADD;
  isOpenUpsertModal.value = true;
};

const filterActions: FilterAction[] = [
  {
    key: "add",
    icon: "material-symbols:add-rounded",
    title: "Add",
    onClick: handleClickAdd,
  },
];

const handleSearch = (value: string) => {
  searchValue.value = value;
  getItems();
};

const handleFilterChange = (values: Record<string, any>) => {
  filterValues.value = values;
  console.log(filterValues.value);
  getItems();
};
//#endregion

//#region Table actions
const itemService = useItemService();
const categoryService = useCategoryService();
const ownerService = useUserService();
const formState = ref<FormState>(FormState.ADD);
const itemId = ref<number | undefined>(undefined);
const isOpenPricingModal = ref(false);
const selectedItem = ref<Item | undefined>(undefined);

const onPricing = (record: Item) => {
  selectedItem.value = record;
  isOpenPricingModal.value = true;
};

const onView = (record: Item) => {
  itemId.value = record.id;
  formState.value = FormState.VIEW;
  isOpenUpsertModal.value = true;
};

const onEdit = (record: Item) => {
  itemId.value = record.id;
  formState.value = FormState.EDIT;
  isOpenUpsertModal.value = true;
};

const confirmRemove = async (record: Item) => {
  if (!record.id) {
    toast.errorOccured();
    return;
  }

  try {
    await itemService.remove(record.id);
    toast.success("Remove item successfully!");
    getItems();
  } catch (error: any) {
    const errorMessage =
      error.response?._data?.message || "Remove item failed!";
    toast.error(errorMessage);
  }
};

const onRemove = async (record: Item) => {
  confirmModal.showDeleteConfirm(
    "Remove Item",
    `Are you sure remove item "${record.name}"?`,
    () => confirmRemove(record),
  );
};

const tableActions = computed<TableAction[]>(() => [
  {
    key: "view",
    icon: tableAction.view,
    title: "View Detail",
    color: color.info,
    onClick: onView,
  },
  {
    key: "pricing",
    icon: tableAction.pricing,
    title: "Pricing Configuration",
    color: color.success,
    onClick: onPricing,
  },
  {
    key: "edit",
    icon: tableAction.edit,
    title: "Edit",
    onClick: onEdit,
  },
  {
    key: "remove",
    icon: tableAction.remove,
    title: "Remove",
    color: color.error,
    onClick: onRemove,
  },
]);

const handleTableChange = (_: any, filters: any, sorter: any, __: any) => {
  getItems({
    sortField: sorter.order ? sorter.field : "",
    sortOrder: sorter.order,
    filters: Object.entries(filters).map(([key, value]) => ({
      field: key,
      value,
    })) as TableFilterParam[],
  });
};

const getItems = async (tableApiParams?: TableAPIParams) => {
  try {
    loading.value = true;

    const params: TableAPIParams = {
      ...tableApiParams,
      search: searchValue.value,
      page: pagination.value.current,
      pageSize: pagination.value.pageSize,
      ...filterValues.value,
    };

    const response = await itemService.getList(params);
    dataSource.value = response.data;
    pagination.value.total = response.total ?? 0;

    // Nếu trang hiện tại không có dữ liệu và không phải là trang đầu tiên, tự động lùi về trang trước
    if (dataSource.value.length === 0 && pagination.value.current > 1) {
      pagination.value.current = Math.max(1, pagination.value.current - 1);
      await getItems(tableApiParams);
    }
  } finally {
    loading.value = false;
  }
};

const getAllCategories = async () => {
  try {
    const res = await categoryService.getAll();
    categoryOptions.value = res.data.map((c) => ({
      label: c.name,
      value: c.id,
    }));
  } catch (error) {
    console.error("Failed to load categories", error);
  }
};

const getAllOwners = async () => {
  try {
    const res = await ownerService.getAll();
    ownerOptions.value = res.data.map((c) => ({
      label: c.username,
      value: c.id,
    }));
  } catch (error) {
    console.error("Failed to load owners", error);
  }
};
//#endregion

//#region Life circle
onMounted(() => {
  Promise.allSettled([getAllCategories(), getAllOwners(), getItems()]);
});
//#endregion
</script>

<template>
  <section>
    <BasePageHeader title="Item" />
    <BaseFilter
      class="mb-3"
      searchPlaceholder="Search by name"
      :useAdvancedFilter="true"
      :actions="filterActions"
      :filtersConfig="itemFiltersConfig"
      @onSearch="handleSearch"
      @onFilterChange="handleFilterChange"
    />
    <BaseTable
      v-model:pagination="pagination"
      :columns="isSuperAdmin ? superadminItemColumns : adminItemColumns"
      :dataSource
      :loading
      :scroll="{
        y: 'calc(100vh - 48px - 20px - 14px - 50px - 44px - 43px - 64px - 14px - 40px)',
      }"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <div v-if="column.key === 'status'">
          <BaseTag
            :label="record.status"
            :color="itemStatusColor[record.status as ItemStatus]"
          >
            {{ capitalize(record.status) }}
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
              @click="() => action.onClick(record)"
            />
          </div>
        </div>
      </template>
    </BaseTable>
    <!-- Modal -->
    <InventoryItemUpsertModal
      v-model="isOpenUpsertModal"
      :id="itemId"
      :state="formState"
      @onSuccess="getItems()"
    />
    <InventoryItemPricingModal
      v-model="isOpenPricingModal"
      :item="selectedItem"
    />
  </section>
</template>
