<script setup lang="ts">
import type { UploadProps } from "ant-design-vue";

export interface BaseUploadDocumentsProps extends /* @vue-ignore */ UploadProps {
  modelValue?: any;
  limitFile?: number;
  disabled?: boolean;
}

const props = withDefaults(defineProps<BaseUploadDocumentsProps>(), {
  limitFile: 6,
  disabled: false,
});

const emit = defineEmits(["update:modelValue"]);

const authStore = useAuthStore();
const config = useRuntimeConfig();

const uploadAction = `${config.public.apiBase}/upload`;
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${authStore.token}`,
}));

const value = computed({
  get() {
    return props.modelValue || [];
  },
  set(newValue) {
    emit("update:modelValue", newValue);
  },
});

const docTypeOptions = [
  { label: "CCCD Front", value: "CCCD_FRONT" },
  { label: "CCCD Back", value: "CCCD_BACK" },
  { label: "Driver License Front", value: "DRIVER_LICENSE_FRONT" },
  { label: "Driver License Back", value: "DRIVER_LICENSE_BACK" },
  { label: "Other", value: "OTHER" },
];

//#region Preview
function getBase64(file: File) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result);
    reader.onerror = (error) => reject(error);
  });
}

const isPreviewVisible = ref(false);
const isPreviewImage = ref("");
const isPreviewTitle = ref("");

const handleCancel = () => {
  isPreviewVisible.value = false;
  isPreviewTitle.value = "";
};

const handlePreview = async (file: any) => {
  if (!file.url && !file.preview && file.originFileObj) {
    file.preview = (await getBase64(file.originFileObj)) as string;
  }
  isPreviewImage.value = file.url || file.response?.url || file.preview;
  isPreviewVisible.value = true;
  isPreviewTitle.value =
    file.name ||
    (file.url && file.url.substring(file.url.lastIndexOf("/") + 1)) ||
    "";
};

const handleRemove = (file: any) => {
  const newValue = value.value.filter((f: any) => f.uid !== file.uid);
  emit("update:modelValue", newValue);
};

const handleFieldChange = () => {
  emit("update:modelValue", [...value.value]);
};

const metadataMap = ref<
  Record<string, { documentType: string; documentNumber: string }>
>({});

watch(
  () => value.value,
  (newVal) => {
    if (!newVal) return;
    newVal.forEach(async (file: any) => {
      // 1. Tạo preview local
      if (!file.url && !file.preview && file.originFileObj) {
        file.preview = (await getBase64(file.originFileObj)) as string;
      }

      // 2. Đồng bộ metadata
      if (file.documentType === undefined) {
        const cached = metadataMap.value[file.uid];
        file.documentType = cached?.documentType || "OTHER";
        file.documentNumber = cached?.documentNumber || "";
      } else {
        metadataMap.value[file.uid] = {
          documentType: file.documentType,
          documentNumber: file.documentNumber || "",
        };
      }
    });
  },
  { deep: true, immediate: true },
);
//#endregion
</script>

<template>
  <div class="flex flex-wrap gap-4 items-start">
    <div
      v-for="file in value"
      :key="file.uid"
      class="relative w-[160px] border border-gray-200 rounded overflow-hidden bg-white flex flex-col group shadow-sm hover:shadow-md transition-shadow duration-200"
    >
      <div
        class="relative w-full aspect-[4/3] bg-gray-50 flex items-center justify-center overflow-hidden"
      >
        <img
          :src="file.url || file.response?.url || file.preview"
          class="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
          alt="preview"
        />
        <div
          class="absolute inset-0 bg-black bg-opacity-40 opacity-0 group-hover:opacity-100 transition-opacity duration-200 flex items-center justify-center gap-3"
        >
          <button
            type="button"
            class="text-white hover:text-blue-400 transition-colors"
            @click="handlePreview(file)"
          >
            <Icon
              name="material-symbols:visibility-outline-rounded"
              :size="18"
            />
          </button>
          <button
            v-if="!disabled"
            type="button"
            class="text-white hover:text-red-400 transition-colors"
            @click="handleRemove(file)"
          >
            <Icon name="material-symbols:delete-outline-rounded" :size="18" />
          </button>
        </div>
      </div>
      <div class="p-2 border-t border-gray-100 bg-gray-50 space-y-1.5">
        <BaseSelect
          v-model="file.documentType"
          size="small"
          class="w-full"
          :options="docTypeOptions"
          :placeholder="placeholders.select('Type')"
          :disabled="disabled"
          :allowClear="false"
          :showSearch="false"
          @change="handleFieldChange"
        />
        <BaseInput
          v-model="file.documentNumber"
          placeholder="Doc Number..."
          class="text-xs rounded border border-gray-200 bg-white"
          :disabled="disabled"
          @change="handleFieldChange"
        />
      </div>
    </div>

    <a-upload
      v-if="value.length < limitFile"
      v-bind="{ ...$attrs, ...props }"
      v-model:file-list="value"
      list-type="picture-card"
      :action="uploadAction"
      :headers="uploadHeaders"
      :show-upload-list="false"
      class="doc-upload-btn"
    >
      <div class="flex flex-col items-center justify-center">
        <Icon name="material-symbols:add-rounded" :size="22" />
        <div style="margin-top: 4px" class="text-xs text-gray-500">Upload</div>
      </div>
    </a-upload>

    <a-modal
      :open="isPreviewVisible"
      :title="isPreviewTitle"
      :footer="null"
      @cancel="handleCancel"
    >
      <img alt="example" style="width: 100%" :src="isPreviewImage" />
    </a-modal>
  </div>
</template>

<style scoped>
:deep(.doc-upload-btn .ant-upload-select-picture-card) {
  width: 160px !important;
  height: 120px !important;
}
</style>
