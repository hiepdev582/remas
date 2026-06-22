<script setup lang="ts">
import type { FilterAction } from "~/types/filter";

export interface BaseFilterProps {
  searchPlaceholder?: string;
  useAdvancedFilter?: boolean;
  actions: FilterAction[];
}

const props = withDefaults(defineProps<BaseFilterProps>(), {
  searchPlaceholder: placeholders.enterDefault,
  useAdvancedFilter: false,
});

const emit = defineEmits(["onSearch"]);
</script>

<template>
  <section class="flex items-center justify-between">
    <section>
      <BasePopover
        v-if="useAdvancedFilter"
        class="flex gap-2"
        :arrow="false"
        :placement="PopoverPlacement.BOTTOM_LEFT"
        :trigger="PopoverTrigger.CLICK"
      >
        <template #content>
          <slot name="filter" />
        </template>
        <BaseButton>
          <div class="flex items-center justify-between gap-1">
            <Icon name="mdi:filter" :size="18" />
            <span>Filter</span>
          </div>
        </BaseButton>
      </BasePopover>
      <BaseInputSearch
        class="min-w-[300px]"
        :placeholder="props.searchPlaceholder"
        @search="($event: string) => emit('onSearch', $event)"
      />
    </section>
    <section class="flex items-center gap-x-2">
      <BaseButton
        v-for="action of props.actions"
        :key="action.key"
        @click="() => action.onClick()"
      >
        <div class="flex items-center justify-between gap-1">
          <Icon :name="action.icon" :size="18" />
          <span>{{ action.title }}</span>
        </div>
      </BaseButton>
    </section>
  </section>
</template>
