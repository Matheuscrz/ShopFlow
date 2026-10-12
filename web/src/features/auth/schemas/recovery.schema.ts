import { z } from "zod";

export const forgotPasswordSchema = z.object({
  email: z.string().trim().email("Formato de e-mail inválido"),
});

export const resetPasswordSchema = z
  .object({
    token: z.string().min(1, "Token de recuperação inválido"),
    newPassword: z
      .string()
      .min(10, "A senha deve conter no mínimo 10 caracteres")
      .max(128, "Máximo de 128 caracteres"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    path: ["confirmPassword"],
    message: "As senhas não coincidem",
  });

export type ForgotPasswordFormData = z.infer<typeof forgotPasswordSchema>;
export type ResetPasswordFormData = z.infer<typeof resetPasswordSchema>;
