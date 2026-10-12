import { z } from "zod";

export const addressSchema = z.object({
  zipCode: z.string().min(8, "Informe um CEP válido").max(20),
  street: z.string().min(1, "Informe a rua").max(150),
  number: z.string().min(1, "Informe o número").max(20),
  complement: z.string().max(150),
  neighborhood: z.string().min(1, "Informe o bairro").max(100),
  city: z.string().min(1, "Informe a cidade").max(100),
  state: z.string().min(1, "Informe o estado").max(100),
  country: z.string().min(1, "Informe o país").max(100),
});
