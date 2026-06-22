<script setup lang="ts">
import type { TableProps } from "ant-design-vue";

//#region Props
export interface BaseTableProps extends /* @vue-ignore */ TableProps {
  dataSource: any[];
  columns: any[];
  bordered?: boolean;
}

const props = withDefaults(defineProps<BaseTableProps>(), {
  dataSource: () => [],
  columns: () => [],
  bordered: false,
});
//#endregion

//#region Pagination
const pagination = defineModel<any>("pagination");

const emit = defineEmits(["change"]);

const onChange = (pag: any, filters: any, sorter: any, extra: any) => {
  if (pagination.value) {
    pagination.value = {
      ...pagination.value,
      ...pag,
    };
  }
  emit("change", pag, filters, sorter, extra);
};
//#endregion
</script>

<template>
  <a-table v-bind="{ ...$attrs, ...props }" :pagination @change="onChange">
    <template #title v-if="$slots.title">
      <slot name="title" />
    </template>

    <template #headerCell="{ column }">
      <span>
        {{ column.title }}
      </span>
    </template>

    <template #bodyCell="{ column, record }">
      <slot name="bodyCell" :column="column" :record="record" />
    </template>

    <template #footer v-if="$slots.footer">
      <slot name="footer" />
    </template>
  </a-table>
</template>

<style lang="css" scoped>
.ant-table-wrapper :deep(.ant-table-thead > tr > th) {
  color: var(--color-text-primary);
  font-weight: 600;
  background-color: #f3f4f6;
  border-bottom: 1px solid #e5e7eb;
  user-select: none;
  padding: 10px 16px;
}

.ant-table-wrapper :deep(.ant-table-tbody > tr > td) {
  padding: 4px 16px;
}

.ant-table-wrapper
  :deep(.ant-table-thead th.ant-table-column-has-sorters:hover) {
  background-color: #e5e7eb;
}
</style>
