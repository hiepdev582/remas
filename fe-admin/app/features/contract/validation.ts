import * as zod from "zod";
import {
  contractFieldLabels,
  ContractStatus,
  CollateralStatus,
  CollateralType,
  CollateralItemStatus,
  PriceType,
  FeeType,
} from "./constants";

export const upsertContractFieldSchema = {
  customerId: zod.number({
    required_error: errorMessages.required(contractFieldLabels.customerId),
    invalid_type_error: errorMessages.required(contractFieldLabels.customerId),
  }),
  startDate: zod
    .string()
    .min(MIN_CHAR, errorMessages.required(contractFieldLabels.startDate)),
  expectedReturnDate: zod
    .string()
    .min(MIN_CHAR, errorMessages.required(contractFieldLabels.expectedReturnDate)),
  actualReturnDate: zod.string().optional().nullable(),
  status: zod.nativeEnum(ContractStatus).optional(),
  collateralStatus: zod.nativeEnum(CollateralStatus, {
    required_error: errorMessages.required(contractFieldLabels.collateralStatus),
    invalid_type_error: errorMessages.required(contractFieldLabels.collateralStatus),
  }),
  details: zod
    .array(
      zod.object({
        itemId: zod.number(),
        quantity: zod.number().min(1, "Quantity must be at least 1"),
        appliedPriceType: zod.nativeEnum(PriceType),
        priceApplied: zod.number().min(0, "Price must be at least 0"),
        subtotal: zod.number().min(0, "Subtotal must be at least 0"),
        handoverStatus: zod.string().optional().nullable(),
      })
    )
    .min(1, "At least one item is required"),
  collaterals: zod
    .array(
      zod.object({
        collateralType: zod.nativeEnum(CollateralType),
        value: zod.number().min(0, "Value must be at least 0"),
        assetDescription: zod.string().optional().nullable(),
        status: zod.nativeEnum(CollateralItemStatus).optional(),
      })
    )
    .optional(),
  fees: zod
    .array(
      zod.object({
        feeType: zod.nativeEnum(FeeType),
        amount: zod.number().min(0, "Amount must be at least 0"),
        pickupLocation: zod.string().optional().nullable(),
        returnLocation: zod.string().optional().nullable(),
        note: zod.string().optional().nullable(),
      })
    )
    .optional(),

  getSchema() {
    return zod.object({
      customerId: this.customerId,
      startDate: this.startDate,
      expectedReturnDate: this.expectedReturnDate,
      actualReturnDate: this.actualReturnDate,
      status: this.status,
      collateralStatus: this.collateralStatus,
      details: this.details,
      collaterals: this.collaterals,
      fees: this.fees,
    });
  },

  getGeneralSchema() {
    return zod.object({
      customerId: this.customerId,
      startDate: this.startDate,
      expectedReturnDate: this.expectedReturnDate,
      actualReturnDate: this.actualReturnDate.optional().nullable().or(zod.literal("")),
      status: this.status,
      collateralStatus: this.collateralStatus,
    });
  },
} as const;
