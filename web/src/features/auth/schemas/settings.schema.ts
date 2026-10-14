import { z } from "zod";

export const updateEmailSchema = z.object({
  newEmail: z.string().email("E-mail inválido"),
  currentPassword: z.string().min(1, "Informe sua senha atual para confirmar"),
});

export type UpdateEmailFormData = z.infer<typeof updateEmailSchema>;

export const changePasswordSchema = z
  .object({
    currentPassword: z.string().min(1, "Senha atual é obrigatória"),
    newPassword: z
      .string()
      .min(8, "A nova senha deve ter no mínimo 8 caracteres")
      .regex(/[A-Z]/, "Ao menos uma letra maiúscula")
      .regex(/[0-9]/, "Ao menos um número")
      .regex(/[^a-zA-Z0-9]/, "Ao menos um caractere especial"),
    confirmNewPassword: z.string(),
  })
  .refine((data) => data.newPassword === data.confirmNewPassword, {
    message: "A confirmação não coincide com a nova senha",
    path: ["confirmNewPassword"],
  });

export type ChangePasswordFormData = z.infer<typeof changePasswordSchema>;
