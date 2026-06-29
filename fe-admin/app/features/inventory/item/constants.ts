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
const ITEM_DESCRIPTION_MAX_CHAR = 255;

const superadminItemColumns: ColumnType[] = [
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
  },
];

const adminItemColumns: ColumnType[] = [
  {
    title: "Name",
    dataIndex: "name",
    key: "name",
    sorter: true,
    ellipsis: true,
    width: "25%",
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
} as const;

const itemFieldNames = {
  name: "name",
  description: "description",
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
  ITEM_DESCRIPTION_MAX_CHAR,
  superadminItemColumns,
  adminItemColumns,
  itemFieldLabels,
  itemFieldNames,
  ItemStatus,
  itemStatusColor,
};
