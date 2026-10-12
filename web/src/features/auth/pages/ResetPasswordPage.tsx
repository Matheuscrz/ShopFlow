import { zodResolver } from "@hookform/resolvers/zod";
import { AlertCircle } from "lucide-react";
import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useNavigate, useSearchParams } from "react-router";
import { Button } from "../../../components/ui/Button";
import { Input } from "../../../components/ui/Input";
import { authApi } from "../api/auth.api";
import {
  resetPasswordSchema,
  type ResetPasswordFormData,
} from "../schemas/recovery.schema";

export const ResetPasswordPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get("token") ?? "";

  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(
    token ? null : "O link de recuperação é inválido ou está incompleto.",
  );
  const [success, setSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResetPasswordFormData>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: {
      token,
      newPassword: "",
      confirmPassword: "",
    },
  });

  const onSubmit = async (data: ResetPasswordFormData) => {
    setLoading(true);
    setErrorMessage(null);

    try {
      await authApi.resetPassword(data);
      setSuccess(true);

      setTimeout(() => {
        navigate("/login", { replace: true });
      }, 1500);
    } catch (error: any) {
      setErrorMessage(
        error.response?.data?.detail ??
          "O link é inválido ou expirou. Solicite uma nova recuperação.",
      );
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <main className="min-h-screen flex items-center justify-center p-4 bg-background text-text-main">
        <div className="w-full max-w-md bg-surface border border-border rounded-xl p-8 shadow-sm text-center">
          <h1 className="text-2xl font-bold mb-3">Senha alterada</h1>
          <p className="text-sm text-text-muted">
            Sua senha foi redefinida. Redirecionando para o login...
          </p>
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-screen flex items-center justify-center p-4 bg-background text-text-main">
      <div className="w-full max-w-md bg-surface border border-border rounded-xl p-8 shadow-sm">
        <header className="mb-6 text-center">
          <h1 className="text-2xl font-bold">Criar nova senha</h1>
          <p className="text-sm text-text-muted mt-1">
            Defina uma nova senha para sua conta.
          </p>
        </header>

        {errorMessage && (
          <div className="mb-6 p-3.5 rounded-lg bg-danger/10 border border-danger/20 flex gap-3 text-danger text-sm">
            <AlertCircle className="w-5 h-5 shrink-0" />
            <span>{errorMessage}</span>
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <input type="hidden" {...register("token")} />

          <Input
            label="Nova senha"
            type="password"
            autoComplete="new-password"
            placeholder="••••••••••"
            error={errors.newPassword?.message}
            {...register("newPassword")}
          />

          <Input
            label="Confirmar nova senha"
            type="password"
            autoComplete="new-password"
            placeholder="••••••••••"
            error={errors.confirmPassword?.message}
            {...register("confirmPassword")}
          />

          <Button type="submit" isLoading={loading} disabled={!token}>
            Redefinir senha
          </Button>

          <Link
            to="/login"
            className="block text-center text-sm text-text-muted hover:text-primary"
          >
            Voltar para o login
          </Link>
        </form>
      </div>
    </main>
  );
};
