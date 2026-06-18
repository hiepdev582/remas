<script setup lang="ts">
import type { TableProps } from "ant-design-vue";

//#region Config
const dataSource = ref([
  {
    id: "1",
    name: "Xe oto",
    description: "VF3 màu đỏ",
  },
  {
    id: "2",
    name: "Xe máy",
    description: "Xe máy điện VinFast Evo Grand",
  },
  {
    id: "3",
    name: "Máy đọc sách",
    description: "Boox Go 6",
  },
]);

const loading = ref(false);

const pagination = computed(() => ({
  total: 200,
  current: 1,
  pageSize: 10,
}));
//#endregion

//#region Actions
const onEdit = (record: any) => {
  console.log(record);
};

const onDelete = (record: any) => {
  console.log(record);
};

const handleTableChange = (event: any) => {
  console.log(event);
};
//#endregion
</script>

<template>
  <section>
    <h1>Category</h1>
    <BaseTable
      :columns="categoryColumns"
      :dataSource
      :pagination
      :loading
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <div class="flex items-center gap-1">
            <BaseTableAction
              title="Edit"
              :icon="tableAction.edit"
              @click="() => onEdit(record)"
            />
            <BaseTableAction
              title="Delete"
              :icon="tableAction.delete"
              @click="() => onDelete(record)"
            />
          </div>
        </template>
      </template>
    </BaseTable>
  </section>
</template>
