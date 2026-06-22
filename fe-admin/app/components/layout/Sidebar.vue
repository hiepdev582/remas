<script setup lang="ts">
const isCollapsed = ref(false);

const menuItems = [
  {
    label: "Dashboard",
    path: ROUTES.DASHBOARD,
    icon: "material-symbols:dashboard-rounded",
  },
  {
    label: "Categories",
    path: ROUTES.INVENTORY.CATEGORY,
    icon: "material-symbols:category-rounded",
  },
];
</script>

<template>
  <a-layout-sider
    v-model:collapsed="isCollapsed"
    collapsible
    :style="{
      height: '100vh',
      color: '#fff',
      backgroundColor: 'var(--color-primary)',
      userSelect: 'none',
    }"
  >
    <section
      class="flex items-center pt-3 pb-4 px-6 overflow-hidden whitespace-nowrap"
    >
      <NuxtLink :to="ROUTES.INVENTORY.CATEGORY" class="flex items-center">
        <NuxtImg
          src="/images/logo_circle.png"
          alt="Logo"
          class="shrink-0"
          :width="34"
          :height="34"
        />
        <span
          v-show="!isCollapsed"
          class="ml-3 text-xl font-bold text-white tracking-wide transition-opacity duration-300"
        >
          Remas
        </span>
      </NuxtLink>
    </section>

    <nav class="px-3">
      <ul class="space-y-1">
        <li v-for="item in menuItems" :key="item.path">
          <NuxtLink
            class="flex items-center gap-2 px-3 py-2.5 rounded text-[#b9cbe7] hover:text-white hover:bg-white/5 transition-all duration-200 select-none overflow-hidden whitespace-nowrap"
            active-class="active-menu-item"
            :to="item.path"
          >
            <Icon :name="item.icon" :size="20" class="shrink-0" />
            <span v-show="!isCollapsed" class="transition-opacity duration-300">
              {{ item.label }}
            </span>
          </NuxtLink>
        </li>
      </ul>
    </nav>
  </a-layout-sider>
</template>

<style lang="css" scoped>
:deep(.ant-layout-sider-trigger) {
  background-color: #113572;
}

.active-menu-item {
  color: #ffffff;
  background-color: rgba(255, 255, 255, 0.12);
}
</style>
