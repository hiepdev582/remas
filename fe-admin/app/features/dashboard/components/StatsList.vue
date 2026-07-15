<script setup lang="ts">
import type { StatsReportItem } from "../types";

defineProps<{
  title: string;
  description: string;
  icon: string;
  iconClass?: string;
  items?: StatsReportItem[];
  valueSuffix?: string;
  valueColorClass?: string;
  isCurrency?: boolean;
  subTextIcon?: string;
}>();
</script>

<template>
  <div
    class="bg-white border border-slate-200/80 rounded p-4 shadow-sm hover:shadow-md transition-all duration-300 flex-1"
  >
    <div
      class="select-none mb-4 flex items-center justify-between border-b pb-3"
    >
      <div>
        <h3 class="text-base font-bold text-slate-800 tracking-tight">
          {{ title }}
        </h3>
        <p class="text-xs text-slate-400 mt-0.5">
          {{ description }}
        </p>
      </div>
      <Icon :name="icon" :class="iconClass || 'text-slate-400'" size="22" />
    </div>
    <div class="divide-y divide-slate-100">
      <div
        v-for="(item, idx) in items || []"
        :key="item.id"
        class="flex items-center justify-between py-3 hover:bg-slate-50/50 px-2 rounded transition-colors duration-150"
      >
        <div class="flex items-center gap-3">
          <span
            class="select-none w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold"
            :class="[
              idx === 0
                ? 'bg-amber-100 text-amber-600'
                : idx === 1
                  ? 'bg-slate-200 text-slate-700'
                  : idx === 2
                    ? 'bg-amber-50 text-amber-700'
                    : 'bg-slate-100 text-slate-500',
            ]"
          >
            {{ idx + 1 }}
          </span>
          <div>
            <div class="text-sm font-semibold text-slate-700">
              {{ item.name }}
            </div>
            <div
              v-if="item.subText"
              class="text-xs text-slate-400 flex items-center gap-1 mt-0.5"
            >
              <Icon
                v-if="subTextIcon"
                :name="subTextIcon"
                size="10"
                class="mt-[2px]"
              />
              {{ item.subText }}
            </div>
          </div>
        </div>
        <span
          class="select-none text-sm font-bold"
          :class="valueColorClass || 'text-slate-700'"
        >
          {{
            isCurrency
              ? `${item.value?.toLocaleString()}đ`
              : `${item.value} ${valueSuffix || ""}`
          }}
        </span>
      </div>
      <div
        v-if="!items?.length"
        class="select-none text-center py-6 text-sm text-slate-400"
      >
        No data found
      </div>
    </div>
  </div>
</template>
