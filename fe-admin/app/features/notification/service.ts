import type { Notification } from "./types";

export const useNotificationService = () => {
  const api = useApi();

  return {
    getAll() {
      return api<Notification[]>("notification", {
        method: HTTP_METHOD.GET,
      });
    },
    getUnreadCount() {
      return api<number>("notification/unread-count", {
        method: HTTP_METHOD.GET,
      });
    },
    markAsRead(id: number) {
      return api<Notification>(`notification/${id}/read`, {
        method: HTTP_METHOD.PUT,
      });
    },
    markAllAsRead() {
      return api<void>("notification/read-all", {
        method: HTTP_METHOD.PUT,
      });
    },
  };
};
