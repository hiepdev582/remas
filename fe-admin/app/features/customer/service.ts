import type { TableAPIParams } from "~/types/table";
import type {
  Customer,
  AddCustomerRequest,
  EditCustomerRequest,
} from "./types";
import { CUSTOMER_FEATURE } from "./constants";
import type { PagingResponse } from "~/types/api";

export const useCustomerService = () => {
  const api = useApi();

  return {
    getAll() {
      return api<PagingResponse<Customer[]>>(`${CUSTOMER_FEATURE}`, {
        method: HTTP_METHOD.GET,
      });
    },
    getList(params: TableAPIParams) {
      return api<PagingResponse<Customer[]>>(`${CUSTOMER_FEATURE}/list`, {
        method: HTTP_METHOD.GET,
        params: formatTableParams(params),
      });
    },
    get(id: number) {
      return api<Customer>(`${CUSTOMER_FEATURE}/${id}`, {
        method: HTTP_METHOD.GET,
      });
    },
    add(data: AddCustomerRequest) {
      return api<Customer>(`${CUSTOMER_FEATURE}`, {
        method: HTTP_METHOD.POST,
        body: data,
      });
    },
    edit(id: number, data: EditCustomerRequest) {
      return api<Customer>(`${CUSTOMER_FEATURE}/${id}`, {
        method: HTTP_METHOD.PUT,
        body: data,
      });
    },
    remove(id: number) {
      return api<void>(`${CUSTOMER_FEATURE}/${id}`, {
        method: HTTP_METHOD.DELETE,
      });
    },
  };
};
