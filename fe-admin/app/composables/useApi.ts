import type { AuthResponse } from "~/features/auth/types";

export const useApi = () => {
  const authStore = useAuthStore();
  const config = useRuntimeConfig();
  const baseURL = config.public.apiBase as string;

  return $fetch.create({
    baseURL,
    onRequest({ options }) {
      // Allow send HttpOnly Cookie to Backend
      options.credentials = "include";

      // Get Access Token from RAM (Pinia) and put it in Header Authorization
      if (authStore.token) {
        options.headers = options.headers || {};
        if (options.headers instanceof Headers) {
          options.headers.set("Authorization", `Bearer ${authStore.token}`);
        } else if (Array.isArray(options.headers)) {
          (options.headers as any[]).push([
            "Authorization",
            `Bearer ${authStore.token}`,
          ]);
        } else {
          (options.headers as Record<string, string>).Authorization =
            `Bearer ${authStore.token}`;
        }
      }
    },
    async onResponseError({ request, options, response }) {
      // If 401 error (Access Token expired) and not on refresh page
      if (
        response.status === 401 &&
        !request.toString().includes("auth/refresh") &&
        !request.toString().includes("auth/login")
      ) {
        try {
          // Call refresh token API
          const refreshRes = await $fetch<AuthResponse>("auth/refresh", {
            baseURL,
            method: HTTP_METHOD.POST,
            credentials: "include",
          });

          // Update Access Token
          authStore.setAuth(refreshRes);

          // Resend request with new token
          options.headers = options.headers || {};
          if (options.headers instanceof Headers) {
            options.headers.set("Authorization", `Bearer ${refreshRes.token}`);
          } else if (Array.isArray(options.headers)) {
            const headers = options.headers as any[];
            const authHeaderIndex = headers.findIndex(
              (h) =>
                Array.isArray(h) && h[0]?.toLowerCase() === "authorization",
            );
            if (authHeaderIndex !== -1) {
              headers[authHeaderIndex][1] = `Bearer ${refreshRes.token}`;
            } else {
              headers.push(["Authorization", `Bearer ${refreshRes.token}`]);
            }
          } else {
            (options.headers as Record<string, string>).Authorization =
              `Bearer ${refreshRes.token}`;
          }

          return $fetch(request, options as any);
        } catch (error) {
          // If error (Refresh Token in cookie is also expired) -> Logout
          toast.error("Session expired, please login again!");
          authStore.clearAuth();
        }
      }
    },
  });
};
