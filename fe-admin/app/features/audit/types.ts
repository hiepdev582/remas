export type AuditLog = {
  id: number;
  username: string;
  userFullName: string;
  action: string;
  description: string;
  ipAddress: string;
  createdAt: string;
};
