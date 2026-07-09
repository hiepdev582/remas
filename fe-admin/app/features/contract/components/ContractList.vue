<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import type { FilterAction } from "~/types/filter";
import type { TablePagination } from "~/types/pagination";
import type {
  TableAction,
  TableAPIParams,
  TableFilterParam,
} from "~/types/table";
import type { Contract } from "../types";
import {
  contractColumns,
  contractStatusColor,
  ContractStatus,
} from "../constants";
import { useContractService } from "../service";
import dayjs from "dayjs";

//#region Config
const dataSource = ref<Contract[]>([]);
const pagination = ref<TablePagination>({ ...tablePaginationDefault });
const loading = ref(false);
//#endregion

//#region Filters
const isOpenUpsertModal = ref(false);
const searchValue = ref<string>("");
const formState = ref<FormState>(FormState.ADD);
const contractId = ref<number | undefined>(undefined);

const handleClickAdd = () => {
  formState.value = FormState.ADD;
  contractId.value = undefined;
  isOpenUpsertModal.value = true;
};

const filterActions = computed<FilterAction[]>(() => [
  {
    key: "add",
    icon: "material-symbols:add-rounded",
    title: "Add",
    onClick: handleClickAdd,
  },
]);

const handleSearch = (value: string) => {
  searchValue.value = value;
  getContracts();
};

const handleTableChange = (_: any, filters: any, sorter: any, __: any) => {
  getContracts({
    sortField: sorter.order ? sorter.field : "",
    sortOrder: sorter.order,
    filters: Object.entries(filters).map(([key, value]) => ({
      field: key,
      value,
    })) as TableFilterParam[],
  });
};
//#endregion

//#region Operations
const contractService = useContractService();

const onView = (contract: Contract) => {
  contractId.value = contract.id;
  formState.value = FormState.VIEW;
  isOpenUpsertModal.value = true;
};

const onEdit = (contract: Contract) => {
  contractId.value = contract.id;
  formState.value = FormState.EDIT;
  isOpenUpsertModal.value = true;
};

const confirmRemove = async (contract: Contract) => {
  if (!contract.id) {
    toast.errorOccured();
    return;
  }

  try {
    await contractService.remove(contract.id);
    toast.success("Cancel contract successfully!");
    getContracts();
  } catch (error: any) {
    const errorMessage =
      error.response?._data?.message || "Cancel contract failed!";
    toast.error(errorMessage);
  }
};

const onRemove = (contract: Contract) => {
  confirmModal.showDeleteConfirm(
    "Cancel Contract",
    `Are you sure you want to cancel this contract?`,
    () => confirmRemove(contract),
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
    key: "edit",
    icon: tableAction.edit,
    title: "Edit",
    onClick: onEdit,
  },
  {
    key: "remove",
    icon: tableAction.remove,
    title: "Cancel Contract",
    color: color.error,
    onClick: onRemove,
  },
]);

const getContracts = async (tableApiParams?: TableAPIParams) => {
  try {
    loading.value = true;

    const params: TableAPIParams = {
      ...tableApiParams,
      search: searchValue.value,
      page: pagination.value.current,
      pageSize: pagination.value.pageSize,
    };

    const response = await contractService.getList(params);
    dataSource.value = response.data;
    pagination.value.total = response.total ?? 0;

    // Auto paginate backward if page gets emptied
    if (dataSource.value.length === 0 && pagination.value.current > 1) {
      pagination.value.current = Math.max(1, pagination.value.current - 1);
      await getContracts(tableApiParams);
    }
  } finally {
    loading.value = false;
  }
};
//#endregion

const formatDateTime = (val: string) => {
  if (!val) return "-";
  return dayjs(val).format("YYYY-MM-DD HH:mm");
};

const getStatusTagColor = (status: ContractStatus) => {
  return contractStatusColor[status] || "default";
};

//#region Life cycle
onMounted(() => {
  getContracts();
});
//#endregion
</script>

<template>
  <section>
    <BasePageHeader title="Contract Management" />
    <BaseFilter
      class="mb-3"
      searchPlaceholder="Search by customer name, phone..."
      :actions="filterActions"
      @onSearch="handleSearch"
    />
    <BaseTable
      v-model:pagination="pagination"
      :columns="contractColumns"
      :dataSource
      :loading
      :scroll="{
        y: 'calc(100vh - 48px - 20px - 14px - 50px - 44px - 43px - 64px - 14px - 40px)',
      }"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <div v-if="column.key === 'startDate'">
          {{ formatDateTime(record.startDate) }}
        </div>

        <div v-if="column.key === 'expectedReturnDate'">
          {{ formatDateTime(record.expectedReturnDate) }}
        </div>

        <div v-if="column.key === 'finalAmount'">
          <span class="font-semibold"
            >{{ record.finalAmount?.toLocaleString() }}đ</span
          >
        </div>

        <div v-if="column.key === 'status'">
          <BaseTag
            :color="getStatusTagColor(record.status)"
            class="font-semibold text-xs py-0.5 px-2"
          >
            {{ record.status }}
          </BaseTag>
        </div>

        <div v-if="column.key === 'action'">
          <div class="flex items-center gap-1">
            <template v-for="action of tableActions" :key="action.key">
              <BaseTableAction
                v-if="
                  action.key !== 'remove' ||
                  record.status !== ContractStatus.CANCELLED
                "
                :title="action.title"
                :icon="action.icon"
                :color="action.color"
                @click="() => action.onClick(record)"
              />
            </template>
          </div>
        </div>
      </template>
    </BaseTable>
    <!-- Modal -->
    <ContractUpsertModal
      v-model="isOpenUpsertModal"
      :id="contractId"
      :state="formState"
      @onSuccess="getContracts"
    />
  </section>
</template>
