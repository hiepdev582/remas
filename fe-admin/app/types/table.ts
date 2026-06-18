export type ColumnType = {
  title: string;
  dataIndex?: string;
  key: string;
  sorter?: boolean;
  width?: string;
  filters?: TableFilter[];
};

export type TableFilter = {
  text: string;
  value: string;
};

export type TableAPIParams = {
  results: number;
  page?: number;
  sortField?: string;
  sortOrder?: number;
  [key: string]: any;
};
