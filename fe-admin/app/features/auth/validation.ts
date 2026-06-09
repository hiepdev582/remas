import * as zod from "zod";

export const loginFieldSchema = {
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
      "Username must contain only letters, numbers, dots, or underscores",
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

  getSchema() {
    return zod.object({
      username: this.username,
      password: this.password,
    });
  },
} as const;
