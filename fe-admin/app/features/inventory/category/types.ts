import type { CategoryStatus } from "./constants";

export type Category = {
  id: number;
  name: string;
  description?: string;
  status: CategoryStatus;
};

export type AddCategoryRequest = Omit<Category, "id">;

export type EditCategoryRequest = Omit<Category, "id">;
