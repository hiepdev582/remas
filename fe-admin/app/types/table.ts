import type { TableFixed } from "~/constants/table";

export type ColumnType = {
  title: string;
  dataIndex?: string;
  key: string;
  ellipsis?: boolean;
  fixed?: TableFixed;
  sorter?: boolean;
  resizable?: boolean;
  width?: string | number;
  minWidth?: string | number;
  maxWidth?: string | number;
  filters?: TableFilter[];
  customCell?: Function;
};

export type TableFilter = {
  text: string;
  value: string;
};

export type TableAPIParams = {
  page?: number;
  pageSize?: number;
  sortField?: string;
  sortOrder?: number;
  [key: string]: any;
};
