import { zodResolver } from "@hookform/resolvers/zod";
import { AlertCircle } from "lucide-react";
import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { Link } from "react-router";
import { Button } from "../../../components/ui/Button";
import { Input } from "../../../components/ui/Input";
import { authApi } from "../api/auth.api";
import {
  forgotPasswordSchema,
  type ForgotPasswordFormData,
} from "../schemas/recovery.schema";

export const ForgotPasswordPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [submitted, setSubmitted] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ForgotPasswordFormData>({
    resolver: zodResolver(forgotPasswordSchema),
    defaultValues: { email: "" },
  });

  const onSubmit = async (data: ForgotPasswordFormData) => {
    setLoading(true);
    setErrorMessage(null);

    try {
      await authApi.forgotPassword(data);
      setSubmitted(true);
    } catch {
      setErrorMessage(
        "Não foi possível processar a solicitação. Tente novamente.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen flex items-center justify-center p-4 bg-background text-text-main">
      <div className="w-full max-w-md bg-surface border border-border rounded-xl p-8 shadow-sm">
        <header className="mb-6 text-center">
          <h1 className="text-2xl font-bold">Recuperar senha</h1>
          <p className="text-sm text-text-muted mt-1">
            Informe seu e-mail para receber as instruções.
          </p>
        </header>

        {errorMessage && (
          <div className="mb-6 p-3.5 rounded-lg bg-danger/10 border border-danger/20 flex gap-3 text-danger text-sm">
            <AlertCircle className="w-5 h-5 shrink-0" />
            <span>{errorMessage}</span>
          </div>
        )}

        {submitted ? (
          <div className="space-y-4">
            <p className="text-sm text-text-muted">
              Se o e-mail estiver cadastrado, enviaremos um link de recuperação.
              Verifique sua caixa de entrada e também a pasta de spam.
            </p>

            <Link
              to="/login"
              className="block text-center text-primary hover:underline text-sm"
            >
              Voltar para o login
            </Link>
          </div>
        ) : (
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input
              label="E-mail"
              type="email"
              autoComplete="email"
              placeholder="usuario@dominio.com"
              error={errors.email?.message}
              {...register("email")}
            />

            <Button type="submit" isLoading={loading}>
              Enviar instruções
            </Button>

            <Link
              to="/login"
              className="block text-center text-sm text-text-muted hover:text-primary"
            >
              Voltar para o login
            </Link>
          </form>
        )}
      </div>
    </main>
  );
};
