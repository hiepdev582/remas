import type { AuthResponse, LoginRequest, RegisterRequest } from "./types";

export const useAuthService = () => {
  const api = useApi();

  return {
    register(data: RegisterRequest) {
      return api<string>(`${AUTH_FEATURE}/register`, {
        method: HTTP_METHOD.POST,
        body: data,
      });
    },
    login(data: LoginRequest) {
      return api<AuthResponse>(`${AUTH_FEATURE}/login`, {
        method: HTTP_METHOD.POST,
        body: data,
      });
    },
    logout() {
      return api<string>(`${AUTH_FEATURE}/logout`, {
        method: HTTP_METHOD.POST,
      });
    },
  };
};
