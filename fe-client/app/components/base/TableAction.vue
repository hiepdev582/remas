<script setup lang="ts">
import { computed } from "vue";

export interface BaseTableActionProps {
  title: string;
  icon: string;
  color?: string;
  onClick: Function;
}

const props = withDefaults(defineProps<BaseTableActionProps>(), {});

const bgStyle = computed(() => {
  return props.color ? `${props.color}15` : "#f3f4f6";
});

const hoverBgStyle = computed(() => {
  return props.color ? `${props.color}30` : "#e5e7eb";
});
</script>

<template>
  <BasePopover :placement="PopoverPlacement.TOP_LEFT">
    <template #content>
      <span>{{ title }}</span>
    </template>
    <p
      class="table-action-btn cursor-pointer rounded-full flex items-center justify-center w-8 h-8"
      @click="onClick()"
    >
      <Icon :name="icon" :style="{ color: color }" :size="18" />
    </p>
  </BasePopover>
</template>

<style scoped>
.table-action-btn {
  background-color: v-bind(bgStyle);
  transition: background-color 0.2s ease;
}
.table-action-btn:hover {
  background-color: v-bind(hoverBgStyle);
}
</style>
