<script setup lang="ts">
import type { FilterAction } from "~/types/filter";
import type { TablePagination } from "~/types/pagination";
import type {
  TableAction,
  TableAPIParams,
  TableFilterParam,
} from "~/types/table";
import type { Item } from "../types";
import type { ItemStatus } from "#imports";

//#region Config
const dataSource = ref<Item[]>([]);

const pagination = ref<TablePagination>({ ...tablePaginationDefault });

const loading = ref(false);
//#endregion

//#region Filters
const isOpenUpsertModal = ref(false);
const searchValue = ref<string>("");

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
//#endregion

//#region Table actions
const itemService = useItemService();
const formState = ref<FormState>(FormState.ADD);
const itemId = ref<number | undefined>(undefined);

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

const tableActions: TableAction[] = [
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
    onClick: onRemove,
  },
];

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
//#endregion

//#region Life circle
onMounted(() => {
  getItems();
});
//#endregion
</script>

<template>
  <section>
    <BasePageHeader title="Item" />
    <BaseFilter
      class="mb-3"
      searchPlaceholder="Search by name"
      :actions="filterActions"
      @onSearch="handleSearch"
    />
    <BaseTable
      v-model:pagination="pagination"
      :columns="itemColumns"
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
  </section>
</template>
