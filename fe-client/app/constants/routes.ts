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
  USER: "/user",
  CUSTOMER: "/customer",
} as const;

export { ROUTES };
