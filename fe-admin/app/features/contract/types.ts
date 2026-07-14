import type { PriceType } from "@/features/inventory/item/constants";
import type {
  ContractStatus,
  CollateralStatus,
  CollateralType,
  CollateralItemStatus,
  FeeType,
} from "./constants";

export type ContractDetail = {
  id?: number;
  itemId: number;
  itemName?: string;
  quantity: number;
  appliedPriceType: PriceType;
  priceApplied: number;
  subtotal: number;
  handoverStatus?: string;
};

export type ContractCollateral = {
  id?: number;
  collateralType: CollateralType;
  value: number;
  assetDescription?: string;
  status: CollateralItemStatus;
};

export type ContractFee = {
  id?: number;
  feeType: FeeType;
  amount: number;
  pickupLocation?: string;
  returnLocation?: string;
  note?: string;
};

export type Contract = {
  id?: number;
  customerId: number;
  customerName?: string;
  customerPhone?: string;
  startDate: string;
  expectedReturnDate: string;
  actualReturnDate?: string;
  totalItemPrice?: number;
  totalFeePrice?: number;
  finalAmount?: number;
  collateralStatus: CollateralStatus;
  status: ContractStatus;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
  details: ContractDetail[];
  collaterals: ContractCollateral[];
  fees: ContractFee[];
};

export type AddContractRequest = Omit<Contract, "id">;
export type EditContractRequest = Omit<Contract, "id">;
