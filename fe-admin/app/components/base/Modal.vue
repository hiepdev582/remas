<script setup lang="ts">
import type { ModalProps } from "ant-design-vue";
import type { CSSProperties } from "vue";

export interface BaseModalProps extends /* @vue-ignore */ ModalProps {
  title: string;
  wrapClassName?: string;
  bodyStyle?: CSSProperties;
  cancelText?: string;
  confirmText?: string;
  confirmLoading?: boolean;
}

const props = withDefaults(defineProps<BaseModalProps>(), {
  title: "",
  wrapClassName: "",
  cancelText: "Cancel",
  confirmText: "Submit",
  confirmLoading: false,
  bodyStyle: () => ({}),
});

const emit = defineEmits(["onConfirm", "onCancel"]);

const handleOk = (event: MouseEvent) => {
  emit("onConfirm", event);
};

const handleCancel = () => {
  emit("onCancel");
  isOpen.value = false;
};

const isOpen = defineModel<boolean>();
</script>

<template>
  <a-modal v-bind="{ ...$attrs, ...props }" v-model:open="isOpen" :closable="!confirmLoading" :maskClosable="!confirmLoading">
    <slot />
    <template #footer>
      <BaseButton :type="ButtonType.DEFAULT" :disabled="confirmLoading" @click="handleCancel">{{
        cancelText
      }}</BaseButton>
      <BaseButton :loading="confirmLoading" @click="handleOk">{{
        confirmText
      }}</BaseButton>
    </template>
  </a-modal>
</template>

<style lang="css">
.ant-modal-title {
  font-size: 20px;
}
</style>
