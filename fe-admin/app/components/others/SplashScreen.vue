<script setup lang="ts"></script>

<template>
  <section
    class="fixed inset-0 z-[9999] flex flex-col items-center justify-center bg-gray-50"
  >
    <section class="text-center mb-6 animate-pulse">
      <h1 class="text-4xl font-extrabold text-primary tracking-tight">REMAS</h1>
      <p class="text-lg mt-1 font-medium">Rental Management System</p>
    </section>

    <section class="relative w-12 h-12">
      <div
        class="w-12 h-12 rounded-full border-4 border-gray-200 border-t-primary animate-spin"
      ></div>
    </section>

    <span class="text-sm mt-4 tracking-widest uppercase font-semibold">
      Loading...
    </span>
  </section>
</template>
