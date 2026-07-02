<script setup lang="ts">
import type { UploadProps } from "ant-design-vue";

//#region Props & Emits
export interface BaseUploadPicturesProps extends /* @vue-ignore */ UploadProps {
  modelValue?: any;
  limitFile?: number;
  disabled?: boolean;
}

const props = withDefaults(defineProps<BaseUploadPicturesProps>(), {
  limitFile: 8,
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
    return props.modelValue;
  },
  set(value) {
    emit("update:modelValue", value);
  },
});
//#endregion

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

const handleNoteChange = (file: any) => {
  if (file.note !== undefined) {
    notesMap.value[file.uid] = file.note;
  }
  emit("update:modelValue", [...value.value]);
};

const notesMap = ref<Record<string, string>>({});

watch(
  () => value.value,
  (newVal) => {
    if (!newVal) return;
    newVal.forEach(async (file: any) => {
      // 1. Tạo preview local
      if (!file.url && !file.preview && file.originFileObj) {
        file.preview = (await getBase64(file.originFileObj)) as string;
      }

      // 2. Đồng bộ note
      if (file.note !== undefined) {
        notesMap.value[file.uid] = file.note;
      } else if (notesMap.value[file.uid] !== undefined) {
        file.note = notesMap.value[file.uid];
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
      class="relative w-[120px] border border-gray-200 rounded overflow-hidden bg-white flex flex-col group shadow-sm hover:shadow-md transition-shadow duration-200"
    >
      <div
        class="relative w-full aspect-square bg-gray-50 flex items-center justify-center overflow-hidden"
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
      <div class="p-1 border-t border-gray-100 bg-gray-50">
        <BaseInput
          v-model="file.note"
          placeholder="Ghi chú..."
          class="text-xs rounded border-none bg-white hover:bg-white focus:bg-white shadow-none text-center"
          :disabled="disabled"
          @change="handleNoteChange(file)"
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
