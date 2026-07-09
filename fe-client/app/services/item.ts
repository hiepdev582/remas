import type { PagingResponse } from "~/types/api";
import type { Item, ItemPricing } from "~/types/item";

export const useItemService = () => {
  const api = useApi();
  const ITEM_FEATURE = "item";

  return {
    getAllByCategory(categoryId: number) {
      return api<PagingResponse<Item[]>>(
        `${ITEM_FEATURE}/by-category/${categoryId}`,
        {
          method: HTTP_METHOD.GET,
        },
      );
    },
    get(id: number) {
      return api<Item>(`${ITEM_FEATURE}/${id}`, {
        method: HTTP_METHOD.GET,
      });
    },
    getPricings(itemId: number) {
      return api<ItemPricing[]>(`${ITEM_FEATURE}/${itemId}/pricing`, {
        method: HTTP_METHOD.GET,
      });
    },
  };
};
