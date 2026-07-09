<script setup lang="ts">
import type { BaseSelectOption } from "~/components/base/Select.vue";
import { useCategoryService } from "~/services/category";
import { useItemService } from "~/services/item";
import type { Item } from "~/types/item";

const categoryService = useCategoryService();
const itemService = useItemService();
const selectedCategory = ref<string>("");

const categoryOptions = ref<BaseSelectOption[]>([]);
const itemOptions = ref<Item[]>([]);

const getAllCategories = async () => {
  try {
    const res = await categoryService.getAll();
    categoryOptions.value = res.data.map((c) => ({
      label: c.name,
      value: c.id,
    }));
  } catch (error) {
    console.error("Failed to load categories", error);
  }
};

const categorySegmented = computed(() => {
  const categoryNames = categoryOptions.value.map((c) => c.label);
  if (categoryNames.length > 0 && !selectedCategory.value) {
    selectedCategory.value = categoryNames[0] as string;
  }
  return categoryNames;
});

const getAllItems = async (categoryId: number) => {
  try {
    const res = await itemService.getAllByCategory(categoryId);
    itemOptions.value = res.data;
  } catch (error) {
    console.error("Failed to load items", error);
  }
};

const selectedItemForGallery = ref<Item | null>(null);
const isGalleryModalOpen = ref(false);

const openGallery = (item: Item) => {
  selectedItemForGallery.value = item;
  isGalleryModalOpen.value = true;
};

watch(
  () => selectedCategory.value,
  async (value) => {
    const item = categoryOptions.value.find((c) => c.label === value);
    if (item) {
      await getAllItems(item?.value as number);
    }
  },
);

onMounted(async () => {
  await getAllCategories();
});
</script>

