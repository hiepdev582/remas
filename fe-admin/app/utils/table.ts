import type { TableAPIParams } from "~/types/table";

export const formatTableParams = (params: TableAPIParams): Record<string, any> => {
  const queryParams: Record<string, any> = { ...params };
  if (params.filters) {
    params.filters.forEach((filter) => {
      if (filter.value && filter.value.length > 0) {
        queryParams[filter.field] = filter.value;
      }
    });
    delete queryParams.filters;
  }
  return queryParams;
};
