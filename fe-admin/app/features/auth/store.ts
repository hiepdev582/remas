import { AUTH_TOKEN_EXPIRATION_TIME } from "./constants";
import type { AuthResponse } from "./types";

export const useAuthStore = defineStore("auth", () => {
  // 1 day token
  const token = useCookie<string | null>("auth_token", {
    maxAge: AUTH_TOKEN_EXPIRATION_TIME,
  });
  const adminInfo = useCookie<Partial<AuthResponse | null>>("admin_info", {
    maxAge: AUTH_TOKEN_EXPIRATION_TIME,
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
    navigateTo(ROUTES.AUTH.LOGIN);
  };

  return {
    token,
    adminInfo,
    isLoggedIn,
    setAuth,
    clearAuth,
  };
});