<template>
  <section class="max-w-7xl mx-auto p-4 sm:px-6 lg:px-8">
    <!-- Hero Header -->
    <header class="text-center mb-6">
      <h1 class="text-2xl font-extrabold tracking-tight sm:text-3xl">
        Premium Rental Equipment
      </h1>
      <p class="mt-2 text-sm text-gray-500 max-w-xl mx-auto">
        Explore and book top-tier items curated specifically for your needs.
      </p>
    </header>

    <!-- Category Filter Bar -->
    <section
      class="max-w-3xl mx-auto mb-8 bg-white p-1.5 rounded shadow-sm border border-gray-100"
    >
      <BaseSegmented
        v-model="selectedCategory"
        block
        class="custom-category-segmented"
        :options="categorySegmented"
      />
    </section>

    <!-- Items Grid -->
    <section
      class="grid gap-6 grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5"
    >
      <div
        v-for="item in itemOptions"
        :key="item.id"
        class="group relative bg-white border border-gray-100 rounded overflow-hidden shadow-sm hover:shadow-md transition-all duration-300 flex flex-col"
      >
        <!-- Cover Photo with Badge -->
        <div
          class="relative aspect-video w-full overflow-hidden bg-gray-100 flex items-center justify-center cursor-pointer"
          @click="openGallery(item)"
        >
          <NuxtImg
            v-if="item.pictures?.length && item.pictures[0]"
            :src="item.pictures[0].url"
            class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
          />
          <div
            v-else
            class="flex flex-col items-center justify-center text-gray-400 p-4"
          >
            <Icon
              name="material-symbols:image-not-supported-outline-rounded"
              class="text-2xl mb-1"
            />
            <span class="text-xs">No image available</span>
          </div>

          <!-- Status Floating Badge -->
          <span class="absolute top-2 right-1">
            <BaseTag
              :color="itemStatusColor[item.status as ItemStatus]"
              class="shadow-sm font-bold text-[9px] uppercase tracking-wider py-0.5 px-2 rounded border-none"
            >
              {{ capitalize(item.status) }}
            </BaseTag>
          </span>
        </div>

        <!-- Content -->
        <div class="p-4 flex flex-col flex-grow">
          <h3
            class="font-bold group-hover:text-primary transition-colors line-clamp-1 cursor-pointer"
            @click="openGallery(item)"
          >
            {{ item.name }}
          </h3>
          <p class="text-xs text-gray-500 mt-1 line-clamp-2 min-h-[32px]">
            {{ item.description || "No description provided." }}
          </p>

          <!-- Gallery Thumbnails (if multiple pictures) -->
          <div
            v-if="item.pictures && item.pictures.length > 1"
            class="flex gap-1 mt-3 cursor-pointer"
            @click.stop="openGallery(item)"
          >
            <div
              v-for="pic in item.pictures.slice(1, 4)"
              :key="pic.url"
              class="w-7 h-7 rounded overflow-hidden border border-gray-100"
            >
              <NuxtImg :src="pic.url" class="w-full h-full object-cover" />
            </div>
            <div
              v-if="item.pictures.length > 4"
              class="w-7 h-7 rounded border border-gray-100 bg-gray-50 flex items-center justify-center text-[9px] font-bold text-gray-400"
            >
              +{{ item.pictures.length - 4 }}
            </div>
          </div>

          <!-- Divider -->
          <div class="mt-auto pt-3 border-t border-gray-50">
            <!-- Pricing Config Row -->
            <div
              v-if="
                item.pricings && item.pricings.filter((p) => p.isActive).length
              "
              class="space-y-1"
            >
              <div
                v-for="price in item.pricings
                  .filter((p) => p.isActive)
                  .slice(0, 2)"
                :key="price.id"
                class="flex justify-between items-center text-xs"
              >
                <span class="text-gray-400">{{
                  capitalize(price.priceType)
                }}</span>
                <span class="font-bold text-gray-800">{{
                  formatPrice(price.price)
                }}</span>
              </div>
            </div>
            <div v-else class="text-center text-xs text-gray-400 italic">
              Pricing details unavailable
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Gallery Detail & Note Modal -->
    <BaseModal v-model="isGalleryModalOpen" width="640px" :footer="null">
      <template #title>
        <div class="flex flex-col">
          <span class="font-bold text-lg">{{
            selectedItemForGallery?.name
          }}</span>
          <span class="text-xs text-gray-400 font-normal"
            >All photos and details for this equipment.</span
          >
        </div>
      </template>

      <div
        class="mt-4 grid grid-cols-2 gap-4 max-h-[60vh] overflow-y-auto pr-1"
      >
        <BaseImagePreviewGroup v-if="selectedItemForGallery?.pictures?.length">
          <div
            v-for="pic in selectedItemForGallery?.pictures"
            :key="pic.url"
            class="flex flex-col bg-gray-50 border border-gray-100 rounded-lg p-2 hover:shadow-sm transition-shadow"
          >
            <!-- Clickable zoomable image container -->
            <div
              class="w-full aspect-[4/3] rounded-md overflow-hidden flex items-center justify-center border border-gray-100"
            >
              <BaseImage
                :src="pic.url"
                class="w-full h-full object-cover cursor-zoom-in"
              />
            </div>
            <!-- Caption note -->
            <span
              class="text-xs text-gray-600 mt-2 text-center font-medium line-clamp-2 min-h-[32px]"
              :title="pic.note"
            >
              {{ pic.note || "No details provided" }}
            </span>
          </div>
        </BaseImagePreviewGroup>
        <div
          v-else
          class="col-span-2 py-8 flex justify-center items-center w-full"
        >
          <BaseEmpty description="No photos available for this equipment" />
        </div>
      </div>
    </BaseModal>
  </section>
</template>

<style scoped>
.custom-category-segmented :deep(.ant-segmented) {
  background-color: transparent;
  padding: 0;
}

.custom-category-segmented :deep(.ant-segmented-item) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s;
}

.custom-category-segmented :deep(.ant-segmented-item-selected) {
  background-color: var(--color-primary) !important;
  color: white !important;
}
</style>
