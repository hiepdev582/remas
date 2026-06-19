<script setup lang="ts">
export interface BaseFilterProps {}

const props = withDefaults(defineProps<BaseFilterProps>(), {});
</script>

<template>
  <section class="flex items-center justify-between">
    <div>1</div>
    <div>2</div>
  </section>
</template>
