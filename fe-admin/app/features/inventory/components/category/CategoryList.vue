<script setup lang="ts">
import type { FilterAction } from "~/types/filter";
import type { TablePagination } from "~/types/pagination";
import type {
  TableAction,
  TableAPIParams,
  TableFilterParam,
} from "~/types/table";
import type { Category } from "../../types";

//#region Config
const dataSource = ref<Category[]>([]);

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
  getCategories();
};
//#endregion

//#region Table actions
const categoryService = useCategoryService();
const formState = ref<FormState>(FormState.ADD);
const categoryId = ref<number | undefined>(undefined);

const onEdit = (record: Category) => {
  console.log("Edit", record);

  categoryId.value = record.id;
  formState.value = FormState.EDIT;
  isOpenUpsertModal.value = true;
};

const confirmRemove = async (record: Category) => {
  if (!record.id) {
    toast.errorOccured();
    return;
  }

  try {
    const response = await categoryService.remove(record.id);
    toast.success(response || "Remove category successfully!");
  } catch (error: any) {
    const errorMessage =
      error.response?._data?.message || "Remove category failed!";
    toast.error(errorMessage);
  }
};

const onRemove = async (record: Category) => {
  console.log("Remove", record);

  confirmModal.showDeleteConfirm(
    "Remove Category",
    `Are you sure remove category "${record.name}"?`,
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
  getCategories({
    sortField: sorter.field,
    sortOrder: sorter.order,
    filters: Object.entries(filters).map(([key, value]) => ({
      field: key,
      value,
    })) as TableFilterParam[],
  });
};

const getCategories = async (tableApiParams?: TableAPIParams) => {
  try {
    loading.value = true;

    const params: TableAPIParams = {
      ...tableApiParams,
      search: searchValue.value,
      page: pagination.value.current,
      pageSize: pagination.value.pageSize,
    };

    const response = await categoryService.getList(params);
    dataSource.value = response.data;
    pagination.value.total = response.total ?? 0;
  } finally {
    loading.value = false;
  }
};
//#endregion

//#region Life circle
onMounted(() => {
  getCategories();
});
//#endregion
</script>

<template>
  <section>
    <BasePageHeader title="Category" />
    <BaseFilter
      class="mb-3"
      searchPlaceholder="Search by name or description"
      :actions="filterActions"
      @onSearch="handleSearch"
    />
    <BaseTable
      v-model:pagination="pagination"
      :columns="categoryColumns"
      :dataSource
      :loading
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
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
    <InventoryCategoryUpsertModal
      v-model="isOpenUpsertModal"
      :id="categoryId"
      :state="formState"
    />
  </section>
</template>
