export type User = {
  id: number;
  username: string;
  email?: string;
  fullName?: string;
  isActive: boolean;
  roles: string[];
};

export type UserRequest = {
  username: string;
  password?: string;
  email?: string;
  fullName?: string;
  isActive: boolean;
  roles: string[];
};
