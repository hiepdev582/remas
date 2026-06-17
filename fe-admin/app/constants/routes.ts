const ROUTES = {
  DASHBOARD: "/",
  AUTH: {
    LOGIN: "/login",
    REGISTER: "/register",
  },
  INVENTORY: {
    CATEGORY: "/inventory/category",
  },
} as const;

export { ROUTES };
