export enum ItemStatus {
  AVAILABLE = "AVAILABLE",
  RENTED = "RENTED",
  MAINTENANCE = "MAINTENANCE",
  DELETED = "DELETED",
}

export enum PriceType {
  HOURLY = "HOURLY",
  DAILY = "DAILY",
  WEEKLY = "WEEKLY",
  MONTHLY = "MONTHLY",
  WEEKEND = "WEEKEND",
  HOLIDAY = "HOLIDAY",
}

export const itemStatusColor = {
  [ItemStatus.AVAILABLE]: "success",
  [ItemStatus.RENTED]: "processing",
  [ItemStatus.MAINTENANCE]: "warning",
  [ItemStatus.DELETED]: "default",
} as const;
