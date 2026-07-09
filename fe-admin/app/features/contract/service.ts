import type { TableAPIParams } from "~/types/table";
import type {
  Contract,
  AddContractRequest,
  EditContractRequest,
} from "./types";
import { CONTRACT_FEATURE } from "./constants";
import type { PagingResponse } from "~/types/api";

export const useContractService = () => {
  const api = useApi();

  return {
    getList(params: TableAPIParams) {
      return api<PagingResponse<Contract[]>>(`${CONTRACT_FEATURE}/list`, {
        method: HTTP_METHOD.GET,
        params: formatTableParams(params),
      });
    },
    get(id: number) {
      return api<Contract>(`${CONTRACT_FEATURE}/${id}`, {
        method: HTTP_METHOD.GET,
      });
    },
    add(data: AddContractRequest) {
      return api<Contract>(`${CONTRACT_FEATURE}`, {
        method: HTTP_METHOD.POST,
        body: data,
      });
    },
    edit(id: number, data: EditContractRequest) {
      return api<Contract>(`${CONTRACT_FEATURE}/${id}`, {
        method: HTTP_METHOD.PUT,
        body: data,
      });
    },
    remove(id: number) {
      return api<void>(`${CONTRACT_FEATURE}/${id}`, {
        method: HTTP_METHOD.DELETE,
      });
    },
  };
};
