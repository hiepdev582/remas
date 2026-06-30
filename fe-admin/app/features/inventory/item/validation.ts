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
      /^[\p{L}\p{N}._\s-]+$/u,
      "Contain only letters, numbers, spaces, dots, underscores, or dashes",
    ),
  categoryId: zod
    .number({
      required_error: errorMessages.required(itemFieldLabels.categoryId),
    })
    .positive(errorMessages.required(itemFieldLabels.categoryId)),
  description: zod
    .string()
    .trim()
    .max(
      ITEM_DESCRIPTION_MAX_CHAR,
      errorMessages.maxLength(
        itemFieldLabels.description,
        ITEM_DESCRIPTION_MAX_CHAR,
      ),
    )
    .optional()
    .nullable()
    .or(zod.literal("")),
  status: zod.string().optional(),
  pictures: zod.array(zod.any()).optional(),

  getSchema() {
    return zod.object({
      name: this.name,
      categoryId: this.categoryId,
      description: this.description,
      status: this.status,
      pictures: this.pictures,
    });
  },
} as const;
