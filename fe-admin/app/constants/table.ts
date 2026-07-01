//#region Constants
const tableAction = {
  edit: "ic:baseline-edit",
  remove: "material-symbols:delete",
  active: "nrk:check-active",
  inactive: "nrk:close-active",
  view: "material-symbols:visibility-outline-rounded",
  pricing: "material-symbols:attach-money-rounded",
} as const;

const tablePaginationDefault = {
  total: 0,
  current: 1,
  pageSize: 10,
} as const;
//#endregion

//#region Enum
enum TableFixed {
  LEFT = "left",
  RIGHT = "right",
}

enum SortValue {
  ASCEND = "ascend",
  DESCEND = "descend",
}
//#endregion

export { tableAction, tablePaginationDefault, TableFixed, SortValue };
