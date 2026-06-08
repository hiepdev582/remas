//#region CONSTANTS
const AUTH_FEATURE = "auth";
const AUTH_TOKEN_EXPIRATION_TIME = 60 * 60 * 24;
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
  authFieldLabels,
  authFieldNames,
  authRoles,
};
