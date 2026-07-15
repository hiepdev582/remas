export enum NotificationType {
  UPCOMING_HANDOVER = "UPCOMING_HANDOVER",
  UPCOMING_RETURN = "UPCOMING_RETURN",
  INACTIVE_CUSTOMER = "INACTIVE_CUSTOMER",
}

export interface Notification {
  id: number;
  type: NotificationType;
  title: string;
  content: string;
  relatedId?: number;
  isRead: boolean;
  createdAt: string;
}
