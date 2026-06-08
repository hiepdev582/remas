export default defineNuxtRouteMiddleware((to, from) => {
  const authStore = useAuthStore();

  const publicPages: string[] = [ROUTES.AUTH.LOGIN];
  const isPublicPage = publicPages.includes(to.path);

  if (!authStore.isLoggedIn && !isPublicPage) {
    return navigateTo(ROUTES.AUTH.LOGIN);
  }

  if (authStore.isLoggedIn && isPublicPage) {
    return navigateTo(ROUTES.DASHBOARD);
  }
});
