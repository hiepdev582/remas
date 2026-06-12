export default defineNuxtRouteMiddleware(async (to, from) => {
  const authStore = useAuthStore();
  const config = useRuntimeConfig();
  const baseURL = config.public.apiBase as string;

  const publicPages: string[] = [ROUTES.AUTH.LOGIN, ROUTES.AUTH.REGISTER];
  const isPublicPage = publicPages.includes(to.path);

  // Case: F5 (Pinia is empty) but accessing protected route
  if (!authStore.isLoggedIn && !isPublicPage) {
    try {
      // Check if Cookie is still valid
      const refreshRes = await $fetch<{ token: string }>("auth/refresh", {
        baseURL,
        method: HTTP_METHOD.POST,
        credentials: "include",
      });

      // Get new Access Token -> Allow
      authStore.updateToken(refreshRes.token);
    } catch (error) {
      // Cookie is expired -> Back to login
      return navigateTo(ROUTES.AUTH.LOGIN);
    }
  }

  // Already have token in RAM -> Don't allow back to login page
  if (authStore.isLoggedIn && isPublicPage) {
    return navigateTo(ROUTES.DASHBOARD);
  }
});
