<script setup lang="ts">
import type { TablePagination } from "~/types/pagination";
import type { TableAPIParams } from "~/types/table";
import type { AuditLog } from "../types";
import { auditColumns } from "../constants";
import { useAuditService } from "../service";

const auditService = useAuditService();
const dataSource = ref<AuditLog[]>([]);
const pagination = ref<TablePagination>({ ...tablePaginationDefault });
const loading = ref(false);
const searchValue = ref("");

const handleSearch = (value: string) => {
  searchValue.value = value;
  pagination.value.current = 1;
  getAuditLogs();
};

const handleTableChange = (_: any, __: any, sorter: any) => {
  getAuditLogs({
    sortField: sorter.order ? sorter.field : "",
    sortOrder: sorter.order,
  });
};

const getAuditLogs = async (tableApiParams?: TableAPIParams) => {
  try {
    loading.value = true;
    const response = await auditService.getAuditLogs({
      page: pagination.value.current,
      pageSize: pagination.value.pageSize,
      search: searchValue.value,
      sortField: tableApiParams?.sortField,
      sortOrder: tableApiParams?.sortOrder,
    });
    dataSource.value = response.data as any;
    pagination.value.total = response.total ?? 0;
  } catch (error) {
    console.error("Failed to load audit logs", error);
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return "";
  const date = new Date(dateStr);
  return date.toLocaleString("vi-VN");
};

const getActionColor = (action: string) => {
  if (action.startsWith("CREATE")) return "success";
  if (action.startsWith("UPDATE")) return "processing";
  if (action.startsWith("DELETE") || action.startsWith("CANCEL"))
    return "error";
  if (action === "LOGIN") return "warning";
  return "default";
};

onMounted(() => {
  getAuditLogs();
});
</script>

<template>
  <section>
    <BasePageHeader title="System Audit Logs" />
    <BaseFilter
      class="mb-3"
      searchPlaceholder="Search by username, description, IP address..."
      @onSearch="handleSearch"
    />
    <BaseTable
      v-model:pagination="pagination"
      :columns="auditColumns"
      :dataSource
      :loading
      :scroll="{
        y: 'calc(100vh - 48px - 20px - 14px - 50px - 44px - 43px - 64px - 14px - 40px)',
      }"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <!-- Time Column -->
        <span v-if="column.key === 'createdAt'">
          {{ formatDate(record.createdAt) }}
        </span>

        <!-- User Column -->
        <div v-if="column.key === 'user'" class="flex flex-col">
          <span class="text-sm font-semibold text-slate-700">{{
            record.userFullName
          }}</span>
          <span class="text-xs text-slate-400">@{{ record.username }}</span>
        </div>

        <!-- Action tag -->
        <div v-if="column.key === 'action'">
          <BaseTag
            :label="record.action"
            :color="getActionColor(record.action)"
          >
            {{ record.action }}
          </BaseTag>
        </div>

        <!-- Description -->
        <span
          v-if="column.key === 'description'"
          class="text-slate-600 text-sm font-medium"
        >
          {{ record.description }}
        </span>

        <!-- IP Address -->
        <span
          v-if="column.key === 'ipAddress'"
          class="font-mono text-xs text-slate-500"
        >
          {{ record.ipAddress }}
        </span>
      </template>
    </BaseTable>
  </section>
</template>
