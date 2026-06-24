import type { ItemStatus } from "./constants";

export type Item = {
  id: number;
  name: string;
  description?: string;
  categoryId: number;
  categoryName?: string;
  userId?: number;
  username?: string;
  status: ItemStatus;
};

export type AddItemRequest = Omit<Item, "id" | "categoryName" | "userId" | "username">;

export type EditItemRequest = Omit<Item, "id" | "categoryName" | "userId" | "username">;
