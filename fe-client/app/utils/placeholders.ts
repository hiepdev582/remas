const placeholders = {
  enterDefault: `Please enter...`,
  enter: (field: string) => `Please enter ${field.toLowerCase()}`,
  selectDefault: `Please select...`,
  select: (field: string) => `Please select ${field.toLowerCase()}`,
} as const;

export { placeholders };
