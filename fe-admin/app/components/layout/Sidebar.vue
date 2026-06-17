<script setup lang="ts">
const isCollapsed = ref(false);
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
    <section class="flex items-center pt-3 pb-5 px-4">
      <NuxtLink :to="ROUTES.INVENTORY.CATEGORY">
        <NuxtImg
          src="/images/logo_circle.png"
          alt="Logo"
          :width="34"
          :height="34"
        />
      </NuxtLink>
      <p class="ml-2 text-xl font-bold text-white tracking-wide">Remas</p>
    </section>
    <section class="px-4">Sidebar</section>
  </a-layout-sider>
</template>

<style lang="css" scoped>
:deep(.ant-layout-sider-trigger) {
  background-color: #113572;
}
</style>
