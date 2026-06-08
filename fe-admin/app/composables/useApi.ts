import { message } from "ant-design-vue";

export const useApi = () => {
  const authStore = useAuthStore();
  const config = useRuntimeConfig();
  const baseURL = config.public.apiBase as string;

  return $fetch.create({
    baseURL,
    onRequest({ options }) {
      options.headers = options.headers || {};

      if (options.headers instanceof Headers) {
        options.headers.set("Authorization", `Bearer ${authStore.token}`);
      } else if (Array.isArray(options.headers)) {
        (options.headers as [string, string][]).push([
          "Authorization",
          `Bearer ${authStore.token}`,
        ]);
      } else {
        (options.headers as Record<string, string>).Authorization =
          `Bearer ${authStore.token}`;
      }
    },
    onResponseError({ response }) {
      if (response.status === 401) {
        message.error("Unauthorized");
        authStore.clearAuth();
      }
    },
  });
};
