<script setup lang="ts">
import { ref, computed } from "vue";
import {
  type FilterAction,
  type AdvancedFilterConfig,
  AdvancedFilterFieldType,
} from "~/types/filter";

export interface BaseFilterProps {
  searchPlaceholder?: string;
  useAdvancedFilter?: boolean;
  actions: FilterAction[];
  filtersConfig?: AdvancedFilterConfig[];
}

const props = withDefaults(defineProps<BaseFilterProps>(), {
  searchPlaceholder: placeholders.enterDefault,
  useAdvancedFilter: false,
  filtersConfig: () => [],
});

const emit = defineEmits(["onSearch", "onFilterChange"]);

const getInitialValues = () => props.filtersConfig.reduce(
  (acc, filter) => {
    acc[filter.key] = filter.defaultValue;
    return acc;
  },
  {} as Record<string, any>,
);

const filterValues = ref<Record<string, any>>(getInitialValues());
const submittedFilterValues = ref<Record<string, any>>(getInitialValues());

const activeFiltersCount = computed(() => {
  return Object.values(submittedFilterValues.value).filter(
    (val) => val !== undefined && val !== null && val !== "",
  ).length;
});

const handleReset = () => {
  props.filtersConfig.forEach((filter) => {
    filterValues.value[filter.key] = filter.defaultValue;
  });
  handleSubmit();
};

const handleSubmit = () => {
  submittedFilterValues.value = { ...filterValues.value };
  emit("onFilterChange", { ...filterValues.value });
};
</script>

<template>
  <section class="flex items-center justify-between">
    <section class="flex items-center gap-x-2">
      <BasePopover
        v-if="useAdvancedFilter"
        class="flex gap-2"
        :arrow="false"
        :placement="PopoverPlacement.BOTTOM_LEFT"
        :trigger="PopoverTrigger.CLICK"
      >
        <template #content>
          <div class="flex flex-col gap-3 min-w-[240px] p-2">
            <div
              v-for="field in props.filtersConfig"
              :key="field.key"
              class="flex flex-col gap-1.5"
            >
              <span class="text-xs font-medium">{{ field.label }}</span>

              <BaseSelect
                v-if="field.type === AdvancedFilterFieldType.SELECT"
                v-model="filterValues[field.key]"
                allowClear
                class="w-full"
                :placeholder="field.placeholder"
                :options="field.options"
              />
              <BaseInput
                v-else-if="field.type === AdvancedFilterFieldType.TEXT"
                v-model="filterValues[field.key]"
                allowClear
                class="w-full"
                :placeholder="field.placeholder"
              />
            </div>

            <div class="flex justify-end gap-2 mt-2 pt-3 border-t">
              <BaseButton :type="ButtonType.DEFAULT" @click="handleReset"
                >Reset</BaseButton
              >
              <BaseButton @click="handleSubmit">Submit</BaseButton>
            </div>
          </div>
        </template>
        <BaseButton>
          <div class="flex items-center justify-between gap-1">
            <Icon name="mdi:filter" :size="18" />
            <span>Filter{{ activeFiltersCount > 0 ? ` (${activeFiltersCount})` : "" }}</span>
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
