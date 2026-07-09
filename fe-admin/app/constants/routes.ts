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
  CONTRACT: "/contract",
} as const;

export { ROUTES };
