// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: "2025-07-15",
  devtools: { enabled: true },
  runtimeConfig: {
    public: {
      apiBase: "http://localhost:8080/api/v1",
    },
  },
  modules: [
    "@nuxtjs/tailwindcss",
    "@ant-design-vue/nuxt",
    "@pinia/nuxt",
    "@vee-validate/nuxt",
    "@nuxt/icon",
    "@nuxt/image",
    "@nuxt/fonts",
    "@nuxtjs/sitemap",
    "@nuxtjs/robots",
    "nuxt-security",
  ],
  app: {
    head: {
      title: "Remas - Rental Management System | hiepnn",
    },
  },
  css: ["~/assets/styles/index.css"],
  // Cấu hình cho Ant Design Vue
  antd: {
    extractStyle: true,
  },
  // Cấu hình cho Tailwindcss
  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },
  // Cấu hình tối ưu cho Nuxt Image - Ưu tiên xuất ảnh ra định dạng siêu nhẹ
  image: {
    format: ["webp", "avif"],
  },
  // Cấu hình bảo mật cơ bản cho Nuxt Security
  security: {
    corsHandler: {
      origin: ["http://localhost:8080"],
      methods: ["GET", "POST", "PUT", "DELETE"],
    },
  },
  components: [
    { path: "~/components/base", prefix: "Base", pathPrefix: false },
    { path: "~/components/layout", prefix: "App", pathPrefix: false },
    { path: "~/components/others", prefix: "App", pathPrefix: false },
    { path: "~/features/auth/components", prefix: "Auth", pathPrefix: false },
    {
      path: "~/features/inventory/category/components",
      prefix: "InventoryCategory",
      pathPrefix: false,
    },
    {
      path: "~/features/inventory/item/components",
      prefix: "InventoryItem",
      pathPrefix: false,
    },
  ],
  imports: {
    dirs: [
      "~/constants/*.ts",
      "~/features/*/store.ts",
      "~/features/*/service.ts",
      "~/features/*/constants.ts",
      "~/features/*/*/store.ts",
      "~/features/*/*/service.ts",
      "~/features/*/*/constants.ts",
    ],
  },
});
