const placeholders = {
  enterDefault: `Please enter...`,
  enter: (field: string) => `Please enter ${field.toLowerCase()}`,
} as const;

export { placeholders };
