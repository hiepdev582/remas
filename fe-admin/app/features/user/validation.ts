import * as zod from "zod";

export const upsertUserFieldSchema = {
  username: zod
    .string()
    .trim()
    .min(MIN_CHAR, errorMessages.required(authFieldLabels.username))
    .min(
      AUTH_USERNAME_MIN_CHAR,
      errorMessages.minLength(authFieldLabels.username, AUTH_USERNAME_MIN_CHAR),
    )
    .max(
      AUTH_USERNAME_MAX_CHAR,
      errorMessages.maxLength(authFieldLabels.username, AUTH_USERNAME_MAX_CHAR),
    )
    .regex(
      /^[a-zA-Z0-9._]+$/,
      "Contain only letters, numbers, dots, or underscores",
    ),
  password: zod
    .string()
    .trim()
    .min(MIN_CHAR, errorMessages.required(authFieldLabels.password))
    .min(
      AUTH_PASSWORD_MIN_CHAR,
      errorMessages.minLength(authFieldLabels.password, AUTH_PASSWORD_MIN_CHAR),
    )
    .max(
      AUTH_PASSWORD_MAX_CHAR,
      errorMessages.maxLength(authFieldLabels.password, AUTH_PASSWORD_MAX_CHAR),
    )
    .regex(hasLowerCase, "Password must contain at least one lowercase letter")
    .regex(hasUpperCase, "Password must contain at least one uppercase letter")
    .regex(hasNumber, "Password must contain at least one digit")
    .regex(
      hasSpecialChar,
      "Password must contain at least one special character",
    ),
  email: zod
    .string()
    .trim()
    .email("Invalid email format")
    .optional()
    .nullable()
    .or(zod.literal("")),
  fullName: zod.string().trim().nullable(),
  isActive: zod.boolean().optional(),
  roles: zod.array(zod.string()).min(1, "At least one role is required"),

  getSchema(isEdit = false) {
    return zod.object({
      username: this.username,
      password: isEdit
        ? zod
            .string()
            .optional()
            .nullable()
            .or(zod.literal(""))
            .superRefine((val, ctx) => {
              if (val && val.trim().length > 0) {
                const res = this.password.safeParse(val);
                if (!res.success) {
                  res.error.issues.forEach((issue) => ctx.addIssue(issue));
                }
              }
            })
        : this.password,
      email: this.email,
      fullName: this.fullName,
      isActive: this.isActive,
      roles: this.roles,
    });
  },
} as const;
