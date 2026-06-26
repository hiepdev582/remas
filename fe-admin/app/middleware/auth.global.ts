import type { AuthResponse } from "~/features/auth/types";

export default defineNuxtRouteMiddleware(async (to, from) => {
  const authStore = useAuthStore();
  const config = useRuntimeConfig();
  const baseURL = config.public.apiBase as string;

  const publicPages: string[] = [ROUTES.AUTH.LOGIN, ROUTES.AUTH.REGISTER];
  const isPublicPage = publicPages.includes(to.path);

  // Case: F5 (Pinia is empty) but accessing protected route
  if (!authStore.isLoggedIn && !isPublicPage) {
    try {
      // Retrieve cookies from browser requests to forward to the backend when running on the server side (SSR).
      const headers = useRequestHeaders(["cookie"]);

      // Check if Cookie is still valid
      const refreshRes = await $fetch<AuthResponse>("auth/refresh", {
        baseURL,
        method: HTTP_METHOD.POST,
        headers,
        credentials: "include",
      });

      // Get new Access Token -> Allow
      authStore.setAuth(refreshRes);
    } catch (error: any) {
      const apiMessage =
        error.response?._data?.message ||
        "Session expired, please login again.";

      if (import.meta.client) {
        toast.error(apiMessage);
      }

      return navigateTo({
        path: ROUTES.AUTH.LOGIN,
        query: {
          error: apiMessage,
        },
      });
    }
  }

  // Already have token in RAM -> Don't allow back to login page
  if (authStore.isLoggedIn) {
    if (isPublicPage) {
      return navigateTo(ROUTES.DASHBOARD);
    }
    if (to.path === ROUTES.USER) {
      const { isSuperAdmin } = usePermission();
      if (!isSuperAdmin.value) {
        if (import.meta.client) {
          toast.error("You don't have permission to access this page!");
        }
        return navigateTo(ROUTES.DASHBOARD);
      }
    }
  }
});
