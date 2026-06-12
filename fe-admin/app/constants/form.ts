//#region Enum
export enum FormFieldType {
  TEXT = "text",
  PASSWORD = "password",
  AREA = "area",
}

export enum FormLayout {
  HORIZONTAL = "horizontal",
  VERTICAL = "vertical",
  INLINE = "inline",
}
//#endregion

//#region Interface
export interface FormFieldConfig {
  name: string;
  label: string;
  type: FormFieldType;
  required?: boolean;
  placeholder?: string;
  options?: { label: string; value: string }[];
}
//#endregion

//#region Constants
export const componentNames = {
  input: "BaseInput",
  password: "BaseInputPassword",
  area: "BaseTextArea",
} as const;
//#endregion
