//#region Constants
const MIN_CHAR = 1;
//#endregion

//#region Regex
const hasLowerCase = /[a-z]/;
const hasUpperCase = /[A-Z]/;
const hasNumber = /[\d]/;
const hasSpecialChar = /[^a-zA-Z\d]/;
const numberFormatterRegex = /\B(?=(\d{3})+(?!\d))/g;
const numberParserRegex = /\$\s?|(,*)/g;
//#endregion

export {
  MIN_CHAR,
  hasLowerCase,
  hasUpperCase,
  hasNumber,
  hasSpecialChar,
  numberFormatterRegex,
  numberParserRegex,
};
