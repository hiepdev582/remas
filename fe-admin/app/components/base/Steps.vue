<script setup lang="ts">
import type { StepsProps } from "ant-design-vue";

export interface BaseStepItem {
  title: string;
  subTitle?: string;
  description?: string;
  status?: StepStatus;
  disabled?: boolean;
}

interface BaseStepsProps extends /* @vue-ignore */ StepsProps {
  items?: BaseStepItem[];
  current?: number;
  direction?: StepDirection;
}

const props = withDefaults(defineProps<BaseStepsProps>(), {
  items: () => [],
  current: 0,
  direction: StepDirection.HORIZONTAL,
});

const attrsWithoutItems = computed(() => {
  const { items, ...rest } = props;
  return rest;
});
</script>

<template>
  <a-steps v-bind="{ ...$attrs, ...attrsWithoutItems }">
    <a-step
      v-for="(item, index) in items"
      :key="index"
      :title="item.title"
      :sub-title="item.subTitle"
      :status="item.status"
      :disabled="item.disabled"
    >
      <template #description>
        <slot :name="`description-${index}`" :item="item">
          <span v-if="item.description">{{ item.description }}</span>
        </slot>
      </template>
    </a-step>
  </a-steps>
</template>
