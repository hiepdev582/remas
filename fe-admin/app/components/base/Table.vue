<script setup lang="ts">
import type { TableProps } from "ant-design-vue";

export interface BaseTableProps extends /* @vue-ignore */ TableProps {
  dataSource: any[];
  columns: any[];
}

const props = withDefaults(defineProps<BaseTableProps>(), {
  dataSource: () => [],
  columns: () => [],
});
</script>

<template>
  <a-table v-bind="{ ...$attrs, ...props }">
    <template #headerCell="{ column }">
      <span>
        {{ column.title }}
      </span>
    </template>

    <template #bodyCell="{ column, record }">
      <slot name="bodyCell" :column="column" :record="record" />
    </template>
  </a-table>
</template>
