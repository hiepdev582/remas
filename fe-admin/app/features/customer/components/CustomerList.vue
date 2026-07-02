<script setup lang="ts">
import type { FilterAction } from "~/types/filter";
import type { TablePagination } from "~/types/pagination";
import type {
  TableAction,
  TableAPIParams,
  TableFilterParam,
} from "~/types/table";
import type { Customer } from "../types";
import UpsertModal from "./UpsertModal.vue";

//#region Config
const dataSource = ref<Customer[]>([]);
const pagination = ref<TablePagination>({ ...tablePaginationDefault });
const loading = ref(false);
//#endregion

//#region Filters
const isOpenUpsertModal = ref(false);
const searchValue = ref<string>("");
const formState = ref<FormState>(FormState.ADD);
const selectedCustomerId = ref<number | undefined>(undefined);

const handleClickAdd = () => {
  formState.value = FormState.ADD;
  selectedCustomerId.value = undefined;
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
  getCustomers();
};
//#endregion

//#region Table actions
const customerService = useCustomerService();

const onViewDetails = (record: Customer) => {
  selectedCustomerId.value = record.id;
  formState.value = FormState.VIEW;
  isOpenUpsertModal.value = true;
};

const onEdit = (record: Customer) => {
  selectedCustomerId.value = record.id;
  formState.value = FormState.EDIT;
  isOpenUpsertModal.value = true;
};

const onDelete = async (record: Customer) => {
  if (!record.id) {
    toast.errorOccured();
    return;
  }

  try {
    loading.value = true;
    await customerService.remove(record.id);
    toast.success("Deleted customer successfully!");
    getCustomers();
  } catch (error: any) {
    const errorMessage =
      error.response?._data?.message || "Delete customer failed!";
    toast.error(errorMessage);
  } finally {
    loading.value = false;
  }
};

const tableActions = computed<TableAction[]>(() => [
  {
    key: "view",
    icon: tableAction.view,
    title: "View Details",
    color: color.info,
    onClick: onViewDetails,
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
    title: "Delete",
    color: color.error,
    onClick: onDelete,
  },
]);

const handleTableChange = (_: any, filters: any, sorter: any, __: any) => {
  getCustomers({
    sortField: sorter.order ? sorter.field : "",
    sortOrder: sorter.order,
    filters: Object.entries(filters).map(([key, value]) => ({
      field: key,
      value,
    })) as TableFilterParam[],
  });
};

const getCustomers = async (tableApiParams?: TableAPIParams) => {
  try {
    loading.value = true;

    const params: TableAPIParams = {
      ...tableApiParams,
      search: searchValue.value,
      page: pagination.value.current,
      pageSize: pagination.value.pageSize,
    };

    const response = await customerService.getList(params);
    dataSource.value = response.data;
    pagination.value.total = response.total ?? 0;

    // Auto paginate backward if page gets emptied
    if (dataSource.value.length === 0 && pagination.value.current > 1) {
      pagination.value.current = Math.max(1, pagination.value.current - 1);
      await getCustomers(tableApiParams);
    }
  } finally {
    loading.value = false;
  }
};
//#endregion

//#region Life cycle
onMounted(() => {
  getCustomers();
});
//#endregion
</script>

<template>
  <section>
    <BasePageHeader title="Customer Management" />
    <BaseFilter
      class="mb-3"
      searchPlaceholder="Search by customer name, phone, CCCD..."
      :actions="filterActions"
      @onSearch="handleSearch"
    />
    <BaseTable
      v-model:pagination="pagination"
      :columns="customerColumns"
      :dataSource
      :loading
      :scroll="{
        y: 'calc(100vh - 48px - 20px - 14px - 50px - 44px - 43px - 64px - 14px - 40px)',
      }"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <div v-if="column.key === 'trustScore'">
          <BaseTag
            :color="
              record.trustScore >= 80
                ? 'success'
                : record.trustScore >= 50
                  ? 'warning'
                  : 'error'
            "
            class="font-semibold text-xs py-0.5 px-2"
          >
            {{ record.trustScore }} / 100
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
    <UpsertModal
      v-model="isOpenUpsertModal"
      :id="selectedCustomerId"
      :state="formState"
      @onSuccess="getCustomers"
    />
  </section>
</template>
