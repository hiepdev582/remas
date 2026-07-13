import type { DashboardReport } from "./types";

export const useDashboardService = () => {
  const api = useApi();

  return {
    getDashboardReport() {
      return api<DashboardReport>("report/dashboard", {
        method: HTTP_METHOD.GET,
      });
    },
  };
};
