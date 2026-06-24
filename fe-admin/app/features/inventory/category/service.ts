import type { TableAPIParams } from "~/types/table";
import type {
  AddCategoryRequest,
  Category,
  DisplayCategoryStatus,
  EditCategoryRequest,
} from "./types";
import type { PagingResponse } from "~/types/api";

export const useCategoryService = () => {
  const api = useApi();

  return {
    getAll() {
      return api<PagingResponse<Category[]>>(`${CATEGORY_FEATURE}`, {
        method: HTTP_METHOD.GET,
      });
    },
    getList(params: TableAPIParams) {
      return api<PagingResponse<Category[]>>(`${CATEGORY_FEATURE}/list`, {
        method: HTTP_METHOD.GET,
        params: formatTableParams(params),
      });
    },
    get(id: number) {
      return api<Category>(`${CATEGORY_FEATURE}/${id}`, {
        method: HTTP_METHOD.GET,
      });
    },
    add(data: AddCategoryRequest) {
      return api<string>(`${CATEGORY_FEATURE}`, {
        method: HTTP_METHOD.POST,
        body: data,
      });
    },
    edit(id: number, data: EditCategoryRequest) {
      return api<string>(`${CATEGORY_FEATURE}/${id}`, {
        method: HTTP_METHOD.PUT,
        body: data,
      });
    },
    updateStatus(id: number, data: { status: DisplayCategoryStatus }) {
      return api<string>(`${CATEGORY_FEATURE}/update-status/${id}`, {
        method: HTTP_METHOD.PUT,
        body: data,
      });
    },
    remove(id: number) {
      return api<string>(`${CATEGORY_FEATURE}/${id}`, {
        method: HTTP_METHOD.DELETE,
      });
    },
  };
};
