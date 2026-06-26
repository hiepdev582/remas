<script setup lang="ts">
import type { FilterAction } from "~/types/filter";
import type { TablePagination } from "~/types/pagination";
import type {
  TableAction,
  TableAPIParams,
  TableFilterParam,
} from "~/types/table";
import type { User } from "../types";
import { userColumns } from "../constants";

//#region Config
const dataSource = ref<User[]>([]);

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
  getUsers();
};
//#endregion

//#region Table actions
const userService = useUserService();
const formState = ref<FormState>(FormState.ADD);
const userId = ref<number | undefined>(undefined);

const onEdit = (record: User) => {
  userId.value = record.id;
  formState.value = FormState.EDIT;
  isOpenUpsertModal.value = true;
};

const onToggleStatus = async (record: User) => {
  if (!record.id) {
    toast.errorOccured();
    return;
  }

  try {
    const newStatus = !record.isActive;
    await userService.updateStatus(record.id, {
      isActive: newStatus,
    });
    toast.success(
      `${newStatus ? "Activated" : "Deactivated"} user successfully!`,
    );
    getUsers();
  } catch (error: any) {
    const errorMessage =
      error.response?._data?.message || "Update user status failed!";
    toast.error(errorMessage);
  }
};

const actionValues = {
  active: {
    icon: tableAction.inactive,
    title: "Deactivate",
    color: color.warning,
  },
  inactive: {
    icon: tableAction.active,
    title: "Activate",
    color: color.success,
  },
};

const tableActions = computed<TableAction[]>(() => [
  {
    key: "edit",
    icon: tableAction.edit,
    title: "Edit",
    onClick: onEdit,
  },
  {
    key: "update-status",
    icon: "",
    title: "",
    color: "",
    onClick: onToggleStatus,
  },
]);

const handleTableChange = (_: any, filters: any, sorter: any, __: any) => {
  getUsers({
    sortField: sorter.order ? sorter.field : "",
    sortOrder: sorter.order,
    filters: Object.entries(filters).map(([key, value]) => ({
      field: key,
      value,
    })) as TableFilterParam[],
  });
};

const getUsers = async (tableApiParams?: TableAPIParams) => {
  try {
    loading.value = true;

    const params: TableAPIParams = {
      ...tableApiParams,
      search: searchValue.value,
      page: pagination.value.current,
      pageSize: pagination.value.pageSize,
    };

    const response = await userService.getList(params);
    dataSource.value = response.data;
    pagination.value.total = response.total ?? 0;

    // Nếu trang hiện tại không có dữ liệu và không phải là trang đầu tiên, tự động lùi về trang trước
    if (dataSource.value.length === 0 && pagination.value.current > 1) {
      pagination.value.current = Math.max(1, pagination.value.current - 1);
      await getUsers(tableApiParams);
    }
  } finally {
    loading.value = false;
  }
};
//#endregion

//#region Life circle
onMounted(() => {
  getUsers();
});
//#endregion
</script>

<template>
  <section>
    <BasePageHeader title="User Management" />
    <BaseFilter
      class="mb-3"
      searchPlaceholder="Search by username, name or email"
      :actions="filterActions"
      @onSearch="handleSearch"
    />
    <BaseTable
      v-model:pagination="pagination"
      :columns="userColumns"
      :dataSource
      :loading
      :scroll="{
        y: 'calc(100vh - 48px - 20px - 14px - 50px - 44px - 43px - 64px - 14px - 40px)',
      }"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <!-- Render Roles -->
        <div v-if="column.key === 'roles'" class="flex flex-wrap gap-1">
          <BaseTag
            v-for="role in record.roles"
            :key="role"
            :label="role"
            :color="
              role === authRoles.superAdmin
                ? color.primary
                : role === authRoles.admin
                  ? color.info
                  : color.deleted
            "
          >
            {{ role }}
          </BaseTag>
        </div>

        <!-- Render Status -->
        <div v-if="column.key === 'isActive'">
          <BaseTag
            :label="record.isActive ? 'Active' : 'Inactive'"
            :color="record.isActive ? 'success' : 'error'"
          >
            {{ record.isActive ? "Active" : "Inactive" }}
          </BaseTag>
        </div>

        <!-- Render Actions -->
        <div v-if="column.key === 'action'">
          <div class="flex items-center gap-1">
            <BaseTableAction
              v-for="action of tableActions"
              :key="action.key"
              :title="
                action.key === 'update-status'
                  ? actionValues[record.isActive ? 'active' : 'inactive'].title
                  : action.title
              "
              :icon="
                action.key === 'update-status'
                  ? actionValues[record.isActive ? 'active' : 'inactive'].icon
                  : action.icon
              "
              :color="
                action.key === 'update-status'
                  ? actionValues[record.isActive ? 'active' : 'inactive'].color
                  : action.color
              "
              @click="() => action.onClick(record)"
            />
          </div>
        </div>
      </template>
    </BaseTable>
    <!-- Modal -->
    <UserUpsertModal
      v-model="isOpenUpsertModal"
      :id="userId"
      :state="formState"
      @onSuccess="getUsers()"
    />
  </section>
</template>
