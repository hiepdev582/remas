import type { ColumnType } from "~/types/table";

//#region CONSTANTS
const CATEGORY_FEATURE = "category";
const CATEGORY_NAME_MIN_CHAR = 2;
const CATEGORY_NAME_MAX_CHAR = 50;
const CATEGORY_DESCRIPTION_MIN_CHAR = 5;
const CATEGORY_DESCRIPTION_MAX_CHAR = 255;

const categoryColumns: ColumnType[] = [
  {
    title: "Name",
    dataIndex: "name",
    key: "name",
    width: "30%",
    sorter: true,
  },
  {
    title: "Description",
    dataIndex: "description",
    key: "description",
    width: "40%",
    filters: [
      { text: "Male", value: "male" },
      { text: "Female", value: "female" },
    ],
  },
  {
    title: "Action",
    key: "action",
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
//#endregion

export {
  CATEGORY_FEATURE,
  CATEGORY_NAME_MIN_CHAR,
  CATEGORY_NAME_MAX_CHAR,
  CATEGORY_DESCRIPTION_MIN_CHAR,
  CATEGORY_DESCRIPTION_MAX_CHAR,
  categoryColumns,
  categoryFieldLabels,
  categoryFieldNames,
};
