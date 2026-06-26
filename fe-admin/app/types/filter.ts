import type { BaseSelectOption } from "~/components/base/Select.vue";

export type FilterAction = {
  key: string;
  icon: string;
  title: string;
  onClick: Function;
};

export enum AdvancedFilterFieldType {
  SELECT = "select",
  TEXT = "text",
}

export type AdvancedFilterConfig = {
  key: string;
  label: string;
  type: AdvancedFilterFieldType;
  placeholder?: string;
  options?: BaseSelectOption[];
  defaultValue?: any;
};
