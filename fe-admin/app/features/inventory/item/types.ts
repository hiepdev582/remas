import type { ItemStatus } from "./constants";

export type Item = {
  id: number;
  name: string;
  categoryId: number;
  categoryName?: string;
  status: ItemStatus;
};

export type AddItemRequest = Omit<Item, "id" | "categoryName">;

export type EditItemRequest = Omit<Item, "id" | "categoryName">;
