import type { BaseInputProps } from "~/components/base/Input.vue";
import type { BaseInputPasswordProps } from "~/components/base/InputPassword.vue";
import type { BaseTextAreaProps } from "~/components/base/TextArea.vue";

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

export enum FormState {
  ADD = "add",
  EDIT = "edit",
  VIEW = "view",
}
//#endregion

//#region Interface
interface BaseFieldConfig {
  name: string;
  label: string;
  required?: boolean;
  options?: { label: string; value: string }[];
}

//#endregion

//#region Types
export type FormFieldConfigMap = {
  [FormFieldType.TEXT]: BaseInputProps;
  [FormFieldType.PASSWORD]: BaseInputPasswordProps;
  [FormFieldType.AREA]: BaseTextAreaProps;
};

export type FormFieldConfig = {
  [K in FormFieldType]: BaseFieldConfig & {
    type: K;
    config: FormFieldConfigMap[K];
  };
}[FormFieldType];
//#endregion

//#region Constants
export const componentNames = {
  input: "BaseInput",
  password: "BaseInputPassword",
  area: "BaseTextArea",
} as const;
//#endregion
