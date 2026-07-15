import type { PagingResponse } from "~/types/api";
import type { AuditLog } from "./types";

export const useAuditService = () => {
  const api = useApi();

  const getAuditLogs = (params: {
    page: number;
    pageSize: number;
    search?: string;
    sortField?: string;
    sortOrder?: string;
  }) => {
    return api<PagingResponse<AuditLog>>("/audit-logs", {
      method: "GET",
      params,
    });
  };

  return {
    getAuditLogs,
  };
};
