import type { ColumnType } from "~/types/table";

//#region Enum
enum DocumentType {
  CCCD_FRONT = "CCCD_FRONT",
  CCCD_BACK = "CCCD_BACK",
  DRIVER_LICENSE_FRONT = "DRIVER_LICENSE_FRONT",
  DRIVER_LICENSE_BACK = "DRIVER_LICENSE_BACK",
  OTHER = "OTHER",
}
//#endregion

//#region CONSTANTS
const CUSTOMER_FEATURE = "customer";
const CUSTOMER_NAME_MIN_CHAR = 2;
const CUSTOMER_NAME_MAX_CHAR = 50;
const CUSTOMER_IDENTITY_CARD_MAX_CHAR = 20;
const CUSTOMER_DRIVER_LICENSE_MAX_CHAR = 20;

const customerColumns: ColumnType[] = [
  {
    title: "Name",
    dataIndex: "name",
    key: "name",
    sorter: true,
    ellipsis: true,
    fixed: TableFixed.LEFT,
    width: "20%",
  },
  {
    title: "Phone",
    dataIndex: "phone",
    key: "phone",
    sorter: true,
    ellipsis: true,
    width: "15%",
  },
  {
    title: "Identity Card",
    dataIndex: "identityCard",
    key: "identityCard",
    ellipsis: true,
    width: "15%",
  },
  {
    title: "Driver License",
    dataIndex: "driverLicense",
    key: "driverLicense",
    ellipsis: true,
    width: "15%",
  },
  {
    title: "Trust Score",
    dataIndex: "trustScore",
    key: "trustScore",
    sorter: true,
    width: "15%",
  },
  {
    title: "Action",
    key: "action",
    fixed: TableFixed.RIGHT,
    width: "20%",
  },
];

export const docTypeOptions = [
  { label: "CCCD Front", value: "CCCD_FRONT" },
  { label: "CCCD Back", value: "CCCD_BACK" },
  { label: "Driver License Front", value: "DRIVER_LICENSE_FRONT" },
  { label: "Driver License Back", value: "DRIVER_LICENSE_BACK" },
  { label: "Other", value: "OTHER" },
] as const;
//#endregion

//#region Objects
const customerFieldLabels = {
  name: "Name",
  phone: "Phone Number",
  identityCard: "Identity Card (CCCD)",
  driverLicense: "Driver License",
  trustScore: "Trust Score",
  documents: "Documents",
} as const;

const customerFieldNames = {
  name: "name",
  phone: "phone",
  identityCard: "identityCard",
  driverLicense: "driverLicense",
  trustScore: "trustScore",
  documents: "documents",
} as const;

const documentTypeLabels: Record<DocumentType, string> = {
  [DocumentType.CCCD_FRONT]: "CCCD Front",
  [DocumentType.CCCD_BACK]: "CCCD Back",
  [DocumentType.DRIVER_LICENSE_FRONT]: "Driver License Front",
  [DocumentType.DRIVER_LICENSE_BACK]: "Driver License Back",
  [DocumentType.OTHER]: "Other",
} as const;
//#endregion

//#region Array
const documentTypeOptions = [
  { label: "CCCD Front", value: DocumentType.CCCD_FRONT },
  { label: "CCCD Back", value: DocumentType.CCCD_BACK },
  { label: "Driver License Front", value: DocumentType.DRIVER_LICENSE_FRONT },
  { label: "Driver License Back", value: DocumentType.DRIVER_LICENSE_BACK },
  { label: "Other", value: DocumentType.OTHER },
];
//#endregion

export {
  CUSTOMER_FEATURE,
  DocumentType,
  customerColumns,
  customerFieldLabels,
  customerFieldNames,
  documentTypeLabels,
  documentTypeOptions,
  CUSTOMER_NAME_MIN_CHAR,
  CUSTOMER_NAME_MAX_CHAR,
  CUSTOMER_IDENTITY_CARD_MAX_CHAR,
  CUSTOMER_DRIVER_LICENSE_MAX_CHAR,
};
