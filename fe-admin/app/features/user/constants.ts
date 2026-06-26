import type { ColumnType } from "~/types/table";

const USER_FEATURE = "user";

const userColumns: ColumnType[] = [
  {
    title: "Username",
    dataIndex: "username",
    key: "username",
    sorter: true,
    ellipsis: true,
    fixed: TableFixed.LEFT,
    width: "15%",
  },
  {
    title: "Full Name",
    dataIndex: "fullName",
    key: "fullName",
    sorter: true,
    ellipsis: true,
    width: "20%",
  },
  {
    title: "Email",
    dataIndex: "email",
    key: "email",
    ellipsis: true,
    width: "20%",
  },
  {
    title: "Roles",
    dataIndex: "roles",
    key: "roles",
    width: "15%",
    filters: [
      { text: "Super Admin", value: "SUPER_ADMIN" },
      { text: "Admin", value: "ADMIN" },
      { text: "Customer", value: "CUSTOMER" },
    ],
  },
  {
    title: "Status",
    dataIndex: "isActive",
    key: "isActive",
    width: "10%",
    filters: [
      { text: "Active", value: "true" },
      { text: "Inactive", value: "false" },
    ],
  },
  {
    title: "Action",
    key: "action",
    fixed: TableFixed.RIGHT,
  },
];

const userFieldLabels = {
  username: "Username",
  password: "Password",
  email: "Email",
  fullName: "Full Name",
  isActive: "Active",
  roles: "Roles",
} as const;

const userFieldNames = {
  username: "username",
  password: "password",
  email: "email",
  fullName: "fullName",
  isActive: "isActive",
  roles: "roles",
} as const;

export { USER_FEATURE, userColumns, userFieldLabels, userFieldNames };
