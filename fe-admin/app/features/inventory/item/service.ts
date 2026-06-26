import type { TableAPIParams } from "~/types/table";
import type {
  AddItemRequest,
  Item,
  EditItemRequest,
} from "./types";
import type { PagingResponse } from "~/types/api";

export const useItemService = () => {
  const api = useApi();

  return {
    getAll() {
      return api<PagingResponse<Item[]>>(`${ITEM_FEATURE}`, {
        method: HTTP_METHOD.GET,
      });
    },
    getList(params: TableAPIParams) {
      return api<PagingResponse<Item[]>>(`${ITEM_FEATURE}/list`, {
        method: HTTP_METHOD.GET,
        params: formatTableParams(params),
      });
    },
    get(id: number) {
      return api<Item>(`${ITEM_FEATURE}/${id}`, {
        method: HTTP_METHOD.GET,
      });
    },
    add(data: AddItemRequest) {
      return api<Item>(`${ITEM_FEATURE}`, {
        method: HTTP_METHOD.POST,
        body: data,
      });
    },
    edit(id: number, data: EditItemRequest) {
      return api<Item>(`${ITEM_FEATURE}/${id}`, {
        method: HTTP_METHOD.PUT,
        body: data,
      });
    },
    remove(id: number) {
      return api<void>(`${ITEM_FEATURE}/${id}`, {
        method: HTTP_METHOD.DELETE,
      });
    },
  };
};
