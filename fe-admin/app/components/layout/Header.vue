<script setup lang="ts">
const authStore = useAuthStore();
const authService = useAuthService();

const handleClickLogout = async () => {
  try {
    const responseMessage = await authService.logout();
    toast.success(responseMessage || "Logged out successfully!");
    authStore.clearAuth();
  } catch (error: any) {
    const apiMessage = error.response?._data?.message || "Logout failed!";
    toast.error(apiMessage);
  }
};
</script>

<template>
  <a-layout-header
    :style="{
      height: '48px',
      lineHeight: '48px',
      paddingInline: '24px',
      color: 'var(--color-text-primary)',
      backgroundColor: '#ffffff',
      borderBottom: '1px solid #f3f4f6',
      userSelect: 'none',
    }"
  >
    <div class="h-full flex items-center justify-between">
      <section></section>
      <section class="h-full flex items-center justify-center">
        <BasePopover :placement="PopoverPlacement.TOP_LEFT">
          <template #content>
            <div class="mb-2 px-2 py-1">
              <p class="font-medium">
                {{ authStore.adminInfo?.username }}
              </p>
              <p class="text-xs">{{ authStore.adminInfo?.email }}</p>
            </div>
            <div
              class="cursor-pointer flex items-center group hover:bg-[#fff1f1] px-2 py-1 rounded select-none"
              @click="handleClickLogout"
            >
              <Icon
                name="material-symbols:logout-rounded"
                class="group-hover:text-[var(--color-error)]"
                :size="18"
              />
              <p class="ml-1 mb-[1px] group-hover:text-[var(--color-error)]">
                Logout
              </p>
            </div>
          </template>
          <BaseAvatar class="cursor-pointer bg-primary">
            {{ authStore.adminInfo?.username?.[0]?.toUpperCase() }}
          </BaseAvatar>
        </BasePopover>
      </section>
    </div>
  </a-layout-header>
</template>
