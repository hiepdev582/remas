const ROUTES = {
  DASHBOARD: "/",
  AUTH: {
    LOGIN: "/login",
    REGISTER: "/register",
  },
  INVENTORY: {
    CATEGORY: "/inventory/category",
    ITEM: "/inventory/item",
  },
} as const;

export { ROUTES };
