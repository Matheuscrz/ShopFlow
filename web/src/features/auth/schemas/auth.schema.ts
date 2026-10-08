import { z } from 'zod';

export const loginSchema = z.object({
  email: z
    .string()
    .trim()
    .min(1, 'E-mail é obrigatório')
    .email('Formato de e-mail inválido')
    .max(255, 'Máximo de 255 caracteres'),
  password: z
    .string()
    .min(1, 'Senha é obrigatória')
    .min(10, 'A senha deve conter no mínimo 10 caracteres')
    .max(128, 'Máximo de 128 caracteres'),
});

export type LoginFormData = z.infer<typeof loginSchema>;