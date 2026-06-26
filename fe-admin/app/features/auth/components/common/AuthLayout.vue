<script setup lang="ts">
const props = defineProps<{
  subtitle: string;
  redirectText: string;
  redirectButtonText: string;
  redirectLink: string;
}>();
</script>

<template>
  <section
    class="select-none relative w-full h-screen overflow-hidden flex items-center justify-center bg-gradient-to-br from-[#0e2450] via-primary to-[#07132c]"
  >
    <!-- Background Effects -->
    <div
      class="absolute top-[-10%] left-[-10%] w-[50%] h-[50%] rounded-full bg-white/10 blur-[120px] pointer-events-none"
    ></div>
    <div
      class="absolute bottom-[-10%] right-[-10%] w-[50%] h-[50%] rounded-full bg-[#17ba98]/10 blur-[120px] pointer-events-none"
    ></div>

    <!-- Card -->
    <div
      class="z-10 w-[400px] max-w-[90%] max-h-[90%] bg-white rounded shadow-[0_25px_50px_-12px_rgba(0,0,0,0.3)] border border-gray-100 py-6 px-8 transition-all duration-300 hover:shadow-[0_30px_60px_-10px_rgba(0,0,0,0.4)] hover:-translate-y-1"
    >
      <section class="text-center mb-5">
        <div
          class="inline-flex items-center justify-center w-16 h-16 rounded-full shadow-md shadow-primary/20 mb-3"
        >
          <NuxtImg
            src="/images/logo_circle.png"
            class="w-full h-full object-cover"
          />
        </div>
        <h1 class="text-2xl font-extrabold tracking-tight">REMAS ADMIN</h1>
        <p class="mt-1 text-xs font-medium">
          {{ subtitle }}
        </p>
      </section>

      <!-- Form -->
      <slot name="form"></slot>

      <!-- Redirect Link -->
      <slot name="redirect">
        <div class="mt-4 text-center text-sm font-medium">
          {{ redirectText }}
          <NuxtLink
            :to="redirectLink"
            class="text-primary hover:text-primary-hover font-semibold transition-colors ml-[2px]"
          >
            {{ redirectButtonText }}
          </NuxtLink>
        </div>
      </slot>
    </div>
  </section>
</template>

<style lang="css" scoped>
:deep(.ant-form-item) {
  margin-bottom: 8px;
}
</style>
