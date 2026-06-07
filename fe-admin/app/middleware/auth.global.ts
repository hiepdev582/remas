import { useAuthStore } from "~/features/auth/store";

export default defineNuxtRouteMiddleware((to, from) => {
  const authStore = useAuthStore();

  const publicPages = ["/login"];

  const isPublicPage = publicPages.includes(to.path);

  if (!authStore.isLoggedIn && !isPublicPage) {
    return navigateTo("/login");
  }

  if (authStore.isLoggedIn && isPublicPage) {
    return navigateTo("/");
  }
});
