import type { PagingResponse } from "~/types/api";
import type { Category } from "~/types/category";

export const useCategoryService = () => {
  const api = useApi();
  const CATEGORY_FEATURE = "category";

  return {
    getAll() {
      return api<PagingResponse<Category[]>>(`${CATEGORY_FEATURE}`, {
        method: HTTP_METHOD.GET,
      });
    },
    get(id: number) {
      return api<Category>(`${CATEGORY_FEATURE}/${id}`, {
        method: HTTP_METHOD.GET,
      });
    },
  };
};
