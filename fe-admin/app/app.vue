<script setup lang="ts">
const themeConfig = {
  token: {
    colorPrimary: "#19489a",
    colorPrimaryHover: "#1852b8",
    colorPrimaryActive: "#16408a",
    colorTextBase: "#212121",
    borderRadius: 6,
  },
};
</script>

<template>
  <a-config-provider :theme="themeConfig">
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </a-config-provider>
</template>
