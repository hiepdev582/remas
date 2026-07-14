import type { CategoryStatus } from "./constants";

export type Category = {
  id: number;
  name: string;
  description?: string;
  status: CategoryStatus;
  createdBy?: string;
  createdBySuperAdmin?: boolean;
};

export type AddCategoryRequest = Omit<Category, "id">;

export type EditCategoryRequest = Omit<Category, "id">;

export type DisplayCategoryStatus = Exclude<
  CategoryStatus,
  CategoryStatus.DELETED
>;
