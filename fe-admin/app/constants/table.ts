const tableAction = {
  edit: "ic:baseline-edit",
  remove: "material-symbols:delete",
} as const;

enum TableFixed {
  LEFT = "left",
  RIGHT = "right",
}

export { tableAction, TableFixed };
