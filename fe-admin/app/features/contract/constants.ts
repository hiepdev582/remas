import type { ColumnType } from "~/types/table";
import type { BaseSelectOption } from "~/components/base/Select.vue";

//#region Enums
export enum ContractStatus {
  RESERVED = "RESERVED",
  ACTIVE = "ACTIVE",
  COMPLETED = "COMPLETED",
  CANCELLED = "CANCELLED",
  OVERDUE = "OVERDUE",
}

export enum CollateralStatus {
  NONE = "NONE",
  CASH = "CASH",
  ASSET = "ASSET",
  BOTH = "BOTH",
}

export enum CollateralType {
  CASH = "CASH",
  IDENTITY_CARD = "IDENTITY_CARD",
  DRIVER_LICENSE = "DRIVER_LICENSE",
  MOTORBIKE = "MOTORBIKE",
  LAPTOP = "LAPTOP",
  OTHER = "OTHER",
}

export enum CollateralItemStatus {
  HOLDING = "HOLDING",
  RETURNED = "RETURNED",
  FORFEITED = "FORFEITED",
}

export enum FeeType {
  DELIVERY = "DELIVERY",
  CLEANING = "CLEANING",
  DAMAGE = "DAMAGE",
  LATE = "LATE",
}
//#endregion

const CONTRACT_FEATURE = "contract";

const contractColumns: ColumnType[] = [
  {
    title: "Customer",
    dataIndex: "customerName",
    key: "customerName",
    sorter: true,
    ellipsis: true,
    fixed: TableFixed.LEFT,
    width: "15%",
  },
  {
    title: "Phone",
    dataIndex: "customerPhone",
    key: "customerPhone",
    ellipsis: true,
    width: "12%",
  },
  {
    title: "Start Date",
    dataIndex: "startDate",
    key: "startDate",
    sorter: true,
    width: "15%",
  },
  {
    title: "Expected Return",
    dataIndex: "expectedReturnDate",
    key: "expectedReturnDate",
    sorter: true,
    width: "15%",
  },
  {
    title: "Final Amount",
    dataIndex: "finalAmount",
    key: "finalAmount",
    sorter: true,
    width: "13%",
  },
  {
    title: "Status",
    dataIndex: "status",
    key: "status",
    width: "12%",
    filters: [
      { text: "Reserved", value: ContractStatus.RESERVED },
      { text: "Active", value: ContractStatus.ACTIVE },
      { text: "Completed", value: ContractStatus.COMPLETED },
      { text: "Cancelled", value: ContractStatus.CANCELLED },
      { text: "Overdue", value: ContractStatus.OVERDUE },
    ],
  },
  {
    title: "Action",
    key: "action",
    fixed: TableFixed.RIGHT,
    width: "18%",
  },
];

export const contractStatusOptions: BaseSelectOption[] = [
  { label: "Reserved", value: ContractStatus.RESERVED },
  { label: "Active", value: ContractStatus.ACTIVE },
  { label: "Completed", value: ContractStatus.COMPLETED },
  { label: "Cancelled", value: ContractStatus.CANCELLED },
  { label: "Overdue", value: ContractStatus.OVERDUE },
];

export const collateralStatusOptions: BaseSelectOption[] = [
  { label: "None", value: CollateralStatus.NONE },
  { label: "Cash", value: CollateralStatus.CASH },
  { label: "Asset", value: CollateralStatus.ASSET },
  { label: "Both", value: CollateralStatus.BOTH },
];

export const collateralTypeOptions: BaseSelectOption[] = [
  { label: "Cash", value: CollateralType.CASH },
  { label: "Identity Card", value: CollateralType.IDENTITY_CARD },
  { label: "Driver License", value: CollateralType.DRIVER_LICENSE },
  { label: "Motorbike", value: CollateralType.MOTORBIKE },
  { label: "Laptop", value: CollateralType.LAPTOP },
  { label: "Other", value: CollateralType.OTHER },
];

export const collateralItemStatusOptions: BaseSelectOption[] = [
  { label: "Holding", value: CollateralItemStatus.HOLDING },
  { label: "Returned", value: CollateralItemStatus.RETURNED },
  { label: "Forfeited", value: CollateralItemStatus.FORFEITED },
];

export const feeTypeOptions: BaseSelectOption[] = [
  { label: "Delivery", value: FeeType.DELIVERY },
  { label: "Cleaning", value: FeeType.CLEANING },
  { label: "Damage", value: FeeType.DAMAGE },
  { label: "Late Fee", value: FeeType.LATE },
];

export const contractStatusColor = {
  [ContractStatus.RESERVED]: "orange",
  [ContractStatus.ACTIVE]: "processing",
  [ContractStatus.COMPLETED]: "success",
  [ContractStatus.CANCELLED]: "error",
  [ContractStatus.OVERDUE]: "warning",
} as const;

const contractFieldLabels = {
  customerId: "Customer",
  startDate: "Start Date",
  expectedReturnDate: "Expected Return Date",
  actualReturnDate: "Actual Return Date",
  status: "Status",
  collateralStatus: "Collateral Status",
  details: "Details",
  collaterals: "Collaterals",
  fees: "Fees",
  itemId: "Item",
  handoverStatus: "Handover Status",
  assetDescription: "Description",
  pickupLocation: "Pickup Location",
  returnLocation: "Return Location",
  note: "Note",
} as const;

const contractFieldNames = {
  customerId: "customerId",
  startDate: "startDate",
  expectedReturnDate: "expectedReturnDate",
  actualReturnDate: "actualReturnDate",
  status: "status",
  collateralStatus: "collateralStatus",
  details: "details",
  collaterals: "collaterals",
  fees: "fees",
} as const;

export {
  CONTRACT_FEATURE,
  contractColumns,
  contractFieldLabels,
  contractFieldNames,
};
