export type Category = {
  id: number;
  name: string;
  description?: string;
};

export type AddCategoryRequest = Omit<Category, "id">;

export type EditCategoryRequest = Omit<Category, "id">;
