import { zodResolver } from "@hookform/resolvers/zod";
import { AlertCircle, Moon, Sun } from "lucide-react";
import React from "react";
import { useForm } from "react-hook-form";
import { Button } from "../../../components/ui/Button";
import { Input } from "../../../components/ui/Input";
import { useThemeStore } from "../../../stores/theme-store";
import { useAuth } from "../hooks/useAuth";
import { type LoginFormData, loginSchema } from "../schemas/auth.schema";

export const LoginPage: React.FC = () => {
  const { login, loading, errorMessage, retryAfter } = useAuth();
  const { theme, toggleTheme } = useThemeStore();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: "", password: "" },
  });

  const onSubmit = (data: LoginFormData) => {
    login(data);
  };

  return (
    <main className="min-h-screen flex items-center justify-center p-4 bg-background text-text-main transition-colors duration-200">
      <div className="absolute top-4 right-4">
        <button
          type="button"
          onClick={toggleTheme}
          className="p-2 rounded-lg border border-border hover:bg-surface text-text-muted hover:text-text-main transition-colors"
          aria-label="Alternar tema"
        >
          {theme === "dark" ? (
            <Sun className="w-5 h-5 text-warning" />
          ) : (
            <Moon className="w-5 h-5" />
          )}
        </button>
      </div>

      <div className="w-full max-w-md bg-surface border border-border rounded-xl p-8 shadow-sm">
        <header className="mb-6 text-center">
          <h1 className="text-2xl font-bold tracking-tight">
            Entrar no ShopFlow
          </h1>
          <p className="text-sm text-text-muted mt-1">
            Acesse sua conta corporativa
          </p>
        </header>

        {errorMessage && (
          <div className="mb-6 p-3.5 rounded-lg bg-danger/10 border border-danger/20 flex items-start gap-3 text-danger text-sm">
            <AlertCircle className="w-5 h-5 shrink-0 mt-0.5" />
            <span>{errorMessage}</span>
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="E-mail"
            type="email"
            placeholder="usuario@dominio.com"
            error={errors.email?.message}
            {...register("email")}
          />

          <Input
            label="Senha"
            type="password"
            placeholder="••••••••••"
            error={errors.password?.message}
            {...register("password")}
          />

          <Button
            type="submit"
            isLoading={loading}
            disabled={retryAfter !== null && retryAfter > 0}
          >
            Entrar
          </Button>
        </form>
      </div>
    </main>
  );
};
