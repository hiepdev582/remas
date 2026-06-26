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
            <div class="w-44 flex flex-col">
              <!-- User Info Header -->
              <div class="flex items-center gap-2.5 pb-2 px-1">
                <BaseAvatar class="bg-primary flex-shrink-0" :size="32">
                  {{ authStore.adminInfo?.username?.[0]?.toUpperCase() }}
                </BaseAvatar>
                <div class="flex flex-col min-w-0">
                  <p
                    class="font-semibold text-[13px] text-slate-800 truncate mb-0 leading-tight"
                  >
                    {{ authStore.adminInfo?.username }}
                  </p>
                  <p class="text-[11px] text-slate-400 truncate mb-0 mt-0.5">
                    {{ authStore.adminInfo?.email }}
                  </p>
                </div>
              </div>

              <!-- Divider -->
              <div class="border-t border-slate-100 -mx-3 my-1"></div>

              <!-- Action Items -->
              <div
                class="cursor-pointer flex items-center gap-2 px-1 py-1.5 group rounded select-none hover:text-[var(--color-error)] hover:bg-red-50/70 transition-all duration-150"
                @click="handleClickLogout"
              >
                <Icon
                  name="material-symbols:logout-rounded"
                  class="group-hover:text-[var(--color-error)]"
                  :size="16"
                />
                <p class="mb-[1px] text-xs font-medium">Logout</p>
              </div>
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
