import type { ColumnType } from "~/types/table";

//#region Enum
enum ItemStatus {
  AVAILABLE = "AVAILABLE",
  RENTED = "RENTED",
  MAINTENANCE = "MAINTENANCE",
  DELETED = "DELETED",
}

enum PriceType {
  HOURLY = "HOURLY",
  DAILY = "DAILY",
  WEEKLY = "WEEKLY",
  MONTHLY = "MONTHLY",
  WEEKEND = "WEEKEND",
  HOLIDAY = "HOLIDAY",
}
//#endregion

//#region CONSTANTS
const ITEM_FEATURE = "item";
const ITEM_NAME_MIN_CHAR = 2;
const ITEM_NAME_MAX_CHAR = 50;
const ITEM_DESCRIPTION_MAX_CHAR = 255;

const superadminItemColumns: ColumnType[] = [
  {
    title: "Name",
    dataIndex: "name",
    key: "name",
    sorter: true,
    ellipsis: true,
    fixed: TableFixed.LEFT,
    width: "15%",
  },
  {
    title: "Category",
    dataIndex: "categoryName",
    key: "categoryName",
    ellipsis: true,
    width: "15%",
  },
  {
    title: "Owner",
    dataIndex: "username",
    key: "username",
    ellipsis: true,
    width: "15%",
  },
  {
    title: "Description",
    dataIndex: "description",
    key: "description",
    ellipsis: true,
    width: "15%",
  },
  {
    title: "Status",
    dataIndex: "status",
    key: "status",
    width: "15%",
    filters: [
      { text: "Available", value: ItemStatus.AVAILABLE },
      { text: "Rented", value: ItemStatus.RENTED },
      { text: "Maintenance", value: ItemStatus.MAINTENANCE },
    ],
  },
  {
    title: "Action",
    key: "action",
    fixed: TableFixed.RIGHT,
    width: "25%",
  },
];

const adminItemColumns: ColumnType[] = [
  {
    title: "Name",
    dataIndex: "name",
    key: "name",
    sorter: true,
    ellipsis: true,
    fixed: TableFixed.LEFT,
    width: "20%",
  },
  {
    title: "Category",
    dataIndex: "categoryName",
    key: "categoryName",
    sorter: true,
    ellipsis: true,
    width: "20%",
  },
  {
    title: "Description",
    dataIndex: "description",
    key: "description",
    ellipsis: true,
    width: "20%",
  },
  {
    title: "Status",
    dataIndex: "status",
    key: "status",
    width: "15%",
    filters: [
      { text: "Available", value: ItemStatus.AVAILABLE },
      { text: "Rented", value: ItemStatus.RENTED },
      { text: "Maintenance", value: ItemStatus.MAINTENANCE },
    ],
  },
  {
    title: "Action",
    key: "action",
    fixed: TableFixed.RIGHT,
    width: "25%",
  },
];

const itemPricingColumns = [
  {
    title: "Price Type",
    dataIndex: "priceType",
    key: "priceType",
  },
  {
    title: "Price",
    dataIndex: "price",
    key: "price",
  },
  {
    title: "Suggested Deposit",
    dataIndex: "suggestedDeposit",
    key: "suggestedDeposit",
  },
  {
    title: "Status",
    dataIndex: "isActive",
    key: "isActive",
    width: 100,
  },
  {
    title: "Actions",
    key: "action",
    width: 120,
  },
];
//#endregion

//#region Objects
const itemFieldLabels = {
  name: "Name",
  description: "Description",
  categoryId: "Category",
  ownerId: "Owner",
  status: "Status",
  pictures: "Pictures",
} as const;

const itemFieldNames = {
  name: "name",
  description: "description",
  categoryId: "categoryId",
  status: "status",
  pictures: "pictures",
} as const;

const itemStatusColor = {
  [ItemStatus.AVAILABLE]: "success",
  [ItemStatus.RENTED]: "processing",
  [ItemStatus.MAINTENANCE]: "warning",
  [ItemStatus.DELETED]: "default",
} as const;

const priceTypeLabels: Record<PriceType, string> = {
  [PriceType.HOURLY]: "Hourly",
  [PriceType.DAILY]: "Daily",
  [PriceType.WEEKLY]: "Weekly",
  [PriceType.MONTHLY]: "Monthly",
  [PriceType.WEEKEND]: "Weekend",
  [PriceType.HOLIDAY]: "Holiday",
} as const;
//#endregion

//#region Array
const priceTypeOptions = [
  { label: "Hourly", value: PriceType.HOURLY },
  { label: "Daily", value: PriceType.DAILY },
  { label: "Weekly", value: PriceType.WEEKLY },
  { label: "Monthly", value: PriceType.MONTHLY },
  { label: "Weekend", value: PriceType.WEEKEND },
  { label: "Holiday", value: PriceType.HOLIDAY },
];

const itemPricingFieldLabels = {
  priceType: "Price Type",
  price: "Price",
  suggestedDeposit: "Suggested Deposit",
} as const;

const itemPricingFieldNames = {
  priceType: "priceType",
  price: "price",
  suggestedDeposit: "suggestedDeposit",
  isActive: "isActive",
} as const;
//#endregion

export {
  ITEM_FEATURE,
  ITEM_NAME_MIN_CHAR,
  ITEM_NAME_MAX_CHAR,
  ITEM_DESCRIPTION_MAX_CHAR,
  superadminItemColumns,
  adminItemColumns,
  itemFieldLabels,
  itemFieldNames,
  ItemStatus,
  itemStatusColor,
  PriceType,
  itemPricingColumns,
  priceTypeOptions,
  priceTypeLabels,
  itemPricingFieldLabels,
  itemPricingFieldNames,
};
