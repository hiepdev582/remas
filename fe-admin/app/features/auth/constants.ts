//#region CONSTANTS
const AUTH_FEATURE = "auth";
const AUTH_TOKEN_EXPIRATION_TIME = 60 * 60 * 24;
const AUTH_USERNAME_MIN_CHAR = 3;
const AUTH_USERNAME_MAX_CHAR = 30;
const AUTH_PASSWORD_MIN_CHAR = 8;
const AUTH_PASSWORD_MAX_CHAR = 64;
//#endregion

//#region Objects
const authFieldLabels = {
  username: "Username",
  password: "Password",
} as const;

const authFieldNames = {
  username: "username",
  password: "password",
} as const;

const authRoles = {
  admin: "ADMIN",
  staff: "STAFF",
  customer: "CUSTOMER",
} as const;
//#endregion

export {
  AUTH_FEATURE,
  AUTH_TOKEN_EXPIRATION_TIME,
  AUTH_USERNAME_MIN_CHAR,
  AUTH_USERNAME_MAX_CHAR,
  AUTH_PASSWORD_MIN_CHAR,
  AUTH_PASSWORD_MAX_CHAR,
  authFieldLabels,
  authFieldNames,
  authRoles,
};
