<script setup lang="ts">
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

const pagination = ref({
  total: 200,
  current: 1,
  pageSize: 10,
});
//#endregion

//#region Actions
const onEdit = (record: any) => {
  console.log("Edit", record);
};

const onRemove = (record: any) => {
  console.log("Remove", record);
};

const handleTableChange = (pag: any, filters: any, sorter: any, extra: any) => {
  console.log("-------------------------------------------------");
  console.log("Current page:", pagination.value.current);
  console.log("Pagination", pag);
  console.log("Filters", filters);
  console.log("Sorter", sorter);
  console.log("Extra", extra);
};

const handleClickAdd = () => {
  console.log("Add");
};
//#endregion
</script>

<template>
  <section>
    <BasePageHeader title="Category">
      <template #extra>
        <BaseButton @click="handleClickAdd">
          <div class="flex items-center justify-between gap-1">
            <Icon name="material-symbols:add-rounded" :size="18" />
            <span>Add</span>
          </div>
        </BaseButton>
      </template>
    </BasePageHeader>
    <BaseFilter></BaseFilter>
    <BaseTable
      v-model:pagination="pagination"
      :columns="categoryColumns"
      :dataSource
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
              title="Remove"
              :icon="tableAction.remove"
              @click="() => onRemove(record)"
            />
          </div>
        </template>
      </template>
    </BaseTable>
  </section>
</template>
