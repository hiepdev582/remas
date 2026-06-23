import * as zod from "zod";
import {
  ITEM_NAME_MIN_CHAR,
  ITEM_NAME_MAX_CHAR,
  itemFieldLabels,
} from "./constants";

export const upsertItemFieldSchema = {
  name: zod
    .string()
    .trim()
    .min(MIN_CHAR, errorMessages.required(itemFieldLabels.name))
    .min(
      ITEM_NAME_MIN_CHAR,
      errorMessages.minLength(itemFieldLabels.name, ITEM_NAME_MIN_CHAR),
    )
    .max(
      ITEM_NAME_MAX_CHAR,
      errorMessages.maxLength(itemFieldLabels.name, ITEM_NAME_MAX_CHAR),
    )
    .regex(
      /^[a-zA-Z0-9._\s-]+$/,
      "Contain only letters, numbers, spaces, dots, underscores, or dashes",
    ),
  categoryId: zod
    .number({ required_error: errorMessages.required(itemFieldLabels.categoryId) })
    .positive(errorMessages.required(itemFieldLabels.categoryId)),
  status: zod.string().optional(),

  getSchema() {
    return zod.object({
      name: this.name,
      categoryId: this.categoryId,
      status: this.status,
    });
  },
} as const;
