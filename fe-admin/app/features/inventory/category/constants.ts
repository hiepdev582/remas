import type { ColumnType } from "~/types/table";

//#region Enum
enum CategoryStatus {
  ACTIVE = "ACTIVE",
  INACTIVE = "INACTIVE",
  DELETED = "DELETED",
}
//#endregion

//#region CONSTANTS
const CATEGORY_FEATURE = "category";
const CATEGORY_NAME_MIN_CHAR = 2;
const CATEGORY_NAME_MAX_CHAR = 50;
const CATEGORY_DESCRIPTION_MAX_CHAR = 255;

const categoryColumns: ColumnType[] = [
  {
    title: "Name",
    dataIndex: "name",
    key: "name",
    sorter: true,
    ellipsis: true,
    fixed: TableFixed.LEFT,
    width: "25%",
  },
  {
    title: "Description",
    dataIndex: "description",
    key: "description",
    ellipsis: true,
    width: "35%",
  },
  {
    title: "Status",
    dataIndex: "status",
    key: "status",
    width: "20%",
    filters: [
      { text: "Active", value: CategoryStatus.ACTIVE },
      { text: "Inactive", value: CategoryStatus.INACTIVE },
    ],
  },
  {
    title: "Action",
    key: "action",
    fixed: TableFixed.RIGHT,
  },
];
//#endregion

//#region Objects
const categoryFieldLabels = {
  name: "Name",
  description: "Description",
} as const;

const categoryFieldNames = {
  name: "name",
  description: "description",
} as const;

const categoryStatusColor = {
  [CategoryStatus.ACTIVE]: "success",
  [CategoryStatus.INACTIVE]: "error",
  [CategoryStatus.DELETED]: "default",
} as const;
//#endregion

export {
  CATEGORY_FEATURE,
  CATEGORY_NAME_MIN_CHAR,
  CATEGORY_NAME_MAX_CHAR,
  CATEGORY_DESCRIPTION_MAX_CHAR,
  categoryColumns,
  categoryFieldLabels,
  categoryFieldNames,
  CategoryStatus,
  categoryStatusColor,
};
