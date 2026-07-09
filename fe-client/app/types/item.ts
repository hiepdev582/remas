export type Item = {
  id: number;
  name: string;
  description?: string;
  categoryId: number;
  categoryName?: string;
  userId?: number;
  username?: string;
  status: ItemStatus;
  pictures?: ItemImageDto[];
  pricings?: ItemPricing[];
};

export type ItemImageDto = {
  url: string;
  note?: string;
};

export type ItemPricing = {
  id: number;
  itemId: number;
  priceType: PriceType;
  price: number;
  suggestedDeposit?: number;
  isActive: boolean;
};
