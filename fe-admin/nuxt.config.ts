// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: "2025-07-15",
  devtools: { enabled: true },
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
  // Cấu hình cho Ant Design Vue
  antd: {
    extractStyle: true,
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
    { path: "~/features/auth/components", prefix: "Auth", pathPrefix: false },
  ],
});
