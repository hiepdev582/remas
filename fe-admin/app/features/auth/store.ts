import type { AuthResponse } from "./types";

export const useAuthStore = defineStore("auth", () => {
  const token = ref<string | null>(null);
  const adminInfo = ref<Partial<AuthResponse> | null>(null);

  const isLoggedIn = computed(() => !!token.value);

  const setAuth = (data: AuthResponse) => {
    token.value = data.token;
    adminInfo.value = {
      username: data.username,
      email: data.email,
      roles: data.roles,
    };
  };

  const updateToken = (newToken: string) => {
    token.value = newToken;
  };

  const clearAuth = () => {
    token.value = null;
    adminInfo.value = null;
    navigateTo(ROUTES.AUTH.LOGIN);
  };

  return {
    token,
    adminInfo,
    isLoggedIn,
    setAuth,
    updateToken,
    clearAuth,
  };
});
