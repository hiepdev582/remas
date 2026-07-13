export type Customer = {
  id: number;
  name: string;
  phone: string;
  identityCard?: string;
  driverLicense?: string;
  trustScore: number;
  gender?: string;
  dob?: string;
  age?: number;
  address?: string;
  lastInteractionDate?: string;
  daysSinceLastInteraction?: number;
  note?: string;
  link?: string;
  documents?: CustomerDocument[];
};

export type CustomerDocument = {
  id: number;
  customerId: number;
  documentType: DocumentType;
  documentNumber?: string;
  imageUrl: string;
  uid?: string; // used for upload list key mapping
  url?: string; // used for preview mapping
  preview?: string;
};

export type AddCustomerRequest = Omit<Customer, "id" | "documents" | "age" | "daysSinceLastInteraction"> & {
  documents?: Omit<CustomerDocument, "id" | "customerId">[];
};

export type EditCustomerRequest = Omit<Customer, "id" | "documents" | "age" | "daysSinceLastInteraction"> & {
  documents?: Omit<CustomerDocument, "id" | "customerId">[];
};
