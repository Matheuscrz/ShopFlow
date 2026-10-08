import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../../stores/auth-store";
import { authApi } from "../api/auth.api";
import type { LoginFormData } from "../schemas/auth.schema";

export function useAuth() {
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [retryAfter, setRetryAfter] = useState<number | null>(null);

  const { setSession, clearSession, user, isAuthenticated } = useAuthStore();
  const navigate = useNavigate();

  const login = async (data: LoginFormData) => {
    setLoading(true);
    setErrorMessage(null);
    setRetryAfter(null);

    try {
      const response = await authApi.login(data);
      setSession(response.accessToken, response.user);
      navigate("/dashboard", { replace: true });
    } catch (err: any) {
      if (err.response?.status === 429) {
        const seconds = Number(err.response.headers["retry-after"] ?? 60);
        setRetryAfter(seconds);
        setErrorMessage(
          `Muitas tentativas. Aguarde ${seconds}s antes de tentar novamente.`,
        );
      } else if (err.response?.status === 401 || err.response?.status === 400) {
        setErrorMessage("Credenciais inválidas. Verifique seu e-mail e senha.");
      } else {
        setErrorMessage("Serviço temporariamente indisponível.");
      }
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } finally {
      clearSession();
      navigate("/login", { replace: true });
    }
  };

  return {
    login,
    logout,
    user,
    isAuthenticated,
    loading,
    errorMessage,
    retryAfter,
  };
}
