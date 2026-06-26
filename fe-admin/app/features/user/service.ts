import type { TableAPIParams } from "~/types/table";
import type { User, UserRequest } from "./types";
import type { PagingResponse } from "~/types/api";
import { USER_FEATURE } from "./constants";

export const useUserService = () => {
  const api = useApi();

  return {
    getAll() {
      return api<PagingResponse<User[]>>(`${USER_FEATURE}`, {
        method: HTTP_METHOD.GET,
      });
    },
    getList(params: TableAPIParams) {
      return api<PagingResponse<User[]>>(`${USER_FEATURE}/list`, {
        method: HTTP_METHOD.GET,
        params: formatTableParams(params),
      });
    },
    get(id: number) {
      return api<User>(`${USER_FEATURE}/${id}`, {
        method: HTTP_METHOD.GET,
      });
    },
    add(data: UserRequest) {
      return api<User>(`${USER_FEATURE}`, {
        method: HTTP_METHOD.POST,
        body: data,
      });
    },
    edit(id: number, data: UserRequest) {
      return api<User>(`${USER_FEATURE}/${id}`, {
        method: HTTP_METHOD.PUT,
        body: data,
      });
    },
    updateStatus(id: number, data: { isActive: boolean }) {
      return api<User>(`${USER_FEATURE}/update-status/${id}`, {
        method: HTTP_METHOD.PUT,
        body: data,
      });
    },
  };
};
