export interface UserProfile {
  id: string;
  email: string;
  name: string;
  cpf: string;
  role: string;
  status?: string;
}

export interface UserAddress {
  id: string;
  userId: string;
  zipCode: string;
  street: string;
  number: string;
  complement?: string;
  neighborhood: string;
  city: string;
  state: string;
  country: string;
  createdAt?: string;
}
