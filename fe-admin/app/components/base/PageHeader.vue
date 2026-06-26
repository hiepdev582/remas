<script setup lang="ts">
import type { PageHeaderProps } from "ant-design-vue";

export interface BasePageHeaderProps extends /* @vue-ignore */ PageHeaderProps {
  title?: string;
  subTitle?: string;
  isShowBack?: boolean;
}

const props = withDefaults(defineProps<BasePageHeaderProps>(), {
  isShowBack: false,
});

const emit = defineEmits(["onBack"]);

const handleBack = () => {
  emit("onBack");
};
</script>

<template>
  <a-page-header
    v-bind="{ ...$attrs, ...props }"
    :backIcon="isShowBack ? undefined : false"
    @back="() => handleBack()"
  >
    <template #tags>
      <slot name="tags" />
    </template>
    <template #extra>
      <slot name="extra" />
    </template>
  </a-page-header>
</template>

<style lang="css" scoped>
.ant-page-header {
  padding: 0 0 10px 0;
}

.ant-page-header :deep(.ant-page-header-back) {
  margin-right: 16px;
}
</style>
