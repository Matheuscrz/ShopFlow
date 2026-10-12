import { z } from "zod";

export const registerSchema = z
  .object({
    name: z
      .string()
      .trim()
      .min(2, "O nome deve conter no mínimo 2 caracteres")
      .max(120, "Máximo de 120 caracteres"),
    email: z
      .string()
      .trim()
      .email("Formato de e-mail inválido")
      .max(255, "Máximo de 255 caracteres"),
    password: z
      .string()
      .min(10, "A senha deve conter no mínimo 10 caracteres")
      .max(128, "Máximo de 128 caracteres"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    path: ["confirmPassword"],
    message: "As senhas não coincidem",
  });

export type RegisterFormData = z.infer<typeof registerSchema>;
