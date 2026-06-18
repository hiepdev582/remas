<script setup lang="ts">
export interface BaseTableActionProps {
  title: string;
  icon: string;
  onClick: Function;
}

const props = withDefaults(defineProps<BaseTableActionProps>(), {});
</script>

<template>
  <BasePopover :placement="PopoverPlacement.TOP_LEFT">
    <template #content>
      <span>{{ title }}</span>
    </template>
    <p
      class="cursor-pointer hover:bg-gray-200 rounded-full flex items-center justify-center w-8 h-8"
      @click="onClick()"
    >
      <Icon :name="icon" :size="18" />
    </p>
  </BasePopover>
</template>
