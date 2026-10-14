import { z } from "zod";
import { isValidCpf, stripCpf } from "../../../lib/cpf";

export const registerSchema = z
  .object({
    name: z.string().trim().min(3, "Nome deve ter pelo menos 3 caracteres"),
    email: z.string().trim().email("E-mail inválido"),
    cpf: z.string().transform(stripCpf).refine(isValidCpf, "CPF inválido"),
    password: z
      .string()
      .min(8, "Senha deve ter no mínimo 8 caracteres")
      .regex(/[A-Z]/, "Ao menos uma letra maiúscula")
      .regex(/[0-9]/, "Ao menos um número")
      .regex(/[^a-zA-Z0-9]/, "Ao menos um caractere especial"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "As senhas não coincidem",
    path: ["confirmPassword"],
  });

export type RegisterFormData = z.infer<typeof registerSchema>;
