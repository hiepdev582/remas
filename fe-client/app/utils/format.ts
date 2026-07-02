export const capitalize = (val: string | null | undefined): string => {
  if (!val) return "";
  const s = String(val).trim();
  return s.charAt(0).toUpperCase() + s.slice(1).toLowerCase();
};

export const formatNumber = (val: number | string | null | undefined): string => {
  if (val === null || val === undefined) return "0";
  const num = Number(val);
  if (isNaN(num)) return "0";
  return num.toLocaleString("en-US");
};
