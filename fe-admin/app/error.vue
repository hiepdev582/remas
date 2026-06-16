<script setup lang="ts">
import type { NuxtError } from "#app";

const props = defineProps<{ error: NuxtError }>();

const errorDetails = computed(() => {
  const status = props.error?.status || HTTP_STATUS.INTERNAL_ERROR;

  switch (status) {
    case HTTP_STATUS.NOT_FOUND:
      return {
        title: status.toString(),
        subtitle: "Page Not Found",
        description:
          "Oops! We couldn't find the page that you're looking for. Please check the address and try again.",
        icon: "lucide:help-circle",
        iconClass: "text-[#f59e0b] bg-[#f59e0b]/15",
      };
    case HTTP_STATUS.FORBIDDEN:
      return {
        title: status.toString(),
        subtitle: "Access Denied",
        description:
          "Oops! Your account does not have sufficient permissions to view the content of this page.",
        icon: "lucide:lock-keyhole",
        iconClass: "text-[#5b92e5] bg-[#5b92e5]/15",
      };
    case HTTP_STATUS.INTERNAL_ERROR:
    default:
      return {
        title: status.toString(),
        subtitle: "System Error",
        description:
          "Oops! An unexpected issue has occurred on our server. Please try again later.",
        icon: "lucide:alert-triangle",
        iconClass: "text-[#ef4444] bg-[#ef4444]/15",
      };
  }
});
</script>

<template>
  <section
    class="select-none w-full h-screen flex flex-col items-center justify-center bg-[#f3f4f6] px-4"
  >
    <div
      class="w-28 h-28 rounded-3xl flex items-center justify-center mb-8"
      :class="errorDetails.iconClass"
    >
      <Icon :name="errorDetails.icon" class="w-16 h-16" />
    </div>
    <h1 class="text-4xl font-bold text-center mb-5 tracking-tight">
      {{ errorDetails.subtitle }}
    </h1>
    <div class="max-w-[480px] text-center mb-6">
      <p class="text-[15px] leading-relaxed">
        {{ errorDetails.description }}
      </p>
    </div>
    <p class="text-xs font-semibold uppercase tracking-wider mb-8">
      Error Code: {{ errorDetails.title }}
    </p>

    <div>
      <BaseButton @click="clearError({ redirect: ROUTES?.DASHBOARD })">
        Back to home page
      </BaseButton>
    </div>
  </section>
</template>
