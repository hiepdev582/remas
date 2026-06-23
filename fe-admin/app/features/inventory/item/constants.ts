import type { ColumnType } from "~/types/table";

//#region Enum
enum ItemStatus {
  AVAILABLE = "AVAILABLE",
  RENTED = "RENTED",
  MAINTENANCE = "MAINTENANCE",
  DELETED = "DELETED",
}
//#endregion

//#region CONSTANTS
const ITEM_FEATURE = "item";
const ITEM_NAME_MIN_CHAR = 2;
const ITEM_NAME_MAX_CHAR = 50;

const itemColumns: ColumnType[] = [
  {
    title: "Name",
    dataIndex: "name",
    key: "name",
    sorter: true,
    ellipsis: true,
    width: "35%",
  },
  {
    title: "Category",
    dataIndex: "categoryName",
    key: "categoryName",
    sorter: true,
    ellipsis: true,
    width: "25%",
  },
  {
    title: "Status",
    dataIndex: "status",
    key: "status",
    width: "20%",
    filters: [
      { text: "Available", value: ItemStatus.AVAILABLE },
      { text: "Rented", value: ItemStatus.RENTED },
      { text: "Maintenance", value: ItemStatus.MAINTENANCE },
      { text: "Deleted", value: ItemStatus.DELETED },
    ],
  },
  {
    title: "Action",
    key: "action",
  },
];
//#endregion

//#region Objects
const itemFieldLabels = {
  name: "Name",
  categoryId: "Category",
  status: "Status",
} as const;

const itemFieldNames = {
  name: "name",
  categoryId: "categoryId",
  status: "status",
} as const;

const itemStatusColor = {
  [ItemStatus.AVAILABLE]: "success",
  [ItemStatus.RENTED]: "processing",
  [ItemStatus.MAINTENANCE]: "warning",
  [ItemStatus.DELETED]: "default",
} as const;
//#endregion

export {
  ITEM_FEATURE,
  ITEM_NAME_MIN_CHAR,
  ITEM_NAME_MAX_CHAR,
  itemColumns,
  itemFieldLabels,
  itemFieldNames,
  ItemStatus,
  itemStatusColor,
};
