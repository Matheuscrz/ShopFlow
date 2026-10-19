import { zodResolver } from "@hookform/resolvers/zod";
import { AlertCircle } from "lucide-react";
import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "../../../components/ui/Button";
import { Input } from "../../../components/ui/Input";
import { authApi } from "../api/auth.api";
import {
  registerSchema,
  type RegisterFormData,
} from "../schemas/register.schema";
import { formatCpf } from "../../../lib/cpf";
import { formatPhone } from "../../../lib/phone";

export const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const {
    register,
    setValue,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      name: "",
      email: "",
      cpf: "",
      phone: "",
      password: "",
      confirmPassword: "",
    },
  });

  const onSubmit = async (data: RegisterFormData) => {
    setLoading(true);
    setErrorMessage(null);

    try {
      await authApi.register(data);
      setSuccess(true);

      setTimeout(() => {
        navigate("/login", { replace: true });
      }, 1200);
    } catch (error: any) {
      if (error.response?.status === 409) {
        setErrorMessage("Este e-mail já está cadastrado.");
      } else if (error.response?.status === 400) {
        setErrorMessage(
          error.response?.data?.detail ?? "Verifique os dados informados.",
        );
      } else {
        setErrorMessage("Não foi possível concluir o cadastro.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen flex items-center justify-center p-4 bg-background text-text-main">
      <div className="w-full max-w-md bg-surface border border-border rounded-xl p-8 shadow-sm">
        <header className="mb-6 text-center">
          <h1 className="text-2xl font-bold">Criar conta</h1>
          <p className="text-sm text-text-muted mt-1">
            Cadastre-se para acessar o ShopFlow
          </p>
        </header>

        {errorMessage && (
          <div className="mb-6 p-3.5 rounded-lg bg-danger/10 border border-danger/20 flex gap-3 text-danger text-sm">
            <AlertCircle className="w-5 h-5 shrink-0" />
            <span>{errorMessage}</span>
          </div>
        )}

        {success && (
          <div className="mb-6 p-3.5 rounded-lg bg-success/10 border border-success/20 text-success text-sm">
            Cadastro realizado. Redirecionando para o login...
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="Nome"
            type="text"
            autoComplete="name"
            placeholder="Seu nome"
            error={errors.name?.message}
            {...register("name")}
          />

          <Input
            label="E-mail"
            type="email"
            autoComplete="email"
            placeholder="usuario@dominio.com"
            error={errors.email?.message}
            {...register("email")}
          />

          <Input
            label="CPF"
            placeholder="000.000.000-00"
            inputMode="numeric"
            autoComplete="off"
            maxLength={14}
            error={errors.cpf?.message}
            {...register("cpf", {
              onChange: (event) => {
                setValue("cpf", formatCpf(event.target.value), {
                  shouldValidate: true,
                  shouldDirty: true,
                });
              },
            })}
          />

          <Input
            label="Telefone"
            type="tel"
            inputMode="tel"
            autoComplete="tel"
            placeholder="(11) 99999-8888"
            maxLength={15}
            error={errors.phone?.message}
            {...register("phone", {
              onChange: (event) => {
                setValue("phone", formatPhone(event.target.value), {
                  shouldValidate: true,
                  shouldDirty: true,
                });
              },
            })}
          />

          <Input
            label="Senha"
            type="password"
            autoComplete="new-password"
            placeholder="••••••••••"
            error={errors.password?.message}
            {...register("password")}
          />

          <Input
            label="Confirmar senha"
            type="password"
            autoComplete="new-password"
            placeholder="••••••••••"
            error={errors.confirmPassword?.message}
            {...register("confirmPassword")}
          />

          <Button type="submit" isLoading={loading} disabled={success}>
            Criar conta
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-text-muted">
          Já possui uma conta?{" "}
          <Link to="/login" className="text-primary hover:underline">
            Entrar
          </Link>
        </p>
      </div>
    </main>
  );
};
