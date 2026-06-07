export interface AuthResponse {
  token: string;
  username: string;
  email: string;
  roles: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  fullName: string;
}
