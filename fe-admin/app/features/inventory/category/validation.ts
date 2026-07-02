import * as zod from "zod";

export const upsertCategoryFieldSchema = {
  name: zod
    .string()
    .trim()
    .min(MIN_CHAR, errorMessages.required(categoryFieldLabels.name))
    .min(
      CATEGORY_NAME_MIN_CHAR,
      errorMessages.minLength(categoryFieldLabels.name, CATEGORY_NAME_MIN_CHAR),
    )
    .max(
      CATEGORY_NAME_MAX_CHAR,
      errorMessages.maxLength(categoryFieldLabels.name, CATEGORY_NAME_MAX_CHAR),
    )
    .regex(
      /^[\p{L}\p{N}._\s-]+$/u,
      "Contain only letters, numbers, spaces, dots, underscores, or dashes",
    ),
  description: zod
    .string()
    .trim()
    .max(
      CATEGORY_DESCRIPTION_MAX_CHAR,
      errorMessages.maxLength(
        categoryFieldLabels.description,
        CATEGORY_DESCRIPTION_MAX_CHAR,
      ),
    )
    .optional()
    .nullable()
    .or(zod.literal("")),

  getSchema() {
    return zod.object({
      name: this.name,
      description: this.description,
    });
  },
} as const;
