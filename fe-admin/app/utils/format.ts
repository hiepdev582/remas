export const capitalize = (val: string | null | undefined): string => {
  if (!val) return "";
  const s = String(val).trim();
  return s.charAt(0).toUpperCase() + s.slice(1).toLowerCase();
};
