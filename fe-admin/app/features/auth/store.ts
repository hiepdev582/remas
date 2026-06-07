import type { AuthResponse } from "./types";

export const useAuthStore = defineStore("auth", () => {
  // 1 day token
  const token = useCookie<string | null>("auth_token", {
    maxAge: 60 * 60 * 24,
  });
  const adminInfo = useCookie<Partial<AuthResponse | null>>("admin_info", {
    maxAge: 60 * 60 * 24,
  });

  const isLoggedIn = computed(() => !!token.value);

  const setAuth = (data: AuthResponse) => {
    token.value = data.token;
    adminInfo.value = {
      username: data.username,
      email: data.email,
      roles: data.roles,
    };
  };

  const clearAuth = () => {
    token.value = null;
    adminInfo.value = null;
    navigateTo("/login");
  };

  return {
    token,
    adminInfo,
    isLoggedIn,
    setAuth,
    clearAuth,
  };
});
