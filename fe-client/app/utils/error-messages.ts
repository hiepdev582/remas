const errorMessages = {
  required: (field: string) => `${field} is required`,
  minLength: (field: string, min: number) =>
    `${field} must be at least ${min} characters long`,
  maxLength: (field: string, max: number) =>
    `${field} must be a maximum of ${max} characters long`,
} as const;

export { errorMessages };
