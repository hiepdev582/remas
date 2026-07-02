import * as zod from "zod";
import {
  customerFieldLabels,
  CUSTOMER_NAME_MIN_CHAR,
  CUSTOMER_NAME_MAX_CHAR,
  CUSTOMER_IDENTITY_CARD_MAX_CHAR,
  CUSTOMER_DRIVER_LICENSE_MAX_CHAR,
} from "./constants";

export const upsertCustomerFieldSchema = {
  name: zod
    .string()
    .trim()
    .min(MIN_CHAR, errorMessages.required(customerFieldLabels.name))
    .min(
      CUSTOMER_NAME_MIN_CHAR,
      errorMessages.minLength(customerFieldLabels.name, CUSTOMER_NAME_MIN_CHAR),
    )
    .max(
      CUSTOMER_NAME_MAX_CHAR,
      errorMessages.maxLength(customerFieldLabels.name, CUSTOMER_NAME_MAX_CHAR),
    )
    .regex(
      /^[\p{L}\p{N}._\s-]+$/u,
      "Contain only letters, numbers, spaces, dots, underscores, or dashes",
    ),
  phone: zod
    .string()
    .trim()
    .min(MIN_CHAR, errorMessages.required(customerFieldLabels.phone))
    .regex(/^(0|84|\+84)[35789][0-9]{8}$/, "Invalid Vietnamese phone number format"),
  identityCard: zod
    .string()
    .trim()
    .max(
      CUSTOMER_IDENTITY_CARD_MAX_CHAR,
      errorMessages.maxLength(customerFieldLabels.identityCard, CUSTOMER_IDENTITY_CARD_MAX_CHAR),
    )
    .optional()
    .nullable()
    .or(zod.literal("")),
  driverLicense: zod
    .string()
    .trim()
    .max(
      CUSTOMER_DRIVER_LICENSE_MAX_CHAR,
      errorMessages.maxLength(customerFieldLabels.driverLicense, CUSTOMER_DRIVER_LICENSE_MAX_CHAR),
    )
    .optional()
    .nullable()
    .or(zod.literal("")),
  trustScore: zod
    .number()
    .min(0, "Trust score must be at least 0")
    .max(100, "Trust score cannot exceed 100")
    .optional(),
  documents: zod.array(zod.any()).optional(),

  getSchema() {
    return zod.object({
      name: this.name,
      phone: this.phone,
      identityCard: this.identityCard,
      driverLicense: this.driverLicense,
      trustScore: this.trustScore,
      documents: this.documents,
    });
  },
} as const;
