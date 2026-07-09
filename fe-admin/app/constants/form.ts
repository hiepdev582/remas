import type { BaseInputProps } from "~/components/base/Input.vue";
import type { BaseInputPasswordProps } from "~/components/base/InputPassword.vue";
import type { BaseTextAreaProps } from "~/components/base/TextArea.vue";
import type { BaseSelectProps } from "~/components/base/Select.vue";
import type { BaseUploadPicturesProps } from "~/components/base/UploadPictures.vue";
import type { BaseInputNumberProps } from "~/components/base/InputNumber.vue";
import type { BaseSwitchProps } from "~/components/base/Switch.vue";
import type { BaseUploadDocumentsProps } from "~/components/base/UploadDocuments.vue";
import type { BaseDatePickerProps } from "~/components/base/DatePicker.vue";

//#region Enum
export enum FormFieldType {
  TEXT = "text",
  PASSWORD = "password",
  AREA = "area",
  SELECT = "select",
  UPLOAD_PICTURES = "upload-pictures",
  NUMBER = "number",
  SWITCH = "switch",
  UPLOAD_DOCUMENTS = "upload-documents",
  DATE = "date",
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
  [FormFieldType.SELECT]: BaseSelectProps;
  [FormFieldType.UPLOAD_PICTURES]: BaseUploadPicturesProps;
  [FormFieldType.NUMBER]: BaseInputNumberProps;
  [FormFieldType.SWITCH]: BaseSwitchProps;
  [FormFieldType.UPLOAD_DOCUMENTS]: BaseUploadDocumentsProps;
  [FormFieldType.DATE]: BaseDatePickerProps;
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
  select: "BaseSelect",
  uploadPictures: "BaseUploadPictures",
  number: "BaseInputNumber",
  switch: "BaseSwitch",
  uploadDocuments: "BaseUploadDocuments",
  date: "BaseDatePicker",
} as const;
//#endregion
